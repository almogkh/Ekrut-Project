package ekrut.net;

import java.io.Serializable;
import java.util.ArrayList;

import ekrut.entity.User;

public class UserResponse implements Serializable{
	
	private static final long serialVersionUID = 7315210241925632873L;
	private ResultType resultCode;
	private User user;
	private ArrayList<User> usersList;

	//Constructor for loginUser
	public UserResponse(ResultType resultCode, User user) {
		this.resultCode = resultCode;
		this.user = user;
	}
	
	//Constructor for fetchUsers (ArrayList)
	public UserResponse(ResultType resultCode, ArrayList<User> usersList) {
		this.resultCode = resultCode;
		this.usersList = usersList;
	}
	
	//Constructor for logoutUser
	public UserResponse(ResultType resultCode) {
		this.resultCode = resultCode;
	}

	public ResultType getResultCode() {
		return resultCode;
	}

	public void setResultCode(ResultType resultCode) {
		this.resultCode = resultCode;
	}

	public User getUser() {
		return user;
	}

	public ArrayList<User> getUsersList() {
		return usersList;
	}
	public void setUser(User user) {
		this.user = user;
	}

	
	
	
}
