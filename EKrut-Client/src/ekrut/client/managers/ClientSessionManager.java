package ekrut.client.managers;

import ekrut.entity.User;
import ekrut.net.UserRequest;
import ekrut.net.UserRequestType;
import ekrut.net.UserResponse;

public class ClientSessionManager {
	
	private String sessionID;
	//What is sessionID? Yovel
	
	public void loginUser(User user) throws Exception{
		if(isLoggedin(user))
			throw new IllegalArgumentException("User is already loggedin");
		if (user.getUsername() == null || user.getPassword() == null)
			throw new IllegalArgumentException("Null Item was provided");
		
		UserRequest userUpdateRequest = new UserRequest(UserRequestType.CONNECT, user.getUsername(),user.getPassword());
		
		UserResponse userUpdateResponse = sendRequest(userUpdateRequest);
		if (userUpdateResponse.getResultCode().equals("OK")) 
			return;
		else
			throw new IllegalArgumentException("Can't connect user");
	}
	
	public void logoutUser(User user) {
		UserRequest userUpdateRequest = new UserRequest(UserRequestType.DISCONNECT, user.getUsername());
		UserResponse userUpdateResponse = sendRequest(userUpdateRequest);
		if (userUpdateResponse.getResultCode().equals("OK")) 
			return;
		else
			throw new IllegalArgumentException("Can't disconnect user");
	}
	
	private boolean isLoggedin(User user) {
		UserRequest userUpdateRequest = new UserRequest(UserRequestType.IS_CONNECTED, user.getUsername());
		UserResponse userUpdateResponse = sendRequest(userUpdateRequest);
		if (userUpdateResponse.getResultCode().equals("OK")) 
			return true;
		else {
			throw new IllegalArgumentException("Can't disconnect user");
		}
	}
	

	private UserResponse sendRequest(UserRequest userUpdateRequest) {
		// TODO Auto-generated method stub
		return null;
	}
}
