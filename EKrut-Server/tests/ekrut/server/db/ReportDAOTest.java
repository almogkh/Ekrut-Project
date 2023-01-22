package ekrut.server.db;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mysql.cj.MysqlType;

import ekrut.entity.Report;
import ekrut.entity.ReportType;

class ReportDAOTest {

	Report JanUAEReport;
	ReportDAO reportDAO;
	
	DBController DBcontroller;
	String URL = "jdbc:mysql://localhost/test_ekrut?serverTimezone=IST";
	String USERNAME = "root";
	String PASSWORD = "Aa123456";
	
	LocalDateTime jan_2022;
	LocalDateTime may_2021;
	
	Report resultReport;

	Report expectedInventoryReport;
	Report expectedInventoryReport2;

	Map<String, ArrayList<Integer>> expectedInventoryReportData;
	
	ArrayList<String> resultThresholdBreaches;
	ArrayList<String> expectedThresholdBreaches;
	

	@BeforeEach
	void setUp() throws Exception {

		DBcontroller = new DBController(URL, USERNAME, PASSWORD);
		DBcontroller.connect();
		
		reportDAO = new ReportDAO(DBcontroller);
		
		jan_2022 = LocalDateTime.of(2022, 1, 31, 0, 0, 0);
		may_2021 = LocalDateTime.of(2021, 5, 31, 0, 0, 0);
		
		expectedInventoryReportData = new HashMap<>();

		ArrayList<Integer> thresholdBreachesBamba = new ArrayList<>(Arrays.asList(1));
		ArrayList<Integer> thresholdBreachesCoke = new ArrayList<>(Arrays.asList(1));
		ArrayList<Integer> thresholdBreachesPepsi = new ArrayList<>(Arrays.asList(1));
		ArrayList<Integer> thresholdBreachesFanta = new ArrayList<>(Arrays.asList(1));
		ArrayList<Integer> thresholdBreachesOreo = new ArrayList<>(Arrays.asList(1));
		ArrayList<Integer> thresholdBreachesBisli = new ArrayList<>(Arrays.asList(1));

		expectedInventoryReportData.put("Bamba", thresholdBreachesBamba);
		expectedInventoryReportData.put("Coke", thresholdBreachesCoke);
		expectedInventoryReportData.put("Pepsi", thresholdBreachesPepsi);
		expectedInventoryReportData.put("Fanta", thresholdBreachesFanta);
		expectedInventoryReportData.put("Oreo", thresholdBreachesOreo);
		expectedInventoryReportData.put("Bisli", thresholdBreachesBisli);
				
		
		expectedInventoryReport = new Report(null, ReportType.INVENTORY, may_2021, "Afula", "North",
				expectedInventoryReportData, 12);
		
		expectedInventoryReport2 = new Report(1, ReportType.INVENTORY, jan_2022, "Akko", "North",
				expectedInventoryReportData, 15);
		
		expectedThresholdBreaches = new ArrayList<>();
        expectedThresholdBreaches.add("Bamba");
        expectedThresholdBreaches.add("Bisli");
	}
	
	@AfterEach
	void tearDown() throws Exception {
		deleteReport(may_2021, "Afula", "North", ReportType.INVENTORY);
	}

	
	// Checking functionality fetchReport: fetch not exists report.
	// Input parameters: date: 2022-01-31, ekrutLocation: "Eilat", area: "South", type: INVENTORY.
	// Expected result: report is null
	@Test
	public void test_fetchReport_NotExsitsReport_ResNullReport() {
		resultReport = reportDAO.fetchReport(jan_2022, "Eilat", "South", ReportType.INVENTORY);
		
		assertNull(resultReport);
	}

	// Checking functionality fetchReport: fetch exists report.
	// Input parameters: date: 2022-01-31, ekrutLocation: "Akko", area: "North", type: INVENTORY.
	// Expected result: Inventory report of the given parameters.
	@Test 
	public void test_fetchReport_ExistsReport_Success(){
		resultReport = reportDAO.fetchReport(jan_2022, "Akko", "North", ReportType.INVENTORY);
		
		assertEquals(expectedInventoryReport2, resultReport);
	}
	
	// Checking functionality createInventoryReport: create new report at the DB.
	// Input parameters: Report with parameters: 
	//					date: 2021-05-31, ekrutLocation: "Afula", area: "North", type: INVENTORY.
	// Expected result: The report created successfully at the DB.
	@Test 
	public void test_CreateInventoryReport_Success() throws SQLException {
		// First check that the report is not exist at the DB.
		resultReport = reportDAO.fetchReport(may_2021, "Afula", "North", ReportType.INVENTORY);
		assertNull(resultReport);
		
		reportDAO.createInventoryReport(expectedInventoryReport);
		
		resultReport = reportDAO.fetchReport(may_2021, "Afula", "North", ReportType.INVENTORY);
		assertEquals(expectedInventoryReport, resultReport);
	}
	
	// Checking functionality getThresholdBreaches: get exists threshold breaches.
	// Input parameters: date: 2022-01-31, ekrutLocation: "Ashdod".
	// Expected result: Inventory report of the given parameters.
	@Test
	public void test_getThresholdBreaches_Success() {
		resultThresholdBreaches = reportDAO.getThresholdBreaches(jan_2022, "Ashdod");
        assertEquals(expectedThresholdBreaches, resultThresholdBreaches);
	}
	
	// Checking functionality getThresholdBreaches: get not exists threshold breaches.
	// Input parameters: date: 2022-01-31, ekrutLocation: "Haifa".
	// Expected result: Inventory report of the given parameters.
	@Test
	public void test_getThresholdBreaches_NotExistsBreaches_ResEmptyArrayList() {
		ArrayList<String> thresholdBreaches = new ArrayList<>();
		resultThresholdBreaches = reportDAO.getThresholdBreaches(jan_2022, "Haifa");
        assertEquals(thresholdBreaches, resultThresholdBreaches);
	}
	
	// Delete given report from DB
	public boolean deleteReport(LocalDateTime date, String ekrutLocation, String area, ReportType type) throws SQLException{
	    PreparedStatement ps = DBcontroller.getPreparedStatement("DELETE FROM reports WHERE"
	    		+ " EXTRACT(MONTH FROM date) = EXTRACT(MONTH FROM ?) "
	    		+ " AND EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM ?)"
	    		+ " AND ekrutLocation = ? AND area = ? and type = ?");

	        ps.setObject(1, date, MysqlType.DATETIME);
	        ps.setObject(2, date, MysqlType.DATETIME);
	        ps.setString(3, ekrutLocation);
	        ps.setString(4, area);
	        ps.setString(5, type.toString());
	        int res = ps.executeUpdate();
	        
	        ps.close();

	        if (res != 1) {
	            return false;
	        }
	        return true; 
	}
}
