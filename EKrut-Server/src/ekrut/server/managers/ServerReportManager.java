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
import ekrut.entity.OrderType;
import ekrut.entity.Report;
import ekrut.entity.ReportType;
import ekrut.net.ReportResponse;
import ekrut.net.ResultType;
import ekrut.server.db.DBController;
import ekrut.server.db.InventoryItemDAO;
import ekrut.server.db.ReportDAO;

/**
 * Manages Report management on the server side.
 * @author Tal Gaon
 */
public class ServerReportManager {
	private ReportDAO reportDAO;
	private InventoryItemDAO inventoryItemDAO;
	
	public ServerReportManager(DBController con) {
		reportDAO = new ReportDAO(con);
		inventoryItemDAO = new InventoryItemDAO(con);
	}
	
	/**
	 * This method fetches a report from the database based on the provided date, location, area, and type.
	 * 
	 * @param date the date of the report to fetch
	 * @param location the location of the report to fetch
	 * @param area the area of the report to fetch
	 * @param type the type of the report to fetch
	 * @return a ReportResponse object containing the report, or a ResultType value indicating if the report was not found
	 * @throws SQLException if a database error occurs while executing the SQL query
	 */
	public ReportResponse fetchReport(LocalDateTime date, String location, String area, ReportType type) {
		
		Report report = reportDAO.fetchReport(date, location, area, type);
		
		if (report == null) 
			return new ReportResponse(ResultType.NOT_FOUND);
	
		return new ReportResponse(ResultType.OK, report); 
	}
	
	/**
	 * This method fetches a list of facilities in a specific area from the database.
	 * 
	 * @param area the area to fetch facilities from
	 * @return a ReportResponse object containing the list of facilities, or a ResultType value indicating if the list was not found
	 * @throws SQLException if a database error occurs while executing the SQL query
	 */
	public ReportResponse fetchFacilitiesByArea(String area){
		
		ArrayList<String> facilities = reportDAO.fetchFacilitiesByArea(area);
			
		if (facilities == null) 
			return new ReportResponse(ResultType.NOT_FOUND);
		
		return new ReportResponse(ResultType.OK, facilities); 
	}
	
