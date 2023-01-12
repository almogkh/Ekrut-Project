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
	Report result;
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
	}
	
	@Test
	public void fetchMonthlyOrdersReport_UAE_JAN_2022() {
		
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
		
		result = reportDAO.fetchOrderReportByID(1);
		
		assertEquals(1, result.getReportID());
		assertEquals(ReportType.ORDER, result.getReportType(), "Incorrect report type");
		assertEquals(jan_2022, result.getDate(), "Incorrect report date");
		assertEquals("UAE", result.getEkrutLocation(), "Incorrect report location");
		assertEquals("UAE", result.getArea(), "Incorrect report area");
		assertEquals(200, result.getTotalOrders(), "Incorrect report total orders");
		assertEquals(1500, result.getTotalOrdersInILS(), "Incorrect report total orders in ILS");
		//implement equals
		assertEquals(expectedTopSellers, result.getTopSellersData(), "Incorrect top sellers data");
		assertEquals(expectedOrderData, result.getOrderReportData(), "Incorrect order report data");

	}
	
	@Test 
	public void fetchMonthlyInventoryReport_North_Akko_JAN_2022() throws Exception {
		
		expectedInventoryReportData = new HashMap<>();
		 // populate the map with the expected data
		expectedInventoryReportData.put("Bamba", new ArrayList<>(Arrays.asList(1)));
		expectedInventoryReportData.put("Coke", new ArrayList<>(Arrays.asList(1)));
		expectedInventoryReportData.put("Pepsi", new ArrayList<>(Arrays.asList(1)));
		expectedInventoryReportData.put("Fanta", new ArrayList<>(Arrays.asList(1)));
		expectedInventoryReportData.put("Oreo", new ArrayList<>(Arrays.asList(1)));
		expectedInventoryReportData.put("Bisli", new ArrayList<>(Arrays.asList(1)));

		result = reportDAO.fetchInventoryReportByID(2);
		
		assertEquals(2, result.getReportID());
		assertEquals(ReportType.INVENTORY, result.getReportType(), "Incorrect report type");
		assertEquals(jan_2022, result.getDate(), "Incorrect report date");
		assertEquals("Akko", result.getEkrutLocation(), "Incorrect report location");
		assertEquals("North", result.getArea(), "Incorrect report area");
		//implement equals
		assertEquals(expectedInventoryReportData, result.getInventoryReportData(),
				"Incorrect inventory report data");

	}
	
	@Test 
	public void fetchMonthlyCustomersReport_South_Dimona_JAN_2022() {
		 
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
		
		result = reportDAO.fetchCustomerReportByID(3);
		
		assertEquals(3, result.getReportID());
		assertEquals(ReportType.CUSTOMER, result.getReportType(), "Incorrect report type");
		assertEquals(jan_2022, result.getDate(), "Incorrect report date");
		assertEquals("Dimona", result.getEkrutLocation(), "Incorrect report location");
		assertEquals("South", result.getArea(), "Incorrect report area");

	}
	
	@Test
	public void createReportTest() {
		// add the rest of the data
		Report reportTest = new Report(4, ReportType.INVENTORY, jan_2022, "Nazereth", "North");
		
		reportDAO.createReport(reportTest);
		
		Report result = reportDAO.fetchReport(jan_2022, "Nazereth", "North", ReportType.INVENTORY);
		
		assertEquals(4, result.getReportID());
		assertEquals(ReportType.INVENTORY, result.getReportType(), "Incorrect report type");
		assertEquals(jan_2022, result.getDate(), "Incorrect report date");
		assertEquals("Nazereth", result.getEkrutLocation(), "Incorrect report location");
		assertEquals("North", result.getArea(), "Incorrect report area");
	}
	
	@Test
	public void createCustomerReportTest() {
		// add the rest of the data
		Report reportTest = new Report(4, ReportType.CUSTOMER, jan_2022, "Nazereth", "North");
		 
		reportDAO.createCustomerReport(reportTest);
		
		Report result = reportDAO.fetchReport(jan_2022, "Nazereth", "North", ReportType.CUSTOMER);
		
		assertEquals(4, result.getReportID());
		assertEquals(ReportType.INVENTORY, result.getReportType(), "Incorrect report type");
		assertEquals(jan_2022, result.getDate(), "Incorrect report date");
		assertEquals("Nazereth", result.getEkrutLocation(), "Incorrect report location");
		assertEquals("North", result.getArea(), "Incorrect report area");
		//more tests
	}
	
	@Test
	public void createInventoryReportTest() {
		// add the rest of the data
		Report reportTest = new Report(4, ReportType.INVENTORY, jan_2022, "Nazereth", "North");
		 
		reportDAO.createCustomerReport(reportTest);
		
		Report result = reportDAO.fetchReport(jan_2022, "Nazereth", "North", ReportType.INVENTORY);
		
		assertEquals(4, result.getReportID());
		assertEquals(ReportType.INVENTORY, result.getReportType(), "Incorrect report type");
		assertEquals(jan_2022, result.getDate(), "Incorrect report date");
		assertEquals("Nazereth", result.getEkrutLocation(), "Incorrect report location");
		assertEquals("North", result.getArea(), "Incorrect report area");
		//more tests
	}
	
	@Test
	public void createOrderReportTest() {
		// add the rest of the data
		Report reportTest = new Report(4, ReportType.ORDER, jan_2022, "North", "North");
		 
		reportDAO.createCustomerReport(reportTest);
		
		Report result = reportDAO.fetchReport(jan_2022, "North", "North", ReportType.ORDER);
		
		assertEquals(4, result.getReportID());
		assertEquals(ReportType.INVENTORY, result.getReportType(), "Incorrect report type");
		assertEquals(jan_2022, result.getDate(), "Incorrect report date");
		assertEquals("Nazereth", result.getEkrutLocation(), "Incorrect report location");
		assertEquals("North", result.getArea(), "Incorrect report area");
		//more tests
	}


}
