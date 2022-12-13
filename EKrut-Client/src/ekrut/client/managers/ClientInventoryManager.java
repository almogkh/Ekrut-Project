package ekrut.client.managers;

import ekrut.entity.InventoryItem;
import ekrut.entity.Item;
import ekrut.net.InventoryItemRequest;
import ekrut.net.InventoryItemResponse;

public class ClientInventoryManager {

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
	
	
	public InventoryItem[] getItems(String ekrutLocation) throws Exception {
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
	
	
	public void updateItemThreshold(Item item, String ekrutLocation, int threshold) throws Exception {
		if (item == null)
			throw new IllegalArgumentException("null Item was provided.");
		if (threshold < 0)
			throw new IllegalArgumentException("Threshold must be a non-negative number.");
		
		// Prepare a InventoryItemRequest to send to server.
		InventoryItemRequest inventoryUpdateItemThresholdRequest = 
				new InventoryItemRequest(item.getItemId(), ekrutLocation, threshold);
		
		// Sending InventoryItemRequest and receiving InventoryItemResponse.
		InventoryItemResponse inventoryUpdateItemThresholdResponse = sendRequest(inventoryUpdateItemThresholdRequest);
		
		// ResultCode is not "OK" meaning we encountered an error.
		String resultCode = inventoryUpdateItemThresholdResponse.getResultCode();
		if (!resultCode.equals("OK"))
			throw new Exception(resultCode); // TBD CHANGE TO SPESIFIC EXCEPTION
	}
	
	@SuppressWarnings("unused")
	private InventoryItemResponse sendRequest(InventoryItemRequest request) {
		// TBD Implementation will be added later.
		return null;
	}
}