	/**
	 * This method generates an Order report for a given month and area.
	 * 
	 * @param date the month for which the report is being generated
	 * @param ekrutLocation the location of the report
	 * @param area the area for which the report is being generated
	 * @return a Report object containing the generated data for the Order report
	 * @throws SQLException if a database error occurs while executing the SQL query
	 */
	public Report generateOrderReport(LocalDateTime date, String ekrutLocation, String area) {
		// Get all the locations of the given area
		ArrayList<String> areaLocations = reportDAO.fetchFacilitiesByArea(area);
		// Create a new Map to hold all the orders at the given area
		Map<String, ArrayList<Order>> areaOrders = new HashMap<>();
		// Iterate trough each location at the area
		for(String location : areaLocations) {
			
			// Get a list of all orders at the given month and location
			ArrayList<Order> locationOrders = reportDAO.fetchAllMonthlyOrders(date, location);
			// Put those orders at areaOrders
			areaOrders.put(location, locationOrders);
		}
		// Create a new Map to hold the topSellers items at the given area and month
		Map<String, Integer> topSellers = new HashMap<>();
		// proccesOrderArea process all the Orders into a order report
		Map<String, ArrayList<Integer>> orderReportData = processOrdersArea(areaOrders);
		
		// Create those for later calculation
		int areaTotalOrders = 0;
		int areaTotalOrdersInILS = 0;
		// Iterate  trough each list of orders(by location)
		for (Map.Entry<String, ArrayList<Order>> entry : areaOrders.entrySet()) {	
			// Process each list of orders: Adds to each item the number of times it was ordered in the list
			topSellers = processOrdersLocation(entry.getValue(), topSellers);
			
			int totalOrders = 0;
			int totalOrdersInILS = 0;	
			// Calculate totalOrders and totalOrdersInILS for each order at the current location
			for (Order order : entry.getValue()) {
				totalOrders += 1;
				totalOrdersInILS += order.getSumAmount();
			}
			// After calculation for each location add to areaTotal..
			areaTotalOrders += totalOrders;
			areaTotalOrdersInILS += totalOrdersInILS;
		}
		
		// Sort out the top 5 best sellers
		topSellers = processItemOrders(topSellers);
		
		// Create a new report object with the generated data
		Report report = new Report(null, ReportType.ORDER, date, ekrutLocation, area,
				areaTotalOrders, areaTotalOrdersInILS, orderReportData, topSellers);
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
	public Report generateCustomerReport(LocalDateTime date, String ekrutLocation, String area) {
		// Get a list of all customer orders for the given date and location	
		ArrayList<String> allCustomersOrders = reportDAO.getAllCustomersOrders(date, ekrutLocation);
		// Process the customer orders to count the number of orders made by each customer
		Map<String, Integer> customersOrders = ProcessCustomersOrders(allCustomersOrders);
		// Create a histogram of customer orders by dividing customers into categories based on their order count
		Map<String, Integer> customersHistogram = createCustomersHistogram(customersOrders);
		// Create a new report object with the generated data
		Report report = new Report(null, ReportType.CUSTOMER, date, ekrutLocation, area, customersHistogram);
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
	public Report generateInventoryReport(LocalDateTime date, String ekrutLocation, String area) {
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
				ekrutLocation, area, inventoryReportData, facilityThreshold);
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
	 * This method processes a map of item quantities and generates a map of the top 5 best-selling items.
	 * 
	 * @param itemsQuantityInOrders the map of item quantities to process
	 * @return the map of the top 5 best-selling items
	 */
	private Map<String, Integer> processItemOrders(Map<String, Integer> itemsQuantityInOrders){
		// Sort the items by quantity in descending order
		List<Map.Entry<String, Integer>> sortedItemsInOrders = new ArrayList<>(itemsQuantityInOrders.entrySet());
		
		Collections.sort(sortedItemsInOrders, new Comparator<Map.Entry<String, Integer>>() {
			
		    @Override
		    public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
		        return entry2.getValue().compareTo(entry1.getValue());
		    }
		    
		});

		Map<String, Integer> bestSellers = new HashMap<>();
		
		for (int i = 0; i < 5; i++) {
		    Map.Entry<String, Integer> entry = sortedItemsInOrders.get(i);
		    if (i < 5) {
		    	bestSellers.put(entry.getKey(), entry.getValue());
		    }
		}
		
	    // Return the map of best-selling items
		return bestSellers;
	} 
	
	/**
	 * This method processes a list of orders and generates a map of item quantities.
	 * 
	 * @param orders the list of orders to process
	 * @param itemsQuantityInOrders the map to store the quantities of each item
	 * @return the map of item quantities
	 */
	private Map<String, Integer> processOrdersLocation(ArrayList<Order> orders, Map<String, Integer> itemsQuantityInOrders){
		
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
	
	private Map<String, ArrayList<Integer>> processOrdersArea(Map<String, ArrayList<Order>> areaOrders){

		Map<String, ArrayList<Integer>> orderReportData = new HashMap<>();
		
		for (Map.Entry<String, ArrayList<Order>> entry : areaOrders.entrySet()) {
			// Iterate trough orders of given location
			ArrayList<Integer> locationOrdersData = new ArrayList<>();
			
			// Initialize locationOrdersData
			for (int i = 0; i < 8; i++) 
				locationOrdersData.add(0);
			
			int totalOrders = 0;
			int totalOrdersInILS = 0;
			
			for (Order order : entry.getValue()) {
				//locationOrdersData(0) and (4) saved for total
				totalOrders += 1;
				totalOrdersInILS += order.getSumAmount();
				switch (order.getType()) {
					case SHIPMENT:
						locationOrdersData.add(1, locationOrdersData.get(1) + 1);
				    	locationOrdersData.add(5, locationOrdersData.get(5) + order.getSumAmount());
				    	break;
				    case PICKUP:
				    	locationOrdersData.add(2, locationOrdersData.get(2) + 1);
				    	locationOrdersData.add(6, locationOrdersData.get(6) + order.getSumAmount());
				    	break;
				    case REMOTE:
				    	locationOrdersData.add(3, locationOrdersData.get(3) + 1);
				    	locationOrdersData.add(7, locationOrdersData.get(7) + order.getSumAmount());
				    	break;
				    default:
				    	break;
				  }
			}
			
			locationOrdersData.add(0, locationOrdersData.get(0) + totalOrders);
			locationOrdersData.add(4, locationOrdersData.get(4) + totalOrdersInILS);
			
			orderReportData.put(entry.getKey(), locationOrdersData);
		}
		
		return orderReportData;
	}
	
}
