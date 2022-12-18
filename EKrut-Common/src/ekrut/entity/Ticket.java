package ekrut.entity;

import java.io.Serializable;



//Noy  



public class Ticket implements Serializable{
	
	private static final long serialVersionUID = -1064172186085363642L;
	private int ticketId;
	private String status;
	private String ekrutLocation;
	
	
	public Ticket(int ticketId, String status, String ekrutLocation) {
		super();
		this.ticketId = ticketId;
		this.status = status;
		this.ekrutLocation = ekrutLocation;
	}
	
	public int getTicketId() {
		return ticketId;
	}


	public void setTicketId(int ticketId) {
		this.ticketId = ticketId;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getEkrutLocation() {
		return ekrutLocation;
	}


	public void setEkrutLocation(String ekrutLocation) {
		this.ekrutLocation = ekrutLocation;
	}


	
}
