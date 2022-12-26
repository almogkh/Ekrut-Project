package ekrut.client.managers;

import java.util.ArrayList;

import ekrut.entity.Ticket;
import ekrut.net.ResultType;
import ekrut.net.TicketRequest;
import ekrut.net.TicketRequestType;
import ekrut.net.TicketResponse;
import ekrut.net.UserRequest;
import ekrut.net.UserResponse;

//need to add- sendRequest


/**
 * The ClientTicketManager class is responsible for managing tickets for a client.
 * This includes updating the status of a ticket and fetching tickets.
 * 
 * @author Noy Malka
 */

public class ClientTicketManager {

	
	/**
     * Updates the status of a given ticket.
     * 
     * @param ticket the ticket to update
     * @return the result of the update operation
     * @throws NullPointerException if ticket is null
     * @throws RuntimeException if there is an error updating the ticket's status
     */
	
	public ResultType updateTicketStatus(Ticket ticket) throws Exception{
		if (ticket==null) {
			throw new IllegalArgumentException("provided null ticket");
		}
		
		TicketRequest ticketRequest = new TicketRequest(TicketRequestType.UPDATE_STATUS,ticket.getTicketId()); 
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
	public ResultType CreateTicket(String ekrutLocation , int itemID) throws Exception{
		if (itemID==0) {
			throw new IllegalArgumentException("provided null itemID");
		}
		
		TicketRequest ticketRequest = new TicketRequest(TicketRequestType.CREATE,ekrutLocation,itemID); 
		TicketResponse ticketResponse = sendRequest(ticketRequest);
		
		return ticketResponse.getResultType();
	}
	
	/**
     * Fetches a list of tickets.
     * 
     * @return a list of tickets
     * @throws Exception if there is an error fetching the tickets
     */
	
	public ArrayList<Ticket> fetchTickets(String ekrutLocation) throws Exception{
		// WAIT FOR USER MANAGMENT TO BE FIXED
		//question - what do we need to get? 
		//question - we will do fetch by what? location? item? maybe cases of each one?
		TicketRequest ticketRequest = new TicketRequest(TicketRequestType.FETCH,ekrutLocation); 
		TicketResponse ticketResponse = sendRequest(ticketRequest);
		
		return ticketResponse.getTicketsList();
		
	}
	
	
	
	/**
     * Sends a ticket request and returns the response.
     * 
     * @param ticketRequest the request to send
     * @return the response to the request
     */

	private TicketResponse sendRequest(TicketRequest ticketRequest) {
		// TODO Auto-generated method stub
		return null;
	}
}
