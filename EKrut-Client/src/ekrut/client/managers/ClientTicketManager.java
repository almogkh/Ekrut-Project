package ekrut.client.managers;

import java.util.ArrayList;

import ekrut.client.EKrutClient;
import ekrut.entity.Ticket;
import ekrut.entity.TicketStatus;
import ekrut.net.ResultType;
import ekrut.net.TicketRequest;
import ekrut.net.TicketRequestType;
import ekrut.net.TicketResponse;

//need to add- sendRequest


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
     * @return the result of the update operation
     * @throws NullPointerException if ticket is null
     * @throws RuntimeException if there is an error updating the ticket's status
     */
	
	public ResultType updateTicketStatus(Ticket ticket, TicketStatus status) {
		if (ticket == null) {
			return ResultType.INVALID_INPUT;
		}
		TicketRequest ticketRequest = new TicketRequest(
				TicketRequestType.UPDATE_STATUS, ticket.getTicketId(), status); 
		TicketResponse ticketResponse = sendRequest(ticketRequest);
		
		return ticketResponse.getResultType();
	}
	
	
	
	/**
	 * Creates a ticket for a given item at a specified location.
	 *
	 * @param ekrutLocation the location where the ticket is to be created
	 * @param itemID the ID of the item for which the ticket is to be created
	 * @return the result of the ticket creation request
	 * @throws Exception if an error occurs while sending the ticket creation request
	 */
	public ResultType CreateTicket(String ekrutLocation , int itemID, String username) throws Exception{
		if (itemID==0) {
			throw new IllegalArgumentException("provided null itemID");
		}
		
		TicketRequest ticketRequest = new TicketRequest(TicketRequestType.CREATE,ekrutLocation,itemID,username); 
		TicketResponse ticketResponse = sendRequest(ticketRequest);
		
		return ticketResponse.getResultType();
	}
	
	
	/**
     * Fetches a list of tickets by area.
     * 
     * @return a list of tickets
     * @throws Exception if there is an error fetching the tickets
     */
	
	public ArrayList<Ticket> fetchTicketsByArea(String area) throws Exception{
		// WAIT FOR USER MANAGMENT TO BE FIXED
		
		TicketRequest ticketRequest = new TicketRequest(TicketRequestType.FETCH_BY_AREA,area); 
		TicketResponse ticketResponse = sendRequest(ticketRequest);
		
		return ticketResponse.getTicketsList();
		
	}
	
	
	/**
     * Fetches a list of tickets by username.
     * 
     * @return a list of tickets
     * @throws Exception if there is an error fetching the tickets
     */
	public ArrayList<Ticket> fetchTicketsByUsername(String username) throws Exception{
	
		TicketRequest ticketRequest = new TicketRequest(TicketRequestType.FETCH_BY_USERNAME,username); 
		TicketResponse ticketResponse = sendRequest(ticketRequest);
		
		return ticketResponse.getTicketsList();
	}
}
