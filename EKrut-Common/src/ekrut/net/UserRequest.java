package ekrut.net;

import java.io.Serializable;

public class UserRequest implements Serializable{
	
	private static final long serialVersionUID = 5395643634235579687L;
	private UserRequestType action;
	private String username;
	private String password;
	private String argument;
	private FetchUserType fetchType;
	
	//Constructor for connect action
	public UserRequest(String username, String password) { 
		this.action = UserRequestType.LOGIN;
		this.username = username;
		this.password = password;
	}
	
	//Constructor for disconnect & isLoggedin actions
	public UserRequest(UserRequestType action, String username) { 
		this.action = action;
		this.username = username;
	}
	
	//Constructor for fetchUser
	public UserRequest(FetchUserType fetchType, String argument) { 
		this.action = UserRequestType.FETCH_USER;
		this.argument = argument;
		this.fetchType = fetchType;
	}

	public UserRequestType getAction() {
		return action;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	public String getArgument() {
		return argument;
	}
	
	public FetchUserType getFetchType() {
		return fetchType;
	}
}
