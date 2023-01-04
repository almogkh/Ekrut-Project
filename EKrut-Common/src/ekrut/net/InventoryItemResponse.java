package ekrut.net;

import java.io.Serializable;
import java.util.ArrayList;

import ekrut.entity.InventoryItem;
import ekrut.entity.Item;

public class InventoryItemResponse implements Serializable{
	
	private static final long serialVersionUID = -1415270822049012022L;
	private ResultType resultType;
	private ArrayList<InventoryItem> inventoryItems;
	private ArrayList<String> ekrutLocations;
	private ArrayList<Item> items;
	
	
	// Constructor for ResultType-only responses.
	public InventoryItemResponse(ResultType resultType) {
		this.resultType = resultType;
	}
	
	public ArrayList<InventoryItem> getInventoryItems() {
		return inventoryItems;
	}
	
	public ArrayList<String> getEkrutLocations() {
		return ekrutLocations;
	}

	public ResultType getResultType() {
		return resultType;
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public void setInventoryItems(ArrayList<InventoryItem> inventoryItems) {
		this.inventoryItems = inventoryItems;
	}

	public void setEkrutLocations(ArrayList<String> ekrutLocations) {
		this.ekrutLocations = ekrutLocations;
	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}
}
