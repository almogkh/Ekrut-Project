package ekrut.server.managers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import ekrut.entity.InventoryItem;
import ekrut.entity.Item;
import ekrut.entity.Order;
import ekrut.entity.OrderItem;
import ekrut.entity.Report;
import ekrut.entity.ReportType;
import ekrut.entity.User;
import ekrut.net.ReportRequest;
import ekrut.net.ReportResponse;
import ekrut.net.ResultType;
import ekrut.server.TimeScheduler;
import ekrut.server.db.DBController;
import ekrut.server.db.InventoryItemDAO;
import ekrut.server.db.OrderDAO;
import ekrut.server.db.ReportDAO;
import ekrut.server.db.UserDAO;

/**
 * Manages Report management on the server side.
 * @author Tal Gaon
 */
public class ServerReportManager extends AbstractServerManager<ReportRequest, ReportResponse> {
	
	private ReportDAO reportDAO;
	private InventoryItemDAO inventoryItemDAO;
	private UserDAO userDAO;
	
	public ServerReportManager(DBController con) {
		super(ReportRequest.class, new ReportResponse(ResultType.UNKNOWN_ERROR));
		OrderDAO orderDAO = new OrderDAO(con);
		reportDAO = new ReportDAO(con, orderDAO);
		inventoryItemDAO = new InventoryItemDAO(con);
		userDAO = new UserDAO(con);
	}
	
	@Override
	protected ReportResponse handleRequest(ReportRequest request, User user) {
		switch (request.getReportRequestType()) {
		case FETCH_FACILITIES:
			return fetchFacilitiesByArea(request, user);
		case FETCH_REPORT:
			return fetchReport(request, user);
		default:
			return new ReportResponse(ResultType.UNKNOWN_ERROR);
		}
	}
	
	/**
	 * Retrieves a report based on the provided request parameters.
	 *
	 * @param reportRequest the request containing the parameters to use for report retrieval
	 * @param user 
	 * @return a response containing the retrieved report or an error result if the report was not found
	 */
	public ReportResponse fetchReport(ReportRequest reportRequest, User user) {

		Report report = reportDAO.fetchReport(reportRequest.getDate(), reportRequest.getLocation(), reportRequest.getArea(), reportRequest.getReportType());
		
		if (report == null) 
			return new ReportResponse(ResultType.NOT_FOUND);
	
		return new ReportResponse(ResultType.OK, report); 
	}
	
	/**
	 * Retrieves a list of facilities in a specific area.
	 *
	 * @param reportRequest the request containing the area to retrieve facilities for
	 * @param client the client requesting the list of facilities
	 * @return a response containing the list of facilities or an error result if no facilities were found in the specified area
	 */
	public ReportResponse fetchFacilitiesByArea(ReportRequest reportRequest, User user) {
		
		ArrayList<String> facilities = reportDAO.fetchFacilitiesByArea(reportRequest.getArea());
		if (facilities == null) 
			return new ReportResponse(ResultType.NOT_FOUND);
		
		return new ReportResponse(ResultType.OK, facilities); 
	}
	
