package ekrut.client.managers;

import ekrut.entity.InventoryItem;
import java.util.ArrayList;
import ekrut.net.InventoryItemRequest;
import ekrut.net.InventoryItemResponse;
import ekrut.net.ResultType;

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
	public ResultType updateInventoryQuantity(int itemId, String ekrutLocation, int quantity) throws RuntimeException {
		if (quantity < 0)
			return ResultType.INVALID_INPUT;
		
		// Prepare a InventoryItemRequest to send to server.
		InventoryItemRequest inventoryUpdateItemRequest = 
				new InventoryItemRequest(itemId, quantity, ekrutLocation);
		
		// Sending InventoryItemRequest and receiving InventoryItemResponse.
		InventoryItemResponse inventoryItemUpdateResponse = sendRequest(inventoryUpdateItemRequest);
		return inventoryItemUpdateResponse.getResultType();
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
		ResultType resultType = inventoryGetItemsResponse.getResultType();
		if (resultType != ResultType.OK)
			throw new Exception(resultType.toString());
		
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
	public ResultType updateItemThreshold(int itemId, String ekrutLocation, int threshold) {
		if (threshold < 0)
			throw new IllegalArgumentException("Threshold must be a non-negative integer.");
		
		// Prepare a InventoryItemRequest to send to server.
		InventoryItemRequest inventoryUpdateItemThresholdRequest = 
				new InventoryItemRequest(itemId, ekrutLocation, threshold);
		
		// Sending InventoryItemRequest and receiving InventoryItemResponse.
		InventoryItemResponse inventoryUpdateItemThresholdResponse = sendRequest(inventoryUpdateItemThresholdRequest);
		
		// ResultCode is not "OK" meaning we encountered an error.
		return inventoryUpdateItemThresholdResponse.getResultType();
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
