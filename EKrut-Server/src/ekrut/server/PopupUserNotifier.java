package ekrut.server;

import ekrut.entity.User;
import ekrut.net.UserNotification;
import ekrut.server.db.DBController;
import ekrut.server.db.UserDAO;
import ekrut.server.intefaces.IUserNotifier;
import ekrut.server.managers.ServerSessionManager;
import ocsf.server.ConnectionToClient;

public class PopupUserNotifier implements IUserNotifier {
	
	private UserDAO userDAO;
	private ServerSessionManager sessionManager;
	
	public PopupUserNotifier(DBController dbCon, ServerSessionManager sessionManager) {
		this.userDAO = new UserDAO(dbCon);
		this.sessionManager = sessionManager;
	}
	
	private boolean handleSend(User user, String notificationMsg) {
		if (user == null)
			return false;
		
		ConnectionToClient connection = sessionManager.getUsersConnection(user);
		if (connection == null)
			return false;
		
		EKrutServer.sendRequestToClient(new UserNotification(notificationMsg), connection);
		return true;
	}

	@Override
	public boolean sendSMS(String notificationMsg, String phoneNumber) {
		User user = userDAO.fetchUserByPhoneNumber(phoneNumber);
		return handleSend(user, notificationMsg);
	}

	@Override
	public boolean sendEmail(String notificationMsg, String email) {
		User user = userDAO.fetchUserByEmail(email);
		return handleSend(user, notificationMsg);
	}

	@Override
	public boolean sendNotification(String notificationMsg, String email, String phoneNumber) {
		return sendEmail(notificationMsg, email);
	}

}
