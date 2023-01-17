package ekrut.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ekrut.client.gui.BaseTemplateController;
import ekrut.client.managers.*;
import ekrut.net.UserNotification;
import ekrut.net.UserRequest;
import javafx.application.Platform;
import ocsf.client.AbstractClient;

/**
 * 
 * EKrutClient is a class that connects to the server, it also has several
 * manager classes that handle different requests to the server. It also has a
 * Map of IResponseHandler classes that handle the server's response to
 * different requests.
 * 
 * @author Ofek Malka
 */
public class EKrutClient extends AbstractClient {

	private ClientInventoryManager clientInventoryManager;
	private ClientOrderManager clientOrderManager;
	private ClientReportManager clientReportManager;
	private ClientSessionManager clientSessionManager;
	private ClientShipmentManager clientShipmentManager;
	private ClientSalesManager clientSalesManager;
	private ClientTicketManager clientTicketManager;
	private Map<Class<?>, IResponseHandler> handlers = new HashMap<>();

	/**
	 * Creates a new EKrutClient object and connects it to the server specified by
	 * the host and port parameters.
	 * 
	 * @param host The host of the server to connect to.
	 * @param port The port of the server to connect to.
	 */
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

	public <T> void registerHandler(Class<T> klass, IResponseHandler handler) {
		handlers.put(klass, handler);
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
		if (msg instanceof UserRequest) {
			clientSessionManager.logoutUser(true);
			Platform.runLater(() -> {
				EKrutClientUI.popupUserNotification("Your session has expired. You have been logged out.");
				BaseTemplateController.getBaseTemplateController().logout();

			});

			return;
		}

		IResponseHandler handler = handlers.get(msg.getClass());
		if (handler == null)
			throw new RuntimeException("Unknown message received");
		handler.handleResponse(msg);
	}
}
