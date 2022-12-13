package ekrut.server.managers;

import ekrut.server.db.InventoryItemDAO;
import ekrut.entity.InventoryItem;
import ekrut.net.InventoryItemRequest;
import ekrut.net.InventoryItemResponse;

public class ServerInventoryManager {

	// pub-sending notifications
	public InventoryItemResponse updateItemQuantity(InventoryItemRequest inventoryUpdateItemRequest) {
		if (inventoryUpdateItemRequest == null)
			throw new IllegalArgumentException("null InventoryItemRequest was provided.");
		
		// Unpack inventoryUpdateItemRequest into it's components.
		int itemId = inventoryUpdateItemRequest.getItemId();
		int quantity = inventoryUpdateItemRequest.getQuantity();
		String ekrutLocation = inventoryUpdateItemRequest.getEkrutLocation();
		
		// Check that updated quantity is a valid value.
		if (quantity < 0)
			return new InventoryItemResponse("Updated quantity must be a non-negative integer.");
		
		// Fetch InventoryItem from DB.
		InventoryItem inventoryItemInDB = InventoryItemDAO.fetchInventoryItem(itemId, ekrutLocation);
		
		// Check that InventoryItem exist in DB.
		if (inventoryItemInDB == null)
			return new InventoryItemResponse("Couldn't find InventoryItem in DB.");
		
		//Check if new quantity is below the threshold of that InventoryItem.
		if (quantity < inventoryItemInDB.getItemThreshold())
			// In order to send 'below threshold' notification we need access to UserNotifier &
			// To know the Area manager's information (who is the manager of this machine??)
			// TBD HOW TO SEND NOTIFICATIONS??
		
		// Try to commit the update in DB.
		if (!InventoryItemDAO.updateItemQuantity(itemId,quantity, ekrutLocation))
			return new InventoryItemResponse("Failed updating item quantity.");
		
		// Updated successfully.
		return new InventoryItemResponse("OK");
	}
	
	// pub+
	public InventoryItemResponse getItems(InventoryItemRequest inventoryGetItemsRequest) {
		if (inventoryGetItemsRequest == null)
				throw new IllegalArgumentException("null InventoryItemRequest was provided.");
		
		// Unpack inventoryGetItemsRequest.
		String ekrutLocation = inventoryGetItemsRequest.getEkrutLocation();
		
		// Fetch InventoryItem(s) fromDB.
		InventoryItem[] inventoryItems = InventoryItemDAO.fetchAllItemsByLocation(ekrutLocation);
		
		// Check if DB could not locate InventoryItem(s) for given ekrutLocation.
		if (inventoryItems == null)
			return new InventoryItemResponse("There are no available InventoryItem(s) for given ekrutLocation: " + ekrutLocation);
		
		// Return the InventoryItems(s) fetched from DB.
		return new InventoryItemResponse("OK", inventoryItems);
	}
	
	// pub-
	public InventoryItemResponse updateItemThreshold(InventoryItemRequest inventoryUpdateItemThresholdRequest) {
		if (inventoryUpdateItemThresholdRequest == null)
			throw new IllegalArgumentException("null InventoryItemRequest was provided.");
		
		// Unpack inventoryUpdateItemThresholdRequest into it's components.
		int itemId = inventoryUpdateItemThresholdRequest.getItemId();
		String ekrutLocation = inventoryUpdateItemThresholdRequest.getEkrutLocation();
		int threshold = inventoryUpdateItemThresholdRequest.getThreshold();
		
		// Check if the new threshold value is valid.
		if (threshold < 0)
			return new InventoryItemResponse("Threshold must be a non-negative integer.");
		
		// Try to update the InventoryItem's threshold.
		if (!InventoryItemDAO.updateItemThreshold(itemId, ekrutLocation, threshold))
			return new InventoryItemResponse("Couldn't update item threshold."); // TBD CHANGE THIS ?
		
		return new InventoryItemResponse("OK");
	}
	
	
}
