package ekrut.server.managers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ekrut.entity.ConnectedClient;
import ekrut.entity.User;
import ekrut.entity.UserType;
import ekrut.net.ResultType;
import ekrut.net.UserResponse;
import ekrut.server.TimeScheduler;
import ekrut.server.db.DBController;
import ekrut.server.db.UserDAO;
import ekrut.server.intefaces.IUserNotifier;
import javafx.collections.ObservableList;
import ocsf.server.ConnectionToClient;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

class ServerSessionMangerTest {

	private UserDAO userDAO;
	private User user;
	private Timer timer;
	private ConnectionToClient client;
	private ServerSessionManager serverSessionManager;
	private DBController dbCon;
	private IUserNotifier userNotifier;
	private Map<User, Timer> connectedUsers;
	private Map<ConnectionToClient, User> clientUserMap;
	private ObservableList<ConnectedClient> connectedClientList;
	private InetAddress inetAddress;
	private Socket clientSocket;

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void setUp() {
		dbCon = new DBController("jdbc:postgresql://localhost:5432/mydb", "username", "password");
		userNotifier = mock(IUserNotifier.class);
		client = mock(ConnectionToClient.class);
		userDAO = mock(UserDAO.class);
		timer = mock(Timer.class);
		clientSocket = mock(Socket.class);
		inetAddress = mock(InetAddress.class);
		serverSessionManager = new ServerSessionManager(dbCon, userNotifier);
		// Use reflection to set the userDAO field in the ServerSessionManager object
		try {
			Field userDAOField = ServerSessionManager.class.getDeclaredField("userDAO");
			userDAOField.setAccessible(true);
			userDAOField.set(serverSessionManager, userDAO);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		// Initialize the connectedUsers, clientUserMap, and connectedClientList fields
		// to be accessible in the test methods
		try {
			Field connectedUsersField = ServerSessionManager.class.getDeclaredField("connectedUsers");
			Field clientUserMapField = ServerSessionManager.class.getDeclaredField("clientUserMap");
			Field connectedClientListField = ServerSessionManager.class.getDeclaredField("connectedClientList");
			Field timerField = TimeScheduler.class.getDeclaredField("timer");
			Field clientSocketField = ConnectionToClient.class.getDeclaredField("clientSocket");
			connectedUsersField.setAccessible(true);
			clientUserMapField.setAccessible(true);
			connectedClientListField.setAccessible(true);
			timerField.setAccessible(true);
			clientSocketField.setAccessible(true);
			clientSocketField.set(client, clientSocket);
			timerField.set(null, timer);
			connectedUsers = (Map<User, Timer>) connectedUsersField.get(serverSessionManager);
			clientUserMap = (Map<ConnectionToClient, User>) clientUserMapField.get(serverSessionManager);
			connectedClientList = (ObservableList<ConnectedClient>) connectedClientListField.get(serverSessionManager);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	// Checking functionality loginUser: User with valid input
	// Input: "username", "password"
	// Expected output: ResultType.OK, User object, connectedUsers map contains the
	// user, clientUserMap contains the client, connectedClientList contains the
	// client
	@Test
	public void test_LoginUser_ExistUser_ResOK() throws UnknownHostException {
		user = new User(UserType.CUSTOMER, "username", "password", "firstName", "lastName", "123", "email", "phone",
				"UAE");
		when(userDAO.fetchUserByUsername("username")).thenReturn(user);
		doNothing().when(timer).schedule(any(TimerTask.class), anyLong());
		when(inetAddress.toString()).thenReturn("/127.0.0.1");
		when(clientSocket.getInetAddress()).thenReturn(inetAddress);
		UserResponse response = serverSessionManager.loginUser("username", "password", client);
		assertEquals(ResultType.OK, response.getResultCode());
		assertEquals(user, response.getUser());
		assertTrue(connectedUsers.containsKey(user));
		assertTrue(clientUserMap.containsKey(client));
		connectedClientList = serverSessionManager.getConnectedClientList();
		assertTrue(connectedClientList.contains(new ConnectedClient(client.getInetAddress().toString().replace("/", ""),
				"username", user.getUserType())));
	}

	// Checking functionality loginUser: Non-existent user
	// Input: "username", "password"
	// Expected output: ResultType.NOT_FOUND, null user object
	@Test
	public void test_LoginUser_UserNotExist_ResNotFound() {
		when(userDAO.fetchUserByUsername("username")).thenReturn(null);
		UserResponse response = serverSessionManager.loginUser("username", "password", client);
		assertEquals(ResultType.NOT_FOUND, response.getResultCode());
		assertNull(response.getUser());
	}

	// Checking functionality loginUser: null username
	// Input: null username, "password"
	// Expected output: ResultType.NOT_FOUND
	@Test
	public void test_LoginUser_NullUsername_ResNotFound() {
		user = new User(UserType.CUSTOMER, "username", "password", "firstName", "lastName", "123", "email", "phone",
				"UAE");
		when(userDAO.fetchUserByUsername("username")).thenReturn(user);
		UserResponse response = serverSessionManager.loginUser(null, "password", client);
		assertEquals(ResultType.NOT_FOUND, response.getResultCode());
	}

	// Checking functionality loginUser: null username
	// Input: "username", null password
	// Expected output: ResultType.INVALID_INPUT
	@Test
	public void test_LoginUser_NullPassword_ResInvalidInput() {
		user = new User(UserType.CUSTOMER, "username", "password", "firstName", "lastName", "123", "email", "phone",
				"UAE");
		when(userDAO.fetchUserByUsername("username")).thenReturn(user);
		UserResponse response = serverSessionManager.loginUser("username", null, client);
		assertEquals(ResultType.INVALID_INPUT, response.getResultCode());
	}

	// Checking functionality loginUser: Incorrect password
	// Input: "username", "incorrect_password"
	// Expected output: ResultType.INVALID_INPUT, null user object
	@Test
	public void test_LoginUser_IncorrectPassword_ResInvalidInput() {
		user = new User(UserType.CUSTOMER, "username", "password", "firstName", "lastName", "123", "email", "phone",
				"UAE");
		when(userDAO.fetchUserByUsername("username")).thenReturn(user);
		UserResponse response = serverSessionManager.loginUser("username", "incorrect_password", client);
		assertEquals(ResultType.INVALID_INPUT, response.getResultCode());
		assertNotNull(response.getUser());
	}

	// Checking functionality loginUser: User already logged in
	// Input: "username", "password"
	// Expected output: ResultType.PERMISSION_DENIED
	@Test
	public void test_LoginUser_UserAlreadyLoggedIn_ResPermissionDenied() {
		user = new User(UserType.CUSTOMER, "username", "password", "firstName", "lastName", "123", "email", "phone",
				"UAE");
		when(userDAO.fetchUserByUsername("username")).thenReturn(user);
		connectedUsers.put(user, timer);
		UserResponse response = serverSessionManager.loginUser("username", "password", client);
		assertEquals(ResultType.PERMISSION_DENIED, response.getResultCode());
	}

}
