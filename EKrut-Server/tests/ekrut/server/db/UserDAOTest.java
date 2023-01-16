package ekrut.server.db;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ekrut.entity.Customer;
import ekrut.entity.User;
import ekrut.entity.UserRegistration;
import ekrut.entity.UserType;

class UserDAOTest {
	String url;
	String username;
	String password;
	UserDAO userDAO;
	DBController dbController;
	User user1, user2, user3, user4;
	User userResult;
	UserRegistration registered1;
	Customer customer1;
	Customer customerResult;
	ArrayList<UserRegistration> userRegistrationList;
	ArrayList<User> usersArrayList;

	@BeforeEach
	void setUp() throws Exception {
		dbController = new DBController("jdbc:mysql://localhost/testekrut?serverTimezone=IST", "root", "1qazZ2wsxX!@");
		dbController.connect();
		userDAO = new UserDAO(dbController);
		userRegistrationList = new ArrayList<>();
		usersArrayList = new ArrayList<>();
		user1 = new User(UserType.CEO, "ofek", "ofek", "ofek", "malka", "1", "uaeCEO@ek.com", "05050050050", "UAE");
		user2 = new User(UserType.AREA_MANAGER, "nir123", "123", "nir", "betesh", "110", "nir123@ek.com", "0509999999",
				"UAE");
		user3 = new User(UserType.AREA_MANAGER, "Yovelg", "123", "yovel", "gabbay", "111", "yovelg@ek.com",
				"0508888888", "North");
		registered1 = new UserRegistration("talg", "1717", "0507777777", "talg@ek.com", false, "customer", "UAE");
		
		userDAO.createOrUpdateCustomer(new Customer(1, "ofek", true, "123", true));

	}

	// Checking functionality fetchUserByUsername: fetch user by exists username
	// Input parameters: "ofek"
	// Expected result: User with user name: "ofek"
	@Test
	void test_FetchUserByUsername_UserNameExists_ResUser() {
		userResult = userDAO.fetchUserByUsername("ofek");
		assertEquals(userResult.getUserType(), UserType.CEO, "Incorrect userType");
		assertEquals(userResult.getPassword(), "ofek", "Incorrect password");
		assertEquals(userResult.getFirstName(), "ofek", "Incorrect first-name");
		assertEquals(userResult.getLastName(), "malka", "Incorrect last-name");
		assertEquals(userResult.getId(), "1", "Incorrect ID");
		assertEquals(userResult.getEmail(), "uaeCEO@ek.com", "Incorrect email");
		assertEquals(userResult.getPhoneNumber(), "05050050050", "Incorrect phone number");
		assertEquals(userResult.getArea(), "UAE", "Incorrect area");
	}

	// Checking functionality fetchUserByUsername: fetch user by not exists username
	// Input parameters: "dana"
	// Expected result: null
	@Test
	void test_FetchUserByUsername_UserNameNotExist_ResNull() {
		assertNull(userDAO.fetchUserByUsername("dana"));
	}

	// Checking functionality fetchUserByUsername: fetch user by null username
		// Input parameters: null
		// Expected result: null
		@Test
		void test_FetchUserByUsername_NullUser_ResNull() {
			assertNull(userDAO.fetchUserByUsername(null));
		}
		
	// Checking functionality fetchUserByPhoneNumber: fetch user by exists
	// phoneNumber
	// Input parameters: "05050050050"
	// Expected result: User with user name: "ofek" , phoneNumbe: "05050050050"
	@Test
	void test_FetchUserByPhoneNumber_PhoneNumberExists_ResUser() {
		userResult = userDAO.fetchUserByPhoneNumber("05050050050");
		assertEquals(userResult.getUserType(), UserType.CEO, "Incorrect userType");
		assertEquals(userResult.getUsername(), "ofek", "Incorrect username");
		assertEquals(userResult.getPassword(), "ofek", "Incorrect password");
		assertEquals(userResult.getFirstName(), "ofek", "Incorrect first-name");
		assertEquals(userResult.getLastName(), "malka", "Incorrect last-name");
		assertEquals(userResult.getId(), "1", "Incorrect ID");
		assertEquals(userResult.getEmail(), "uaeCEO@ek.com", "Incorrect email");
		assertEquals(userResult.getArea(), "UAE", "Incorrect area");
	}

