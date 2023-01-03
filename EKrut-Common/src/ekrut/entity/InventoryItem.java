package ekrut.entity;

import java.io.Serializable;

public class InventoryItem implements Serializable{

	private static final long serialVersionUID = -6441102031194818468L;
	private Item item;
	private int itemQuantity;
	private String ekrutLocation;
	private String area;
	private int itemThreshold;
	
	public InventoryItem(Item item, int itemQuantity, String ekrutLocation, String area, int itemThreshold) {
		this.item = item;
		this.itemQuantity = itemQuantity;
		this.ekrutLocation = ekrutLocation;
		this.itemThreshold = itemThreshold;
		this.area = area;
	}
	
	// Added by nir, in order to get all inventory items from DB for shipent use.
	// there is no need in thresholt in order.
	public InventoryItem(Item item, int itemQuantity, String ekrutLocation, String area) {
		this.item = item;
		this.itemQuantity = itemQuantity;
		this.ekrutLocation = ekrutLocation;
		this.area = area;
	}

	public Item getItem() {
		return item;
	}
	
	public int getItemQuantity() {
		return itemQuantity;
	}
	
	public String getEkrutLocation() {
		return ekrutLocation;
	}
	
	public String getArea() {
		return area;
	}

	public int getItemThreshold() {
		return itemThreshold;
	}
}
