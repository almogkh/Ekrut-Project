package ekrut.server.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import ekrut.entity.ConnectedClient;
import ekrut.entity.Customer;
import ekrut.entity.User;
import ekrut.entity.UserRegistration;
import ekrut.entity.UserType;
import ekrut.net.FetchUserType;
import ekrut.net.ResultType;
import ekrut.net.UserRequest;
import ekrut.net.UserRequestType;
import ekrut.net.UserResponse;
import ekrut.server.EKrutServer;
import ekrut.server.TimeScheduler;
import ekrut.server.UsersImporter;
import ekrut.server.db.DBController;
import ekrut.server.db.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ocsf.server.ConnectionToClient;

/**
 * The `ServerSessionManager` class provides methods for managing user sessions
 * on the server side.
 * 
 * @author Yovel Gabay
 */
public class ServerSessionManager {

	// The user currently being managed by this session manager.
	private User user = null;
	// A map of logged-in users and their corresponding timer tasks, which will log
	// them out after a certain time period.
	private HashMap<User, TimerTask> connectedUsers;
	// The data access object for interacting with the database.
	private UserDAO userDAO;
	// A map of client connections and the users associated with them.
	private HashMap<ConnectionToClient, User> clientUserMap;
	// The time, in milliseconds, after which a user will be automatically logged
	// out if they have not made any requests.
	private static final long LOGOUT_TIME = 300_000; // 5 minutes
	private ObservableList<ConnectedClient> connectedClientList;

	public HashMap<ConnectionToClient, User> getClientUserMap() {
		return clientUserMap;
	}

	/**
	 * Constructs a new `ServerSessionManager` object and initializes the
	 * `connectedUsers` list, `clientUserMap` hash map, and `userDAO` object.
	 *
	 * @param con the {@link DBController} object to use for database operations
	 */
	public ServerSessionManager(DBController con) {
		connectedClientList = FXCollections.observableArrayList();
		userDAO = new UserDAO(con);
		connectedUsers = new HashMap<>();
		clientUserMap = new HashMap<>();
	}

	public ObservableList<ConnectedClient> getConnectedClientList() {
		return connectedClientList;
	}


	/**
	 * Attempts to login the user with the given username and password. If
	 * successful, adds the user to the list of connected users, adds the user and
	 * the associated {@link ConnectionToClient} object to the `clientUserMap` hash
	 * map, adds the user and and the new timer for him to the `connectedUsers` map.
	 *
	 * @param username the username of the user to login
	 * @param password the password of the user to login
	 * @param client   the {@link ConnectionToClient} object associated with the
	 *                 user
	 * @return a {@link UserResponse} object with the result of the login attempt
	 *         and the {@link User} object, if successful
	 */
	public UserResponse loginUser(String username, String password, ConnectionToClient client) {
		ResultType result = null;
		user = userDAO.fetchUserByUsername(username);
		UserResponse userResponse = new UserResponse(result, user);
		if (user == null) {
			result = ResultType.NOT_FOUND;
		}
		// if password isn't correct
		else if (!user.getPassword().equals(password)) {
			result = ResultType.INVALID_INPUT;
		} else {
			userResponse.setUser(user);
			connectedUsers.put(user, startTimer(username, client));
			clientUserMap.put(client, user);
			connectedClientList.add(new ConnectedClient(client.getInetAddress().toString().replace("/", ""), username,
					user.getUserType()));
			result = ResultType.OK;
		}
		userResponse.setResultCode(result);
		return userResponse;
	}

	/**
	 * Logs out the user with the given username. If the logout is successful,
	 * removes the user from the list of connected users, cancels the timer for the
	 * user's session, and removes the {@link ConnectionToClient} object associated
	 * with the user from the `clientUserMap` hash map.
	 *
	 * @param username the username of the user to log out
	 * @param client   the client connection associated with the user
	 * @param reason   the reason for the logout (e.g. "Session expired")
	 * @return a {@link UserResponse} object with the result of the logout attempt
	 */
	public UserResponse logoutUser(ConnectionToClient client, String reason) {
		ResultType result = null;
		User user = clientUserMap.get(client);

		UserResponse userResponse = new UserResponse(result);
		// Check if user not exist in DB
		if (user == null) {
			result = ResultType.NOT_FOUND;
		}

		else {
			// the session has expired
			if (reason != null) {
				sendRequestToClient(new UserRequest(UserRequestType.LOGOUT, user.getUsername()), client);
			} else
				result = ResultType.OK;
			connectedUsers.get(user).cancel(); // cancel timer
			connectedUsers.remove(user);
			clientUserMap.remove(client);
			connectedClientList
					.remove(new ConnectedClient(/* not relevant */ null, user.getUsername(), user.getUserType()));
		}
		userResponse.setResultCode(result);
		return userResponse;
	}

	private void sendRequestToClient(UserRequest userRequest, ConnectionToClient client) {
		EKrutServer.sendRequestToClient(userRequest, client);
	}

