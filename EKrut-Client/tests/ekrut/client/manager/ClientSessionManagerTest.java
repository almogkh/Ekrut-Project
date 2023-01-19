package ekrut.client.manager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import ekrut.client.EKrutClient;
import ekrut.client.managers.AbstractClientManager;
import ekrut.client.managers.ClientSessionManager;
import ekrut.entity.User;
import ekrut.entity.UserType;
import ekrut.net.ResultType;
import ekrut.net.UserRequest;
import ekrut.net.UserResponse;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

class ClientSessionManagerTest {

	private ClientSessionManager clientSessionManager;
	private EKrutClient ekrutClient;
	private User user = new User(null, null, null, null, null, null, null, null, null);

	@BeforeEach
	public void setup() {
		ekrutClient = mock(EKrutClient.class);
		clientSessionManager = new ClientSessionManager(ekrutClient);
	}

	@Test
	public void testLoginUser_AlreadyLoggedIn() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		// Set user to a non-nu value to simulate a user already being logged
		// in

		Field userField = ClientSessionManager.class.getDeclaredField("user");
		userField.setAccessible(true);
		userField.set(clientSessionManager, user);
		try {
			clientSessionManager.loginUser("username", "password");
			fail("Expected a RuntimeException to be thrown");
		} catch (RuntimeException e) {
			assertEquals("User is already loggedin", e.getMessage());
		}
	}

	@Test
	public void testLoginUser_NullUsername() {
		try {
			clientSessionManager.loginUser(null, "password");
			fail("Expected a NullPointerException to be thrown");
		} catch (NullPointerException e) {
			assertEquals("Null Item was provided", e.getMessage());
		}
	}

	@Test
	public void testLoginUser_NullPassword() {
		try {
			clientSessionManager.loginUser("username", null);
			fail("Expected a NullPointerException to be thrown");
		} catch (NullPointerException e) {
			assertEquals("Null Item was provided", e.getMessage());
		}
	}

	@Test
	public void testLoginUser_Success() {
		User expected =  new User(UserType.AREA_MANAGER, "username",
				"password", "yovel", "gabay", "123", "email", "phone", "UAE");
		UserResponse response = new UserResponse(ResultType.OK, expected);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock inv) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
				Field res = AbstractClientManager.class.getDeclaredField("response");
				res.setAccessible(true);
				res.set(clientSessionManager, response);
				return null;
			}
		}).when(ekrutClient).sendRequestToServer(any(UserRequest.class));

		assertEquals(clientSessionManager.loginUser("username", "password"), expected);	
	}

	/*
	 * @Test public void testLoginUser_Failure() { UserResponse userResponse = new
	 * UserResponse(ResultType.FAILURE, null); UserRequest userLoginRequest =
	 * mock(UserRequest.class);
	 * doReturn(userResponse).when(clientSessionManager).sendRequest(
	 * userLoginRequest); try { clientSessionManager.loginUser("username",
	 * "password"); fail("Expected a RuntimeException to be thrown"); } catch
	 * (RuntimeException e) { assertEquals("Failed to login", e.getMessage()); } }
	 * 
	 */
}
