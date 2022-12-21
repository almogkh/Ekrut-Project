package ekrut.server.managers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ekrut.entity.InventoryItem;
import ekrut.entity.Item;
import ekrut.entity.Order;
import ekrut.entity.OrderItem;
import ekrut.entity.Report;
import ekrut.entity.ReportType;
import ekrut.server.db.DBController;
import ekrut.server.db.InventoryItemDAO;
import ekrut.server.db.ReportDAO;

public class ServerReportManager {
	private ReportDAO reportDAO;
	private InventoryItemDAO inventoryItemDAO;
	
	//TBD.tal what to do about orderDAO that reportDAO gets
	public ServerReportManager(DBController con) {
		reportDAO = new ReportDAO(con);
		inventoryItemDAO = new InventoryItemDAO(con);
	}
	
	
	public void generateReport() {
		
	}
	//TBD.tal add avgSalesSerCustomer
	// this method fetch all the relevant data to generate order report and process it into report
	public Report generateOrderReport(LocalDateTime date, String ekrutLocation) {
		// Fetch all order from the past month at the given location
		ArrayList<Order> orders = reportDAO.fetchAllMonthlyOrders(date, ekrutLocation); 
		
		int totalOrders = orders.size();
		
		int totalOrdersInILS = 0;
		
		for (Order order : orders) {
			// Calculate the order price and add it to totalOrderInILS
			totalOrdersInILS += order.getSumAmount();
		}
		// Add for each item the quantity of sales in the current order to all the previous orders.
		Map<String, Integer> itemsQuantityInOrders = ProcessOrders(orders);
		
		// If less than 6 items have been ordered all month then we do not generate an order report
		if (itemsQuantityInOrders.size() < 6) {
			return null;
		}
		// Save the 5 best sellers items and save the rest of the items into 1 "item" that named "allRest"
		Map<String, Integer> bestSellers = ProcessItemOrders(itemsQuantityInOrders);
				
		Report report = new Report(null, ReportType.CUSTOMER, date, ekrutLocation, totalOrders, totalOrdersInILS, bestSellers);
		
		return report;
	}
	
	public Report generateCustomerReport(LocalDateTime date, String ekrutLocation) {
		// Need to count how many orders has customer that made at least an order in the past month
		ArrayList<String> allCustomersOrders;
		
		allCustomersOrders = reportDAO.getAllCustomersOrders(date, ekrutLocation);
		// doc
		Map<String, Integer> customersOrders = ProcessCustomersOrders(allCustomersOrders);

		/*
		 * Now we will divide the customers into categories,
		 * each customer will count in the category according
		 * to the number of monthly orders he made
		 */ 
		
		Map<String, Integer> customersHistogram = createcustomersHistogram(customersOrders);
		
		Report report = new Report(null, ReportType.CUSTOMER, date, ekrutLocation, customersHistogram);
		
		return report;
	}
	
	public Report generateInventoryReport(LocalDateTime date, String ekrutLocation) {
		
		ArrayList<String> thersholdAlerts = reportDAO.getThresholdAlert(date, ekrutLocation);
		
		Map<String, Integer> tresholdAlertCounted = new HashMap<>();

		for (String itemName : thersholdAlerts) {

			if (tresholdAlertCounted.containsKey(itemName)) {
				int currAlerts = tresholdAlertCounted.get(itemName);
				tresholdAlertCounted.put(itemName, currAlerts + 1);
			}

			else {
				tresholdAlertCounted.put(itemName, 1);
			}
		}
		ArrayList<InventoryItem> allItemsInLocation = inventoryItemDAO.fetchAllItemsByLocation(ekrutLocation);
		int facilityThreshold = 0;
		if (!allItemsInLocation.isEmpty()) {
			// Get the facility threshold from the first item
			facilityThreshold = allItemsInLocation.get(0).getItemThreshold();
		}
		
		Map<String, ArrayList<Integer>> inventoryReportData = new HashMap<>();
		
		for (InventoryItem inventoryItem : allItemsInLocation) {
			String itemName = inventoryItem.getItem().getItemName();
			
			int thresholdAlerts = 0;
			if (tresholdAlertCounted.containsKey(itemName)) {
				thresholdAlerts = tresholdAlertCounted.get(itemName);
			}
			ArrayList<Integer> temp = new ArrayList<>();
			temp.add(inventoryItem.getItemQuantity());
			temp.add(thresholdAlerts);
			inventoryReportData.put(itemName, temp);
		}
		
		Report report = new Report(null, ReportType.INVENTORY, date,
				ekrutLocation, inventoryReportData, facilityThreshold);

		return report;
	}
	
