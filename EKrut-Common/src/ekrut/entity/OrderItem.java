package ekrut.entity;

import java.io.Serializable;

public class OrderItem implements Serializable {
	
	private static final long serialVersionUID = 2396357415321135812L;
	
	private Item item;
	private int itemQuantity;
	
	public OrderItem(Item item, int itemQuantity) {
		this.item = item;
		this.itemQuantity = itemQuantity;
	}

	public Item getItem() {
		return item;
	}
	
	public int getItemQuantity() {
		return itemQuantity;
	}
	
	public void setItemQuantity(int itemQuantity) {
		this.itemQuantity = itemQuantity;
	}

}
