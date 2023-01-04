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
	
	// Constructor for (ArrayList<InventoryItem>) responses.
	public InventoryItemResponse(ArrayList<InventoryItem> inventoryItems, InventoryItem ignoreMe) {
		this.resultType = ResultType.OK;
		this.inventoryItems = inventoryItems;
	}
	
	// Constructor for (ArrayList<String>) responses.
	public InventoryItemResponse(ArrayList<String> ekrutLocations,  String ignoreMe) {
		this.resultType = ResultType.OK;
		this.ekrutLocations = ekrutLocations;
	}
	
	// Constructor for (ArrayList<Item>) responses.
	public InventoryItemResponse(ArrayList<Item> items, Item ignoreMe) {
		this.resultType = ResultType.OK;
		this.items = items;
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
	
	
}
