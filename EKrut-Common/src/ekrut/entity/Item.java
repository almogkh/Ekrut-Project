package ekrut.entity;

import java.io.Serializable;

public class Item implements Serializable{
	
	private static final long serialVersionUID = -6933424479604025362L;
	private int itemId;
	private String itemName;
	private String itemDescription;
	private float itemPrice;
	private byte[] imgByteArray;
	
	// constructor 
	public Item(int itemId, String itemName, String itemDescription, float itemPrice) {
		this.itemId = itemId;
		this.itemName = itemName;
		this.itemDescription = itemDescription;
		this.itemPrice = itemPrice;
	}
	
	public void setImg(byte[] imgByteArray) {
		this.imgByteArray = imgByteArray;
	}

	public byte[] getImg() {
		return imgByteArray;
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
	
	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (!(other instanceof Item))
			return false;
		return itemId == ((Item) other).itemId;
	}
}