	// Checking functionality fetchUserByPhoneNumber: fetch user by not exists phoneNumber
	// Input parameters: "0"
	// Expected result: null
	@Test
	void test_FetchUserByPhoneNumber_PhoneNumberNotExists_ResNull() {
		assertNull(userDAO.fetchUserByPhoneNumber("0"));
	}
	
	// Checking functionality fetchUserByPhoneNumber: fetch user by null phoneNumber
	// Input parameters: null
	// Expected result: null
	@Test
	void test_FetchUserByPhoneNumber_NullPhoneNumber_ResNull() {
		assertNull(userDAO.fetchUserByPhoneNumber(null));
	}

	// Checking functionality fetchUserByEmail: fetch user by exists Email
	// Input parameters: "uaeCEO@ek.com"
	// Expected result: User with user name: "ofek" , email: "uaeCEO@ek.com"
	@Test
	void test_FetchUserByEmail_EmailExists_ResUser() {
		userResult = userDAO.fetchUserByEmail("uaeCEO@ek.com");
		assertEquals(userResult.getUserType(), UserType.CEO, "Incorrect userType");
		assertEquals(userResult.getUsername(), "ofek", "Incorrect username");
		assertEquals(userResult.getPassword(), "ofek", "Incorrect password");
		assertEquals(userResult.getFirstName(), "ofek", "Incorrect first-name");
		assertEquals(userResult.getLastName(), "malka", "Incorrect last-name");
		assertEquals(userResult.getId(), "1", "Incorrect ID");
		assertEquals(userResult.getPhoneNumber(), "05050050050", "Incorrect phone number");
		assertEquals(userResult.getArea(), "UAE", "Incorrect area");
	}

	// Checking functionality fetchUserByEmail: fetch user by exists Email
	// Input parameters: "uaeCEO@ek.com"
	// Expected result: null
	void test_FetchUserByEmail_EmailNotExists_ResNull() {
		assertNull(userDAO.fetchUserByEmail("@ek.com"));
	}

	// Checking functionality fetchUserByEmail: fetch user by null Email
	// Input parameters: null
	// Expected result: null
		void test_FetchUserByEmail_NullEmail_ResNull() {
			assertNull(userDAO.fetchUserByEmail(null));
		}
		
	// Checking functionality fetchCustomerInfo: fetch customer by user that is not customer
	// Input parameters: user2 - not customer
	// Expected result: null
	@Test
	void test_FetchCustomerInfo_UserIsNotCustomer_ResNull() {
		assertNull(userDAO.fetchCustomerInfo(user2));
	}

	// Checking functionality fetchCustomerInfo: fetch customer by user that is customer
	// Input parameters: user1 - customer
	// Expected result: customer with username: "ofek"
	@Test
	void test_FetchCustomerInfo_UserIsCustomer_ResCustomer() {
		customer1 = new Customer(1, "ofek", true, "123", false);
		customerResult = userDAO.fetchCustomerInfo(user1);
		assertEquals(customerResult, customerResult);
	}

	// Checking functionality fetchManagerByArea: fetch AREA_MANAGER by exist area
	// Input parameters: "UAE"
	// Expected result: area manager of UAE  - username: "nir123"
	@Test
	void test_FetchManagerByArea_AreaExists_ResManager() {
		assertEquals(user2, userDAO.fetchManagerByArea("UAE"), "Incorrect manager or area");
	}
	
	// Checking functionality fetchManagerByArea: fetch AREA_MANAGER by not exist area
	// Input parameters: "Nahariya"
	// Expected result: null
	@Test
	void test_FetchManagerByArea_AreaNotExists_ResNull() {
		assertNull(userDAO.fetchManagerByArea("Nahariya"));
	}

