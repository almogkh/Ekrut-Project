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
	private ClientTicketManager clientTicketManager;
	private Map<Class<?>, Consumer<Object>> handlers = new HashMap<>();
	
	
	
	public EKrutClient(String host, int port) {
		super(host, port);
		clientInventoryManager = new ClientInventoryManager(this);
		clientOrderManager = new ClientOrderManager(this, EKrutClientUI.ekrutLocation);
		clientReportManager = new ClientReportManager(this);
		clientSessionManager = new ClientSessionManager(this);
		//clientShipmentManager = new ClientShipmentManager(this);
		clientTicketManager = new ClientTicketManager(this);
	}
	
	
	public ClientTicketManager getClientTicketManager() {
		return clientTicketManager;
	}
	
	public ClientInventoryManager getClientInventoryManager() {
		return clientInventoryManager;
	}

	public ClientOrderManager getClientOrderManager() {
		return clientOrderManager;
	}

	public ClientReportManager getClientReportManager() {
		return clientReportManager;
	}

	public ClientSessionManager getClientSessionManager() {
		return clientSessionManager;
	}

	public ClientShipmentManager getClientShipmentManager() {
		return clientShipmentManager;
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
			return;
		}
		
		Consumer<Object> handler = handlers.get(msg.getClass());
		if (handler == null)
			throw new RuntimeException("Unknown message received");
		handler.accept(msg);
	}
}
