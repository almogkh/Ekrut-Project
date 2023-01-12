package ekrut.server.db;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ekrut.entity.Report;
import ekrut.entity.ReportType;

class ReportDAOTest {

	Report JanUAEReport;
	ReportDAO reportDAO;
	String URL = "jdbc:mysql://localhost/test_ekrut?serverTimezone=IST";
	String USERNAME = "root";
	String PASSWORD = "Aa123456";
	LocalDateTime jan_2022;
	LocalDateTime may_2021;
	Report resultReport;
	Report expectedReport;
	Map<String, ArrayList<Integer>> expectedOrderData;
	Map<String, Integer> expectedTopSellers;
	Map<String, ArrayList<Integer>> expectedInventoryReportData;
	Map<String, Integer> expectedCustomerReportData;
	Map<Integer, Integer> expectedCustomersOrdersByDate;

	@BeforeEach
	void setUp() throws Exception {

		DBController DBcontroller = new DBController(URL, USERNAME, PASSWORD);
		reportDAO = new ReportDAO(DBcontroller);
		jan_2022 = LocalDateTime.now();
		jan_2022 = jan_2022.withMonth(1);
		jan_2022 = jan_2022.withYear(2022);
		jan_2022 = jan_2022.withDayOfMonth(31);
		
		may_2021 = LocalDateTime.now();
		may_2021 = may_2021.withMonth(5);
		may_2021 = may_2021.withYear(2021);
		may_2021 = may_2021.withDayOfMonth(31);
	}
	
	@Test
	public void testFetchMonthlyOrdersReport_UAE_JAN_2022() {
		
		expectedOrderData = new HashMap<>();
        // populate the map with the expected data
		expectedOrderData.put("Abu Dhabi", new ArrayList<>(Arrays.asList(1,2,3,4,5,6)));
		expectedOrderData.put("Amjan", new ArrayList<>(Arrays.asList(1,2,3,4,5,6)));
		expectedOrderData.put("Dubai", new ArrayList<>(Arrays.asList(1,2,3,4,5,6)));
		expectedOrderData.put("Fujarah", new ArrayList<>(Arrays.asList(1,2,3,4,5,6)));
		expectedOrderData.put("Ras_Al_Khaimah", new ArrayList<>(Arrays.asList(1,2,3,4,5,6)));
		expectedOrderData.put("Sharjah", new ArrayList<>(Arrays.asList(1,2,3,4,5,6)));
		expectedOrderData.put("Umm_Al_Quwain", new ArrayList<>(Arrays.asList(1,2,3,4,5,6)));

		expectedTopSellers = new HashMap<>();
        // populate the map with the expected data
		expectedTopSellers.put("Bamba", 10);
		expectedTopSellers.put("Bisli", 20);
		expectedTopSellers.put("Chips", 30);
		expectedTopSellers.put("Coke", 40);
		expectedTopSellers.put("Pepsi", 50);
		
		resultReport = reportDAO.fetchOrderReportByID(1);
		
		assertEquals(1, resultReport.getReportID(),
				"Incorrect reportID");
		assertEquals(ReportType.ORDER, resultReport.getReportType(),
				"Incorrect report type");
		assertEquals(jan_2022, resultReport.getDate(),
				"Incorrect report date");
		assertEquals("UAE", resultReport.getEkrutLocation(),
				"Incorrect report location");
		assertEquals("UAE", resultReport.getArea(),
				"Incorrect report area");
		assertEquals(200, resultReport.getTotalOrders(),
				"Incorrect report total orders");
		assertEquals(1500, resultReport.getTotalOrdersInILS(),
				"Incorrect report total orders in ILS");
		assertEquals(expectedTopSellers, resultReport.getTopSellersData(),
				"Incorrect top sellers data");
		assertEquals(expectedOrderData, resultReport.getOrderReportData(),
				"Incorrect order report data");

	}
	// add not exist report test(equals null)
	
