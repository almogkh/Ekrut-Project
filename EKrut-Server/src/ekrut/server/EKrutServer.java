package ekrut.server;

import java.io.IOException;

import ekrut.entity.User;
import ekrut.net.InventoryItemRequest;
import ekrut.net.InventoryItemResponse;
import ekrut.net.OrderRequest;
import ekrut.net.OrderResponse;
import ekrut.net.ReportRequest;
import ekrut.net.ReportResponse;
import ekrut.net.TicketRequest;
import ekrut.net.TicketResponse;
import ekrut.net.UserRequest;
import ekrut.net.UserRequestType;
import ekrut.net.UserResponse;
import ekrut.server.db.UserDAO;
import ekrut.server.managers.ServerSessionManager;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class EKrutServer extends AbstractServer{
	
	private ServerSessionManager serverSessionManager;
	private ServerTicketManager serverTicketManager;
	private ServerOrderManager serverOrderManager;
	private ServerInventoryManager serverInventoryManager;
	private ServerReportManager serverReportManager;
	
	public EKrutServer(int port) {
		super(port);
		serverSessionManager = new ServerSessionManager();
		serverTicketManager = new ServerTicketManager();
		serverOrderManager = new ServerOrderManager();
		serverInventoryManager = new ServerInventoryManager();
		serverReportManager = new ServerReportManager();
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		
		try {
			if (msg instanceof UserRequest) {
				UserRequest userRequest = (UserRequest) msg;
				UserResponse userResponse = null;
				
				switch(userRequest.getAction()) {
					case CONNECT:
						userResponse = serverSessionManager.loginUser();
						break;	
					case DISCONNECT: 
						userResponse = serverSessionManager.logoutUser();
						break;
					case IS_CONNECTED:
						userResponse = serverSessionManager.isLoggedin();
						break;	
				}
				client.sendToClient(userResponse);
			}

			else if (msg instanceof TicketRequest) {
				TicketRequest ticketRequest = (TicketRequest) msg;
				TicketResponse TicketResponse = null;
				switch(ticketRequest.getAction()) {
					case CREATE:
						TicketResponse = serverTicketManager.createTicket();
						break;	
					case UPDATE_STATUS: 
						TicketResponse = serverTicketManager.updateTicketStatus();
						break;
					case FETCH:
						TicketResponse = serverTicketManager.fetchTicket();
						break;	
				}
				client.sendToClient(TicketResponse);
			}
			
			
			else if (msg instanceof OrderRequest) {
				OrderRequest orderRequest = (OrderRequest) msg;
				OrderResponse orderResponse = null;
				switch(orderRequest.getAction()) {
					case CREATE:
						orderResponse = serverOrderManager.createOrder();
						break;
					case FETCH:
						orderResponse = serverOrderManager.fetchOrders();
						break;
					case PICKUP: 
						orderResponse = serverOrderManager.pickupOrder();
						break;
				}
				client.sendToClient(orderResponse);
			}
			
			//What about requsts from shipment Yovel

			else if (msg instanceof InventoryItemRequest) {
				InventoryItemRequest inventoryItemRequest = (InventoryItemRequest) msg;
				InventoryItemResponse inventoryItemResponse = null;
				switch(inventoryItemRequest.getAction()) {
					case UPDATE_ITEM_QUANTITY:
						inventoryItemResponse = serverInventoryManager.createOrder();
						break;
					case FETCH:
						inventoryItemResponse = serverInventoryManager.fetchOrders();
						break;
					case UPDATE_ITEM_THRESHOLD: 
						inventoryItemResponse = serverInventoryManager.pickupOrder();
						break;
				}
				client.sendToClient(inventoryItemResponse);
			}
			 
			else if (msg instanceof ReportRequest) {
				ReportRequest reportRequest = (ReportRequest) msg;
				ReportResponse reportResponse = null;
				switch(reportRequest.getAction()) { //check about ENUM for reports
					case GENERATE:
						reportResponse = serverReportManager.generateReport();
						break;
					case FETCH:
						reportResponse = serverReportManager.fetchReports();
						break;
				}
				client.sendToClient(reportResponse);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	@Override
	protected void clientConnected(ConnectionToClient client) {
		// TODO
	}

	@Override
	protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
		// TODO
	}
	

}