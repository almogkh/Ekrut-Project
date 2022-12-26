package ekrut.entity;

import java.io.Serializable;



/**
 * 
 * @author Noy Malka
 *
 */

public class Ticket implements Serializable{
	
	private static final long serialVersionUID = -1064172186085363642L;
	private Integer ticketId;
	private TicketStatus status;
	private String ekrutLocation;
	private int threshold;
	private int itemID;
	private String itemName;
	
	
	public Ticket(Integer ticketId, TicketStatus status, String ekrutLocation, int threshold, int itemID, String itemName) {
		this.ticketId = ticketId;
		this.status = status;
		this.ekrutLocation = ekrutLocation;
		this.itemID = itemID;
		this.itemName = itemName;
		this.threshold = threshold;
	}

	public int getTicketId() {
		return ticketId;
	}

	public void setTicketId(int ticketId) {
		this.ticketId = ticketId;
	}

	public TicketStatus getStatus() {
		return status;
	}

	public void setStatus(TicketStatus status) {
		this.status = status;
	}

	public String getEkrutLocation() {
		return ekrutLocation;
	}

	public void setEkrutLocation(String ekrutLocation) {
		this.ekrutLocation = ekrutLocation;
	}

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
	
	
	
}
