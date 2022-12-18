package ekrut.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.cj.MysqlType;

import ekrut.entity.Ticket;



//Noy 
//need to finish - createTicket 

public class TicketDAO {
	
	private DBController con;
	private TicketDAO ticketDAO;
	
	//Constructor - provides connection con to a new TicketDAO so we can use DBcontroller
	public TicketDAO(DBController con) {
		this.con = con;
		ticketDAO=new TicketDAO(con);
	}
	
	
	/** NOT FINISHED - NEED TO CONTINUE 
	 * Create new ticket line in DB and set its details
	 * @param ticket - the ticket that we want to add to DB
	 * @return true if the addition succeeded , false if failed
	 */
	public boolean createTicket(Ticket ticket) {
		con.beginTransaction();
		PreparedStatement ps = con.getPreparedStatement("INSERT INTO tickets " +
                "(status,location) " + "VALUES(?,?)", true);
		try {
			ps.setInt(1, ticket.getTicketId());
			ps.setString(2, ticket.getStatus());
			ps.setString(3, ticket.getEkrutLocation());
			
			// need to understand the syntex
			
			
			
			
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
	 * fetch a ticket from DB, by given ticketID
	 * @param ticketID - the ID of the ticket we want to fetch
	 * @return  ticket with the given ticketID , else NULL
	 */
	public Ticket fetchTicket(int ticketID) {
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM tickets WHERE ticketID = ?;");
		try {
			ps.setInt(1, ticketID);
			ResultSet rs = con.executeQuery(ps);
			if(rs.next()) { 
				return new Ticket(rs.getInt(1), rs.getString(2), rs.getString(3)); 
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
	 * fetch a list of tickets from DB, by given location
	 * @param Ekrut location
	 * @return list of all tickets from this Ekrut location
	 */
	public ArrayList<Ticket> fetchTicketsByLocation(String location) {
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM tickets WHERE location = ?;");
		try {
			ArrayList<Ticket> ticketsByLocation = new ArrayList<>();
			ps.setString(1, location);
			ResultSet rs = con.executeQuery(ps);
			while(rs.next()) { 
				Ticket ticket = ticketDAO.fetchTicket(rs.getInt(1));
				if (ticket != null)
					ticketsByLocation.add(new Ticket(rs.getInt(1), rs.getString(2), rs.getString(3)));
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
	 * Update ticket status in DB
	 * @param ticketID - the ID of the ticket that we want to update its status
	 * @param status - the status we want to update to 
	 * @return true if the update succeeded , false if failed 
	 */
	public boolean updateTicketStatus(int ticketID, String status) {
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
