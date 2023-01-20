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
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Timer;

class ServerSessionMangerTest {

	private UserDAO userDAO;
	private User user;
	private Timer timer;
	private Timer timerMock;
	private ConnectionToClient client;
	private ServerSessionManager serverSessionManager;
	private DBController dbCon;
	private IUserNotifier userNotifier;
	private Map<User, Timer> connectedUsers;
	private Map<ConnectionToClient, User> clientUserMap;
	private ObservableList<ConnectedClient> connectedClientList;
	private InetAddress inetAddress;

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void setUp() {
		dbCon = new DBController("jdbc:postgresql://localhost:5432/mydb", "username", "password");
		userNotifier = mock(IUserNotifier.class);
		client = mock(ConnectionToClient.class);
		//inetAddress = mock(InetAddress.class);
		userDAO = mock(UserDAO.class);
		timerMock = mock(Timer.class);
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
			connectedUsersField.setAccessible(true);
			clientUserMapField.setAccessible(true);
			connectedClientListField.setAccessible(true);
			timerField.setAccessible(true);
			timerField.set(null,timerMock);
			
			connectedUsers = (Map<User, Timer>) connectedUsersField.get(serverSessionManager);
			clientUserMap = (Map<ConnectionToClient, User>) clientUserMapField.get(serverSessionManager);
			connectedClientList = (ObservableList<ConnectedClient>) connectedClientListField.get(serverSessionManager);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testLoginUser_Success() throws UnknownHostException {
		user = new User(UserType.CUSTOMER, "username", "password", "firstName", "lastName", "123", "email", "phone",
				"UAE");
		when(userDAO.fetchUserByUsername("username")).thenReturn(user);
		// ****need to be fixed*** doNothing().when(timer).schedule(any()); ****need to be fixed***
//		doNothing().when(serverSessionManager).startTimer(Mockito.anyString(),Mockito.any(ConnectionToClient.class));
//		when(inetAddress.getHostAddress()).thenReturn("/127.0.0.1");
//		when(client.getInetAddress()).thenReturn(inetAddress);
		UserResponse response = serverSessionManager.loginUser("username", "password", client);
		assertEquals(ResultType.OK, response.getResultCode());
		assertEquals(user, response.getUser());
		assertTrue(connectedUsers.containsKey(user));
		assertTrue(clientUserMap.containsKey(client));
//		assertTrue(connectedClientList.contains(new ConnectedClient(client.getInetAddress().toString().replace("/", ""),"username", user.getUserType())));
	}

}
