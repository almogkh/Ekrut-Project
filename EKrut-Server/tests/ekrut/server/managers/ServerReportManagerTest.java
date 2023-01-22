package ekrut.server.managers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ekrut.entity.InventoryItem;
import ekrut.entity.Item;
import ekrut.entity.Report;
import ekrut.entity.ReportType;
import ekrut.server.EKrutServer;
import ekrut.server.db.DBController;
import ekrut.server.db.InventoryItemDAO;
import ekrut.server.db.ReportDAO;
import ekrut.server.db.UserDAO;
import ekrut.server.gui.ServerUI;

class ServerReportManagerTest {
	
	private ReportDAO reportDAO;
	private UserDAO userDAO;
	private EKrutServer ekrutServer;
	private InventoryItemDAO inventoryItemDAO;
	private DBController dbCon;
	private ServerReportManager serverReportManager;
	
	@BeforeEach
	void setUp() throws Exception {
		dbCon = new DBController("jdbc:postgresql://localhost:5432/mydb", "username", "password");
		reportDAO = mock(ReportDAO.class);
        inventoryItemDAO = mock(InventoryItemDAO.class);
		userDAO = mock(UserDAO.class);
		ekrutServer = mock(EKrutServer.class);
		Field ekrutServerField = ServerUI.class.getDeclaredField("server");
        
		ekrutServerField.setAccessible(true);
		ekrutServerField.set(null, ekrutServer);
		doNothing().when(ekrutServer).registerHandler(any(), any());
		
        serverReportManager = new ServerReportManager(dbCon);
        try {
			Field reportDAOField = ServerReportManager.class.getDeclaredField("reportDAO");
			Field inventoryItemDAOField = ServerReportManager.class.getDeclaredField("inventoryItemDAO");
			Field userDAOField = ServerReportManager.class.getDeclaredField("userDAO");

			reportDAOField.setAccessible(true);
			inventoryItemDAOField.setAccessible(true);
			userDAOField.setAccessible(true);
			
			reportDAOField.set(serverReportManager, reportDAO);
			inventoryItemDAOField.set(serverReportManager, inventoryItemDAO);
			userDAOField.set(serverReportManager, userDAO);
			
			
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}
	
	// Checking functionality generateInventoryReport : generate not empty inventory report.
	// Input parameters: date = current date, location = "Rahat" area = "South".
	// Expected result: inventory report with of the given parameters.
    @Test
    public void test_generateInventoryReport_Success() throws DeadlockException {
        LocalDateTime date = LocalDateTime.now();
        String ekrutLocation = "Rahat";
        String area = "South";
        
        ArrayList<String> thersholdBreaches = new ArrayList<>();
        thersholdBreaches.add("Bamba");
        thersholdBreaches.add("Bisli");
        ArrayList<InventoryItem> allItemsInLocation = new ArrayList<>();
        Item Bamba = new Item(1, "Bamba", "Good Bamba", (float) 7.0);
        Item Bisli = new Item(2, "Bisli", "Good Bisli", (float) 8.0);
        InventoryItem inventoryBamba = new InventoryItem(Bamba, 10, "Rahat", "South", 15);
        InventoryItem inventoryBisli = new InventoryItem(Bisli, 15, "Rahat", "South", 15);
        allItemsInLocation.add(inventoryBamba);
        allItemsInLocation.add(inventoryBisli);
        
        int facilityThreshold = 15;
        
        Map<String, ArrayList<Integer>> inventoryReportData = new HashMap<>();
        ArrayList<Integer> thresholdBreachesBamba = new ArrayList<>();
        ArrayList<Integer> thresholdBreachesBisli = new ArrayList<>();
        thresholdBreachesBamba.add(1);
        thresholdBreachesBisli.add(1);
        inventoryReportData.put("Bamba", thresholdBreachesBamba);
        inventoryReportData.put("Bisli", thresholdBreachesBisli);

        Report expectedReport = new Report(null, ReportType.INVENTORY, date, ekrutLocation, area, inventoryReportData, facilityThreshold);
        
        when(reportDAO.getThresholdBreaches(date, ekrutLocation)).thenReturn(thersholdBreaches);
        when(inventoryItemDAO.fetchAllItemsByEkrutLocation(ekrutLocation)).thenReturn(allItemsInLocation);
        
        Report resultReport = serverReportManager.generateInventoryReport(date, ekrutLocation, area);
        assertEquals(expectedReport, resultReport);
	    }

		 // Checking functionality generateInventoryReport: generate empty inventory report.
		 // Input parameters: date = current date, location = null area = null.
		 // Expected result: an empty inventory report with the current date and threshold -1.
	    @Test
	    public void test_generateInventoryReport_NullParameters_ResEmptyReport() throws DeadlockException {
	        LocalDateTime date = LocalDateTime.now();
	        String ekrutLocation = null;
	        String area = null;
	        
	        ArrayList<InventoryItem> allItemsInLocation = new ArrayList<>();
	        ArrayList<String> thersholdBreaches = new ArrayList<>();
	        Map<String, ArrayList<Integer>> inventoryReportData = new HashMap<>();
	        
	        Report expectedReport = new Report(null, ReportType.INVENTORY, date,
	        		ekrutLocation, area ,inventoryReportData, -1);

	        when(reportDAO.getThresholdBreaches(date, ekrutLocation)).thenReturn(thersholdBreaches);
	        when(inventoryItemDAO.fetchAllItemsByEkrutLocation(ekrutLocation)).thenReturn(allItemsInLocation);
	        
	        Report actualReport = serverReportManager.generateInventoryReport(date, ekrutLocation, area);
	        assertEquals(expectedReport, actualReport);
	    } 
	}