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
	private String area;
	private int threshold;
	private int itemID;
	private String itemName;
	private String username ;
	
	
	public Ticket(Integer ticketId, TicketStatus status, String ekrutLocation,String area,
					int threshold, int itemID, String itemName,String username) {
		
		this.ticketId = ticketId;
		this.status = status;
		this.ekrutLocation = ekrutLocation;
		this.area=area;
		this.itemID = itemID;
		this.itemName = itemName;
		this.threshold = threshold;
		this.username=username;
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

	public int getItemID() {
		return itemID;
	}

	public String getItemName() {
		return itemName;
	}

	public int getThreshold() {
		return threshold;
	}
	
	public String getUsername() {
		return username;
	}

	
}
