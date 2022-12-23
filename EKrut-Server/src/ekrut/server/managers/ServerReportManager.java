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
	
	public ServerReportManager(DBController con) {
		reportDAO = new ReportDAO(con);
		inventoryItemDAO = new InventoryItemDAO(con);
	}
	/*
	 * TBD.tal create this
	public void generateReport() {
		
	}*/
	
	/**
	 * Generates an order report for a given location and date.
	 * 
	 * @param date the date for which to generate the report
	 * @param ekrutLocation the location for which to generate the report
	 * @return the generated order report, or null
	 */
	public Report generateOrderReport(LocalDateTime date, String ekrutLocation) {
		// Get a list of all orders made in the given month and location
		ArrayList<Order> orders = reportDAO.fetchAllMonthlyOrders(date, ekrutLocation); 
		// Initialize the total order count and total order amount to 0
		int totalOrders = orders.size();
		int totalOrdersInILS = 0;
		float avgOrderPrice = -1;
		 // Iterate over the orders to calculate the total order amount
		for (Order order : orders) {
			totalOrdersInILS += order.getSumAmount();
		}
		if (totalOrders > 0) {
			avgOrderPrice = totalOrdersInILS / totalOrders;
		}
		// Process the orders to count the quantity of each item ordered
		Map<String, Integer> itemsQuantityInOrders = ProcessOrders(orders);
		
		// If less than 6 items have been ordered all month then we do not generate an order report
		if (itemsQuantityInOrders.size() < 6) {
			return null;
		}
		// Save the 5 best sellers items and save the rest of the items into "item" that named "allRest"
		Map<String, Integer> bestSellers = ProcessItemOrders(itemsQuantityInOrders);
		// Create a new report object with the generated data		
		Report report = new Report(null, ReportType.ORDER, date, ekrutLocation, totalOrders, totalOrdersInILS, avgOrderPrice, bestSellers);
		// Return the report
		return report;
	}
	
	/**
	 * Generates a customer report for a given location and date.
	 * 
	 * @param date the date for which to generate the report
	 * @param ekrutLocation the location for which to generate the report
	 * @return the generated customer report
	 */
	public Report generateCustomerReport(LocalDateTime date, String ekrutLocation) {
		// Get a list of all customer orders for the given date and location	
		ArrayList<String> allCustomersOrders = reportDAO.getAllCustomersOrders(date, ekrutLocation);
		// Process the customer orders to count the number of orders made by each customer
		Map<String, Integer> customersOrders = ProcessCustomersOrders(allCustomersOrders);
		// Create a histogram of customer orders by dividing customers into categories based on their order count
		Map<String, Integer> customersHistogram = createCustomersHistogram(customersOrders);
		// Create a new report object with the generated data
		Report report = new Report(null, ReportType.CUSTOMER, date, ekrutLocation, customersHistogram);
		// Return the report
		return report;
	}
	
	/**
	 * Generates an inventory report for a given location and date.
	 * 
	 * @param date the date for which to generate the report
	 * @param ekrutLocation the location for which to generate the report
	 * @return the generated inventory report
	 */
	public Report generateInventoryReport(LocalDateTime date, String ekrutLocation) {
		// Get the list of threshold alert messages for the given date and location
		ArrayList<String> thersholdAlerts = reportDAO.getThresholdAlert(date, ekrutLocation);
		// Count the number of alerts for each item
		Map<String, Integer> tresholdAlertCounted = CountAlerts(thersholdAlerts);
		// Get the list of all items in the location
		ArrayList<InventoryItem> allItemsInLocation = inventoryItemDAO.fetchAllItemsByEkrutLocation(ekrutLocation);
		int facilityThreshold = 0;
		// If the list of items is not empty, extract the facility threshold from the first item
		if (!allItemsInLocation.isEmpty()) {
			facilityThreshold = allItemsInLocation.get(0).getItemThreshold();
		}
		// Process the inventory data
		Map<String, ArrayList<Integer>> inventoryReportData = 
				ProcessInventoryData(allItemsInLocation, tresholdAlertCounted);
		// Create a new report object with the generated data
		Report report = new Report(null, ReportType.INVENTORY, date,
				ekrutLocation, inventoryReportData, facilityThreshold);
		// Return the report
		return report;
	}
	
	/**
	 * Counts the number of threshold alerts for each item.
	 * 
	 * @param thersholdAlerts a list of item names for which threshold alerts have been triggered
	 * @return a map of item names to the number of threshold alerts for that item
	 */
	private Map<String, Integer> CountAlerts(ArrayList<String> thersholdAlerts){
		
		Map<String, Integer> tresholdAlertCounted = new HashMap<>();
		// Iterate over the list of item names
		for (String itemName : thersholdAlerts) {
			// change doc
			tresholdAlertCounted.merge(itemName, 1, Integer::sum);
		}
		// Return the map of alert counts
		return tresholdAlertCounted;
	}
	
	/**
	 * Processes inventory data for a given location.
	 * 
	 * @param allItemsInLocation a list of all inventory items in the location
	 * @param tresholdAlertCounted a map of item names to threshold alert counts
	 * @return a map of item names to lists containing the item quantity and threshold alert count
	 */
	private Map<String, ArrayList<Integer>> ProcessInventoryData(
			ArrayList<InventoryItem> allItemsInLocation, 
			Map<String, Integer> tresholdAlertCounted){
		
		Map<String, ArrayList<Integer>> inventoryReportData = new HashMap<>();
		 // Iterate over the list of inventory items
		for (InventoryItem inventoryItem : allItemsInLocation) {
			String itemName = inventoryItem.getItem().getItemName();
			
			int thresholdAlerts = 0;
			// If the item name is present in the map of threshold alerts, extract the count
			if (tresholdAlertCounted.containsKey(itemName)) {
				thresholdAlerts = tresholdAlertCounted.get(itemName);
			}
			// Create a list containing the item quantity and threshold alert count
			ArrayList<Integer> temp = new ArrayList<>();
			temp.add(inventoryItem.getItemQuantity());
			temp.add(thresholdAlerts);
			
			// Store the list in the inventory report data map using the item name as the key
			inventoryReportData.put(itemName, temp);
		}
		// Return the inventory report data map
		return inventoryReportData;
	}
	
	/**
	 * Creates a histogram of customers and their number of orders in categories. 
	 *
	 * @param customersOrders a map containing customers as keys and their number of orders categories as values
	 * @return a map containing keys for the number of orders (1, 2, 3, 4, 5, 6+) and values for the number of customers with that number of orders
	 */
	private Map<String, Integer> createCustomersHistogram(Map<String, Integer> customersOrders){
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
			// Increment the count of orders for the corresponding customer in the map
			customersOrders.merge(username, 1, Integer::sum);
	    // Return the map of customer order counts
		}
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
				
				itemsQuantityInOrders.merge(itemName, orderItem.getItemQuantity(), Integer::sum);
			}
		}
		// Return the map of item quantities
		return itemsQuantityInOrders;
	}
	
}
