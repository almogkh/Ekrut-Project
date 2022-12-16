package ekrut.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database controller that manages the connection to a MySQL database.
 * 
 * @author Almog Khaikin
 */
public class DBController {
	
	private Connection conn;
	private final String url, username, password;
	
	/**
	 * Constructs a new DBController with the specified parameters. Does not connect
	 * to the database.
	 * 
	 * @param url      the URL of the database
	 * @param username the username to use for the login
	 * @param password the password to use for the login
	 */
	public DBController(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	/**
	 * Connects to the database.
	 * 
	 * @return true when the connection succeeds.
	 * 	       false otherwise.
	 */
	public boolean connect() {
		if (conn != null)
			return true;
		try {
			conn = DriverManager.getConnection(url, username, password);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	/**
	 * Closes the connection to the database.
	 */
	public void close() {
		try {
			conn.close();
			conn = null;
		} catch (SQLException e) {}
	}
	
	/**
	 * Begins a new database transaction. Callers <b>must</b> call {@link #commitTransaction()} or
	 * {@link #abortTransaction()} before continuing.
	 */
	public void beginTransaction() {
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Commits an in-progress transaction. Can only be called after a call to {@link #beginTransaction()}.
	 */
	public void commitTransaction() {
		try {
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Aborts an in-progress transaction. Can only be called after a call to {@link #beginTransaction()}.
	 */
	public void abortTransaction() {
		try {
			conn.rollback();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Retrieves a PreparedStatement that can be used to send a query to the DB.
	 * Users must manually call {@link PreparedStatement#close() close()} when they
	 * are done with it.
	 * 
	 * @param query        the SQL query to use as a template for the statement
	 * @param autoGenKeys  whether the statement should support retrieving auto
	 *                     generated keys
	 * @return             the prepared statement representing the SQL query
	 */
	public PreparedStatement getPreparedStatement(String query, boolean autoGenKeys) {
		try {
			return conn.prepareStatement(query,
                                         autoGenKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retrieves a PreparedStatement that can be used to send a query to the DB.
	 * Users must manually call {@link PreparedStatement#close() close()} when they
	 * are done with it.
	 * 
	 * @param query the SQL query to use as a template for the statement
	 * @return the prepared statement representing the SQL query
	 */
	public PreparedStatement getPreparedStatement(String query) {
		return getPreparedStatement(query, false);
	}

	/**
	 * Executes a PreparedStatement that is intended to return a result.
	 * 
	 * @param p the prepared statement to execute
	 * @return  ResultSet representing the result of the query
	 */
	public ResultSet executeQuery(PreparedStatement p) {
		try {
			ResultSet result = p.executeQuery();
			return result;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Executes a PreparedStatement that is intended to update the database.
	 * 
	 * @param p the prepared statement to execute
	 * @return  The number of rows in the database that were updated
	 */
	public int executeUpdate(PreparedStatement p) {
		try {
			int result = p.executeUpdate();
			return result;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
