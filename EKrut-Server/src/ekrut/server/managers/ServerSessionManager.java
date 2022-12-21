package ekrut.server.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import ekrut.entity.User;
import ekrut.net.UserResponse;
import ekrut.server.db.DBController;
import ekrut.server.db.UserDAO;
import ocsf.server.ConnectionToClient;

/**
 * The `ServerSessionManager` class provides methods for managing user sessions on the server side.
 * 
 *  @author Yovel Gabay
 */
public class ServerSessionManager {

	private User user = null;
	private ArrayList<User> connectedUsers;
	private UserDAO userDAO;
	private HashMap<ConnectionToClient,User> clientUserMap;
	private Timer timer;
	private static final long LOGOUT_TIME = 1800000; // 30 minutes

	
	/**
     * Constructs a new `ServerSessionManager` object and initializes the `connectedUsers` list,
     * `clientUserMap` hash map, and `userDAO` object.
     *
     * @param con the {@link DBController} object to use for database operations
     */
	public ServerSessionManager(DBController con) {
		userDAO = new UserDAO(con);
		connectedUsers =  new ArrayList<>();
		clientUserMap = new HashMap<>();
	}

	 /**
     * Attempts to login the user with the given username and password. If successful, adds the user to
     * the list of connected users, adds the user and the associated {@link ConnectionToClient} object
     * to the `clientUserMap` hash map, and starts a timer for the user's session.
     *
     * @param username the username of the user to login
     * @param password the password of the user to login
     * @param client the {@link ConnectionToClient} object associated with the user
     * @return a {@link UserResponse} object with the result of the login attempt and the {@link User} object, if successful
     */
	public UserResponse loginUser(String username, String password, ConnectionToClient client) {
		String result = null;
		user = userDAO.fetchUserByUsername(username);
		UserResponse userResponse = new UserResponse(result,user);
		if (user == null) {
			result = "Couldn't locate subscriber";
		}
		//if password isn't correct 
		else if	(!user.getPassword().equals(password)){	
			result = "Incorrect username or password";
		}
		else {
			userResponse.setUser(user);
			connectedUsers.add(user);
			clientUserMap.put(client,user);
			startTimer(username);
			result= "OK";
		}
		userResponse.setResultCode(result);
		return userResponse;
	}
    /**
     * Logs out the user with the given username. If the logout is successful, removes the user from
     * the list of connected users, cancels the timer for the user's session, and removes the
     * {@link ConnectionToClient} object associated with the user from the `clientUserMap` hash map.
     *
     * @param username the username of the user to logout
     * @return a {@link UserResponse} object with the result of the logout attempt
     */
	public UserResponse logoutUser(String username) {
		String result = null;
		user = userDAO.fetchUserByUsername(username);
		UserResponse userResponse = new UserResponse(result);
		//Check if user not exist in DB
		if (user == null) {
			result = "Couldn't locate subscriber";
		}
		else {
			connectedUsers.remove(connectedUsers.indexOf(user));
			timer.cancel();
			result= "OK";
			//Remove client-user from clientUserMap
			for(Entry<ConnectionToClient, User> entry : clientUserMap.entrySet()) {
				if(entry.getValue().equals(user))
					clientUserMap.remove(entry.getKey());
			}
		}
		userResponse.setResultCode(result);
		return userResponse;
	}

	/**
	 * Determines if the user with the given username is logged in.
	 *
	 * @param username The username of the user.
	 * @return `true` if the user is logged in, `false` if the user is not logged in or if a database error occurred.
	 */
	public boolean isLoggedin(String username) {
		user = userDAO.fetchUserByUsername(username);
		return connectedUsers.contains(user);
	}
	
	/**
	 * Returns the {@link User} object associated with the given {@link ConnectionToClient} object.
	 * Also resets the timer for the user's session.
	 *
	 * @param client the {@link ConnectionToClient} object associated with the user
	 * @return the {@link User} object associated with the given {@link ConnectionToClient} object
	 */
	public User getUser(ConnectionToClient client) {
		user = clientUserMap.get(client);
		resetTimer(user.getUsername());
		return user;
		
	}
	
	/**
	 * Resets the timer for the user's session.
	 *
	 * @param username the username of the user whose timer needs to be reset
	 */
	public void resetTimer(String username) {
        // Cancel the current timer and start a new one
        timer.cancel();
        startTimer(username);
    }
	
	/**
	 * Starts a timer for the user's session. If the timer expires, the user will be logged out.
	 *
	 * @param username the username of the user whose timer needs to be started
	 */
    public void startTimer(String username) {
        // Start the timer
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logoutUser(username);
            }
        }, LOGOUT_TIME);
    }
	
	
}