	/**
	 * Generates an order report for a specific area and date.
	 *
	 * @param date the date to generate the report for
	 * @param area the area to generate the report for
	 * @return the generated order report
	 */
	public Report generateOrderReport(LocalDateTime date,String area) {
		// Get all the locations of the given area
		ArrayList<String> areaLocations = reportDAO.fetchFacilitiesByArea(area);
		// Create a new Map to hold all the orders at the given area by locations without shipment orders
		Map<String, ArrayList<Order>> areaOrdersByLocations = new HashMap<>();
		// Iterate trough each location at the area
		for(String location : areaLocations) {
			// Get a list of all orders at the given month and location
			ArrayList<Order> locationOrders = reportDAO.fetchAllMonthlyOrdersByLocation(date, location);
			// Put those orders at areaOrders
			areaOrdersByLocations.put(location, locationOrders);
		}
		// Create a new Map to hold the topSellers items at the given area and month
		Map<String, Integer> topSellers = new HashMap<>();
		// proccesOrderArea process all the Orders into a order report
		Map<String, ArrayList<Integer>> orderReportData = processOrdersArea(areaOrdersByLocations);
		/* shipmentOrders will contain all orders of the shipment type.
		 * The ArrayList will be filtered to include only orders from customers in the specified area.*/
		ArrayList<Order> shipmentOrders = filterShipmentOrdersByUserArea(reportDAO.fetchAllMonthlyShipmentOrders(date), area);
		
		
		int areaTotalOrders = 0;
		int areaTotalOrdersInILS = 0;
		// Iterate  trough each list of orders(by location)
		for (Map.Entry<String, ArrayList<Order>> entry : areaOrdersByLocations.entrySet()) {	
			// Process each list of orders: Adds to each item the number of times it was ordered in the list
			topSellers = countItemQuantitiesByLocation(entry.getValue(), topSellers);
			
			int totalOrders = 0;
			int totalOrdersInILS = 0;	
			// Calculate totalOrders and totalOrdersInILS for each order at the current location
			for (Order order : entry.getValue()) {
				totalOrders += 1;
				totalOrdersInILS += Math.round(order.getSumAmount());
			}
			// After calculation for each location add to areaTotal..
			areaTotalOrders += totalOrders;
			areaTotalOrdersInILS += totalOrdersInILS;
		}
		Integer shipmentTotalOrders = 0;
		Integer shipmentTotalOrderInILS = 0;
		
		// Calculate the same for shipment Orders
		for (Order order : shipmentOrders) {
			areaTotalOrders += 1;
			shipmentTotalOrders += 1;
			
			areaTotalOrdersInILS += order.getSumAmount();
			shipmentTotalOrderInILS += Math.round(order.getSumAmount());
		}
		// Add the items at shipment orders into topSellers
		topSellers = countItemQuantitiesByLocation(shipmentOrders, topSellers);
		
		// Sort out the top 5 best sellers
		topSellers = sortItemOrdersByQuantity(topSellers);
		
		// Create a new report object with the generated data
		Report report = new Report(null, ReportType.ORDER, date, area, area,
				areaTotalOrders, areaTotalOrdersInILS, shipmentTotalOrders, shipmentTotalOrderInILS, orderReportData, topSellers);
		// Return the report
		return report;
	}
	
	/**
	 * Filters a list of orders by the area of the user associated with each order.
	 *
	 * @param orders the list of orders to filter
	 * @param area the area to filter by
	 * @return the filtered list of orders
	 */
	private ArrayList<Order> filterShipmentOrdersByUserArea(ArrayList<Order> orders, String area) {
		ArrayList<Order> filterdList = new ArrayList<>();
	    // Iterate over the list of orders
		for (Order order : orders) {
			// Check if the user at the order is from the given area
			if (userDAO.fetchUserByUsername(order.getUsername()).getArea().equals(area)) {
				filterdList.add(order);
			}
		}
		return filterdList;
	}

	/**
	 * Generates a customer report for a specific area, location, and date.
	 *
	 * @param date the date to generate the report for
	 * @param ekrutLocation the location to generate the report for
	 * @param area the area to generate the report for
	 * @return the generated customer report
	 */
	public Report generateCustomerReport(LocalDateTime date, String ekrutLocation, String area) {
		// Get a list of all customer orders for the given date and location	
		ArrayList<String> allCustomersOrders = reportDAO.getAllCustomersOrdersByNameWithOutShipment(date, ekrutLocation); 
		// Process the customer orders to count the number of orders made by each customer
		Map<String, Integer> customersOrders = countOrdersByCustomer(allCustomersOrders);
		// Create a histogram of customer orders by dividing customers into categories based on their order count
		Map<String, Integer> customersHistogram = createCustomersHistogram(customersOrders);

		// Get a list of all customer orders by date for the given date and location
		ArrayList<LocalDateTime> allCustomersOrdersByDate = reportDAO.getAllCustomersOrdersByDateWithOutShipment(date, ekrutLocation);
		// Process the customer orders by date to count the number of orders made by each customer on each day
		Map<Integer, Integer> customersOrdersByDate = countOrdersByCustomerAndDate(allCustomersOrdersByDate);
		// Create a new report object with the generated data
		Report report = new Report(null, ReportType.CUSTOMER, date, ekrutLocation, area, customersHistogram, customersOrdersByDate);
		// Return the report
		return report;
	}
	

