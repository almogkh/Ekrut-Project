package ekrut.entity;

public class User {
	private String username;
	private String password;
	private UserType userType;
	private String area;
	
	public User(String username, String password, UserType userType, String area) {
		this.username = username;
		this.password = password;
		this.userType = userType;
		this.area = area;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}
}
