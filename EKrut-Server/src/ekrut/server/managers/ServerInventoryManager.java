package ekrut.server.managers;

import ekrut.server.db.DBController;
import ekrut.server.db.InventoryItemDAO;
import java.util.ArrayList;
import ekrut.entity.InventoryItem;
import ekrut.net.InventoryItemRequest;
import ekrut.net.InventoryItemResponse;
import ekrut.net.ResultType;


/**
 * Inventory Items server manager that handles all Client's requests regarding InventoryItem.
 * 
 * @author Ofek Malka
 */
public class ServerInventoryManager {
	
	private InventoryItemDAO inventoryItemDAO;
	
	/**
	 * Constructs a new ServerInventoryManager.
	 */
	public ServerInventoryManager(DBController con) {
		inventoryItemDAO = new InventoryItemDAO(con);
	}
	
	/**
	 * Handles Client's request to <b>update</b> InventoryItem's quantity.
	 * 
	 * @param inventoryUpdateItemRequest the InventoryItemRequest that was instantiated with:
	 * 			InventoryItemRequest(int itemId, int quantity, String ekrutLocation)
	 * @return response to be sent to the client.
	 */
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
			return new InventoryItemResponse(ResultType.INVALID_INPUT);
		
		// Fetch InventoryItem from DB.
		InventoryItem inventoryItemInDB = null;
		try {
			inventoryItemInDB = inventoryItemDAO.fetchInventoryItem(itemId, ekrutLocation);
		}catch(Exception e) {
			return new InventoryItemResponse(ResultType.UNKNOWN_ERROR);
		}
		
		// Check that InventoryItem exist in DB.
		if (inventoryItemInDB == null)
			return new InventoryItemResponse(ResultType.NOT_FOUND);
		
		//Check if new quantity is below the threshold of that InventoryItem.
		if (quantity < inventoryItemInDB.getItemThreshold()) {}
			// In order to send 'below threshold' notification we need access to UserNotifier &
			// To know the Area manager's information (who is the manager of this machine??)
			// TBD HOW TO SEND NOTIFICATIONS??
		
		// Try to commit the update in DB.
		if (!inventoryItemDAO.updateItemQuantity(itemId,quantity, ekrutLocation))
			return new InventoryItemResponse(ResultType.UNKNOWN_ERROR);
		
		// Updated successfully.
		return new InventoryItemResponse(ResultType.OK);
	}
	
	
	/**
	 * Handles Client's request to <b>get</b> InventoryItem(s).
	 * 
	 * @param inventoryGetItemsRequest the InventoryItemRequest that was instantiated with:
	 * 			InventoryItemRequest(int itemId, int quantity, String ekrutLocation)
	 * @return response to be sent to the client.
	 */
	// pub+
	public InventoryItemResponse getItems(InventoryItemRequest inventoryGetItemsRequest) {
		if (inventoryGetItemsRequest == null)
				throw new IllegalArgumentException("null InventoryItemRequest was provided.");
		
		// Unpack inventoryGetItemsRequest.
		String ekrutLocation = inventoryGetItemsRequest.getEkrutLocation();
		
		// Fetch InventoryItem(s) fromDB.
		
		ArrayList<InventoryItem> inventoryItems = inventoryItemDAO.fetchAllItemsByLocation(ekrutLocation);
		
		// Check if DB could not locate InventoryItem(s) for given ekrutLocation.
		if (inventoryItems == null)
			return new InventoryItemResponse(ResultType.NOT_FOUND);
		
		// Return the InventoryItems(s) fetched from DB.
		return new InventoryItemResponse(ResultType.OK, inventoryItems);
	}
	
	/**
	 * Handles Client's request to <b>update</b> InventoryItem's <b>threshold</b>.
	 * 
	 * @param inventoryUpdateItemThresholdRequest the InventoryItemRequest that was instantiated with:
	 * 			InventoryItemRequest(int itemId, int quantity, String ekrutLocation)
	 * @return response to be sent to the client.
	 */
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
			return new InventoryItemResponse(ResultType.INVALID_INPUT);
		
		// Try to update the InventoryItem's threshold.
		if (!inventoryItemDAO.updateItemThreshold(itemId, ekrutLocation, threshold))
			return new InventoryItemResponse(ResultType.UNKNOWN_ERROR);
		
		return new InventoryItemResponse(ResultType.OK);
	}
}
