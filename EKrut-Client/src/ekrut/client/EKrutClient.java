package ekrut.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import ekrut.client.gui.BaseTemplateController;
import ekrut.client.managers.*;
import ekrut.net.UserNotification;
import ekrut.net.UserRequest;
import javafx.application.Platform;
import ocsf.client.AbstractClient;

public class EKrutClient extends AbstractClient{

	private ClientInventoryManager clientInventoryManager;
	private ClientOrderManager clientOrderManager;
	private ClientReportManager clientReportManager; 
	private ClientSessionManager clientSessionManager;
	private ClientShipmentManager clientShipmentManager;
	private ClientSalesManager clientSalesManager;
	private ClientTicketManager clientTicketManager;
	private Map<Class<?>, Consumer<Object>> handlers = new HashMap<>();
	
	public EKrutClient(String host, int port) { 
		super(host, port);
		clientInventoryManager = new ClientInventoryManager(this);
		clientSalesManager = new ClientSalesManager(this);
		clientSessionManager = new ClientSessionManager(this);
		clientOrderManager = new ClientOrderManager(this, EKrutClientUI.ekrutLocation);
		clientReportManager = new ClientReportManager(this);
		clientShipmentManager = new ClientShipmentManager(this);
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
	
	public ClientSalesManager getClientSalesManager() {
		return clientSalesManager;
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
			Platform.runLater(() -> EKrutClientUI.popupUserNotification(((UserNotification) msg).getNotificationMsg()));
			return;
		}
		if(msg instanceof UserRequest) {
			clientSessionManager.logoutUser(true);
			Platform.runLater(() ->{
				EKrutClientUI.popupUserNotification("Your session has expired. You have been logged out.");
				BaseTemplateController.getBaseTemplateController().logout();
				
				} );
			
			return;
		}
		
		Consumer<Object> handler = handlers.get(msg.getClass());
		if (handler == null)
			throw new RuntimeException("Unknown message received");
		handler.accept(msg);
	}
}
