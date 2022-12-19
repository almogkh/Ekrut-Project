package ekrut.client.managers;

import ekrut.entity.User;
import ekrut.net.UserRequest;
import ekrut.net.UserRequestType;
import ekrut.net.UserResponse;

/**
 * The `ClientSessionManager` class provides methods for managing user sessions on the client side.
 */
public class ClientSessionManager {
	
	private User user = null;	
	
	/**
	 * Attempts to login a user with the given username and password.
	 *
	 * @param username The username of the user.
	 * @param password The password of the user.
	 * @return The `User` object if the login was successful, or throws an exception if an error occurred.
	 * @throws Exception if the user is already logged in, if the user has entered null items, or if the login failed.
	 */
	public User loginUser(String username, String password) throws Exception{
		if (user != null)
			throw new RuntimeException("User is already loggedin");

		else if (username == null || password == null)
			throw new NullPointerException("Null Item was provided");
		
		UserRequest userLoginRequest = new UserRequest(username, password);	
		UserResponse userResponse = sendRequest(userLoginRequest);

		if (userResponse.getResultCode().equals("OK")) {
			user = userResponse.getUser();
			return user;
		}
		else
			throw new RuntimeException(userResponse.getResultCode());
	}

	/**
	 * Logs out the given user.
	 *
	 * @param user The user to log out.
	 * @throws Exception if the user is not logged in or if the logout failed.
	 */
	public void logoutUser(User user) throws Exception{
		if (!isLoggedin()) {
			throw new RuntimeException("User is not loggedin");
		}
		//Send request to logout user 
		UserRequest userRequest = new UserRequest(UserRequestType.LOGOUT, user.getUsername());
		UserResponse userResponse = sendRequest(userRequest);
		
		if (userResponse.getResultCode().equals("OK")) 
			return;
		else
			throw new RuntimeException(userResponse.getResultCode());
	}
	
	/**
	 * Determines if the user is logged in.
	 *
	 * @return `true` if the user is logged in, `false` if the user is not logged in or if an error occurred.
	 */
	public boolean isLoggedin() {
		UserRequest userRequest = new UserRequest(UserRequestType.IS_LOGGEDIN, user.getUsername());
		UserResponse userResponse = sendRequest(userRequest);
		if (userResponse.getResultCode().equals("OK")) 
			return true;
		return false;
	}
	
	/**
	 * Sends a request to the server.
	 *
	 * @param userLoginRequest The request to send.
	 * @return The response from the server.
	 */
	private UserResponse sendRequest(UserRequest userLoginRequest) {
		// TODO Auto-generated method stub
		return null;
	}
}
