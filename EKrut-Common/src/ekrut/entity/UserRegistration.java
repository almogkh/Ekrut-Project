package ekrut.entity;

import java.io.Serializable;

public class UserRegistration  implements Serializable{

	private static final long serialVersionUID = -8284486543163289939L;
	private String userName;
	private boolean monthlyCharge;
	private String creditCardNumber;
	private String email;
	private String phoneNumber;
	private String customerOrSub;

	public String getCustomerOrSub() {
		return customerOrSub;
	}

	public void setCustomerOrSub(String customerOrSub) {
		this.customerOrSub = customerOrSub;
	}

	public UserRegistration(String userName,  String creditCardNumber,String phoneNumber,String email,
			boolean monthlyCharge,  String customerOrSub) {
		this.userName = userName;
		this.creditCardNumber = creditCardNumber;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.monthlyCharge = monthlyCharge;
		this.customerOrSub = customerOrSub;


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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	

	public boolean getMonthlyCharge() {
		return monthlyCharge;
	}

	public void setMonthlyCharge(boolean monthlyCharge) {
		this.monthlyCharge = monthlyCharge;
	}

	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}



}
