package ekrut.server;

import java.io.IOException;
import ekrut.entity.User;
import ekrut.net.InventoryItemRequest;
import ekrut.net.InventoryItemResponse;
import ekrut.net.OrderRequest;
import ekrut.net.OrderResponse;
import ekrut.net.ReportRequest;
import ekrut.net.ReportResponse;
import ekrut.net.ResultType;
import ekrut.net.SaleDiscountRequest;
import ekrut.net.SaleDiscountResponse;
import ekrut.net.ShipmentRequest;
import ekrut.net.ShipmentResponse;
import ekrut.net.TicketRequest;
import ekrut.net.TicketResponse;
import ekrut.net.UserRequest;
import ekrut.net.UserResponse;
import ekrut.server.db.DBController;
import ekrut.server.intefaces.IUserNotifier;
import ekrut.server.managers.ServerInventoryManager;
import ekrut.server.managers.ServerOrderManager;
import ekrut.server.managers.ServerReportManager;
import ekrut.server.managers.ServerSalesManager;
import ekrut.server.managers.ServerSessionManager;
import ekrut.server.managers.ServerShipmentManager;
import ekrut.server.managers.ServerTicketManager;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

/**
 * 
 * The EKrutServer class is the main server class of the EKrut application. It
 * extends the AbstractServer class from the OCSF library and provides methods
 * for handling incoming client requests.
 * 
 * @author Yovel Gabay
 */
public class EKrutServer extends AbstractServer {

	public static final int DEFAULT_PORT = 5555;
	private DBController dbCon;
	private ServerSessionManager serverSessionManager;
	private ServerTicketManager serverTicketManager;
	private ServerOrderManager serverOrderManager;
	private ServerInventoryManager serverInventoryManager;
	private ServerReportManager serverReportManager;
	private ServerShipmentManager serverShipmentManager;
	private ServerSalesManager serverSalesManager;

	/**
	 * 
	 * Constructs a new EKrutServer object and initializes the dbCon,
	 * serverSessionManager, serverTicketManager, serverOrderManager,
	 * serverInventoryManager, serverReportManager, serverShipmentManager,
	 * serverSalesManager.
	 * 
	 * @param port       the port number to use for the server
	 * @param DBuserName the username of the database user
	 * @param dbUsername the username of the database
	 * @param dbPassword the password of the database
	 */
	public EKrutServer(int port, String DBuserName, String dbUsername, String dbPassword) {
		super(port);
		dbCon = new DBController(DBuserName, dbUsername, dbPassword);
		serverSessionManager = new ServerSessionManager(dbCon);
		IUserNotifier userNotifier = new PopupUserNotifier(dbCon, serverSessionManager);
		serverInventoryManager = new ServerInventoryManager(dbCon, userNotifier);
		serverTicketManager = new ServerTicketManager(dbCon);
		serverSalesManager = new ServerSalesManager(dbCon, serverSessionManager);
		serverOrderManager = new ServerOrderManager(dbCon, serverSessionManager, serverSalesManager,
				serverInventoryManager);
		serverShipmentManager = new ServerShipmentManager(dbCon, serverSessionManager);
		serverReportManager = new ServerReportManager(dbCon);

	}

