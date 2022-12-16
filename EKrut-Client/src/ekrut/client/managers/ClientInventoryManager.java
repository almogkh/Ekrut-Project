package ekrut.client.managers;

import ekrut.entity.InventoryItem;
import ekrut.entity.Item;
import java.util.ArrayList;
import ekrut.net.InventoryItemRequest;
import ekrut.net.InventoryItemResponse;

/**
 * Inventory Items client manager that handles client actions regarding InventoryItem.
 * 
 * @author Ofek Malka
 */
public class ClientInventoryManager {

	/**
	 * Handles Client's request to <b>update</b> InventoryItem's <b>quantity</b>.
	 * 
	 * @param item the item its quantity will be changed.
	 * @param ekrutLocation the specific machine string descriptor.
	 * @param quantity the new item's quantity at the given ekrutLocation.
	 * @throws IllegalArgumentException when a null item is provided.
	 * @throws Exception when the servers response is anything but "OK".
	 */
	public void updateInventoryQuantity(Item item, String ekrutLocation, int quantity) throws Exception {
		if (item == null)
			throw new IllegalArgumentException("null Item was provided.");
		if (quantity < 0)
			throw new IllegalArgumentException("Quantity must be a non-negative number.");
		
		// Prepare a InventoryItemRequest to send to server.
		InventoryItemRequest inventoryUpdateItemRequest = 
				new InventoryItemRequest(item.getItemId(), quantity, ekrutLocation);
		
		// Sending InventoryItemRequest and receiving InventoryItemResponse.
		InventoryItemResponse inventoryItemUpdateResponse = sendRequest(inventoryUpdateItemRequest);
		if (inventoryItemUpdateResponse.getResultCode().equals("OK")) return;
		
		// ResultCode is not "OK" meaning we encountered an error.
		throw new Exception(inventoryItemUpdateResponse.getResultCode()); // TBD CHANGE TO SPESIFIC EXCEPTION
	}
	
	/**
	 * Return InentoryItem list for a given ekrutLocation.
	 * 
	 * @param ekrutLocation the specific machine string descriptor.
	 * @throws IllegalArgumentException when a null item is provided.
	 * @throws Exception when the servers response is anything but "OK".
	 */
	public ArrayList<InventoryItem> getItems(String ekrutLocation) throws Exception {
		// Prepare a InventoryItemRequest to send to server.
		InventoryItemRequest inventoryGetItemsRequest = 
				new InventoryItemRequest(ekrutLocation);
		
		// Sending InventoryItemRequest and receiving InventoryItemResponse.
		InventoryItemResponse inventoryGetItemsResponse = sendRequest(inventoryGetItemsRequest);
		
		// ResultCode is not "OK" meaning we encountered an error.
		String resultCode = inventoryGetItemsResponse.getResultCode();
		if (!resultCode.equals("OK"))
			throw new Exception(resultCode); // TBD CHANGE TO SPESIFIC EXCEPTION
		
		// return the InventoryItem(s) attached to the response.
		return inventoryGetItemsResponse.getInventoryItems();
	}
	
	/**
	 * Handles Client's request to <b>update</b> InventoryItem's <b>threshold</b>.
	 * 
	 * @param item the item its threshold will be changed.
	 * @param ekrutLocation the specific machine string descriptor.
	 * @param threshold the new item's threshold for the given ekrutLocation.
	 * @throws IllegalArgumentException when a null item is provided.
	 * @throws Exception when the servers response is anything but "OK".
	 */
	public void updateItemThreshold(Item item, String ekrutLocation, int threshold) throws Exception {
		if (item == null)
			throw new IllegalArgumentException("null Item was provided.");
		if (threshold < 0)
			throw new IllegalArgumentException("Threshold must be a non-negative integer.");
		
		// Prepare a InventoryItemRequest to send to server.
		InventoryItemRequest inventoryUpdateItemThresholdRequest = 
				new InventoryItemRequest(item.getItemId(), ekrutLocation, threshold);
		
		// Sending InventoryItemRequest and receiving InventoryItemResponse.
		InventoryItemResponse inventoryUpdateItemThresholdResponse = sendRequest(inventoryUpdateItemThresholdRequest);
		
		// ResultCode is not "OK" meaning we encountered an error.
		String resultCode = inventoryUpdateItemThresholdResponse.getResultCode();
		if (!resultCode.equals("OK"))
			throw new Exception(resultCode); // TBD CHANGE TO SPESIFIC EXCEPTION?
	}
	
	/**
	 * Send Client's request to the server, returns the server's response.
	 * 
	 * @param request the InventoryItemRequest instance representing the request.
	 * @return the server's response for the given request.
	 */
	@SuppressWarnings("unused")
	private InventoryItemResponse sendRequest(InventoryItemRequest request) {
		// TBD Implementation will be added later.
		return null;
	}
}
