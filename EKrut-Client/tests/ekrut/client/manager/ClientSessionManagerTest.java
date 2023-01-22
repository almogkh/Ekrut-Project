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
import ekrut.net.FetchUserType;
import ekrut.net.ResultType;
import ekrut.net.UserRequest;
import ekrut.net.UserResponse;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;

class ClientSessionManagerTest {

	private ClientSessionManager clientSessionManager;
	private EKrutClient ekrutClient;
	private User user;

	@BeforeEach
	public void setup() {
		ekrutClient = mock(EKrutClient.class);
		clientSessionManager = new ClientSessionManager(ekrutClient);
		user = new User(null, null, null, null, null, null, null, null, null);
	}

	/*
	 * This method is set up a mock for the ekrutClient.sendRequestToServer()method.
	 * This is setting up a mock to simulate the response from the server, which can
	 * be used to test different scenarios in the loginUser() function, such as
	 * different result codes being returned by the sendRequest method.
	 */
	private void sendRequest(UserResponse response) {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock inv)
					throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
				Field res = AbstractClientManager.class.getDeclaredField("response");
				res.setAccessible(true);
				res.set(clientSessionManager, response);
				return null;
			}
		}).when(ekrutClient).sendRequestToServer(any(UserRequest.class));
	}

	// Checking functionality loginUser:  A user is already logged in when the loginUser function is called
	// Input parameters: "username", "password"
	// Expected result: A RuntimeException is thrown with the message "User is already loggedin"
	@Test
	public void test_LoginUser_AlreadyLoggedIn_ThrowException()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		// Set user to a non-null value to simulate a user already being logged in
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

	// Checking functionality loginUser: Testing fetch user by null username
	// Input parameters: null, "password"
	// Expected result: A NullPointerException is thrown with the message "Null Item was provided"
	@Test
	public void test_LoginUser_NullUsername_ThrowException() {
		try {
			clientSessionManager.loginUser(null, "password");
			fail("Expected a NullPointerException to be thrown");
		} catch (NullPointerException e) {
			assertEquals("Null Item was provided", e.getMessage());
		}
	}

	// Checking functionality loginUser: Testing fetch user by null username
	// Input parameters: "username", null
	// Expected result: A NullPointerException is thrown with the message "Null Item was provided"
	@Test
	public void test_LoginUser_NullPassword_throwException() {
		try {
			clientSessionManager.loginUser("username", null);
			fail("Expected a NullPointerException to be thrown");
		} catch (NullPointerException e) {
			assertEquals("Null Item was provided", e.getMessage());
		}
	}

	// Checking functionality loginUser: A valid username and password are provided
	// Input parameters: "username", "password"
	// Expected result:User with "username" username & ResultType.OK
	@Test
	public void test_LoginUser_Success() {
		User expected = new User(UserType.AREA_MANAGER, "username", "password", "yovel", "gabay", "123", "email",
				"phone", "UAE");
		UserResponse response = new UserResponse(ResultType.OK, expected);
		sendRequest(response);
		assertEquals(clientSessionManager.loginUser("username", "password"), expected);
	}


	// Checking functionality loginUser: An invalid username is provided
	// Input parameters: wrong username: "username", "password"
	// Expected result: A RuntimeException is thrown with the message ResultType.NOT_FOUND
	@Test
	public void test_LoginUser_InvaildUsername_throwException() {
		User expected = null;
		UserResponse response = new UserResponse(ResultType.NOT_FOUND, expected);
		sendRequest(response);
		try {
			clientSessionManager.loginUser("username", "password");
			fail("Expected a RuntimeException to be thrown");
		} catch (RuntimeException e) {
			assertEquals(ResultType.NOT_FOUND.toString(), e.getMessage());
		}
	}

	// Checking functionality loginUser: An invalid password are provided
	// Input parameters: Exist username: "username", wrong password: "password" 
	// Expected result: A RuntimeException is thrown with the message ResultType.INVALID_INPUT
	@Test
	public void test_LoginUser_InvaildPassword_throwException() {
		User expected = null;
		UserResponse response = new UserResponse(ResultType.INVALID_INPUT, expected);
		sendRequest(response);
		try {
			clientSessionManager.loginUser("username", "password");
			fail("Expected a RuntimeException to be thrown");
		} catch (RuntimeException e) {
			assertEquals(ResultType.INVALID_INPUT.toString(), e.getMessage());
		}
	}

	// Checking functionality loginUser: The server returns PERMISSION_DENIED because the user is already loggedin
	// Input parameters: "username", "password"
	// Expected result: A RuntimeException is thrown with the message ResultType.PERMISSION_DENIED
	@Test
	public void test_LoginUser_PermissionDenied_AlreadyLoggedIn_ThrowException() {
		User expected = null;
		UserResponse response = new UserResponse(ResultType.PERMISSION_DENIED, expected);
		sendRequest(response);
		try {
			clientSessionManager.loginUser("username", "password");
			fail("Expected a RuntimeException to be thrown");
		} catch (RuntimeException e) {
			assertEquals(ResultType.PERMISSION_DENIED.toString(), e.getMessage());
		}
	}
}