	/**
	 * Generates an inventory report for a specific area, location, and date.
	 *
	 * @param date the date to generate the report for
	 * @param ekrutLocation the location to generate the report for
	 * @param area the area to generate the report for
	 * @return the generated inventory report
	 */
	public Report generateInventoryReport(LocalDateTime date, String ekrutLocation, String area) {
		// Get a list of all threshold breaches for the given date and location
		ArrayList<String> thersholdBreaches = reportDAO.getThresholdBreaches(date, ekrutLocation);
		// Process the threshold breaches to count the number of breaches for each item
		Map<String, Integer> tresholdBreachesCounted = CountThresholdBreaches(thersholdBreaches);
		// Get the list of all items in the location
		ArrayList<InventoryItem> allItemsInLocation;
		try {
			allItemsInLocation = inventoryItemDAO.fetchAllItemsByEkrutLocation(ekrutLocation);
		} catch (DeadlockException e) {
			allItemsInLocation = new ArrayList<>();
		}
		int facilityThreshold = 0;
		// If the list of items is not empty, extract the facility threshold from the first item
		if (!allItemsInLocation.isEmpty()) {
			facilityThreshold = allItemsInLocation.get(0).getItemThreshold();
		}
		// Process the inventory data
		Map<String, ArrayList<Integer>> inventoryReportData = 
				createInventoryReportData(allItemsInLocation, tresholdBreachesCounted);
		// Create a new report object with the generated data
		Report report = new Report(null, ReportType.INVENTORY, date,
				ekrutLocation, area, inventoryReportData, facilityThreshold);
		// Return the report
		return report;
	}
	
	/**
	 * Counts the number of threshold breaches for each item.
	 * 
	 * @param thersholdBreaches a list of item names for which threshold breach have been triggered
	 * @return a map of item names to the number of threshold breaches for that item
	 */
	private Map<String, Integer> CountThresholdBreaches(ArrayList<String> thersholdBreaches){
		
		Map<String, Integer> tresholdBreachesCounted = new HashMap<>();
		// Iterate over the list of item names
		for (String itemName : thersholdBreaches) {
			// change doc
			tresholdBreachesCounted.merge(itemName, 1, Integer::sum);
		}
		// Return the map of threshold breaches counts
		return tresholdBreachesCounted;
	}
	 
