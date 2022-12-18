package ekrut.server.managers;

import java.util.ArrayList;

import ekrut.entity.User;
import ekrut.net.UserResponse;
import ekrut.server.db.UserDAO;

public class ServerSessionManager {
	
	private User user = null;
	private ArrayList<User> connectedUsers;
	
	public ServerSessionManager(ArrayList<User> connectedUsers, User user) {
		this.connectedUsers = new ArrayList<User>();
		this.user = user;
	}

	public UserResponse loginUser(String username, String password) {
		String result = null;
		user = UserDAO.fetchUserByUsername(username);
		UserResponse userResponse = new UserResponse(result,user);
		//Check if user not exist in DB
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
	
	
	public UserResponse logoutUser(String username) {
		String result = null;
		user = UserDAO.fetchUserByUsername(username);
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

	public boolean isLoggedin(String username) {
		user = UserDAO.fetchUserByUsername(username);
		return connectedUsers.contains(user);
	}

	



}
