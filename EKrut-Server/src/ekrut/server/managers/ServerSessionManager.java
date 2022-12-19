package ekrut.server.managers;

import java.sql.SQLException;
import java.util.ArrayList;

import ekrut.entity.User;
import ekrut.net.UserResponse;
import ekrut.server.db.UserDAO;

/**
 * The `ServerSessionManager` class provides methods for managing user sessions on the server side.
 */
public class ServerSessionManager {

	private User user = null;
	private ArrayList<User> connectedUsers;
	private UserDAO userDAO;
	/**
	 * Constructs a new `ServerSessionManager` with the given connected users and user.
	 *
	 * @param connectedUsers The list of connected users.
	 * @param user The user.
	 */
	public ServerSessionManager(ArrayList<User> connectedUsers, User user) {
		this.connectedUsers = new ArrayList<User>();
		this.user = user;
	}

	/**
	 * Attempts to login a user with the given username and password.
	 *
	 * @param username The username of the user.
	 * @param password The password of the user.
	 * @return The `UserResponse` object with the login result and user details.
	 * @throws SQLException if a database error occurred.
	 */
	public UserResponse loginUser(String username, String password) throws SQLException {
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
		//Else, add user to the connected users list, and return the connected user with response message OK
		else {
			userResponse.setUser(user);
			connectedUsers.add(user);
			result= "OK";
		}
		userResponse.setResultCode(result);
		return userResponse;
	}
	
	/**
	 * Logs out the user with the given username.
	 *
	 * @param username The username of the user to log out.
	 * @return The `UserResponse` object with the logout result.
	 * @throws SQLException if a database error occurred.
	 */
	public UserResponse logoutUser(String username) throws SQLException {
		String result = null;
		user = userDAO.fetchUserByUsername(username);
		UserResponse userResponse = new UserResponse(result);
		//Check if user not exist in DB
		if (user == null) {
			result = "Couldn't locate subscriber";
		}
		else {
			connectedUsers.remove(connectedUsers.indexOf(user));
			result= "OK";
		}
		userResponse.setResultCode(result);
		return userResponse;
	}

	/**
	 * Determines if the user with the given username is logged in.
	 *
	 * @param username The username of the user.
	 * @return `true` if the user is logged in, `false` if the user is not logged in or if a database error occurred.
	 * @throws SQLException if a database error occurred.
	 */
	public boolean isLoggedin(String username) throws SQLException {
		user = userDAO.fetchUserByUsername(username);
		return connectedUsers.contains(user);
	}

	



}
