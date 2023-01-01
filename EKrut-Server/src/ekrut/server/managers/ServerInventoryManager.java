package ekrut.server.managers;

import ekrut.server.db.DBController;
import ekrut.server.db.InventoryItemDAO;
import ekrut.server.db.UserDAO;
import ekrut.server.intefaces.IUserNotifier;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import ekrut.entity.InventoryItem;
import ekrut.entity.Item;
import ekrut.entity.User;
import ekrut.entity.UserType;
import ekrut.net.InventoryItemRequest;
import ekrut.net.InventoryItemRequestType;
import ekrut.net.InventoryItemResponse;
import ekrut.net.ResultType;


/**
 * Inventory Items server manager that handles all Client's requests regarding InventoryItem.
 * 
 * @author Ofek Malka
 */
public class ServerInventoryManager {
	
	private InventoryItemDAO inventoryItemDAO;
	private UserDAO userDAO;
	private IUserNotifier userNotifier;
	
	/**
	 * Constructs a new ServerInventoryManager.
	 */
	public ServerInventoryManager(DBController con, IUserNotifier userNotifier) {
		inventoryItemDAO = new InventoryItemDAO(con);
		userDAO = new UserDAO(con);
		this.userNotifier = userNotifier;
	}
	

	/**
	 * Updates the quantity of an InventoryItem in the inventory system. 
	 * If the updated quantity falls below the item's threshold, a notification may be sent to the appropriate parties.
	 *
	 * @param inventoryUpdateItemRequest an InventoryItemRequest object that contains the item ID, ekrut location, and new quantity value for the InventoryItem to be updated
	 * @return an InventoryItemResponse object indicating the result of the update operation
	 * @throws IllegalArgumentException if the provided InventoryItemRequest object is null
	 */
	public InventoryItemResponse updateItemQuantity(InventoryItemRequest inventoryItemRequest) {
		if (inventoryItemRequest == null || 
			inventoryItemRequest.getAction() != InventoryItemRequestType.UPDATE_ITEM_QUANTITY)
			return new InventoryItemResponse(ResultType.INVALID_INPUT);
		
		// Unpack request components.
		int itemId = inventoryItemRequest.getItemId();
		int quantity = inventoryItemRequest.getQuantity();
		String ekrutLocation = inventoryItemRequest.getEkrutLocation();
		
		// Check components values.
		if (quantity < 0 || ekrutLocation == null)
			return new InventoryItemResponse(ResultType.INVALID_INPUT);
		
		// Fetch InventoryItem from DB.
		InventoryItem inventoryItemInDB = null;
		inventoryItemInDB = inventoryItemDAO.fetchInventoryItem(itemId, ekrutLocation);
		if (inventoryItemInDB == null)
			return new InventoryItemResponse(ResultType.NOT_FOUND);
		
		// Check if new quantity is breaching the threshold of a machine.
		if ((inventoryItemInDB.getItemQuantity() > inventoryItemInDB.getItemThreshold()) && 
				(quantity < inventoryItemInDB.getItemThreshold())) {
			String notificationMsg = "The quantity of: " + inventoryItemInDB.getItem().getItemName() 
									+ " in the machine: " + inventoryItemInDB.getEkrutLocation()
									+ " is below the specified threshold of " + inventoryItemInDB.getItemThreshold() 
									+ " and currently has " + inventoryItemInDB.getItemQuantity() 
									+ " units.";
			User areaManagerUser = userDAO.fetchManagerByArea(inventoryItemInDB.getArea());
			userNotifier.sendNotification(notificationMsg, areaManagerUser.getEmail(), areaManagerUser.getPhoneNumber());
			
			inventoryItemDAO.addThresholdBreach(LocalDateTime.now(), inventoryItemInDB.getItem().getItemName(), 
					inventoryItemInDB.getEkrutLocation(), inventoryItemInDB.getArea());
		}
		
		// Try to commit the update in DB.
		if (!inventoryItemDAO.updateItemQuantity(ekrutLocation, itemId, quantity))
			return new InventoryItemResponse(ResultType.UNKNOWN_ERROR);
		
		// Updated successfully.
		return new InventoryItemResponse(ResultType.OK);
	}
	
