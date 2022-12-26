package ekrut.net;

import java.io.Serializable;

import ekrut.entity.TicketStatus;

public class TicketRequest implements Serializable{
	
	private static final long serialVersionUID = 7134346281040324590L;
	private TicketRequestType action;
	private int ticketId;
	private TicketStatus status;
	private String ekrutLocation;
	private int threshold;
	private int itemID;
	private String itemName;
	
	
	public TicketRequest(TicketRequestType action, int ticketId, TicketStatus status, String ekrutLocation,
			int threshold, int itemID, String itemName) {
		this.action = action;
		this.ticketId = ticketId;
		this.status = status;
		this.ekrutLocation = ekrutLocation;
		this.threshold = threshold;
		this.itemID = itemID;
		this.itemName = itemName;
	}
	
	public TicketRequest(TicketRequestType action, String ekrutLocation) {
		this.action = action;
		this.ekrutLocation = ekrutLocation;
	}
	
	public TicketRequest(TicketRequestType action, int ticketId) {
		this.action = action;
		this.ticketId = ticketId;
	}

	
	public TicketRequest(TicketRequestType action, String ekrutLocation, int itemID) {
		this.action = action;
		this.ekrutLocation = ekrutLocation;
		this.itemID = itemID;
	}

	public TicketRequest(TicketRequestType action) {
		this.action = action;
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

	public int getThreshold() {
		return threshold;
	}

	public int getItemID() {
		return itemID;
	}

	public String getItemName() {
		return itemName;
	}
	
}
