package ekrut.server;

import java.io.IOException;
import java.util.HashMap;

import ekrut.net.ResultType;
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

public class EKrutServer extends AbstractServer {

	private DBController dbCon;
	private ServerSessionManager serverSessionManager;
	private ServerReportManager serverReportManager;
	private HashMap<Class<?>, IRequestHandler> handlers = new HashMap<>();

	public EKrutServer(int port) {
		super(port);
	}
	
	public void init(String DBuserName, String dbUsername, String dbPassword) {
		dbCon = new DBController(DBuserName, dbUsername, dbPassword);
		serverSessionManager = new ServerSessionManager(dbCon);
		IUserNotifier userNotifier = new PopupUserNotifier(dbCon, serverSessionManager);
		ServerInventoryManager serverInventoryManager = new ServerInventoryManager(dbCon, userNotifier);
		new ServerTicketManager(dbCon);
		ServerSalesManager serverSalesManager = new ServerSalesManager(dbCon, serverSessionManager);
		new ServerOrderManager(dbCon, serverSalesManager, serverInventoryManager);
		new ServerShipmentManager(dbCon, serverSessionManager);
		serverReportManager = new ServerReportManager(dbCon);
	}
	
	public void registerHandler(Class<?> klass, IRequestHandler handler) {
		handlers.put(klass, handler);
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		if (msg instanceof UserRequest) {
			handleMessageUser((UserRequest) msg, client);
			return;
		}
		
		IRequestHandler handler = handlers.get(msg.getClass());
		if (handler == null)
			throw new RuntimeException("Unknown message received");
		sendRequestToClient(handler.handleMessage(msg, client), client);
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

	public static void sendRequestToClient(Object msg, ConnectionToClient client) {
		try {
			client.sendToClient(msg);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
		serverSessionManager.logoutUser(client, null);
		dbCon.close();
	}

	public ServerSessionManager getSession() {
		return serverSessionManager;
	}
	
	public DBController getDBController() {
		return dbCon;
	}

	public boolean connect() {
		if (!dbCon.connect())
			return false;
		serverReportManager.startReportGeneration();
		return true;
	}

	@Override
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
		dbCon.close();
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