	/**
	 * Retrieves all InventoryItem objects associated with a specific ekrut location from the inventory system.
	 *
	 * @param inventoryGetItemsRequest an InventoryItemRequest object that contains the ekrut location for the InventoryItem(s) to be retrieved
	 * @return an InventoryItemResponse object that contains the result of the fetch operation and, if successful,
	 * 			 a list of the retrieved InventoryItem objects
	 * @throws IllegalArgumentException if the provided InventoryItemRequest object is null
	 */
	public InventoryItemResponse getItems(InventoryItemRequest inventoryItemRequest) {
		if (inventoryItemRequest == null ||
			inventoryItemRequest.getAction() != InventoryItemRequestType.FETCH_ITEM)
			return new InventoryItemResponse(ResultType.INVALID_INPUT);
		
		// Unpack inventoryGetItemsRequest.
		String ekrutLocation = inventoryItemRequest.getEkrutLocation();
		if (ekrutLocation == null)
			return new InventoryItemResponse(ResultType.INVALID_INPUT);
		
		// Fetch InventoryItem(s) fromDB.
		ArrayList<InventoryItem> inventoryItems = inventoryItemDAO.fetchAllItemsByEkrutLocation(ekrutLocation);
		if (inventoryItems == null)
			return new InventoryItemResponse(ResultType.NOT_FOUND);
		
		// Attaching images to each Item.
		for (InventoryItem inventoryItem : inventoryItems) {
			Item item = inventoryItem.getItem();
			File imgFile = new File(item.getItemId() + ".jpg");
			try (FileInputStream fis = new FileInputStream(imgFile);
				BufferedInputStream bis = new BufferedInputStream(fis)) {
				byte [] imgByteArray  = new byte [(int)imgFile.length()];
				bis.read(imgByteArray, 0, imgByteArray.length);
				item.setImg(imgByteArray);
			} catch (IOException e) {
				System.out.println("Cant attach image for itemId: " + item.getItemId() 
				+ ". Error: " + e.getMessage());
			}
		}
		
		// Return the InventoryItems(s) fetched from DB.
		return new InventoryItemResponse(ResultType.OK, inventoryItems);
	}
	
	/**
	 * Updates the <b>threshold</b> of an InventoryItem in the inventory system.
	 *
	 * @param inventoryUpdateItemThresholdRequest an InventoryItemRequest object that contains the 
	 * 			item ID, ekrut location, and new threshold value for the InventoryItem to be updated
	 * @return an InventoryItemResponse object indicating the result of the update operation
	 * @throws IllegalArgumentException if the provided InventoryItemRequest object is null
	 */
	public InventoryItemResponse updateItemThreshold(InventoryItemRequest inventoryItemRequest) {
		if (inventoryItemRequest == null ||
			inventoryItemRequest.getAction() != InventoryItemRequestType.UPDATE_ITEM_THRESHOLD)
			return new InventoryItemResponse(ResultType.INVALID_INPUT);
		
		// Unpack inventoryUpdateItemThresholdRequest into it's components.
		String ekrutLocation = inventoryItemRequest.getEkrutLocation();
		int threshold = inventoryItemRequest.getThreshold();
		
		// Check if the new threshold value is valid.
		if (threshold < 0 || ekrutLocation == null)
			return new InventoryItemResponse(ResultType.INVALID_INPUT);
		
		// Try to update the InventoryItem's threshold.
		if (!inventoryItemDAO.updateEkrutLocationThreshold(ekrutLocation, threshold))
			return new InventoryItemResponse(ResultType.UNKNOWN_ERROR);
		
		return new InventoryItemResponse(ResultType.OK);
	}
}
