package ekrut.net;

import java.io.Serializable;

import ekrut.entity.TicketStatus;

public class TicketRequest implements Serializable{
	
	private static final long serialVersionUID = 7134346281040324590L;
	private TicketRequestType action;
	private int ticketId;
	private TicketStatus status;
	private String ekrutLocation;
	private String area;
	private int threshold;
	private int itemID;
	private String itemName;
	private String username;
	
	
	//FETCH 
	public TicketRequest(TicketRequestType action, String areaOrUsername) {
		this.action = action;
		if (action.equals(TicketRequestType.FETCH_BY_AREA)) {
			this.area = areaOrUsername;
		} 
		else
			this.username=areaOrUsername;
		} 
	
	//UPDATE
	public TicketRequest(TicketRequestType action, int ticketId, int itemId, String ekrutLocation, TicketStatus status) {
		this.action = action;
		this.ticketId = ticketId;
		this.itemID = itemId;
		this.status = status;
		this.ekrutLocation = ekrutLocation;
	}

	//CREATE 
	public TicketRequest(TicketRequestType action, String ekrutLocation, int itemID, String username) {
		this.action = action;
		this.ekrutLocation = ekrutLocation;
		this.itemID = itemID;
		this.username = username;
		
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
	
	public String getArea() {
		return area;
	}

	public int getThreshold() {
		return threshold;
	}

	public int getItemID() {
		return itemID;
	}

	public String getItemName() {
		return itemName;
	}
	
	public String getUsername() {
		return username;
	}
	
}
