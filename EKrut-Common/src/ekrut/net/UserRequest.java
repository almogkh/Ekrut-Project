package ekrut.net;

import java.io.Serializable;

import ekrut.entity.User;
import ekrut.entity.UserRegistration;

public class UserRequest implements Serializable {

	private static final long serialVersionUID = 5395643634235579687L;
	private UserRequestType action;
	private String username;
	private String password;
	private String argument;
	private FetchUserType fetchType;
	private UserRegistration userToRegister;
	private String area;
	private User user;

	public UserRequest(UserRequestType action) {
		this.action = action;
	}

	// Constructor for connect action
	public UserRequest(String username, String password) {
		this.action = UserRequestType.LOGIN;
		this.username = username;
		this.password = password;
	}

	// Constructor for disconnect & isLoggedin actions
	public UserRequest(UserRequestType action, String username) {
		this.action = action;
		this.username = username;
	}

	// Constructor for fetchUser
	public UserRequest(FetchUserType fetchType, String argument) {
		this.action = UserRequestType.FETCH_USER;
		this.argument = argument;
		this.fetchType = fetchType;
	}

	public UserRequest(UserRegistration userToRegister, UserRequestType action) {
		this.action = action;
		this.userToRegister = userToRegister;
	}

	public UserRequest(User user, UserRequestType action) {
		this.action = action;
		this.user = user;
	}

	public UserRequest(String area) {
		this.action = UserRequestType.GET_REGISTRATION_LIST;
		this.area = area;
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

	public UserRegistration getUserToRegister() {
		return userToRegister;
	}

	public void setUserToRegister(UserRegistration userToRegister) {
		this.userToRegister = userToRegister;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