	// Checking functionality fetchManagerByArea: fetch AREA_MANAGER by null area
	// Input parameters: null
	// Expected result: null
	@Test
	void test_FetchManagerByArea_NullArea_ResNull() {
		assertNull(userDAO.fetchManagerByArea(null));
	}

	// Checking functionality fetchAllUsersByRole: fetch users by Role - not exist users in this role
	// Input parameters: OPERATIONS_WORKER
	// Expected result: null
	@Test
	void test_fetchAllUsersByRole_NotExistUsersInThisRole_ResNull() {
		assertNull(userDAO.fetchAllUsersByRole(UserType.OPERATIONS_WORKER), "usersArrayList by role isn't null");
	}

	// Checking functionality fetchAllUsersByRole: fetch users by Role - exist users in this role
	// Input parameters: AREA_MANAGER
	// Expected result: area managers arrayList
	@Test
	void test_fetchAllUsersByRole_ExistUsersInThisRole_ResUsersArrayList() {
		usersArrayList.add(user2);
		usersArrayList.add(user3);
		assertEquals(usersArrayList, userDAO.fetchAllUsersByRole(UserType.AREA_MANAGER), "Incorrect usersArrayList");
	}
	
	// Checking functionality fetchAllUsersByRole: fetch users by null role
	// Input parameters: null
	// Expected result: null
	@Test
	void test_fetchAllUsersByRole_NullRole_ResUsersArrayList() {
		assertNull(userDAO.fetchAllUsersByRole(null));
	}

	// Checking functionality updateUser: update null user
	// Input parameters: null
	// Expected result: false
	@Test
	void test_updateUser_setNullUser_ResFalse() {
		assertFalse(userDAO.updateUser(null));
	}

	// Checking functionality updateUser: update password of exist user
	// Input parameters:  exist user with username: "nir123"
	// Expected result: true
	@Test
	void test_updateUser_setExistUser_ResTrue() {
		user4 = new User(UserType.AREA_MANAGER, "nir123", "100", "nir", "betesh", "110", "nir123@ek.com", "0509999999",
				"UAE");
		assertTrue(userDAO.updateUser(user4));
		assertEquals(user4.getPassword(), "100");
	}
	
	// Checking functionality getUserRegistrationList: get user registration list of AUE area
	// Input parameters: "UAE"
	// Expected result:  user registration arrayList of "UAE" area
	@Test
	void test_GetUserRegistrationList_ResArrayListUAE() {
		userRegistrationList.add(registered1);
		assertEquals(userRegistrationList, userDAO.getUserRegistrationList("UAE"));
	}

	// Checking functionality getUserRegistrationList: there are no users to register in South area
	// Input parameters: "South"
	// Expected result: empty user registration arrayList
	@Test
	void test_GetUserRegistrationList_EmptyList_ResNullArrayList() {
		assertNull(userDAO.getUserRegistrationList("South"));
	}

	// Checking functionality deleteUserFromRegistration: delete exist User
	// Input parameters: "talg"
	// Expected result: true
	@Test
	void test_deleteUserFromRegistration__DeleteExistUser_ResTrue() {
		assertTrue(userDAO.deleteUserFromRegistration("talg"));
		assertNull(userDAO.getUserRegistrationList("UAE")); // Tal is not in the list
	}

	// Checking functionality deleteUserFromRegistration: delete null User
	// Input parameters: null
	// Expected result: false
	@Test
	void test_deleteUserFromRegistration__DeleteNullUser_ResFalse() {
		assertFalse(userDAO.deleteUserFromRegistration(null));
	}
	
	// Checking functionality deleteUserFromRegistration: delete not exist User
	// Input parameters: null
	// Expected result: false
	@Test
	void test_deleteUserFromRegistration_ResFalse() {
		assertFalse(userDAO.deleteUserFromRegistration("dana"));
	}

}
