package ekrut.net;

import java.io.Serializable;

public class UserNotification implements Serializable {

	private static final long serialVersionUID = 682977901955530812L;
	
	private String notificationMsg;
	
	public UserNotification(String notificationMsg) {
		this.notificationMsg = notificationMsg;
	}

	public String getNotificationMsg() {
		return notificationMsg;
	}
}
