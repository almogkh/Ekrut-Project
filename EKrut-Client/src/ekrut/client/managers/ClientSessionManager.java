package ekrut.client.managers;

import ekrut.entity.User;
import ekrut.net.UserRequest;
import ekrut.net.UserRequestType;
import ekrut.net.UserResponse;

public class ClientSessionManager {
	
	private User user = null;	

	public User loginUser(String username, String password) throws Exception{
		
		//Check if user is already logged in
		if (user != null)
			throw new RuntimeException("User is already loggedin");
		
		//Checking if the user has entered invalid details
		else if (username == null || password == null)
			throw new NullPointerException("Null Item was provided");
		
		//Send request to login user 
		UserRequest userLoginRequest = new UserRequest(username, password);	
		UserResponse userResponse = sendRequest(userLoginRequest);
		
		//If the response has confirmed return user details to know the next operations for this user
		if (userResponse.getResultCode().equals("OK")) {
			user = userResponse.getUser();
			return user;
		}
		else
			throw new RuntimeException(userResponse.getResultCode());
	}

	//Send to the server request to logout user
	public void logoutUser(User user) throws Exception{
		//Check if user is not loggedin
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
	
	public boolean isLoggedin() {
		UserRequest userRequest = new UserRequest(UserRequestType.IS_LOGGEDIN, user.getUsername());
		UserResponse userResponse = sendRequest(userRequest);
		if (userResponse.getResultCode().equals("OK")) 
			return true;
		return false;
	}
	
	
	private UserResponse sendRequest(UserRequest userLoginRequest) {
		// TODO Auto-generated method stub
		return null;
	}
}
