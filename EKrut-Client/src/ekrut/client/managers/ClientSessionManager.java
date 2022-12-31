package ekrut.client.managers;

import ekrut.client.EKrutClient;
import ekrut.entity.User;
import ekrut.net.ResultType;
import ekrut.net.UserRequest;
import ekrut.net.UserRequestType;
import ekrut.net.UserResponse;

/**
 * The `ClientSessionManager` class provides methods for managing user sessions on the client side.
 */
public class ClientSessionManager {
	
	private User user = null;
	private EKrutClient client;
	private Object lock = new Object();
	private UserResponse response;
	
	public ClientSessionManager(EKrutClient client){
		this.client = client;
		client.registerHandler(UserResponse.class, (res) -> {
			synchronized(lock) {
				response = res;
				lock.notify();
			}
		});
	}
	
	
	
	public User getUser() {
		return user;
	}



	/**
	 * Attempts to login a user with the given username and password.
	 *
	 * @param username The username of the user.
	 * @param password The password of the user.
	 * @return The `User` object if the login was successful, or throws an exception if an error occurred.
	 * @throws Exception if the user is already logged in, if the user has entered null items, or if the login failed.
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
		}
		else
			throw new RuntimeException(userResponse.getResultCode().toString());
	}

	/**
	 * Logs out the given user.
	 *
	 * @param user The user to log out.
	 * @throws Exception if the user is not logged in or if the logout failed.
	 */
	public void logoutUser() throws Exception{ ////////////// TBD OFEK NO INPUT NEEDED
		if (!isLoggedin()) {
			throw new RuntimeException("User is not loggedin");
		}
		//Send request to logout user 
		UserRequest userRequest = new UserRequest(UserRequestType.LOGOUT, user.getUsername());
		UserResponse userResponse = sendRequest(userRequest);
		
		if (userResponse.getResultCode().equals(ResultType.OK)) 
			user = null;
		else
			throw new RuntimeException(userResponse.getResultCode().toString());
	}
	
	/**
	 * Determines if the user is logged in.
	 *
	 * @return `true` if the user is logged in, `false` if the user is not logged in or if an error occurred.
	 */
	public boolean isLoggedin() {
		UserRequest userRequest = new UserRequest(UserRequestType.IS_LOGGEDIN, user.getUsername());
		UserResponse userResponse = sendRequest(userRequest);
		if (userResponse.getResultCode().equals(ResultType.OK)) 
			return true;
		return false;
	}
	
	/**
	 * Sends a request to the server.
	 *
	 * @param userLoginRequest The request to send.
	 * @return The response from the server.
	 */
	private UserResponse sendRequest(UserRequest request) {
		this.response = null;
		client.sendRequestToServer(request);
		synchronized(lock) {
			while (response == null) {
				try {
					lock.wait();
				} catch (InterruptedException e) {}
			}
		}
		return response;
	}
	
	private void reciveMassageFromServer(UserRequest userRequest) {
		//display to user "Your session has expired. You have been logged out."
		// TBD OFEK: this method won't be needed here, the session-timer-logout
		//				is not initiated by this class and won't be handled by it(?)
	}
}
