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

	Report expectedInventoryReport;

	

	Map<String, ArrayList<Integer>> expectedInventoryReportData;


	@BeforeEach
	void setUp() throws Exception {

		DBController DBcontroller = new DBController(URL, USERNAME, PASSWORD);
		DBcontroller.connect();
		
		reportDAO = new ReportDAO(DBcontroller);
		
		jan_2022 = LocalDateTime.now();
		jan_2022 = jan_2022.withMonth(1);
		jan_2022 = jan_2022.withYear(2022);
		jan_2022 = jan_2022.withDayOfMonth(31);
		
		may_2021 = LocalDateTime.now();
		may_2021 = may_2021.withMonth(5);
		may_2021 = may_2021.withYear(2021);
		may_2021 = may_2021.withDayOfMonth(31);
		
		
		expectedInventoryReportData = new HashMap<>();
		 // populate the map with the expected data
		expectedInventoryReportData.put("Bamba", new ArrayList<>(Arrays.asList(1)));
		expectedInventoryReportData.put("Coke", new ArrayList<>(Arrays.asList(1)));
		expectedInventoryReportData.put("Pepsi", new ArrayList<>(Arrays.asList(1)));
		expectedInventoryReportData.put("Fanta", new ArrayList<>(Arrays.asList(1)));
		expectedInventoryReportData.put("Oreo", new ArrayList<>(Arrays.asList(1)));
		expectedInventoryReportData.put("Bisli", new ArrayList<>(Arrays.asList(1)));
		
		expectedInventoryReport = new Report(null, ReportType.INVENTORY, may_2021, "Afula", "North",
				expectedInventoryReportData, 12);
	}

	@Test 
	public void testFetchMonthlyInventoryReport_North_Akko_JAN_2022(){

		resultReport = reportDAO.fetchInventoryReportByID(2);
		
		assertEquals(2, resultReport.getReportID(),
				"Incorrect reportID");
		assertEquals(ReportType.INVENTORY, resultReport.getReportType(),
				"Incorrect report type");
		assertEquals(jan_2022.toLocalDate(), resultReport.getDate().toLocalDate(),
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
	public void testCreateInventoryReport_North_Afula_may_2021() {
		
		reportDAO.createInventoryReport(expectedInventoryReport);
		
		resultReport = reportDAO.fetchReport(may_2021, "Afula", "North", ReportType.INVENTORY);
		
		assertEquals(expectedInventoryReport.getReportID(), resultReport.getReportID(),
				"Incorrect reportID");
		assertEquals(expectedInventoryReport.getReportType(), resultReport.getReportType(),
				"Incorrect report type");
		assertEquals(expectedInventoryReport.getDate().toLocalDate(), resultReport.getDate().toLocalDate(),
				"Incorrect report date");
		assertEquals(expectedInventoryReport.getEkrutLocation(), resultReport.getEkrutLocation(),
				"Incorrect report location");
		assertEquals(expectedInventoryReport.getArea(), resultReport.getArea(),
				"Incorrect report area");
		assertEquals(expectedInventoryReport.getInventoryReportData(), resultReport.getInventoryReportData(),
				"Incorrect inventory report data");
		assertEquals(expectedInventoryReport.getThreshold(), resultReport.getThreshold(),
				"Incorrect threshold");
	}
	
	
}
