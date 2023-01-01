package ekrut.client.managers;

import ekrut.client.EKrutClient;
import ekrut.entity.InventoryItem;
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
public class ClientInventoryManager {
	
	private EKrutClient client;
	private Object lock = new Object();
	private InventoryItemResponse response;
	
	
	/**
	 * Constructs a new ClientInventoryManager instance and registers an InventoryItemResponse handler.
	 * 
	 * @param client the EKrutClient for which to register the InventoryItemResponse handler
	 */
	public ClientInventoryManager(EKrutClient client) {
		this.client = client;
		client.registerHandler(InventoryItemResponse.class, (res) -> {
			synchronized(lock) {
				response = res;
				lock.notify();
			}
		});
	}

	/**
	 * Handles Client's request to <b>update</b> InventoryItem's <b>quantity</b>.
	 * 
	 * @param item the item its quantity will be changed.
	 * @param ekrutLocation the specific machine string descriptor.
	 * @param quantity the new item's quantity at the given ekrutLocation.
	 * @throws IllegalArgumentException when a null item is provided.
	 * @throws Exception when the servers response is anything but "OK".
	 */
	public ResultType updateInventoryQuantity(int itemId, String ekrutLocation, int quantity) {
		if (quantity < 0)
			throw new IllegalArgumentException("Quantity must be a non-negative integer.");
			// TBD OFEK or rather should i :return ResultType.INVALID_INPUT;
		
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
	public ArrayList<InventoryItem> getItems(String ekrutLocation) {
		// Prepare a InventoryItemRequest to send to server.
		InventoryItemRequest inventoryGetItemsRequest = new InventoryItemRequest(ekrutLocation);
		
		// Sending InventoryItemRequest and receiving InventoryItemResponse.
		InventoryItemResponse inventoryGetItemsResponse = sendRequest(inventoryGetItemsRequest);
		
		// ResultCode is not "OK" meaning we encountered an error.
		ResultType resultType = inventoryGetItemsResponse.getResultType();
		
		if (resultType == ResultType.NOT_FOUND)
			return null;
		if (resultType != ResultType.OK)
			throw new RuntimeException(resultType.toString());
		
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
	public void updateItemThreshold(int itemId, String ekrutLocation, int threshold) {
		if (threshold < 0)
			throw new IllegalArgumentException("Threshold must be a non-negative integer.");
		
		// Prepare a InventoryItemRequest to send to server.
		InventoryItemRequest inventoryUpdateItemThresholdRequest = 
				new InventoryItemRequest(itemId, ekrutLocation, threshold);
		
		// Sending InventoryItemRequest and receiving InventoryItemResponse.
		InventoryItemResponse inventoryUpdateItemThresholdResponse = sendRequest(inventoryUpdateItemThresholdRequest);
		
		// ResultCode is not "OK" meaning we encountered an error.
		ResultType resultType = inventoryUpdateItemThresholdResponse.getResultType();
		if (resultType != ResultType.OK)
			throw new RuntimeException(resultType.toString());
	}
	
	
	
	
	
	public ArrayList<String> fetchAllEkrutLocationsByArea(String ekrutLocation){
		InventoryItemRequest inventoryFentchAllEkrutLocationByAreaRequest = 
				new InventoryItemRequest(ekrutLocation, InventoryItemRequestType.FETCH_LOCATION_IN_AREA);
		InventoryItemResponse response = sendRequest(inventoryFentchAllEkrutLocationByAreaRequest);
		ResultType resultType = response.getResultType();
		if (resultType != ResultType.OK)
			throw new RuntimeException(resultType.toString());
		return response.getEkrutLocations();
	}
	
	
	
	
	
	
	/**
	 * Send Client's request to the server, returns the server's response.
	 * 
	 * @param request the InventoryItemRequest instance representing the request.
	 * @return the server's response for the given request.
	 */
	private InventoryItemResponse sendRequest(InventoryItemRequest request) {
		this.response = null;
		client.sendRequestToServer(request);
		synchronized(lock) {
			while (response == null) {
				try {
					lock.wait();
				} catch (InterruptedException e) {}
			}
		}
		return response;
	}
}
