package ekrut.server.intefaces;

/**
 * 
 * @author Nir Betesh
 *
 */
public interface IUserNotifier {
	public boolean sendSMS(String notificationMsg, String phoneNumber);
	public boolean sendEmail(String notificationMsg, String email);
	public boolean sendNotification(String notificationMsg, String email, String phoneNumber);

}