	/**
	 * Processes inventory data for a given location.
	 * 
	 * @param allItemsInLocation a list of all inventory items in the location
	 * @param tresholdAlertCounted a map of item names to threshold alert counts
	 * @return a map of item names to lists containing the item quantity and threshold alert count
	 */
	private Map<String, ArrayList<Integer>> createInventoryReportData(
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
			// Create a list containing the item threshold alert count
			ArrayList<Integer> temp = new ArrayList<>();
			temp.add(inventoryItem.getItemThreshold());
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
	private Map<String, Integer> countOrdersByCustomer(ArrayList<String> allCustomersOrders){
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
	 * Processes a list of customer order dates and returns a map of order counts by date.
	 *
	 * @param allCustomersOrdersByDate a list of customer order dates
	 * @return a map of order counts by date
	 */
	private Map<Integer, Integer> countOrdersByCustomerAndDate(ArrayList<LocalDateTime> allCustomersOrdersByDate) {
		Map<Integer, Integer> customersOrdersByDate = new HashMap<>();
		// Iterate through the list of customer orders
		for (LocalDateTime date : allCustomersOrdersByDate) {
			// Increment the count of orders for the corresponding customer in the map
			customersOrdersByDate.merge(date.getDayOfMonth(), 1, Integer::sum);
		}
		//add: add to the map all the dates with zero orders
		for (int i = 1; i <= 31; i++) {
			if (!customersOrdersByDate.containsKey(i)) {
				customersOrdersByDate.put(i, 0);
			}
		}
		return customersOrdersByDate;
	}


	
	/**
	 * This method processes a map of item quantities and generates a map of the top 5 best-selling items.
	 * 
	 * @param itemsQuantityInOrders the map of item quantities to process
	 * @return the map of the top 5 best-selling items
	 */
	private Map<String, Integer> sortItemOrdersByQuantity(Map<String, Integer> itemsQuantityInOrders){
		// Sort the items by quantity in descending order
		List<Map.Entry<String, Integer>> sortedItemsInOrders = new ArrayList<>(itemsQuantityInOrders.entrySet());
		
		Collections.sort(sortedItemsInOrders, new Comparator<Map.Entry<String, Integer>>() {
			
		    @Override
		    public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
		        return -entry2.getValue().compareTo(entry1.getValue());
		    }
		});

		Map<String, Integer> bestSellers = new HashMap<>();
		
		for (Map.Entry<String, Integer> entry : sortedItemsInOrders) {
			if (bestSellers.size() == 5)
				break;
		    bestSellers.put(entry.getKey(), entry.getValue());
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
	private Map<String, Integer> countItemQuantitiesByLocation(ArrayList<Order> orders, Map<String, Integer> itemsQuantityInOrders){
		
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
	
	/**
	 * Processes a map of orders by location and returns a map of order data for each location.
	 *
	 * @param areaOrders a map of orders by location
	 * @return a map of order data for each location
	 */
	private Map<String, ArrayList<Integer>> processOrdersArea(Map<String, ArrayList<Order>> areaOrders){

		Map<String, ArrayList<Integer>> orderReportData = new HashMap<>();
		
		for (Map.Entry<String, ArrayList<Order>> entry : areaOrders.entrySet()) {
			// Iterate trough orders of given location
			ArrayList<Integer> locationOrdersData = new ArrayList<>();
			
			// Initialize locationOrdersData
			for (int i = 0; i < 6; i++) 
				locationOrdersData.add(0);
			
			int totalOrders = 0;
			int totalOrdersInILS = 0;
			
	        // Iterate through orders of given location
			for (Order order : entry.getValue()) {
				
				totalOrders += 1;
				int sumAmount = Math.round(order.getSumAmount());
				totalOrdersInILS += sumAmount;
				switch (order.getType()) {
				    case PICKUP:
				    	locationOrdersData.set(1, locationOrdersData.get(1) + 1);
				    	locationOrdersData.set(4, locationOrdersData.get(4) + sumAmount);
				    	break;
				    case REMOTE:
				    	locationOrdersData.set(2, locationOrdersData.get(2) + 1);
				    	locationOrdersData.set(5, locationOrdersData.get(5) + sumAmount);
				    	break;
				    default:
				    	break;
				  }
			}
	        // Update locationOrdersData with total values
			locationOrdersData.set(0, locationOrdersData.get(0) + totalOrders);
			locationOrdersData.set(3, locationOrdersData.get(3) + totalOrdersInILS);
	        // Put locationOrdersData in output map
			orderReportData.put(entry.getKey(), locationOrdersData);
		}
		return orderReportData;
	}
	
	
	/**
	 * Generates monthly reports for the given date.
	 *
	 * @param date the date for which to generate the reports
	 */
	public void generateMonthlyReports(LocalDateTime date) {
		String areas[] = {"UAE", "North", "South"};
		
		date = date.withMonth(01);
		date = date.withYear(2023);

		ArrayList<String> locationsList;
		for (String area : areas) {
			locationsList = reportDAO.fetchFacilitiesByArea(area);
			reportDAO.createOrderReport(generateOrderReport(date, area));
			for (String location : locationsList) {
				reportDAO.createCustomerReport(generateCustomerReport(date, location, area));
				reportDAO.createInventoryReport(generateInventoryReport(date, location, area));
			}
		}
	}
	
	private LocalDateTime getEndOfMonth(LocalDateTime now) {
		// Set the date to the last day of the month
		LocalDate nowDate = now.toLocalDate();
		LocalDate endOfMonth = nowDate.with(TemporalAdjusters.lastDayOfMonth());
		// Set the time to 23:59:59
		LocalDateTime expiry = LocalDateTime.of(endOfMonth, LocalTime.of(23, 59, 59));
		return expiry;
	}
	
	private class ReportTask extends TimerTask {

		private LocalDateTime expiry;
		
		public ReportTask(LocalDateTime expiry) {
			this.expiry = expiry;
		}
		
		@Override
		public void run() {
			generateMonthlyReports(expiry); 
			
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime nextExpiry = getEndOfMonth(expiry.plusSeconds(1));
			ReportTask task = new ReportTask(nextExpiry);
			TimeScheduler.getTimer().schedule(task, now.until(nextExpiry, ChronoUnit.MILLIS));
		}
		
	}
	
	/**
	 * Starts a timer that expires at the end of the current month and runs the
	 * generateMonthlyReports method when it expires.
	 *
	 * @return the timer object
	 */
	public void startReportGeneration() {
		LocalDateTime now = LocalDateTime.now();
		generateReportsIfNotUpToDate(now);
		LocalDateTime expiry = getEndOfMonth(now);

		// Start the timer
		ReportTask task = new ReportTask(expiry);
		TimeScheduler.getTimer().schedule(task, now.until(expiry, ChronoUnit.MILLIS));
	}
	
	/**
	 * Generates reports for the previous month if they are not up to date.
	 */
	private void generateReportsIfNotUpToDate(LocalDateTime now) {
		LocalDate nowDate = now.toLocalDate();

		// Check if now is not the last day of the month
		if (nowDate.getDayOfMonth() < nowDate.lengthOfMonth()) {
			// generate reports for the last month if the latest report is not updated
			if (!reportDAO.isLatestReportUpdated(now.minusMonths(1))) {
				generateMonthlyReports(now.minusMonths(1));
			}
		}
	}
}
