package ekrut.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ekrut.entity.Customer;
import ekrut.entity.User;
import ekrut.entity.UserType;

/**
 * The `UserDAO` class provides methods for fetching and creating users in a database.
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
	 * @return The `User` object if found, or `null` if not found or an error occurred.
	 */
	public User fetchUserByUsername(String username){
		User user = null;
		PreparedStatement ps= con.getPreparedStatement("SELECT * FROM users WHERE username = ?"); 
		try {
			ps.setString(1,username);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				//(userType, username, password, firstName, lastName, id, email, phoneNumber, area)
				user = new User(UserType.valueOf(rs.getString(1)),username,
						rs.getString(3), rs.getString(4),rs.getString(5),
						rs.getString(6),  rs.getString(7),
						rs.getString(8), rs.getString(9));
				Customer customerInfo = fetchCustomerInfo(user);
				user.setCustomerInfo(customerInfo);
			}
			
			return user;
		} catch (SQLException e1) {
			return null;
		}finally {
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
	 * @return The `User` object if found, or `null` if not found or an error occurred.
	 */
	public User fetchUserByPhoneNumber(String phoneNumber) {
		User user = null;
		PreparedStatement ps= con.getPreparedStatement("SELECT * FROM users WHERE phoneNumber = ?"); 
		try {
			ps.setString(1, phoneNumber);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				//(userType, username, password, firstName, lastName, id, email, phoneNumber, area)
				user = new User(UserType.valueOf(rs.getString(1)),rs.getString(2),
						rs.getString(3), rs.getString(4),rs.getString(5),
						rs.getString(6),  rs.getString(7),
						phoneNumber, rs.getString(9));
				Customer customerInfo = fetchCustomerInfo(user);
				user.setCustomerInfo(customerInfo);
			}
				
			return user;
		} catch (SQLException e1) {
			return null;
		}finally {
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
	 * @return The `User` object if found, or `null` if not found or an error occurred.
	 */
	public User fetchUserByEmail(String email) {
		User user = null;
		PreparedStatement ps= con.getPreparedStatement("SELECT * FROM users WHERE email = ?"); 
		try {
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				//(userType, username, password, firstName, lastName, id, email, phoneNumber, area)
				user = new User(UserType.valueOf(rs.getString(1)),rs.getString(2),
						rs.getString(3), rs.getString(4),rs.getString(5),
						rs.getString(6),  email,
						rs.getString(8), rs.getString(9));
				Customer customerInfo = fetchCustomerInfo(user);
				user.setCustomerInfo(customerInfo);
			}
				
			return user;
		} catch (SQLException e1) {
			return null;
		}finally {
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
	 * @return     the customer specific info of the user
	 */
	public Customer fetchCustomerInfo(User user) {
		if (user.getUserType() == UserType.REGISTERED)
			return null;
		
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM customers WHERE username = ?");
		
		try {
			ps.setString(1, user.getUsername());
			ResultSet rs = ps.executeQuery();
			
			if (rs.next())
				return new Customer(rs.getString("subscriberNumber"), rs.getString("username"),
									rs.getString("address"), rs.getBoolean("monthlyCharge"),
									rs.getString("creditCardNumber"), rs.getBoolean("orderedAsSub"));
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
	 * 	This method retrieves a user with the role of AREA_MANAGER from the database based on the specified area.
	 * 
	 * 	@param area The area for which the AREA_MANAGER is being retrieved.
	 * 	@return The AREA_MANAGER user for the specified area, or null if no such user exists in the database.
	 * (role, username, password, firstName, lastName, id, email, phoneNumber, area)
	*/
	public User fetchManagerByArea(String area){
		User user = null;
		PreparedStatement ps= con.getPreparedStatement("SELECT * FROM users WHERE role = 'AREA_MANAGER' AND area = ?"); 
		try {
			ps.setString(1,area);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				//(userType, username, password, firstName, lastName, id, email, phoneNumber, area)
				user = new User(UserType.AREA_MANAGER, rs.getString(2),
						rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6),  rs.getString(7),  rs.getString(8), area);
				Customer customerInfo = fetchCustomerInfo(user);
				user.setCustomerInfo(customerInfo);
			}
			return user;
		} catch (SQLException e1) {
			return null;
		}finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public ArrayList<User> fetchAllUsersByRole(UserType userType){
		ArrayList<User> usersList = new ArrayList<>();
		User user = null;
		PreparedStatement ps= con.getPreparedStatement("SELECT * FROM users WHERE userType = ?"); 
		try {
			ps.setString(1,userType.toString());
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				//(userType, username, password, firstName, lastName, id, email, phoneNumber, area)
				user = new User(userType, rs.getString(2),
						rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6),  rs.getString(7),  rs.getString(8),  rs.getString(9));
				Customer customerInfo = fetchCustomerInfo(user);
				user.setCustomerInfo(customerInfo);
				usersList.add(user);				
			}
			
			return usersList.size() != 0 ? usersList : null;
		} catch (SQLException e1) {
			return null;
		}finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	/**
	 * Creates a new user in the database.
	 *
	 * @return `true` if the user was successfully created, `false` if an error occurred.
	 * UserType userType, String username, String password, String firstName,
			String lastName, String id, String email, String phoneNumber, String area
	 */
	public boolean createUser(User user){
		String query="INSERT INTO ekrut.users"
				+ " (userType, username, password, firstName, lastName, id, email, phoneNumber, area)"
				+ " VALUES (?,?,?,?,?,?,?,?,?)";
		PreparedStatement ps= con.getPreparedStatement(query); 
		try {
			ps.setString(1,user.getUserType().toString());
			ps.setString(2,user.getUsername());
			ps.setString(3,user.getPassword());
			ps.setString(4,user.getFirstName());
			ps.setString(5,user.getLastName());
			ps.setString(6,user.getId());
			ps.setString(7,user.getEmail());
			ps.setString(8,user.getPhoneNumber());
			ps.setString(9,user.getArea());
			return 1==ps.executeUpdate();
		} catch (SQLException e1) {
			return false;
		}finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

