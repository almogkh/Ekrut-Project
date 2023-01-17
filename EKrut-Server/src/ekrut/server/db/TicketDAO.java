package ekrut.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
		PreparedStatement ps = con.getPreparedStatement("INSERT INTO tickets "
				+ "(status, area, ekrutLocation, itemID, itemName, threshold, username)"
				+ "VALUES(?,?,?,?,?,?,?)",true);
		try {
			ps.setString(1, ticket.getStatus().toString());
			ps.setString(2, ticket.getArea());
			ps.setString(3, ticket.getEkrutLocation());
			ps.setInt(4, ticket.getItemID());
			ps.setString(5, ticket.getItemName());
			ps.setInt(6, ticket.getThreshold());
			ps.setString(7, ticket.getUsername());
			int res = ps.executeUpdate();
			if (res!=1) {
				con.abortTransaction();
				return false;
			}
			con.commitTransaction();
		} catch (SQLException e) {
			con.abortTransaction();
			return false;
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
	 * Retrieves a list of tickets from the database based on their area.
	 * 
	 * @param area the area of the tickets to be retrieved
	 * @return a list of Ticket objects if tickets were found, null otherwise
	 */
	public ArrayList<Ticket> fetchTicketsByArea(String area) {
		ArrayList<Ticket> ticketsByArea = new ArrayList<>();
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM tickets WHERE status = 'IN_PROGRESS' AND area = ?;");
		try {
			ps.setString(1, area);
			ResultSet rs = con.executeQuery(ps);
			while(rs.next()) { 
				// ticketID, status, area, ekrutLocation, itemID, itemName, threshold, username
				Ticket ticket = new Ticket(rs.getInt("ticketID"),TicketStatus.valueOf(rs.getString("status")), rs.getString("ekrutLocation"),rs.getString("area"),rs.getInt("threshold"),rs.getInt("itemID"),rs.getString("itemName"),rs.getString("username"));
				ticketsByArea.add(ticket);
			}
			

			
			return ticketsByArea.size() != 0? ticketsByArea : null;
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
	 * Retrieves a list of tickets that are associated with the specified username (operation worker).
	 *
	 * @param username The username of the operation worker to search for.
	 * @return A list of tickets that are associated with the specified username, or null if no tickets are found.
	 */
	public ArrayList<Ticket> fetchTicketsByUsername(String username) {
		ArrayList<Ticket> ticketsByUserId = new ArrayList<>();
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM tickets WHERE status = 'IN_PROGRESS' AND username = ?;");
		try {
			ps.setString(1, username);
			ResultSet rs = con.executeQuery(ps);
			while(rs.next()) { 
				Ticket ticket = new Ticket(
						rs.getInt("ticketID"),
						TicketStatus.valueOf(rs.getString("status")),
						rs.getString("ekrutLocation"),
						rs.getString("area"),
						rs.getInt("threshold"),
						rs.getInt("itemID"),
						rs.getString("itemName"),
						rs.getString("username"));
				ticketsByUserId.add(ticket);
			}
			return ticketsByUserId.size() != 0? ticketsByUserId : null;
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

	
	
	/**
	 * Retrieves an area in which a given ekrutLocation is assigned to.
	 *
	 * @param ekrutLocation a string representing the ekrutLocation in the wanted area.
	 * @return String representing the area that the givan ekrutLocation is assigned to.
	 */
	public String fetchAreaByEkrutLocation(String ekrutLocation){

		PreparedStatement ps1 = con.getPreparedStatement("SELECT area FROM ekrut_machines WHERE ekrutLocation = ?");
		
		try {
			ps1.setString(1, ekrutLocation);
			ResultSet rs1 = con.executeQuery(ps1);
			String area;
			if(!rs1.next()) {
				return null;
			}
			area = rs1.getString("area");
			
			return area ;
			
			}catch (SQLException e) {
				return null;
			} finally { 
				try {
					ps1.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
	}
	
//	/**
//	 * Retrieves a ticket from the database based on its ID.
//	 * 
//	 * @param ticketID the ID of the ticket to be retrieved
//	 * @return a Ticket object if the ticket was found, null otherwise
//	 * @throws RuntimeException if there was an error executing the SQL statement
//	 */
//	public Ticket fetchTicket(int ticketID) {
//		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM tickets WHERE ticketID = ?;");
//		try {
//			ps.setInt(1, ticketID);
//			ResultSet rs = con.executeQuery(ps);
//			if(rs.next()) { 
//				return new Ticket(rs.getInt(1),TicketStatus.valueOf(rs.getString(2)), rs.getString(3),rs.getInt(4),rs.getInt(5),rs.getString(6),rs.getString(7)); 
//			}
//			return null;
//		} catch (SQLException e1) {
//			return null;
//		}finally {
//			try {
//				ps.close();
//			} catch (SQLException e) {
//				throw new RuntimeException(e);
//			}
//		}
//	}

	
	
}
