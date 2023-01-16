package ekrut.client.managers;

import java.util.ArrayList;

import ekrut.client.EKrutClient;
import ekrut.entity.User;
import ekrut.entity.UserRegistration;
import ekrut.net.FetchUserType;
import ekrut.net.ResultType;
import ekrut.net.UserRequest;
import ekrut.net.UserRequestType;
import ekrut.net.UserResponse;

/**
 * The `ClientSessionManager` class provides methods for managing user sessions
 * on the client side.
 * 
 * @author Yovel Gabay
 */
public class ClientSessionManager extends AbstractClientManager<UserRequest, UserResponse> {

	private User user;
	private ArrayList<Runnable> onLogout;

	public ClientSessionManager(EKrutClient client) {
		super(client, UserResponse.class);
		this.onLogout = new ArrayList<>();
	}

	/**
	 * Attempts to login a user with the given username and password.
	 *
	 * @param username The username of the user.
	 * @param password The password of the user.
	 * @return The `User` object if the login was successful, or throws an exception
	 *         if an error occurred.
	 * @throws Exception if the user is already logged in, if the user has entered
	 *                   null items, or if the login failed.
	 */
	public User loginUser(String username, String password) {
		if (user != null)
			throw new RuntimeException("User is already loggedin");

		else if (username == null || password == null)
			throw new NullPointerException("Null Item was provided");

		UserRequest userLoginRequest = new UserRequest(username, password);
		UserResponse userResponse = sendRequest(userLoginRequest);

		if (userResponse.getResultCode().equals(ResultType.OK)) {
			user = userResponse.getUser();
			return user;
		} else
			throw new RuntimeException(userResponse.getResultCode().toString());
	}

	/**
	 * Register a new user
	 *
	 * @param userToRegister the user to register
	 * @return UserResponse with the result of the registration
	 */
	public UserResponse registerUser(UserRegistration userToRegister) {
		UserRequest userLoginRequest = new UserRequest(userToRegister, UserRequestType.REGISTER_USER);
		UserResponse userResponse = sendRequest(userLoginRequest);
		return userResponse;
	}

	/**
	 * Request the list of registered users by area
	 *
	 * @param area the area of the registered users
	 * @return ArrayList of UserRegistration
	 */
	public ArrayList<UserRegistration> getRegistrationList(String area) {
		UserRequest getUsersRegistrationList = new UserRequest(area);
		UserResponse userResponse = sendRequest(getUsersRegistrationList);
		return userResponse.getUserRegistrationList();
	}

	/**
	 * Logs out the given user.
	 *
	 * @param user The user to log out.
	 * @throws Exception if the user is not logged in or if the logout failed.
	 */
	public void logoutUser() {
		logoutUser(false);
	}

	public void logoutUser(boolean quiet) {
		for (Runnable r : onLogout)
			r.run();

		if (quiet) {
			user = null;
			return;
		}

		if (!isLoggedin()) {
			throw new RuntimeException("User is not loggedin");
		}
		// Send request to logout user
		UserRequest userRequest = new UserRequest(UserRequestType.LOGOUT, user.getUsername());
		UserResponse userResponse = sendRequest(userRequest);

		if (userResponse.getResultCode().equals(ResultType.OK))
			user = null;
		else
			throw new RuntimeException(userResponse.getResultCode().toString());
	}

	/**
	 * Register a runnable to be called when the user logout
	 *
	 * @param runnable the runnable to be called when the user logout
	 */
	public void registerOnLogoutHandler(Runnable runnable) {
		onLogout.add(runnable);
	}

	/**
	 * Determines if the user is logged in.
	 *
	 * @return `true` if the user is logged in, `false` if the user is not logged in
	 *         or if an error occurred.
	 */
	public boolean isLoggedin() {
		if (user == null) {
			return false;
		}
		UserRequest userRequest = new UserRequest(UserRequestType.IS_LOGGEDIN, user.getUsername());
		UserResponse userResponse = sendRequest(userRequest);
		if (userResponse.getResultCode().equals(ResultType.OK))
			return true;
		return false;
	}

	/**
	 * Request users from the server based on a specific criteria.
	 *
	 * @param fetchType the criteria to fetch user by
	 * @param argument  the argument of the criteria
	 * @return ArrayList of User
	 */
	/*
	 * if someone need to fetchUser by somthing, he need to call to fetchUser()
	 * function that return ArrayList<User>. if he expecting for result with one
	 * user he need to: user = userResponse.getUsersList().get(0);
	 */
	public ArrayList<User> fetchUser(FetchUserType fetchType, String argument) {
		UserRequest userRequest = new UserRequest(fetchType, argument);
		UserResponse userResponse = sendRequest(userRequest);
		return userResponse.getUsersList();
	}

	/**
	 * Get the logged in user.
	 *
	 * @return User object
	 */
	public User getUser() {
		return user;
	}

	/**
	 * 
	 * Add a user to the registration list
	 * 
	 * @param user the user to create
	 * @return true if user created successfully, false otherwise
	 */
	public boolean createUserToRegister(UserRegistration user) {
		UserRequest userRequest = new UserRequest(user, UserRequestType.CREATE_USER_TO_REGISTER);
		UserResponse userResponse = sendRequest(userRequest);
		return userResponse.getResultCode() == ResultType.OK ? true : false;

	}

	/**
	 * 
	 * Update a user in the database.
	 * 
	 * @param user the user to update
	 * @return true if user updated successfully, false otherwise
	 */
	public boolean updateUser(User user) {
		UserRequest userRequest = new UserRequest(user, UserRequestType.UPDATE_USER);
		UserResponse userResponse = sendRequest(userRequest);
		return userResponse.getResultCode() == ResultType.OK ? true : false;
	}

}
