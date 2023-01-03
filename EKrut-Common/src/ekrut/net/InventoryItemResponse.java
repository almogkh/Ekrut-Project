package ekrut.net;

import java.io.Serializable;
import java.util.ArrayList;

import ekrut.entity.InventoryItem;

public class InventoryItemResponse implements Serializable{
	
	private static final long serialVersionUID = -1415270822049012022L;
	private ResultType resultType;
	private ArrayList<InventoryItem> inventoryItems;
	private ArrayList<String> ekrutLocations;
	
	// Constructor for ResultType-only responses.
	public InventoryItemResponse(ResultType resultType) {
		this.resultType = resultType;
	}
	
	// Constructor for (ResultType & ArrayList<InventoryItem>) responses.
	public InventoryItemResponse(ResultType resultType, ArrayList<InventoryItem> inventoryItems) {
		this.resultType = resultType;
		this.inventoryItems = inventoryItems;
	}
	
	// Constructor for (ResultType & ArrayList<String>) responses.
	public InventoryItemResponse(ArrayList<String> ekrutLocations) {
		this.resultType = ResultType.OK;
		this.ekrutLocations = ekrutLocations;
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
}