	/**
	 * Creates a histogram of customers and their number of orders in categories. 
	 *
	 * @param customersOrders a map containing customers as keys and their number of orders categories as values
	 * @return a map containing keys for the number of orders (1, 2, 3, 4, 5, 6+) and values for the number of customers with that number of orders
	 */
	private Map<String, Integer> createcustomersHistogram(Map<String, Integer> customersOrders){
		Map<String, Integer> customersHistogram = new HashMap<>();
	    // Initialize the histogram map with keys "1" through "6+" and values of 0
		customersHistogram.put("1", 0);
		customersHistogram.put("2", 0);
		customersHistogram.put("3", 0);
		customersHistogram.put("4", 0);
		customersHistogram.put("5", 0);
		customersHistogram.put("6+", 0);
		
		// Iterate through the customersOrders map
		for (Map.Entry<String, Integer> entry : customersOrders.entrySet()) {
			  int numOrders = entry.getValue();
			// Increment the appropriate value in the histogram map based on the number of orders
			  switch (numOrders) {
			    case 1:
			    	customersHistogram.put("1", customersHistogram.get("1") + 1);
			      break;
			    case 2:
			    	customersHistogram.put("2", customersHistogram.get("2") + 1);
			      break;
			    case 3:
			    	customersHistogram.put("3", customersHistogram.get("3") + 1);
			      break;
			    case 4:
			    	customersHistogram.put("4", customersHistogram.get("4") + 1);
			      break;
			    case 5:
			    	customersHistogram.put("5", customersHistogram.get("5") + 1);
			      break;
			    default:
			    	customersHistogram.put("6+", customersHistogram.get("6+") + 1);
			      break;
			  }
		}
		// Return the histogram map
		return customersHistogram;
		
	}
	
	/**
	 * Processes a list of customer orders and creates a map of customers and their number of orders.
	 *
	 * @param allCustomersOrders a list of customer orders
	 * @return a map containing customers as keys and their number of orders as values
	 */
	private Map<String, Integer> ProcessCustomersOrders(ArrayList<String> allCustomersOrders){
		Map<String, Integer> customersOrders = new HashMap<>();
		// Iterate through the list of customer orders
		for (String username : allCustomersOrders) {
		     // If the customer has already has an order, increment their order count
			if (customersOrders.containsKey(username)) {
				int currOrders = customersOrders.get(username);
				customersOrders.put(username, currOrders + 1);
			}
	        // Otherwise, add the customer to the map with an order count of 1
			else {
				customersOrders.put(username, 1);
			}
		}
	    // Return the map of customer order counts
		return customersOrders;
	}
	
	/**
	 * Processes a given map of items and their quantities and returns a new map containing the 5 best-selling items
	 * and a single entry for all the rest of the items. The new map is sorted in descending order by item quantity.
	 *
	 * @param itemsQuantityInOrders a map of items and their quantities
	 * @return a new map containing the 5 best-selling items and a single entry for all the rest of the items
	 */	
	private Map<String, Integer> ProcessItemOrders(Map<String, Integer> itemsQuantityInOrders){
		// Sort the items by quantity in descending order
		List<Map.Entry<String, Integer>> sortedItemsInOrders = new ArrayList<>(itemsQuantityInOrders.entrySet());
		
		Collections.sort(sortedItemsInOrders, new Comparator<Map.Entry<String, Integer>>() {
			
		    @Override
		    public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
		        return entry2.getValue().compareTo(entry1.getValue());
		    }
		    
		});
		// Add the 5 best-selling items to the map and sum the quantities of all other items
		Map<String, Integer> bestSellers = new HashMap<>();
		
		int allRestQuantity = 0;
		
		for (int i = 0; i < sortedItemsInOrders.size(); i++) {
		    Map.Entry<String, Integer> entry = sortedItemsInOrders.get(i);
		    if (i < 5) {
		    	bestSellers.put(entry.getKey(), entry.getValue());
		    }
		    else {
		    	allRestQuantity += entry.getValue();
		    }
		}
		 // Add the total quantity of all other items to the map under the key "allRest"
		bestSellers.put("allRest", allRestQuantity);
		
	    // Return the map of best-selling items
		return bestSellers;
	}
	
	/**
	 * Processes a list of orders by counting the total quantities of each item sold.
	 * @param orders a list of orders
	 * @return a map where the keys are item names and the values are the quantities of those items sold
	 */
	private Map<String, Integer> ProcessOrders(ArrayList<Order> orders){
		Map<String, Integer> itemsQuantityInOrders = new HashMap<>();
		

	    // Iterate through the orders and count the quantities of each item sold
		for (Order order : orders) {
			for (OrderItem orderItem : order.getItems()) {
				Item item = orderItem.getItem();
				String itemName = item.getItemName();
				int quantity = orderItem.getItemQuantity();
				
		          // If the item is already in the map, add the quantity of this order to the previous quantity
				if (itemsQuantityInOrders.containsKey(itemName)) {
					int preQuantity = itemsQuantityInOrders.get(itemName);
					itemsQuantityInOrders.put(itemName, preQuantity + quantity);
				}
	            // Otherwise, set the quantity of this item to be the quantity of this order
				else {
					itemsQuantityInOrders.put(itemName, quantity); 
				}
			}
		}
		// Return the map of item quantities
		return itemsQuantityInOrders;
	}
	
}
