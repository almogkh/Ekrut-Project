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
	Report expectedInventoryReport2;

	Map<String, ArrayList<Integer>> expectedInventoryReportData;

	@BeforeEach
	void setUp() throws Exception {

		DBController DBcontroller = new DBController(URL, USERNAME, PASSWORD);
		DBcontroller.connect();
		
		reportDAO = new ReportDAO(DBcontroller);
		
		jan_2022 = LocalDateTime.of(2022, 1, 31, 0, 0, 0);
		may_2021 = LocalDateTime.of(2021, 5, 31, 0, 0, 0);
		
		expectedInventoryReportData = new HashMap<>(Map.of("Bamba", new ArrayList<>(Arrays.asList(1)),
				"Coke", new ArrayList<>(Arrays.asList(1)), "Pepsi", new ArrayList<>(Arrays.asList(1)),
				"Fanta", new ArrayList<>(Arrays.asList(1)), "Oreo", new ArrayList<>(Arrays.asList(1)),
				"Bisli", new ArrayList<>(Arrays.asList(1))));
		
		expectedInventoryReport = new Report(null, ReportType.INVENTORY, may_2021, "Afula", "North",
				expectedInventoryReportData, 12);
		
		expectedInventoryReport2 = new Report(1, ReportType.INVENTORY, jan_2022, "Akko", "North",
				expectedInventoryReportData, 15);
	}
	
	// Checking functionality fetchReport: fetch not exist report
	// Input parameters: Parameters were entered so that there is no corresponding report in the DB
	// Expected result: report is null
	@Test
	public void testFetchNotExsitsReport() {
		
		resultReport = reportDAO.fetchReport(jan_2022, "Eilat", "South", ReportType.INVENTORY);
		
		assertNull(resultReport);
	}

	// Checking functionality fetchReport: fetch Inventory report from jan 2022 at "North" "Akko".
	// Input parameters: date: 2022-01-31, ekrutLocation: "Akko", area: "North", type: INVENTORY.
	// Expected result: All the result report data is equals to the data at the DB
	@Test 
	public void testFetchMonthlyInventoryReport_North_Akko_JAN_2022(){
		
		resultReport = reportDAO.fetchReport(jan_2022, "Akko", "North", ReportType.INVENTORY);
		
		assertEquals(expectedInventoryReport2, resultReport);
	}
	
	// Checking functionality createInventoryReport: insert inventory report from may 2021 at "Afula" "North" into the DB.
	// Input parameters: excepted inventory report
	// Expected result: the fetched report from the DB is equals to the report that inserted.
	@Test 
	public void testCreateInventoryReport_North_Afula_may_2021() {
		
		resultReport = reportDAO.fetchReport(may_2021, "Afula", "North", ReportType.INVENTORY);
		
		assertNull(resultReport);
		
		reportDAO.createInventoryReport(expectedInventoryReport);
		
		resultReport = reportDAO.fetchReport(may_2021, "Afula", "North", ReportType.INVENTORY);
		
		assertEquals(expectedInventoryReport, resultReport);
	}
}
