package ekrut.client.managers;

import java.util.ArrayList;

import ekrut.client.EKrutClient;
import ekrut.entity.Ticket;
import ekrut.entity.TicketStatus;
import ekrut.net.ResultType;
import ekrut.net.TicketRequest;
import ekrut.net.TicketRequestType;
import ekrut.net.TicketResponse;

/**
 * The ClientTicketManager class is responsible for managing tickets for a client.
 * This includes updating the status of a ticket and fetching tickets.
 * 
 * @author Noy Malka
 */

public class ClientTicketManager extends AbstractClientManager<TicketRequest, TicketResponse> {

	public ClientTicketManager(EKrutClient client) {
		super(client, TicketResponse.class);
	}
	
	/**
     * Updates the status of a given ticket.
     * 
     * @param ticket the ticket to update
     * @param status of ticket
     * @return the result of the update operation
     */
	
	public ResultType updateTicketStatus(Ticket ticket, TicketStatus status) {
		if (ticket == null) {
			return ResultType.INVALID_INPUT;
		}
		TicketRequest ticketRequest = new TicketRequest(
				TicketRequestType.UPDATE_STATUS, ticket.getTicketId(), ticket.getItemID(), ticket.getEkrutLocation(), status); 
		TicketResponse ticketResponse = sendRequest(ticketRequest);
		
		return ticketResponse.getResultType();
	}
	
	
	
	/**
	 * Creates a ticket for a given item at a specified location.
	 *
	 * @param ekrutLocation the location where the ticket is to be created.
	 * @param itemID the ID of the item for which the ticket is to be created.
	 * @param username to create.
	 * @return the result of the ticket creation request
	 */
	public ResultType CreateTicket(String ekrutLocation, int itemID, String username) {
		if (itemID == 0) {
			return ResultType.INVALID_INPUT;
		}
		
		TicketRequest ticketRequest = new TicketRequest(TicketRequestType.CREATE, ekrutLocation, itemID, username); 
		TicketResponse ticketResponse = sendRequest(ticketRequest);
		
		return ticketResponse.getResultType();
	}
	
	
	/**
     * Fetches a list of tickets by area.
     * 
     * @param area of tickets.
     * @return ArrayList<Ticket> list of tickets
     */
	
	public ArrayList<Ticket> fetchTicketsByArea(String area) {
		if (area == null)
			return null;
		
		TicketRequest ticketRequest = new TicketRequest(TicketRequestType.FETCH_BY_AREA, area); 
		TicketResponse ticketResponse = sendRequest(ticketRequest);
		
		return ticketResponse.getTicketsList();
		
	}
	
	
	/**
     * Fetches a list of tickets by username.
     * 
     * @param username for fetch ticket by username
     * @return ArrayList<Ticket> a list of tickets
     */
	public ArrayList<Ticket> fetchTicketsByUsername(String username) {
	
		TicketRequest ticketRequest = new TicketRequest(TicketRequestType.FETCH_BY_USERNAME, username); 
		TicketResponse ticketResponse = sendRequest(ticketRequest);
		
		return ticketResponse.getTicketsList();
	}
}
