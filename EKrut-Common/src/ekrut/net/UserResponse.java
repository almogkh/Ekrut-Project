package ekrut.net;

import java.io.Serializable;

import ekrut.entity.User;

public class UserResponse implements Serializable{
	
	private static final long serialVersionUID = 7315210241925632873L;
	private String resultCode;
	private User user;

	//Constructor for loginUser
	public UserResponse(String resultCode, User user) {
		this.resultCode = resultCode;
		this.user = user;
	}
	
	//Constructor for logoutUser
	public UserResponse(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	
	
	
}