	/**
	 * Determines if the user with the given username is logged in.
	 *
	 * @param username The username of the user.
	 * @return `true` if the user is logged in, `false` if the user is not logged in
	 *         or if a database error occurred.
	 */
	public UserResponse isLoggedin(String username) {
		user = userDAO.fetchUserByUsername(username);
		for (User connectedUser : connectedUsers.keySet())
			if (connectedUser.getUsername().equals(username))
				return new UserResponse(ResultType.OK);
		return new UserResponse(ResultType.NOT_FOUND);
	}

	/**
	 * Imports users into the system from the external user management system.
	 * 
	 * @param dbCon the database connection to use for the operation
	 * @return response indicating if the operation was successful or not
	 */
	public UserResponse importUsers(DBController dbCon) {
		if (!UsersImporter.importUsers(dbCon))
			return new UserResponse(ResultType.UNKNOWN_ERROR);
		return new UserResponse(ResultType.OK);
	}

	/**
	 * Returns the {@link User} object associated with the given
	 * {@link ConnectionToClient} object. Also resets the timer for the user's
	 * session. Returns the {@link User} object associated with the given
	 * {@link ConnectionToClient} object. Also resets the timer for the user's
	 * session.
	 *
	 * @param client the {@link ConnectionToClient} object associated with the user
	 * @return the {@link User} object associated with the given
	 *         {@link ConnectionToClient} object
	 */
	public User getUser(ConnectionToClient client) {
		user = clientUserMap.get(client);
		resetTimer(user, client);
		return user;

	}

	/**
	 * Retrieves the client connection that's associated with a given user. Also
	 * resets the timer for the user's session.
	 * 
	 * @param user the user whose connection should be retrieved
	 * @return the user's client connection
	 */
	public ConnectionToClient getUsersConnection(User user) {
		for (Map.Entry<ConnectionToClient, User> entry : clientUserMap.entrySet()) {
			if (entry.getValue().getUsername().equals(user.getUsername())) {
				resetTimer(entry.getValue(), entry.getKey());
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Resets the timer for the user's session.
	 *
	 * @param user   the user whose timer is being reset
	 * @param client the client associated with the given user
	 */
	public void resetTimer(User user, ConnectionToClient client) {
		// Cancel the current timer and start a new one
		connectedUsers.get(user).cancel();
		connectedUsers.put(user, startTimer(user.getUsername(), client));
	}

	/**
	 * Starts a timer for the user's session. If the timer expires, the user will be
	 * logged out.
	 *
	 * @param username the username of the user whose timer is being started
	 * @param client   the client associated with the given user
	 * @return the timer that was started
	 */
	public TimerTask startTimer(String username, ConnectionToClient client) {
		// Start the timer
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				logoutUser(client, "Session expired");
			}
		};
		TimeScheduler.getTimer().schedule(task, LOGOUT_TIME);
		return task;
	}

	public UserResponse fetchUser(FetchUserType fetchType, String argument) {
		if (argument == null)
			return new UserResponse(ResultType.INVALID_INPUT);
		ArrayList<User> usersList = new ArrayList<>();
		switch (fetchType) {
		case USER_NAME:
			usersList.add(userDAO.fetchUserByUsername(argument));
			break;
		case PHONE_NUMBER:
			usersList.add(userDAO.fetchUserByPhoneNumber(argument));
			break;
		case EMAIL:
			usersList.add(userDAO.fetchUserByEmail(argument));
			break;
		case AREA_MANAGER_AND_AREA:
			usersList.add(userDAO.fetchManagerByArea(argument));
			break;
		case AREA:
			usersList.add(userDAO.fetchUserByArea(argument));
			break;
		case ROLE:
			usersList = userDAO.fetchAllUsersByRole(UserType.valueOf(argument));
			break;
		}
		if (usersList.size() != 0)
			return new UserResponse(ResultType.OK, usersList);
		return new UserResponse(ResultType.NOT_FOUND);
	}

	// String userName, String creditCardNumber,String phoneNumber,String email,
	// boolean monthlyCharge, String customerOrSub,String subscriberNumber
	public UserResponse acceptRegisterUser(UserRegistration userToRegister) {
		Customer customer;
		User user = userDAO.fetchUserByUsername(userToRegister.getUsername());
		if (user.getUserType() == UserType.REGISTERED)
			user.setUserType(UserType.CUSTOMER);
		user.setEmail(userToRegister.getEmail());
		user.setPhoneNumber(userToRegister.getPhoneNumber());
		// String subscriberNumber, String username, boolean monthlyCharge, String
		// creditCardNumber, boolean orderedAsSub
		customer = new Customer(userToRegister.getCustomerOrSub().equals("subscriber") ? 0 : -1,
				userToRegister.getUsername(), userToRegister.getMonthlyCharge(),
				userToRegister.getCreditCardNumber(), false);
		if (!userDAO.updateUser(user) || !userDAO.createOrUpdateCustomer(customer) || !userDAO.deleteUserFromRegistration(userToRegister.getUsername()))
			return new UserResponse(ResultType.NOT_FOUND);
		return new UserResponse(ResultType.OK);
	}
	
	public UserResponse getRegistrationList(String area) {
		ArrayList<UserRegistration> registrationList=userDAO.getUserRegistrationList(area);
		if (registrationList==null)
			return new UserResponse(ResultType.NOT_FOUND);
		return new UserResponse(registrationList,ResultType.OK);
	}
}
