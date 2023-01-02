package ekrut.entity;

import java.io.Serializable;

public class Customer implements Serializable {

	private static final long serialVersionUID = -2291478383986722760L;
	
	private String subscriberNumber;
	private String username;
	private String creditCardNumber;
	private boolean monthlyCharge;

	public Customer(String subscriberNumber, String username, boolean monthlyCharge, String creditCardNumber) {
		this.subscriberNumber = subscriberNumber;
		this.username = username;
		this.creditCardNumber = creditCardNumber;
		this.monthlyCharge = monthlyCharge;
	}

	public String getsubscriberNumber() {
		return subscriberNumber;
	}

	public void setsubscriberNumber(String subscriberNumber) {
		this.subscriberNumber = subscriberNumber;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCreditCard() {
		return creditCardNumber;
	}

	public void setCreditCard(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	public boolean isMonthlyCharge() {
		return monthlyCharge;
	}

	public void setMonthlyCharge(boolean monthlyCharge) {
		this.monthlyCharge = monthlyCharge;
	}
}
