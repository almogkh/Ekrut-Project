package ekrut.entity;

import java.io.Serializable;

public class User implements Serializable{

	private static final long serialVersionUID = 7107357504490406916L;
	private UserType userType;
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private String id;
	private String email;
	private String phoneNumber;
	private String area;
	private Customer customerInfo;
	// this entity on DB: (userType, username, password, firstName, lastName, 
	//						id, email, phoneNumber, area)
	
	public User(UserType userType, String username, String password, String firstName, String lastName, String id,
			String email, String phoneNumber, String area) {
		this.userType = userType;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.id = id;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.area = area;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Customer getCustomerInfo() {
		return customerInfo;
	}

	public void setCustomerInfo(Customer customerInfo) {
		this.customerInfo = customerInfo;
	}
	
	public boolean isCustomer() {
		return customerInfo != null;
	}

}