	@Test 
	public void testFetchMonthlyInventoryReport_North_Akko_JAN_2022(){
		
		expectedInventoryReportData = new HashMap<>();
		 // populate the map with the expected data
		expectedInventoryReportData.put("Bamba", new ArrayList<>(Arrays.asList(1)));
		expectedInventoryReportData.put("Coke", new ArrayList<>(Arrays.asList(1)));
		expectedInventoryReportData.put("Pepsi", new ArrayList<>(Arrays.asList(1)));
		expectedInventoryReportData.put("Fanta", new ArrayList<>(Arrays.asList(1)));
		expectedInventoryReportData.put("Oreo", new ArrayList<>(Arrays.asList(1)));
		expectedInventoryReportData.put("Bisli", new ArrayList<>(Arrays.asList(1)));

		resultReport = reportDAO.fetchInventoryReportByID(2);
		
		assertEquals(2, resultReport.getReportID(),
				"Incorrect reportID");
		assertEquals(ReportType.INVENTORY, resultReport.getReportType(),
				"Incorrect report type");
		assertEquals(jan_2022, resultReport.getDate(),
				"Incorrect report date");
		assertEquals("Akko", resultReport.getEkrutLocation(),
				"Incorrect report location");
		assertEquals("North", resultReport.getArea(),
				"Incorrect report area");
		assertEquals(expectedInventoryReportData, resultReport.getInventoryReportData(),
				"Incorrect inventory report data");
		assertEquals(15, resultReport.getThreshold(),
				"Incorrect threshold");

	}
	
	@Test 
	public void testFetchMonthlyCustomerReport_South_Dimona_JAN_2022() {
		 
		expectedCustomerReportData = new HashMap<>();
		 // populate the map with the expected data
		expectedCustomerReportData.put("1", 1);
		expectedCustomerReportData.put("2", 1);
		expectedCustomerReportData.put("3", 1);
		expectedCustomerReportData.put("4", 1);
		expectedCustomerReportData.put("5", 1);
		expectedCustomerReportData.put("6+", 1);
		
		expectedCustomersOrdersByDate = new HashMap<>();
		// populate the map with the expected data
		for (int i = 1; i <= 31; i++) {
			expectedCustomersOrdersByDate.put(i, 1);
		}
		
		resultReport = reportDAO.fetchCustomerReportByID(3);
		
		assertEquals(3, resultReport.getReportID(),
				"Incorrect reportID");
		assertEquals(ReportType.CUSTOMER, resultReport.getReportType(),
				"Incorrect report type");
		assertEquals(jan_2022, resultReport.getDate(),
				"Incorrect report date");
		assertEquals("Dimona", resultReport.getEkrutLocation(),
				"Incorrect report location");
		assertEquals("South", resultReport.getArea(),
				"Incorrect report area");
		assertEquals(expectedCustomerReportData, resultReport.getCustomerReportData(),
				"Incorrect customer report data");
		assertEquals(expectedCustomersOrdersByDate, resultReport.getCustomersOrdersByDate(),
				"Incorrect customer orders by date");

	}
	
	@Test
	public void testCreateCustomerReport_North_Nazereth_may_2021() {
		
		expectedCustomerReportData = new HashMap<>();
		 // populate the map with the expected data
		expectedCustomerReportData.put("1", 1);
		expectedCustomerReportData.put("2", 1);
		expectedCustomerReportData.put("3", 1);
		expectedCustomerReportData.put("4", 1);
		expectedCustomerReportData.put("5", 1);
		expectedCustomerReportData.put("6+", 1);
		
		expectedCustomersOrdersByDate = new HashMap<>();
		// populate the map with the expected data
		for (int i = 1; i <= 31; i++) {
			expectedCustomersOrdersByDate.put(i, 1);
		} 
		
		expectedReport = new Report(null, ReportType.CUSTOMER, may_2021, "Nazereth", "North",
				expectedCustomerReportData, expectedCustomersOrdersByDate);
		
		// check this later
		assertNull(expectedReport.getReportID());
		 
		reportDAO.createCustomerReport(expectedReport);
		
		resultReport = reportDAO.fetchReport(may_2021, "Nazereth", "North", ReportType.CUSTOMER);
		
		assertEquals(expectedReport.getReportID(), resultReport.getReportID());
		assertEquals(expectedReport.getReportType(), resultReport.getReportType(),
				"Incorrect report type");
		assertEquals(expectedReport.getDate(), resultReport.getDate(),
				"Incorrect report date");
		assertEquals(expectedReport.getEkrutLocation(), resultReport.getEkrutLocation(),
				"Incorrect report location");
		assertEquals(expectedReport.getArea(), resultReport.getArea(),
				"Incorrect report area");
		assertEquals(expectedReport.getCustomerReportData(), resultReport.getCustomerReportData(),
				"Incorrect customer report data");
		assertEquals(expectedReport.getCustomersOrdersByDate(), resultReport.getCustomersOrdersByDate(),
				"Incorrect customer orders by date");
	}
	 
