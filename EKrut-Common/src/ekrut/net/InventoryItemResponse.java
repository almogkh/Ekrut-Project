package ekrut.net;

import java.io.Serializable;

import ekrut.entity.InventoryItem;

public class InventoryItemResponse implements Serializable{
	private static final long serialVersionUID = -1415270822049012022L;
	private String resultCode;
	private InventoryItem[] inventoryItems;
	
	// TBD Anything BUT resultCode = "OK" means some sort of an error!
	public String getResultCode() {
		return resultCode;
	}

	public InventoryItem[] getInventoryItems() {
		return inventoryItems;
	}
	
	
}
