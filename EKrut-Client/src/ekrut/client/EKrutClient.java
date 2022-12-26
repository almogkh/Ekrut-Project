package ekrut.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import ekrut.client.managers.*;
import ekrut.net.UserNotification;
import ocsf.client.AbstractClient;

public class EKrutClient extends AbstractClient{

	private EKrutClientUI clientUI;
	private ClientInventoryManager clientInventoryManager;
	private ClientOrderManager clientOrderManager;
	private ClientReportManager clientReportManager; 
	private ClientSessionManager clientSessionManager;
	private ClientShipmentManager clientShipmentManager;
	
	private Map<Class<?>, Consumer<Object>> handlers = new HashMap<>();
	
	
	
	public EKrutClient(String host, int port) {
		super(host, port);
	}
	
	public <T> void registerHandler(Class<T> klass, Consumer<T> handler) {
		handlers.put(klass, (response) -> handler.accept(klass.cast(response)));
	}
	
	
	public void sendRequestToServer(Object request) {
		try {
			sendToServer(request);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	@Override
	protected void handleMessageFromServer(Object msg) {
		if (msg instanceof UserNotification) {
			clientUI.popupUserNotification(((UserNotification) msg).getNotificationMsg());
		}
		
		Consumer<Object> handler = handlers.get(msg.getClass());
		if (handler == null)
			throw new RuntimeException("Unknown message received");
		handler.accept(msg);
	}

}
