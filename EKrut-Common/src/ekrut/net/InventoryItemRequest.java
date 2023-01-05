package ekrut.net;

import java.io.Serializable;

public class InventoryItemRequest implements Serializable{
	
	private static final long serialVersionUID = 6395346832081668652L;
	private InventoryItemRequestType action;
	private int itemId;
	private int quantity;
	private int threshold;
	private String ekrutLocation;
	private String area;

	
	public InventoryItemRequest(InventoryItemRequestType action){
		this.action = action;
	}
	
	public InventoryItemRequest(InventoryItemRequestType action, String areaOrLocation){
		this.action = action;
		this.area = areaOrLocation;
		this.ekrutLocation = areaOrLocation;
	}
	
	public InventoryItemRequest(InventoryItemRequestType action, int itemId, int threshold, String ekrutLocation){
		this.action = action;
		this.itemId = itemId;
		this.threshold = threshold;
		this.ekrutLocation = ekrutLocation;
	}
	
	public InventoryItemRequestType getAction() {
		return action;
	}
	
	public int getItemId() {
		return itemId;
	}
	
	public int getQuantity() {
		return quantity;
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
	
	
}
