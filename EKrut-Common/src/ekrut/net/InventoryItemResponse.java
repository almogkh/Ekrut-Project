package ekrut.net;

import java.io.Serializable;
import java.util.ArrayList;

import ekrut.entity.InventoryItem;

public class InventoryItemResponse implements Serializable{
	
	private static final long serialVersionUID = -1415270822049012022L;
	private ResultType resultType;
	private ArrayList<InventoryItem> inventoryItems;
	
	// Constructor for String-only responses.
	public InventoryItemResponse(ResultType resultType) {
		this.resultType = resultType;
	}
	
	public InventoryItemResponse(ResultType resultType, ArrayList<InventoryItem> inventoryItems) {
		this.resultType = resultType;
		this.inventoryItems = inventoryItems;
	}
	
	public ArrayList<InventoryItem> getInventoryItems() {
		return inventoryItems;
	}
	
	// TBD Anything BUT resultCode = "OK" means some sort of an error!
	// THINKING ABOUT CHANGING IT TO INVENTORY_ITEM_TYPE
	public ResultType getResultType() {
		return resultType;
	}
}
