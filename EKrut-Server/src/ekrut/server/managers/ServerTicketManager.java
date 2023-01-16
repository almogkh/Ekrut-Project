package ekrut.server.managers;

import java.util.ArrayList;

import ekrut.entity.InventoryItem;
import ekrut.entity.Item;
import ekrut.entity.Ticket;
import ekrut.entity.TicketStatus;
import ekrut.entity.User;
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
 */

public class ServerTicketManager extends AbstractServerManager<TicketRequest, TicketResponse> {

	private TicketDAO ticketDAO;
	private ItemDAO itemDAO;
	private InventoryItemDAO inventoryItemDAO;
	
	/**
     * Constructs a new ServerTicketManager object with the given DBController.
     *
     * @param con the DBController to use for database operations
     */
	public ServerTicketManager(DBController con) {
		super(TicketRequest.class, new TicketResponse(ResultType.UNKNOWN_ERROR));
		ticketDAO = new TicketDAO(con);
		itemDAO = new ItemDAO(con);
		inventoryItemDAO = new InventoryItemDAO(con);
	}
	
	@Override
	protected TicketResponse handleRequest(TicketRequest request, User user) {
		switch (request.getAction()) {
		case CREATE:
			return createTicket(request);
		case UPDATE_STATUS:
			return updateTicketStatus(request);
		case FETCH_BY_AREA:
			return fetchTicketsByArea(request);
		case FETCH_BY_USERNAME:
			return fetchTicketsByUsername(request);
		default:
			return new TicketResponse(ResultType.UNKNOWN_ERROR);
		}
	}
	
	/**
	 * Creates a new ticket using the information provided in the TicketRequest.
	 * 
	 * @param ticketRequest the request containing the information for the ticket to be created
	 * @return a TicketResponse indicating the result of the operation
	 */
	private TicketResponse createTicket(TicketRequest ticketRequest) {
		if (ticketRequest == null) {
			return new TicketResponse(ResultType.INVALID_INPUT);
		}
		//get ticket information from ticket request
		String ekrutLocation = ticketRequest.getEkrutLocation();
		int itemID= ticketRequest.getItemID();
		
		//get area by ekrutLocation
		String area = ticketDAO.fetchAreaByEkrutLocation(ekrutLocation);
		
		//get item details 
		Item thisItem=itemDAO.fetchItem(itemID);
		String itemName = thisItem.getItemName();
		
		//get ekrutLocation's threshold
		InventoryItem thisInventoryItem = inventoryItemDAO.fetchInventoryItem(itemID,ekrutLocation);
		int threshold = thisInventoryItem.getItemThreshold();
		
		//get username name of the operation worker
		String username =ticketRequest.getUsername();
			
		//build new ticket to create 
		Ticket newTicket = new Ticket(null,TicketStatus.IN_PROGRESS,ekrutLocation,area,threshold,itemID,itemName,username);
		
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
	 */
	private TicketResponse updateTicketStatus(TicketRequest ticketRequest) {
		if (ticketRequest == null) {
			return new TicketResponse(ResultType.INVALID_INPUT);
		}
		//unpack ticketID that the client requests to update
		int ticketID = ticketRequest.getTicketId();
		TicketStatus status = ticketRequest.getStatus();
		
		//ticket's status update can only turn from IN_PROGRESS to DONE
		if(!ticketDAO.updateTicketStatus(ticketID, status)) {
			return new TicketResponse(ResultType.UNKNOWN_ERROR);
		}
		
		return new TicketResponse(ResultType.OK);
	}
	
	
	/**
	 * Fetches tickets from the database based on the area specified in the given ticket request.
	 *
	 * @param ticketRequest the ticket request containing the location to search for
	 * @return a TicketResponse object with the result of the operation and the fetched tickets, if found
	 */
	
	private TicketResponse fetchTicketsByArea(TicketRequest ticketRequest) {
		//if ticketRequest is null throw NullPointerException
		if (ticketRequest == null) {
			return new TicketResponse(ResultType.INVALID_INPUT);
		}
		//get ticket Ekrut location from ticket request
		String ticketArea = ticketRequest.getArea();
		
		//fetch tickets by given location and save them in ArrayList
		ArrayList<Ticket> ticketsByArea = ticketDAO.fetchTicketsByArea(ticketArea);
		
		//if the ArrayList is empty , return ticket response=NOT_FOUND
		if (ticketsByArea==null) {
			return new TicketResponse(ResultType.NOT_FOUND);
		}
		
		return new TicketResponse(ResultType.OK,ticketsByArea);
		
	}
	
	
	/**
	 * Fetches a list of tickets that are associated with the specified username.
	 *
	 * @param ticketRequest The request containing the username to search for.
	 * @return A response with a list of tickets that are associated with the specified username, if found
	 */
	private TicketResponse fetchTicketsByUsername(TicketRequest ticketRequest) {
		//if ticketRequest is null throw NullPointerException
		if (ticketRequest == null) {
			return new TicketResponse(ResultType.INVALID_INPUT);
		}
		//get username of the operation worker from ticket request
		String username=ticketRequest.getUsername();
		
		//fetch tickets by given username and save them in ArrayList
		ArrayList<Ticket> ticketsByUsername = ticketDAO.fetchTicketsByUsername(username);
		
		//if the ArrayList is empty , return ticket response=NOT_FOUND
		if (ticketsByUsername==null) {
			return new TicketResponse(ResultType.NOT_FOUND);
		}
		
		return new TicketResponse(ResultType.OK,ticketsByUsername);
		
	}
	
}