	@Test
	public void testCreateInventoryReport_North_Afula_may_2021() {
		
		expectedInventoryReportData = new HashMap<>();
		 // populate the map with the expected data
		expectedInventoryReportData.put("Bamba", new ArrayList<>(Arrays.asList(1)));
		expectedInventoryReportData.put("Coke", new ArrayList<>(Arrays.asList(1)));
		expectedInventoryReportData.put("Pepsi", new ArrayList<>(Arrays.asList(1)));
		expectedInventoryReportData.put("Fanta", new ArrayList<>(Arrays.asList(1)));
		expectedInventoryReportData.put("Oreo", new ArrayList<>(Arrays.asList(1)));
		expectedInventoryReportData.put("Bisli", new ArrayList<>(Arrays.asList(1)));
		
		expectedReport = new Report(null, ReportType.INVENTORY, may_2021, "Afula", "North",
				expectedInventoryReportData, 12);
		 
		reportDAO.createInventoryReport(expectedReport);
		
		resultReport = reportDAO.fetchReport(may_2021, "Afula", "North", ReportType.INVENTORY);
		
		assertEquals(expectedReport.getReportID(), resultReport.getReportID(),
				"Incorrect reportID");
		assertEquals(expectedReport.getReportType(), resultReport.getReportType(),
				"Incorrect report type");
		assertEquals(expectedReport.getDate(), resultReport.getDate(),
				"Incorrect report date");
		assertEquals(expectedReport.getEkrutLocation(), resultReport.getEkrutLocation(),
				"Incorrect report location");
		assertEquals(expectedReport.getArea(), resultReport.getArea(),
				"Incorrect report area");
		assertEquals(expectedReport.getInventoryReportData(), resultReport.getInventoryReportData(),
				"Incorrect inventory report data");
		assertEquals(expectedReport.getThreshold(), resultReport.getThreshold(),
				"Incorrect threshold");
	}
	
	@Test
	public void testCreateOrderReport_North_may_2021() {
		
		expectedOrderData = new HashMap<>();
        // populate the map with the expected data
		expectedOrderData.put("Abu Dhabi", new ArrayList<>(Arrays.asList(1,2,3,4,5,6)));
		expectedOrderData.put("Amjan", new ArrayList<>(Arrays.asList(1,2,3,4,5,6)));
		expectedOrderData.put("Dubai", new ArrayList<>(Arrays.asList(1,2,3,4,5,6)));
		expectedOrderData.put("Fujarah", new ArrayList<>(Arrays.asList(1,2,3,4,5,6)));
		expectedOrderData.put("Ras_Al_Khaimah", new ArrayList<>(Arrays.asList(1,2,3,4,5,6)));
		expectedOrderData.put("Sharjah", new ArrayList<>(Arrays.asList(1,2,3,4,5,6)));
		expectedOrderData.put("Umm_Al_Quwain", new ArrayList<>(Arrays.asList(1,2,3,4,5,6)));

		expectedTopSellers = new HashMap<>();
        // populate the map with the expected data
		expectedTopSellers.put("Bamba", 10);
		expectedTopSellers.put("Bisli", 20);
		expectedTopSellers.put("Chips", 30);
		expectedTopSellers.put("Coke", 40);
		expectedTopSellers.put("Pepsi", 50);
		
		expectedReport = new Report(null, ReportType.ORDER, may_2021, "North", "North",
				10, 1000, 5, 500, expectedOrderData, expectedTopSellers);
		 
		reportDAO.createCustomerReport(expectedReport);
		
		resultReport = reportDAO.fetchReport(may_2021, "North", "North", ReportType.ORDER);
		
		assertEquals(expectedReport.getReportID(), resultReport.getReportID(),
				"Incorrect reportID");
		assertEquals(expectedReport.getReportType(), resultReport.getReportType(),
				"Incorrect report type");
		assertEquals(expectedReport.getDate(), resultReport.getDate(),
				"Incorrect report date");
		assertEquals(expectedReport.getEkrutLocation(), resultReport.getEkrutLocation(),
				"Incorrect report location");
		assertEquals(expectedReport.getArea(), resultReport.getArea(),
				"Incorrect report area");
		assertEquals(expectedReport.getTotalOrders(), resultReport.getTotalOrders(),
				"Incorrect report total orders");
		assertEquals(expectedReport.getTotalOrdersInILS(), resultReport.getTotalOrdersInILS(),
				"Incorrect report total orders in ILS");
		assertEquals(expectedReport.getTopSellersData(), resultReport.getTopSellersData(),
				"Incorrect top sellers data");
		assertEquals(expectedReport.getOrderReportData(), resultReport.getOrderReportData(),
				"Incorrect order report data");
	}
	
	
}