	/**
	 * 
	 * Handles incoming messages from the client and routes them to the appropriate
	 * handling method based on the message's type.
	 * 
	 * @param msg    the incoming message from the client
	 * @param client the {@link ConnectionToClient} object associated with the
	 *               client
	 */
	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		if (msg instanceof UserRequest) {
			handleMessageUser((UserRequest) msg, client);
		} else if (msg instanceof TicketRequest) {
			handleMessageTicket((TicketRequest) msg, client);
		} else if (msg instanceof InventoryItemRequest) {
			handleMessageInventory((InventoryItemRequest) msg, client);
		} else if (msg instanceof OrderRequest) {
			handleMessageOrder((OrderRequest) msg, client);
		} else if (msg instanceof ShipmentRequest) {
			handleMessageShipment((ShipmentRequest) msg, client);
		} else if (msg instanceof ReportRequest) {
			handleMessageReport((ReportRequest) msg, client);
		} else if (msg instanceof SaleDiscountRequest) {
			handleMessageSales((SaleDiscountRequest) msg, client);
		}
		// TBD OFEK: NEED TO SEND A ERROR RESPONSE
	}

	private void handleMessageUser(UserRequest userRequest, ConnectionToClient client) {
		UserResponse userResponse = null;
		switch (userRequest.getAction()) {
		case LOGIN:
			userResponse = serverSessionManager.loginUser(userRequest.getUsername(), userRequest.getPassword(), client);
			break;
		case LOGOUT:
			userResponse = serverSessionManager.logoutUser(client, null);
			break;
		case IS_LOGGEDIN:
			userResponse = serverSessionManager.isLoggedin(userRequest.getUsername());
			break;
		case FETCH_USER:
			userResponse = serverSessionManager.fetchUser(userRequest.getFetchType(), userRequest.getArgument());
			break;
		case REGISTER_USER:
			userResponse = serverSessionManager.acceptRegisterUser(userRequest.getUserToRegister());
			break;
		case GET_REGISTRATION_LIST:
			userResponse = serverSessionManager.getRegistrationList(userRequest.getArea());
			break;
		case CREATE_USER_TO_REGISTER:
			userResponse = serverSessionManager.createUserToRegister(userRequest.getUserToRegister());
			break;
		case UPDATE_USER:
			userResponse = serverSessionManager.updateUser(userRequest.getUser());
			break;
		default:
			userResponse = new UserResponse(ResultType.UNKNOWN_ERROR);
			break;
		}
		try {
			client.sendToClient(userResponse);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}

	private void handleMessageTicket(TicketRequest ticketRequest, ConnectionToClient client) {
		TicketResponse ticketResponse = null;
		switch (ticketRequest.getAction()) {
		case CREATE:
			ticketResponse = serverTicketManager.CreateTicket(ticketRequest);
			break;
		case UPDATE_STATUS:
			ticketResponse = serverTicketManager.updateTicketStatus(ticketRequest);
			break;
		case FETCH_BY_AREA:
			ticketResponse = serverTicketManager.fetchTicketsByArea(ticketRequest);
			break;
		case FETCH_BY_USERNAME:
			ticketResponse = serverTicketManager.fetchTicketsByUsername(ticketRequest);
			break;
		default:
			ticketResponse = new TicketResponse(ResultType.UNKNOWN_ERROR);
			break;
		}
		try {
			client.sendToClient(ticketResponse);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private void handleMessageInventory(InventoryItemRequest inventoryItemRequest, ConnectionToClient client) {
		User currUser = serverSessionManager.getUser(client);
		InventoryItemResponse inventoryItemResponse = serverInventoryManager.handleRequest(inventoryItemRequest,
				currUser);
		try {
			client.sendToClient(inventoryItemResponse);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private void handleMessageOrder(OrderRequest orderRequest, ConnectionToClient client) {
		OrderResponse orderResponse = null;
		switch (orderRequest.getAction()) {
		case CREATE:
			orderResponse = serverOrderManager.createOrder(orderRequest, client);
			break;
		case FETCH:
			orderResponse = serverOrderManager.fetchOrders(orderRequest, client);
			break;
		case PICKUP:
			orderResponse = serverOrderManager.pickupOrder(orderRequest, client);
			break;
		default:
			orderResponse = new OrderResponse(ResultType.UNKNOWN_ERROR);
			break;
		}
		try {
			client.sendToClient(orderResponse);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private void handleMessageShipment(ShipmentRequest shipmentRequest, ConnectionToClient client) {
		ShipmentResponse shipmentResponse = null;
		switch (shipmentRequest.getAction()) {
		case FETCH_SHIPMENT_ORDERS:
			shipmentResponse = serverShipmentManager.fetchShipmentRequests(shipmentRequest,
					serverSessionManager.getUser(client).getArea());// add user.area
			break;
		case UPDATE_STATUS:
			switch (shipmentRequest.getStatus()) {
			case SUBMITTED:
				shipmentResponse = serverShipmentManager.confirmShipment(shipmentRequest);
				break;
			case AWAITING_DELIVERY:
				shipmentResponse = serverShipmentManager.confirmDelivery(shipmentRequest);
				break;
			case DELIVERY_CONFIRMED:
				shipmentResponse = serverShipmentManager.setDone(shipmentRequest);
				break;
			default:
				break;
			}
		default:
			shipmentResponse = new ShipmentResponse(ResultType.UNKNOWN_ERROR);
			break;

		}
		try {
			client.sendToClient(shipmentResponse);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}

	private void handleMessageReport(ReportRequest reportRequest, ConnectionToClient client) {
		ReportResponse reportResponse = null;
		switch (reportRequest.getReportRequestType()) {
		case FETCH_FACILITIES:
			reportResponse = serverReportManager.fetchFacilitiesByArea(reportRequest, client);
			break;
		case FETCH_REPORT:
			reportResponse = serverReportManager.fetchReport(reportRequest, client);
			break;
		default:
			reportResponse = new ReportResponse(ResultType.UNKNOWN_ERROR);
			break;
		}
		try {
			client.sendToClient(reportResponse);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private void handleMessageSales(SaleDiscountRequest request, ConnectionToClient client) {
		SaleDiscountResponse response = serverSalesManager.handleRequest(request, client);
		sendRequestToClient(response, client);
	}

	/**
	 * 
	 * The method sends object message to the client.
	 * 
	 * @param msg    The message object to send to the client.
	 * @param client The client to send the message to.
	 */
	public static void sendRequestToClient(Object msg, ConnectionToClient client) {
		try {
			client.sendToClient(msg);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * 
	 * This method is called when a client disconnects or when an exception is
	 * thrown by a client. it logs out the user associated with the disconnected
	 * client.
	 * 
	 * @param client    the client that disconnected
	 * @param exception the exception that was thrown
	 */
	@Override
	protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
		serverSessionManager.logoutUser(client, null);
	}

	/**
	 * 
	 * This method returns the session manager
	 * 
	 * @return the session manager
	 */
	public ServerSessionManager getSession() {
		return serverSessionManager;
	}

	/**
	 * 
	 * This method connects the server to the database
	 * 
	 * @return true if the connection was successful, false otherwise
	 */
	public boolean connect() {
		if (!dbCon.connect())
			return false;
		serverReportManager.startReportGeneration();
		return true;
	}

	@Override
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
	}

	@Override
	protected void clientConnected(final ConnectionToClient client) {
	}

	/**
	 * Imports users into the system from the external user management system.
	 * 
	 * @param dbCon the database connection to use for the operation
	 * @return response indicating if the operation was successful or not
	 */
	public UserResponse importUsers() {
		if (!UsersImporter.importUsers(dbCon))
			return new UserResponse(ResultType.UNKNOWN_ERROR);
		return new UserResponse(ResultType.OK);
	}
}
