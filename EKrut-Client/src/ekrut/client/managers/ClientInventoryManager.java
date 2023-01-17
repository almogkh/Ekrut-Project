package ekrut.client.managers;

import ekrut.client.EKrutClient;
import ekrut.entity.InventoryItem;
import ekrut.entity.Item;
import java.util.ArrayList;
import ekrut.net.InventoryItemRequest;
import ekrut.net.InventoryItemRequestType;
import ekrut.net.InventoryItemResponse;
import ekrut.net.ResultType;

/**
 * Inventory Items client manager that handles client actions regarding InventoryItem.
 * 
 * @author Ofek Malka
 */
public class ClientInventoryManager extends AbstractClientManager<InventoryItemRequest, InventoryItemResponse> {
	
	/**
	 * Constructs a new ClientInventoryManager instance and registers an InventoryItemResponse handler.
	 * 
	 * @param client the EKrutClient for which to register the InventoryItemResponse handler
	 */
	public ClientInventoryManager(EKrutClient client) {
		super(client, InventoryItemResponse.class);
	}
	
	/**
	 * Handles Client's request to <b>update</b> InventoryItem's <b>quantity</b>.
	 * 
	 * @param itemId the item its quantity will be changed.
	 * @param ekrutLocation the specific machine string descriptor.
	 * @param quantity the new item's quantity at the given ekrutLocation.
	 * @return a ResultType representing the server's response.
	 */
	public ResultType updateInventoryQuantity(int itemId, String ekrutLocation, int quantity) {
		if (quantity < 0) return ResultType.INVALID_INPUT;
		InventoryItemRequest inventoryItemRequest = new InventoryItemRequest(
				InventoryItemRequestType.UPDATE_ITEM_QUANTITY, itemId, quantity, ekrutLocation);
		InventoryItemResponse inventoryItemResponse = sendRequest(inventoryItemRequest);
		return inventoryItemResponse.getResultType();
	}
	
	/**
	 * Return InentoryItem list for a given ekrutLocation.
	 * 
	 * @param ekrutLocation the specific machine string descriptor.
	 * @return an ArrayList of InventoryItem(s) available in the specified ekrutLocation.
	 */
	public ArrayList<InventoryItem> fetchInventoryItemsByEkrutLocation(String ekrutLocation) {
		InventoryItemRequest inventoryItemRequest = new InventoryItemRequest(
				InventoryItemRequestType.FETCH_ALL_INVENTORYITEMS_IN_MACHINE, ekrutLocation);
		InventoryItemResponse inventoryItemResponse = sendRequest(inventoryItemRequest);
		ResultType resultType = inventoryItemResponse.getResultType();
		if (resultType == ResultType.NOT_FOUND) 
			return null;
		if (resultType != ResultType.OK) 
			throw new RuntimeException(resultType.toString());
		return inventoryItemResponse.getInventoryItems();
	}
	
	/**
	 * Handles Client's request to <b>update</b> InventoryItem's <b>threshold</b>.
	 * 
	 * @param itemId the item its threshold will be changed.
	 * @param ekrutLocation the specific machine string descriptor.
	 * @param threshold the new item's threshold for the given ekrutLocation.
	 */
	public void updateItemThreshold(int itemId, String ekrutLocation, int threshold) {
		if (threshold < 0) throw new IllegalArgumentException("Threshold must be a non-negative integer.");
		InventoryItemRequest inventoryItemRequest = new InventoryItemRequest(
				InventoryItemRequestType.UPDATE_ITEM_THRESHOLD, itemId, threshold, ekrutLocation);
		InventoryItemResponse inventoryItemResponse = sendRequest(inventoryItemRequest);
		ResultType resultType = inventoryItemResponse.getResultType();
		if (resultType != ResultType.OK) throw new RuntimeException(resultType.toString());
	}

	public ArrayList<String> fetchAllEkrutLocationsByArea(String area) throws RuntimeException {
		InventoryItemRequest inventoryItemRequest = new InventoryItemRequest(
				InventoryItemRequestType.FETCH_ALL_LOCATIONS_IN_AREA, area);
		InventoryItemResponse inventoryItemResponse = sendRequest(inventoryItemRequest);
		ResultType resultType = inventoryItemResponse.getResultType();
		if (resultType != ResultType.OK) throw new RuntimeException(resultType.toString());
		return inventoryItemResponse.getEkrutLocations();
	}
	
	public ArrayList<Item> fetchAllItems()throws RuntimeException {
		InventoryItemRequest inventoryItemRequest = new InventoryItemRequest(
				InventoryItemRequestType.FETCH_ALL_ITEMS);
		InventoryItemResponse inventoryItemResponse = sendRequest(inventoryItemRequest);
		ResultType resultType = inventoryItemResponse.getResultType();
		if (resultType != ResultType.OK)
			throw new RuntimeException(resultType.toString());
		return inventoryItemResponse.getItems();
	}
	
}
