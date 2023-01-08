package ekrut.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ekrut.entity.Customer;
import ekrut.entity.User;
import ekrut.entity.UserRegistration;
import ekrut.entity.UserType;

/**
 * The `UserDAO` class provides methods for fetching and creating users in a
 * database.
 * 
 * @author Yovel Gabay
 */
public class UserDAO {

	private DBController con;

	public UserDAO(DBController con) {
		this.con = con;
	}

	/**
	 * Fetches a user from the database by their username.
	 *
	 * @param username The username of the user to fetch.
	 * @return The `User` object if found, or `null` if not found or an error
	 *         occurred.
	 */
	public User fetchUserByUsername(String username) {
		User user = null;
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM users WHERE username = ?");
		try {
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				// (userType, username, password, firstName, lastName, id, email, phoneNumber,
				// area)
				user = new User(UserType.valueOf(rs.getString(1)), username, rs.getString(3), rs.getString(4),
						rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9));

			return user;
		} catch (SQLException e1) {
			return null;
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Fetches a user from the database by their phone number.
	 *
	 * @param phoneNumber The phone number of the user to fetch.
	 * @return The `User` object if found, or `null` if not found or an error
	 *         occurred.
	 */
	public User fetchUserByPhoneNumber(String phoneNumber) {
		User user = null;
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM users WHERE phoneNumber = ?");
		try {
			ps.setString(1, phoneNumber);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				// (userType, username, password, firstName, lastName, id, email, phoneNumber,
				// area)
				user = new User(UserType.valueOf(rs.getString(1)), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5), rs.getString(6), rs.getString(7), phoneNumber, rs.getString(9));

			return user;
		} catch (SQLException e1) {
			return null;
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Fetches a user from the database by their email.
	 *
	 * @param email The email of the user to fetch.
	 * @return The `User` object if found, or `null` if not found or an error
	 *         occurred.
	 */
	public User fetchUserByEmail(String email) {
		User user = null;
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM users WHERE email = ?");
		try {
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				// (userType, username, password, firstName, lastName, id, email, phoneNumber,
				// area)
				user = new User(UserType.valueOf(rs.getString(1)), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5), rs.getString(6), email, rs.getString(8), rs.getString(9));

			return user;
		} catch (SQLException e1) {
			return null;
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Retrieves customer specific info from the database.
	 * 
	 * @param user the user whose info should be retrieved
	 * @return the customer specific info of the user
	 */
	public Customer fetchCustomerInfo(User user) {
		if (user.getUserType() == UserType.REGISTERED)
			return null;

		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM customers WHERE username = ?");

		try {
			ps.setString(1, user.getUsername());
			ResultSet rs = ps.executeQuery();

			if (rs.next())
				return new Customer(rs.getInt("subscriberNumber"), rs.getString("username"),
						rs.getBoolean("monthlyCharge"), rs.getString("creditCardNumber"),
						rs.getBoolean("orderedAsSub"));
			return null;

		} catch (SQLException e) {
			return null;
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * This method retrieves a user with the role of AREA_MANAGER from the database
	 * based on the specified area.
	 * 
	 * @param area The area for which the AREA_MANAGER is being retrieved.
	 * @return The AREA_MANAGER user for the specified area, or null if no such user
	 *         exists in the database. (role, username, password, firstName,
	 *         lastName, id, email, phoneNumber, area)
	 */
	public User fetchManagerByArea(String area) {
		User user = null;
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM users WHERE role = 'AREA_MANAGER' AND area = ?");
		try {
			ps.setString(1, area);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				// (userType, username, password, firstName, lastName, id, email, phoneNumber,
				// area)
				user = new User(UserType.AREA_MANAGER, rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), area);
			return user;
		} catch (SQLException e1) {
			return null;
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public User fetchUserByArea(String area) {
		User user = null;
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM users WHERE area = ?");
		try {
			ps.setString(1, area);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				// (userType, username, password, firstName, lastName, id, email, phoneNumber,
				// area)
				user = new User(UserType.valueOf(rs.getString(1)), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), area);
			return user;
		} catch (SQLException e1) {
			return null;
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public ArrayList<User> fetchAllUsersByRole(UserType userType) {
		ArrayList<User> usersList = new ArrayList<>();
		User user = null;
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM users WHERE userType = ?");
		try {
			ps.setString(1, userType.toString());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				// (userType, username, password, firstName, lastName, id, email, phoneNumber,
				// area)
				user = new User(userType, rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9));
				usersList.add(user);
			}

			return usersList.size() != 0 ? usersList : null;
		} catch (SQLException e1) {
			return null;
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

	}

	/**
	 * Updates an existing user in the database.
	 *
	 * @return `true` if the user was successfully updated, `false` if an error
	 *         occurred. UserType userType, String username, String password, String
	 *         firstName, String lastName, String id, String email, String
	 *         phoneNumber, String area
	 */
	public boolean updateUser(User user) {
		String query = "UPDATE users SET userType = ?, email = ?, phoneNumber = ? WHERE username = ?";
		PreparedStatement ps = con.getPreparedStatement(query);
		try {
			ps.setString(1, user.getUserType().toString());
			ps.setString(2, user.getEmail());
			ps.setString(3, user.getPhoneNumber());
			ps.setString(4, user.getUsername());
			return 1 == ps.executeUpdate();
		} catch (SQLException e1) {
			return false;
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	// String userName, String creditCardNumber,String phoneNumber,String email,
	// boolean monthlyCharge, String customerOrSub,String subscriberNumber
	public ArrayList<UserRegistration> getUserRegistrationList(String area) {
		ArrayList<UserRegistration> usersRegistrationList = new ArrayList<>();
		UserRegistration userRegistration = null;
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM user_registration WHERE area= ?");
		try {
			ps.setString(1, area);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				//String userName,  String creditCardNumber,String phoneNumber,String email,
				//boolean monthlyCharge,  String customerOrSub
				userRegistration = new UserRegistration(rs.getString(1), rs.getString(2), rs.getString(3),
						rs.getString(4), rs.getBoolean(5),
						rs.getBoolean(6) ? "customer" : "subscriber");
				usersRegistrationList.add(userRegistration);
			}
			return usersRegistrationList.size() != 0 ? usersRegistrationList : null;
		} catch (SQLException e1) {
			return null;
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	// String username, String subscriberNumber, Int monthlyCharge, String
	// creditCardNumber, int orderedAsSub
	public boolean createOrUpdateCustomer(Customer customer) {
		String query = "INSERT INTO customers"
				+ " VALUES(?,?,?,?,?) ON DUPLICATE KEY UPDATE subscriberNumber = ?, monthlyCharge = ?";
		PreparedStatement ps = con.getPreparedStatement(query);
		try {
			ps.setString(1, customer.getUsername());
			ps.setInt(2, customer.getSubscriberNumber());
			ps.setBoolean(3, customer.isMonthlyCharge());
			ps.setString(4, customer.getCreditCard());
			ps.setBoolean(5, false); // When we create a new customer he still not placed order as a subscriber
			ps.setInt(6, customer.getSubscriberNumber());
			ps.setBoolean(7, customer.isMonthlyCharge());
			return 1 >= ps.executeUpdate();
		} catch (SQLException e1) {
			return false;
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public boolean deleteUserFromRegistration(String username) {
		PreparedStatement ps = con.getPreparedStatement("DELETE FROM user_registration WHERE userName = ?;");
		try {
			ps.setString(1, username);
			return (ps.executeUpdate() == 1);
		} catch (SQLException e) {
			return false;
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

	}
}
