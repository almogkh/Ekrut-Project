package ekrut.entity;

import java.io.Serializable;

public class Item implements Serializable{
	
	private static final long serialVersionUID = -6933424479604025362L;
	private int itemId;
	private String itemName;
	private String itemDescription;
	private float itemPrice;
	
	// constructor 
	public Item(int itemId, String itemName, String itemDescription, float itemPrice) {
		this.itemId = itemId;
		this.itemName = itemName;
		this.itemDescription = itemDescription;
		this.itemPrice = itemPrice;
	}

	public int getItemId() {
		return itemId;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public String getItemDescription() {
		return itemDescription;
	}
	
	public float getItemPrice() {
		return itemPrice;
	}
}
