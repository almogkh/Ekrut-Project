package ekrut.server.managers;

import java.util.ArrayList;

import ekrut.entity.InventoryItem;
import ekrut.entity.Item;
import ekrut.entity.Ticket;
import ekrut.entity.TicketStatus;
import ekrut.net.ResultType;
import ekrut.net.TicketRequest;
import ekrut.net.TicketResponse;
import ekrut.server.db.DBController;
import ekrut.server.db.InventoryItemDAO;
import ekrut.server.db.ItemDAO;
import ekrut.server.db.TicketDAO;

/**
 * Manages tickets on the server side.
 * Uses a TicketDAO for database operations.
 * 
 * @author Noy Malka
 * 
 */

public class ServerTicketManager {

	private TicketDAO ticketDAO;
	private ItemDAO itemDAO;
	private InventoryItemDAO inventoryItemDAO;
	
	
	/**
     * Constructs a new ServerTicketManager object with the given DBController.
     *
     * @param con the DBController to use for database operations
     */
	public ServerTicketManager(DBController con) {
		ticketDAO = new TicketDAO(con);
		itemDAO = new ItemDAO(con);
		inventoryItemDAO =new InventoryItemDAO(con);
	}
	
	
	/**
	 * Creates a new ticket using the information provided in the TicketRequest.
	 * 
	 * @param ticketRequest the request containing the information for the ticket to be created
	 * @return a TicketResponse indicating the result of the operation
	 * @throws NullPointerException if the ticketRequest is null
	 */
	public TicketResponse CreateTicket(TicketRequest ticketRequest) throws NullPointerException {
		if (ticketRequest==null) {
			throw new NullPointerException("null ticketRequest");
		}
		//get ticket information from ticket request
		String ekrutLocation =ticketRequest.getEkrutLocation();
		int itemID= ticketRequest.getItemID();
		
		//get item details 
		Item thisItem=itemDAO.fetchItem(itemID);
		String itemName = thisItem.getItemName();
		
		//get ekrutLocation's threshold
		InventoryItem thisInventoryItem = inventoryItemDAO.fetchInventoryItem(itemID,ekrutLocation);
		int threshold = thisInventoryItem.getItemThreshold();
		
		//build new ticket to create 
		Ticket newTicket = new Ticket(null,TicketStatus.IN_PROGRESS,ekrutLocation,threshold,itemID,itemName);
		
		if(!ticketDAO.createTicket(newTicket)) {
			return new TicketResponse(ResultType.UNKNOWN_ERROR);
		}
		
		return new TicketResponse(ResultType.OK);
	}
	
	
	/**
	 * Updates the status of a ticket.
	 * 
	 * @param ticketRequest the request containing the ticket ID and new status
	 * @return the response to the request, indicating the result of the update operation
	 * @throws NullPointerException if ticketRequest is null
	 */
	public TicketResponse updateTicketStatus(TicketRequest ticketRequest) throws NullPointerException {
		if (ticketRequest==null) {
			throw new NullPointerException("null ticketRequest");
		}
		//unpack ticketID that the client requests to update
		int ticketID =ticketRequest.getTicketId();
		
		//ticket's status update can only turn from IN_PROGRESS to DONE
		if(!ticketDAO.updateTicketStatus(ticketID, TicketStatus.DONE)) {
			return new TicketResponse(ResultType.UNKNOWN_ERROR);
		}
		
		return new TicketResponse(ResultType.OK);
	}
	
	
	/**
	 * Fetches tickets from the database based on the location specified in the given ticket request.
	 *
	 * @param ticketRequest the ticket request containing the location to search for
	 * @return a TicketResponse object with the result of the operation and the fetched tickets, if found
	 * @throws NullPointerException if the ticketRequest parameter is null
	 */
	
	public TicketResponse fetchTickets(TicketRequest ticketRequest) throws NullPointerException {
		//if ticketRequest is null throw NullPointerException
		if (ticketRequest==null) {
			throw new NullPointerException("null ticketRequest");
		}
		//get ticket Ekrut location from ticket request
		String ticketEkrutLocation=ticketRequest.getEkrutLocation();
		
		/*
		 * Question - I think that I need to add another case of ResultType in case the fetch method didnt successed(Unknown Error)
		 * but I'm not sure if I should activate fetchTicketsByLocation() method again. 
		 * Almog, you probably have a better idea ...
		*/
		
		//fetch tickets by given location and save them in ArrayList
		ArrayList<Ticket> ticketsByLocation = ticketDAO.fetchTicketsByLocation(ticketEkrutLocation);
		
		//if the ArrayList is empty , return ticket response=NOT_FOUND
		if (ticketsByLocation==null) {
			return new TicketResponse(ResultType.NOT_FOUND);
		}
		
		return new TicketResponse(ResultType.OK,ticketsByLocation);
		
	}
	
}
