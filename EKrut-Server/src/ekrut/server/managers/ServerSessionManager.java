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
import ekrut.server.db.DBController;
import ekrut.server.db.UserDAO;
import ekrut.server.intefaces.IUserNotifier;
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

	// A map of logged-in users and their corresponding timer tasks, which will log
	// them out after a certain time period.
	private HashMap<User, TimerTask> connectedUsers;
	// The data access object for interacting with the database.
	private UserDAO userDAO;
	// A map of client connections and the users associated with them.
	private HashMap<ConnectionToClient, User> clientUserMap;
	// The time, in milliseconds, after which a user will be automatically logged
	// out if they have not made any requests.
	private static final long LOGOUT_TIME = 90_000; // 1 minute
	private ObservableList<ConnectedClient> connectedClientList;
	private IUserNotifier userNotifier;

	public HashMap<ConnectionToClient, User> getClientUserMap() {
		return clientUserMap;
	}

	/**
	 * Constructs a new `ServerSessionManager` object and initializes the
	 * `connectedUsers` list, `clientUserMap` hash map, and `userDAO` object.
	 *
	 * @param con the {@link DBController} object to use for database operations
 	 * @param userNotifier the message that sens to user.
	 */
	public ServerSessionManager(DBController con, IUserNotifier userNotifier) {
		connectedClientList = FXCollections.observableArrayList();
		userDAO = new UserDAO(con);
		connectedUsers = new HashMap<>();
		clientUserMap = new HashMap<>();
		this.userNotifier = userNotifier;
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
	public synchronized UserResponse loginUser(String username, String password, ConnectionToClient client) {
		ResultType result = null;
		User user = userDAO.fetchUserByUsername(username);
		UserResponse userResponse = new UserResponse(result, user);
		if (user == null) {
			result = ResultType.NOT_FOUND;
		}
		// if password isn't correct
		else if (!user.getPassword().equals(password)) {
			result = ResultType.INVALID_INPUT;
		} else {
			if (connectedUsers.containsKey(user))
				return new UserResponse(ResultType.PERMISSION_DENIED);
			userResponse.setUser(user);
			connectedUsers.put(user, startTimer(username, client));
			clientUserMap.put(client, user);
			connectedClientList.add(new ConnectedClient(client.getInetAddress().toString().replace("/", ""),
					username, user.getUserType()));
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
	 * @param client   the client connection associated with the user
	 * @param reason   the reason for the logout (e.g. "Session expired")
	 * @return a {@link UserResponse} object with the result of the logout attempt
	 */
	public synchronized UserResponse logoutUser(ConnectionToClient client, String reason) {
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
			} else {
				result = ResultType.OK;
			}
			connectedUsers.get(user).cancel(); // cancel timer
			connectedUsers.remove(user);
			clientUserMap.remove(client);
			connectedClientList
					.remove(new ConnectedClient(/* not relevant */ null, user.getUsername(), user.getUserType()));
		}
		userResponse.setResultCode(result);
		return userResponse;
	}

	/**
	 * 
	 * Sends a user request to a client.
	 * 
	 * @param userRequest the {@link UserRequest} object to send to the client
	 * @param client      the {@link ConnectionToClient} object representing the
	 *                    client to send the request to
	 */
	private void sendRequestToClient(UserRequest userRequest, ConnectionToClient client) {
		EKrutServer.sendRequestToClient(userRequest, client);
	}

	/**
	 * private void sendRequestToClient(UserRequest userRequest, ConnectionToClient
	 * client) { EKrutServer.sendRequestToClient(userRequest, client); }
	 * 
	 * /** Determines if the user with the given username is logged in.
	 *
	 * @param username The username of the user.
	 * @return `true` if the user is logged in, `false` if the user is not logged in
	 *         or if a database error occurred.
	 */
	public synchronized UserResponse isLoggedin(String username) {
		for (User connectedUser : connectedUsers.keySet())
			if (connectedUser.getUsername().equals(username))
				return new UserResponse(ResultType.OK);
		return new UserResponse(ResultType.NOT_FOUND);
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
	public synchronized User getUser(ConnectionToClient client) {
		User user = clientUserMap.get(client);
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
	public synchronized ConnectionToClient getUsersConnection(User user) {
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
	public synchronized void resetTimer(User user, ConnectionToClient client) {
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
	 * @return TimerTask the timer that was started
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

	/**
	 * Fetches a user from the database based on the given fetch type and argument.
	 * 
	 * @param fetchType the {@link FetchUserType} that specify the type of the data
	 *                  to fetch by
	 * @param argument  the argument to use for fetching the user, can be a
	 *                  username, phone number, email or area
	 * @return UserResponse {@link UserResponse} object with the result of the fetch attempt
	 *         and the {@link User} object, if successful.
	 */

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
		case ROLE:
			usersList = userDAO.fetchAllUsersByRole(UserType.valueOf(argument));
			break;
		}
		if (usersList.size() != 0)
			return new UserResponse(ResultType.OK, usersList);
		return new UserResponse(ResultType.NOT_FOUND);
	}

	/**
	 * 
	 * Accepts a user registration request and updates the user's information in the
	 * database. This method will update the user's email, phone number, user type
	 * and also create a new customer object. If the user is a subscriber, a new
	 * subscriber number will be created.
	 * 
	 * @param userToRegister The UserRegistration object to be added to the system.
	 * @return UserResponse {@link UserResponse} object with the result of the registration
	 *         process. If the registration was successful, the result code will be
	 *         {@link ResultType#OK}, otherwise it will be
	 *         {@link ResultType#NOT_FOUND}.
	 * 
	 */
	public UserResponse acceptRegisterUser(UserRegistration userToRegister) {
		Customer customer;
		User user = userDAO.fetchUserByUsername(userToRegister.getUsername());
		if (user.getUserType() == UserType.REGISTERED)
			user.setUserType(UserType.CUSTOMER);
		customer = new Customer(userToRegister.getCustomerOrSub().equals("subscriber") ? 0 : -1,
				userToRegister.getUsername(), userToRegister.getMonthlyCharge(), userToRegister.getCreditCardNumber(),
				false);
		if (!userDAO.updateUser(user) || !userDAO.createOrUpdateCustomer(customer)
				|| !userDAO.deleteUserFromRegistration(userToRegister.getUsername()))
			return new UserResponse(ResultType.NOT_FOUND);
		userNotifier.sendNotification("Your registration request has been accepted!", user.getEmail(),
									user.getPhoneNumber());
		return new UserResponse(ResultType.OK);
	}

	/**
	 * 
	 * Fetches a user registration list from the database based on the given area.
	 * 
	 * @param area the area to use for fetching the registration list
	 * @return UserResponse {@link UserResponse} object with the registration list, if
	 *         successful, or the result of the fetch attempt.
	 */
	public UserResponse getRegistrationList(String area) {
		ArrayList<UserRegistration> registrationList = userDAO.getUserRegistrationList(area);
		if (registrationList == null)
			return new UserResponse(ResultType.NOT_FOUND);
		return new UserResponse(registrationList, ResultType.OK);
	}

	/**
	 * 
	 * Create a user to register in the database based .
	 * 
	 * @param user {@link UserRegistration} user the user to register
	 * @return UserResponse {@link UserResponse} object with the result of the create process.
	 *         If the create was successful, the result code will be
	 *         {@link ResultType#OK}, otherwise it will be
	 *         {@link ResultType#NOT_FOUND}.
	 */
	public UserResponse createUserToRegister(UserRegistration user) {
		if (userDAO.createUserToRegister(user))
			return new UserResponse(ResultType.OK);
		return new UserResponse(ResultType.NOT_FOUND);

	}

	/**
	 * 
	 * Update a user in the system.
	 * 
	 * @param user the user to update
	 * @return a {@link UserResponse} indicating the result of the update operation
	 */
	public UserResponse updateUser(User user) {
		if (userDAO.updateUser(user))
			return new UserResponse(ResultType.OK);
		return new UserResponse(ResultType.NOT_FOUND);

	}

}
