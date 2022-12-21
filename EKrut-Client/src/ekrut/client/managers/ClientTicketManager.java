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
			throw new NullPointerException("provided null ticket");
		}
		
		TicketRequest ticketRequest = new TicketRequest(TicketRequestType.UPDATE_STATUS,ticket.getTicketId()); 
		TicketResponse ticketResponse = sendRequest(ticketRequest);
		if (ticketResponse.getResultType().toString()!="OK") {
			throw new RuntimeException("Error updating ticket's status"); //**Need to think whats better then Runtime here
			//maybe just return the ResultType? so it will show the problem (INVALID..)
		}
		
		return ticketResponse.getResultType();
	}
	
	
	
	/**
     * Fetches a list of tickets.
     * 
     * @return a list of tickets
     * @throws Exception if there is an error fetching the tickets
     */
	
	public ArrayList<Ticket> fetchTickets() throws Exception{
		
		//question - what do we need to get? 
		//question - we will do fetch by what? location? item? maybe cases of each one?
		TicketRequest ticketRequest = new TicketRequest(TicketRequestType.FETCH); 
		TicketResponse ticketResponse = sendRequest(ticketRequest);
		
		if (ticketResponse.getResultType().toString()!="OK") {
			throw new Exception("Error fetching tickets"); //**Need to think whats better then RuntimeException here
		}
		
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
