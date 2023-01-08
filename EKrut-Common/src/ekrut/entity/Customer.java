package ekrut.entity;

import java.io.Serializable;

public class Customer implements Serializable {

	private static final long serialVersionUID = -2291478383986722760L;
	
	private int subscriberNumber;
	private String username;
	private String creditCardNumber;
	private boolean monthlyCharge;
	private boolean orderedAsSub;

	public Customer(int subscriberNumber, String username, boolean monthlyCharge, String creditCardNumber, boolean orderedAsSub) {
		this.subscriberNumber = subscriberNumber;
		this.username = username;
		this.creditCardNumber = creditCardNumber;
		this.monthlyCharge = monthlyCharge;
		this.orderedAsSub = orderedAsSub;
	}

	public int getSubscriberNumber() {
		return subscriberNumber;
	}

	public void setSubscriberNumber(int subscriberNumber) {
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

	public boolean hasOrderedAsSub() {
		return orderedAsSub;
	}

	public void setOrderedAsSub(boolean orderedAsSub) {
		this.orderedAsSub = orderedAsSub;
	}
}
