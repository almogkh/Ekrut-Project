package ekrut.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.cj.protocol.Resultset;

import ekrut.entity.User;
import ekrut.entity.UserType;
import ekrut.server.db.DBController;

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
			if(rs.next()) 
				user = new User(rs.getString(1), rs.getString(2), UserType.valueOf(rs.getString(3)), rs.getString(4));
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
	 * 	This method retrieves a user with the role of AREA_MANAGER from the database based on the specified area.
	 * 
	 * 	@param area The area for which the AREA_MANAGER is being retrieved.
	 * 	@return The AREA_MANAGER user for the specified area, or null if no such user exists in the database.
	*/
	public User fetchManagerByArea(String area){
		User user = null;
		PreparedStatement ps= con.getPreparedStatement("SELECT * FROM users WHERE role = 'AREA_MANAGER' AND area = ?"); 
		try {
			ps.setString(1,area);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) 
				user = new User(rs.getString(1), rs.getString(2), UserType.valueOf(rs.getString(3)), rs.getString(4));
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
	 * Creates a new user in the database.
	 *
	 * @param role The role of the user.
	 * @param username The username of the user.
	 * @param password The password of the user.
	 * @param firstName The first name of the user.
	 * @param lastName The last name of the user.
	 * @param id The ID of the user.
	 * @param email The email of the user.
	 * @param phoneNumber The phone number of the user.
	 * @param area The area of the user.
	 * @return `true` if the user was successfully created, `false` if an error occurred.
	 */
	public boolean createUser
	(String role, String username, String password, String firstName,
			String lastName, String id, String email, String phoneNumber, String area){
		String query="INSERT INTO ekrut.users"
				+ " (role, username, password, firstName, lastName, id, email, phoneNumber, area)"
				+ " VALUES (?,?,?,?,?,?,?,?,?)";
		PreparedStatement ps= con.getPreparedStatement(query); 
		try {
			ps.setString(1,role);
			ps.setString(2,username);
			ps.setString(3,password);
			ps.setString(4,firstName);
			ps.setString(5,lastName);
			ps.setString(6,id);
			ps.setString(7,email);
			ps.setString(8,phoneNumber);
			ps.setString(9,area);
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

