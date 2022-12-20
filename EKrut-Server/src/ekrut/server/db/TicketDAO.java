package ekrut.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.cj.MysqlType;

import ekrut.entity.Ticket;
import ekrut.entity.TicketStatus;


/**
 * A Data Access Object (DAO) class that provides CRU (Create, Read, Update) 
 * operations for the {@link Ticket} entity in a MySQL database.
 * The DAO requires a {@link DBController} object to establish a connection to the database.
 * 
 * @author Noy Malka
 */

public class TicketDAO {
	
	private DBController con;
	
	
	/**
	 * Constructs a new TicketDAO object and initializes the connection to the database.
	 * 
	 * @param con a DBController object used to establish a connection to the database
	 */
	public TicketDAO(DBController con) {
		this.con = con;
	}

	
	/**
	 * Creates a new ticket in the database.
	 * 
	 * @param ticket the Ticket object to be created in the database
	 * @return true if the ticket was successfully created, false otherwise
	 * @throws RuntimeException if there was an error executing the SQL statement
	 */
	
	
	public boolean createTicket(Ticket ticket) {
		con.beginTransaction();
		PreparedStatement ps = con.getPreparedStatement("INSERT INTO tickets " +
	            "(ticketID,status,location,threshold,itemID,itemName) " + "VALUES(?,?,?,?,?,?)");
		try {
			ps.setInt(1, ticket.getTicketId());
			ps.setString(2, ticket.getStatus().toString());
			ps.setString(3, ticket.getEkrutLocation());
			ps.setInt(4, ticket.getThreshold());
			ps.setInt(5, ticket.getItemID());
			ps.setString(6, ticket.getItemName());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return true;
	}

	
	/**
	 * Retrieves a ticket from the database based on its ID.
	 * 
	 * @param ticketID the ID of the ticket to be retrieved
	 * @return a Ticket object if the ticket was found, null otherwise
	 * @throws RuntimeException if there was an error executing the SQL statement
	 */
	public Ticket fetchTicket(int ticketID) {
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM tickets WHERE ticketID = ?;");
		try {
			ps.setInt(1, ticketID);
			ResultSet rs = con.executeQuery(ps);
			if(rs.next()) { 
				return new Ticket(rs.getInt(1),TicketStatus.valueOf(rs.getString(2)), rs.getString(3),rs.getInt(4),rs.getInt(5),rs.getString(6)); 
			}
			return null;
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
	 * Retrieves a list of tickets from the database based on their location.
	 * 
	 * @param location the location of the tickets to be retrieved
	 * @return a list of Ticket objects if tickets were found, null otherwise
	 * @throws RuntimeException if there was an error executing the SQL statement
	 */
	
	
	public ArrayList<Ticket> fetchTicketsByLocation(String location) {
		ArrayList<Ticket> ticketsByLocation = new ArrayList<>();
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM tickets WHERE location = ?;");
		try {
			ps.setString(1, location);
			ResultSet rs = con.executeQuery(ps);
			while(rs.next()) { 
				Ticket ticket = new Ticket(rs.getInt(1),TicketStatus.valueOf(rs.getString(2)), rs.getString(3),rs.getInt(4),rs.getInt(5),rs.getString(6));
				ticketsByLocation.add(ticket);
			}
			return ticketsByLocation.size() != 0? ticketsByLocation : null;
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
	* Updates the status of a ticket in the database with the specified ticket ID.
	*
	* @param ticketID The ID of the ticket to be updated.
	* @param status The new status for the ticket.
	* @return  true if the ticket was successfully updated, false otherwise.
	* @throws RuntimeException if there was an error executing the SQL statement
	*/
	public boolean updateTicketStatus(int ticketID, TicketStatus status) {
			PreparedStatement ps = con.getPreparedStatement("UPDATE tickets SET status = ? WHERE ticketID = ?");
			
			try {
				ps.setString(1, status.toString());
				ps.setInt(2, ticketID);
				
				if (ps.executeUpdate() != 1)
					return false;
				
			} catch (SQLException e) {
				throw new RuntimeException(e);
				
			} finally {
				try {
					ps.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
			
			return true;
		}

	
	
}
