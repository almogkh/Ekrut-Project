package ekrut.entity;

import java.io.Serializable;

public class InventoryItem implements Serializable{

	private static final long serialVersionUID = -6441102031194818468L;
	private Item item;
	private int itemQuantity;
	private String ekrutLocation;
	private int itemThreshold;
	
	public InventoryItem(Item item, int itemQuantity, String ekrutLocation, int itemThreshold) {
		this.item = item;
		this.itemQuantity = itemQuantity;
		this.ekrutLocation = ekrutLocation;
		this.itemThreshold = itemThreshold;
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

	public int getItemThreshold() {
		return itemThreshold;
	}
}
