package ekrut.client.managers;
/*
 *  Keep it like that?
 *  
 */
public class UserNotifier {


	  // Private fields for storing recipient's phone number and email address
	  private String phoneNumber;
	  private String emailAddress;
	  
	  // Constructor to initialize SMSResponder with recipient's phone number and email address
	  public UserNotifier(String phoneNumber, String emailAddress) {
	    this.phoneNumber = phoneNumber;
	    this.emailAddress = emailAddress;
	  }

	  // Method for sending an SMS message
	  public void sendSMS(String message) {
	    // Code for sending SMS goes here
	    // For the purposes of this example, we will simply print the message to the console
	    System.out.println("Sending SMS to " + this.phoneNumber + ": " + message);
	  }

	  // Method for sending an email
	  public void sendEmail(String message) {
	    // Code for sending email goes here
	    // For the purposes of this example, we will simply print the message to the console
	    System.out.println("Sending email to " + this.emailAddress + ": " + message);
	  }

	  // Method for sending both an SMS and email
	  public void sendNotification(String smsMessage, String emailMessage) {
	    this.sendSMS(smsMessage);
	    this.sendEmail(emailMessage);
	  }
	  
	  public static void main(String[] args) {
		  UserNotifier sms = new UserNotifier("0544944431", "Nir@gmail.com");
		  sms.sendSMS("Phone SMS");
		  sms.sendEmail("Email message");
	}

}
