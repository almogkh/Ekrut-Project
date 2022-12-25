package ekrut.server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import ekrut.entity.Order;
import ekrut.entity.User;
import ekrut.net.InventoryItemRequest;
import ekrut.net.InventoryItemResponse;
import ekrut.net.OrderRequest;
import ekrut.net.OrderResponse;
import ekrut.net.ResultType;
import ekrut.net.ShipmentRequest;
import ekrut.net.ShipmentResponse;
import ekrut.net.TicketRequest;
import ekrut.net.TicketResponse;
import ekrut.net.UserRequest;
import ekrut.net.UserRequestType;
import ekrut.net.UserResponse;
import ekrut.server.db.DBController;
import ekrut.server.managers.ServerInventoryManager;
import ekrut.server.managers.ServerOrderManager;
import ekrut.server.managers.ServerReportManager;
import ekrut.server.managers.ServerSessionManager;
import ekrut.server.managers.ServerShipmentManager;
import ekrut.server.managers.ServerTicketManager;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class EKrutServer extends AbstractServer{
	
	private DBController dbCon;
	private String url, username, password;
	private ServerSessionManager serverSessionManager;
	private ServerTicketManager serverTicketManager;
	private ServerOrderManager serverOrderManager;
	private ServerInventoryManager serverInventoryManager;
	private ServerReportManager serverReportManager;
	private ServerShipmentManager serverShipmentManager;
	
	public EKrutServer(int port) {
		super(port);
		dbCon = new DBController(url, username, password);
		dbCon.connect(); //need to check return value		
		serverSessionManager = new ServerSessionManager(dbCon);
		serverInventoryManager = new ServerInventoryManager(dbCon);
		serverTicketManager = new ServerTicketManager(dbCon);
		serverOrderManager = new ServerOrderManager(dbCon);
		serverShipmentManager = new ServerShipmentManager(dbCon);
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		if(msg instanceof UserRequest) {
			handleMessageUser((UserRequest)msg, client);
		}
		else if(msg instanceof TicketRequest) {
			//Wait for the initialize of serverTicketManager class
			//handleMessageTicket((TicketRequest)msg, client);
		}
		else if(msg instanceof InventoryItemRequest) {
			handleMessageInventory((InventoryItemRequest)msg, client);
		}
		else if(msg instanceof OrderRequest) {
			//Wait for the initialize of serverOrderManager class
			//handleMessageOrder((OrderRequest)msg, client);
		}
		else if(msg instanceof ShipmentRequest) {
			//handleMessageShipment((ShipmentRequest)msg, client);
		}
	}

	private void handleMessageUser(UserRequest userRequest, ConnectionToClient client) {
		UserResponse userResponse = null;
		switch(userRequest.getAction()) {
			case LOGIN:	
				userResponse = serverSessionManager.loginUser(username,password,client);
				break;
			case LOGOUT:	
				userResponse = serverSessionManager.logoutUser(username, client, null);
				break;
			case IS_LOGGEDIN:
				userResponse = serverSessionManager.isLoggedin(username);
				break;
			default:
				userResponse = new UserResponse(ResultType.UNKNOWN_ERROR);
				break;
		}
		try {
			client.sendToClient(userResponse);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);}
			
	}
	//Wait for the initialize of serverTicketManager class
	/*
	private void handleMessageTicket(TicketRequest ticketRequest, ConnectionToClient client) {
		TicketResponse ticketResponse = null;
		switch(ticketRequest.getAction()) {
			case CREATE:	
				ticketResponse = serverTicketManager.createTicket();
			case UPDATE_STATUS:	
				ticketResponse = serverTicketManager.updateTicketStatus();
			case FETCH:	
				ticketResponse = serverTicketManager.fetchTicket();
		}		
		default:
			try {
				client.sendToClient(ticketResponse);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
	}*/

	private void handleMessageInventory(InventoryItemRequest inventoryItemRequest, ConnectionToClient client) {
		InventoryItemResponse inventoryItemResponse = null;
		switch(inventoryItemRequest.getAction()) {
			case UPDATE_ITEM_QUANTITY:	
				inventoryItemResponse = serverInventoryManager.updateItemQuantity(inventoryItemRequest);
				break;
			case FETCH_ITEM:	
				inventoryItemResponse = serverInventoryManager.getItems(inventoryItemRequest);
				break;
			case UPDATE_ITEM_THRESHOLD:	
				inventoryItemResponse = serverInventoryManager.updateItemThreshold(inventoryItemRequest);
				break;
			default:
				inventoryItemResponse = new InventoryItemResponse(ResultType.UNKNOWN_ERROR);
				break;
		}		
		try {
			client.sendToClient(inventoryItemResponse);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	//Wait for the initialize of serverOrderManager class
	/*
	private void handleMessageOrder(OrderRequest orderRequest, ConnectionToClient client) {
		OrderResponse orderResponse = null;
		switch(orderRequest.getAction()) {
			case CREATE:	
				orderResponse = serverOrderManager.createOrder(orderRequest);
				break;
			case FETCH:	
				orderResponse = serverOrderManager.fetchOrders(orderRequest);
				break;
			case PICKUP:	
				orderResponse = serverOrderManager.pickupOrder(orderRequest);
				break;
			default:
			
		}		
		try {
					client.sendToClient(orderResponse);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(-1);
				}
	}
	
	//Wait for the initialize of serverShipmentManager class
	
	private void handleMessageShipment(ShipmentRequest shipmentRequest, ConnectionToClient client) {
		ShipmentResponse shipmentResponse = null;
		Order order;
		switch(shipmentRequest.getAction()) {
			case FETCH_SHIPMENT_ORDERS:	
				shipmentResponse = serverShipmentManager.fetchShipmentRequest(shipmentRequest);//add user.area
				break;
			case UPDATE_STATUS:	
				switch(order.getStatus()) {
					case SUBMITTED:
						shipmentResponse = serverShipmentManager.confirmShipment(shipmentRequest);
						break;
					case AWAITING_DELIVERY:
						shipmentResponse = serverShipmentManager.confirmDelivery(shipmentRequest);
						break;
					case DELIVERY_CONFIRMED:
						shipmentResponse = serverShipmentManager.setDone(shipmentRequest);
						break;
				}
			default:
			
		}		
		try {
			client.sendToClient(shipmentResponse);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	*/

	public static void sendRequestToClient(Object msg,ConnectionToClient client) {
		if(msg instanceof UserRequest) {
			try {
				client.sendToClient((UserRequest)msg);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}	
		}
	}

}
