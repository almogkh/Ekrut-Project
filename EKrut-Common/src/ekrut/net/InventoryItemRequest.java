package ekrut.net;

import java.io.Serializable;

public class InventoryItemRequest implements Serializable{
	
	private static final long serialVersionUID = 6395346832081668652L;
	private InventoryItemRequestType action;
	private int itemId;
	private int quantity;
	private String ekrutLocation;
	private int threshold;
	
	// Get items request.
	public InventoryItemRequest(String ekrutLocation) {
		this.action = InventoryItemRequestType.FETCH_ITEM;
		this.ekrutLocation = ekrutLocation;
	}
	
	// Update item quantity request.
	public InventoryItemRequest(int itemId, int quantity, String ekrutLocation) {
		this.action = InventoryItemRequestType.UPDATE_ITEM_QUANTITY;
		this.itemId = itemId;
		this.quantity = quantity;
		this.ekrutLocation = ekrutLocation;
	}

	// Update item threshold request.
	public InventoryItemRequest(int itemId, String ekrutLocation, int threshold) {
		this.action = InventoryItemRequestType.UPDATE_ITEM_THRESHOLD;
		this.itemId = itemId;
		this.ekrutLocation = ekrutLocation;
		this.threshold = threshold;
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
	
	public int getThreshold() {
		return threshold;
	}
}
