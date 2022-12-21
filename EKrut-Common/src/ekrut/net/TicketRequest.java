package ekrut.net;

import java.io.Serializable;

import ekrut.entity.TicketStatus;


//initial TicketRequest 


public class TicketRequest implements Serializable{
	private static final long serialVersionUID = 7134346281040324590L;
	private TicketRequestType action;
	private int ticketId;
	private TicketStatus status;
	private String ekrutLocation;
	
	
	public TicketRequest(TicketRequestType action, String ekrutLocation) {
		this.action = action;
		this.ekrutLocation = ekrutLocation;
	}

	//Do we need also ticketID? 
	public TicketRequest(TicketRequestType action, TicketStatus status) {
		this.action = action;
		this.status = status;
	}

	public TicketRequestType getAction() {
		return action;
	}

	public int getTicketId() {
		return ticketId;
	}
	
	public TicketStatus getStatus() {
		return status;
	}

	public String getEkrutLocation() {
		return ekrutLocation;
	}

	
	
	
	
	
	
	
	
	
	
}
