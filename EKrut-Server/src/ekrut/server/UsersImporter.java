package ekrut.server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ekrut.entity.Customer;
import ekrut.entity.User;
import ekrut.entity.UserType;
import ekrut.server.db.DBController;

/**
 * This class handles performing the one-time import of user data from the external system.
 * 
 * @author Almog Khaikin
 */
public final class UsersImporter {

	// Ensure no instances of the class can be created
	private UsersImporter() {
	}
	
	/**
	 * Performs the import operation.
	 * 
	 * @param con the database connection through which the method will perform its operations
	 * @return    true if the import occurred and succeeded, false otherwise
	 */
	public static boolean importUsers(DBController con) {
		try (PreparedStatement ps1 = con.getPreparedStatement("SELECT username FROM users LIMIT 1");
			 PreparedStatement ps2 = con.getPreparedStatement("SELECT * FROM external_user_data");
			 PreparedStatement ps3 = con.getPreparedStatement("INSERT INTO users VALUES(?,?,?,?,?,?,?,?,?)");
			 PreparedStatement ps4 = con.getPreparedStatement("INSERT INTO customers VALUES(?,?,?,?)")) {
			
			con.beginTransaction();
			ResultSet rs1 = ps1.executeQuery();
			// Did we already import users?
			if (rs1.next()) {
				con.abortTransaction();
				return false;
			}
			
			// Get the user information from the external system
			ResultSet rs2 = ps2.executeQuery();
			while (rs2.next()) {
				User user = new User(UserType.valueOf(rs2.getString(1)),rs2.getString(2),
									 rs2.getString(3), rs2.getString(4),rs2.getString(5),
									 rs2.getString(6), rs2.getString(7),
									 rs2.getString(8), rs2.getString(9));
				Customer customer = new Customer(rs2.getInt(10), user.getUsername(), rs2.getBoolean(11),
										rs2.getString(12), rs2.getBoolean(13));
				user.setCustomerInfo(customer);
				
				ps3.setString(1, user.getUserType().toString());
				ps3.setString(2, user.getUsername());
				ps3.setString(3, user.getPassword());
				ps3.setString(4, user.getFirstName());
				ps3.setString(5, user.getLastName());
				ps3.setString(6, user.getId());
				ps3.setString(7, user.getEmail());
				ps3.setString(8, user.getPhoneNumber());
				ps3.setString(9, user.getArea());
				ps3.addBatch();
				
				// Customers have additional information
				if (rs2.getString(12) != null) {
					ps4.setString(1, user.getUsername());
					ps4.setInt(2, rs2.getInt(10));
					ps4.setBoolean(3, rs2.getBoolean(11));
					ps4.setString(4, rs2.getString(12));
					ps4.setBoolean(5, rs2.getBoolean(13));
					ps4.addBatch();
				}
			}
			
			int[] rs3 = ps3.executeBatch();
			for (int i : rs3) {
				if (i != 1) {
					con.abortTransaction();
					return false;
				}
			}
			
			int[] rs4 = ps4.executeBatch();
			for (int i : rs4) {
				if (i != 1) {
					con.abortTransaction();
					return false;
				}
			}
			
			con.commitTransaction();
			return true;
			
		} catch (SQLException e) {
			con.abortTransaction();
			return false;
		}
	}
}
