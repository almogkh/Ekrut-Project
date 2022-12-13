package ekrut.entity;

public class InventoryItem {
	
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
	
	public void setItemQuantity(int itemQuantity) {
		this.itemQuantity = itemQuantity;
	}
	
	public String getEkrutLocation() {
		return ekrutLocation;
	}

	public int getItemThreshold() {
		return itemThreshold;
	}
}
