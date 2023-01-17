package ekrut.server.managers;

import ekrut.server.db.DBController;
import ekrut.server.db.InventoryItemDAO;
import ekrut.server.db.ItemDAO;
import ekrut.server.db.UserDAO;
import ekrut.server.intefaces.IUserNotifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import ekrut.entity.InventoryItem;
import ekrut.entity.Item;
import ekrut.entity.User;
import ekrut.net.InventoryItemRequest;
import ekrut.net.InventoryItemRequestType;
import ekrut.net.InventoryItemResponse;
import ekrut.net.ResultType;


/**
 * Inventory Items server manager that handles all Client's requests regarding InventoryItem.
 * 
 * @author Ofek Malka
 */
public class ServerInventoryManager extends AbstractServerManager<InventoryItemRequest, InventoryItemResponse> {
	
	private DBController con;
	private InventoryItemDAO inventoryItemDAO;
	private ItemDAO itemDAO;
	private UserDAO userDAO;
	private IUserNotifier userNotifier;
	public static final int RESTOCK_AMOUNT = 50;
	
	
	/**
	 * Constructs a new ServerInventoryManager.
	 */
	public ServerInventoryManager(DBController con, IUserNotifier userNotifier) {
		super(InventoryItemRequest.class, new InventoryItemResponse(ResultType.UNKNOWN_ERROR));
		this.con = con;
		inventoryItemDAO = new InventoryItemDAO(con);
		itemDAO = new ItemDAO(con);
		userDAO = new UserDAO(con);
		this.userNotifier = userNotifier;
	}
	
	
	/**
	 * Handle the given {@link InventoryItemRequest} and return an {@link InventoryItemResponse}.
	 * 
	 * @param inventoryItemRequest the request to handle
	 * @param user the user making the request
	 * @return an {@link InventoryItemResponse} indicating the result of the request
	 */
	@Override
	protected InventoryItemResponse handleRequest(InventoryItemRequest inventoryItemRequest, User user) {
		if (inventoryItemRequest == null) return new InventoryItemResponse(ResultType.UNKNOWN_ERROR);
		InventoryItemRequestType action = inventoryItemRequest.getAction();
		InventoryItemResponse response;
		int retries = 5;
		do {
			try {
				con.beginTransaction();
				switch (action) {
				case UPDATE_ITEM_QUANTITY:
					response = updateInventoryQuantity(inventoryItemRequest);
					break;
				case FETCH_ALL_LOCATIONS_IN_AREA:
					response = fetchAllEkrutLocationsByArea(inventoryItemRequest);
					break;
				case UPDATE_ITEM_THRESHOLD:
					response = updateItemThreshold(inventoryItemRequest);
					break;
				case FETCH_ALL_ITEMS:
					response =fetchAllItems(inventoryItemRequest);
					break;
				case FETCH_ALL_INVENTORYITEMS_IN_MACHINE:
					response = fetchInventoryItemsByEkrutLocation(inventoryItemRequest);
					break;
				default:
					response = new InventoryItemResponse(ResultType.UNKNOWN_ERROR);
				}
				con.commitTransaction();
				return response;
			} catch (DeadlockException e) {
				con.abortTransaction();
				retries--;
			}
		} while (retries > 0);
		
		return new InventoryItemResponse(ResultType.UNKNOWN_ERROR);
	}
	
	
	/**
	 * Updates the quantity of an InventoryItem in the inventory system. 
	 * If the updated quantity falls below the item's threshold, a notification may be sent to the appropriate parties.
	 *
	 * @param inventoryItemRequest an InventoryItemRequest object that contains the item ID, ekrut location, and new quantity value for the InventoryItem to be updated
	 * @return an InventoryItemResponse object indicating the result of the update operation
	 */
	public InventoryItemResponse updateInventoryQuantity(InventoryItemRequest inventoryItemRequest) throws DeadlockException {
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
									+ " and currently has " + quantity
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
	 * @param inventoryItemRequest an InventoryItemRequest object that contains the ekrut location for the InventoryItem(s) to be retrieved
	 * @return an InventoryItemResponse object that contains the result of the fetch operation and, if successful,
	 * 			 a list of the retrieved InventoryItem objects
	 */
	public InventoryItemResponse fetchInventoryItemsByEkrutLocation(InventoryItemRequest inventoryItemRequest) throws DeadlockException {
		if (inventoryItemRequest == null ||
			inventoryItemRequest.getAction() != InventoryItemRequestType.FETCH_ALL_INVENTORYITEMS_IN_MACHINE)
			return new InventoryItemResponse(ResultType.INVALID_INPUT);
		
		// Unpack inventoryGetItemsRequest.
		String ekrutLocation = inventoryItemRequest.getEkrutLocation();
		if (ekrutLocation == null)
			return new InventoryItemResponse(ResultType.INVALID_INPUT);
		
		// Fetch InventoryItem(s) from DB.
		ArrayList<InventoryItem> inventoryItems = inventoryItemDAO.fetchAllItemsByEkrutLocation(ekrutLocation);
		if (inventoryItems == null)
			return new InventoryItemResponse(ResultType.NOT_FOUND);
		
		// Return the InventoryItems(s) fetched from DB.
		InventoryItemResponse response = new InventoryItemResponse(ResultType.OK);
		response.setInventoryItems(inventoryItems);
		return response;
	}
	
	
	/**
	 * Updates the <b>threshold</b> of an InventoryItem in the inventory system.
	 *
	 * @param inventoryItemRequest an InventoryItemRequest object that contains the 
	 * 			item ID, ekrut location, and new threshold value for the InventoryItem to be updated
	 * @return an InventoryItemResponse object indicating the result of the update operation
	 */
	public InventoryItemResponse updateItemThreshold(InventoryItemRequest inventoryItemRequest) throws DeadlockException {
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
	
	/**
	 * Fetch all Ekrut locations in the given area and return them in an {@link InventoryItemResponse}.
	 * 
	 * @param inventoryFetchLocationsRequest the request to fetch the locations
	 * @return an {@link InventoryItemResponse} containing the list of locations, or an error if the request is invalid or no locations are found
	 */
	public InventoryItemResponse fetchAllEkrutLocationsByArea(InventoryItemRequest inventoryFetchLocationsRequest) {
		if (inventoryFetchLocationsRequest == null)
			return new InventoryItemResponse(ResultType.INVALID_INPUT);
		String area = inventoryFetchLocationsRequest.getArea();
		ArrayList<String> ekrutLocations = inventoryItemDAO.fetchAllEkrutLocationsByArea(area);
		if (ekrutLocations == null)
			return new InventoryItemResponse(ResultType.NOT_FOUND);
		InventoryItemResponse response = new InventoryItemResponse(ResultType.OK);
		response.setEkrutLocations(ekrutLocations);
		return response;
	}
	

	/**
	 * Fetch all items and return them in an {@link InventoryItemResponse}.
	 * 
	 * @param inventoryItemRequest the request to fetch the items
	 * @return an {@link InventoryItemResponse} containing the list of items, or an error if the request is invalid or no items are found
	 */
	public InventoryItemResponse fetchAllItems(InventoryItemRequest inventoryItemRequest) {
		if (inventoryItemRequest == null)
			return new InventoryItemResponse(ResultType.INVALID_INPUT);
		ArrayList<Item> allItems = itemDAO.fetchAllItems();
		if (allItems == null)
			return new InventoryItemResponse(ResultType.NOT_FOUND);
		InventoryItemResponse response = new InventoryItemResponse(ResultType.OK);
		response.setItems(allItems);
		return response;
	}
	
}
