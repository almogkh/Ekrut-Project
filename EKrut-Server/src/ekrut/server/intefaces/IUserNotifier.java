package ekrut.server.intefaces;

/**
 * 
 * @author Nir Betesh
 *
 */
public interface IUserNotifier {
	public boolean sendSMS();
	public boolean sendEmail();
	public boolean sendNotification();

}
