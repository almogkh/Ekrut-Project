package ekrut.client.manager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import ekrut.client.EKrutClient;
import ekrut.client.managers.AbstractClientManager;
import ekrut.client.managers.ClientReportManager;
import ekrut.client.managers.ClientSessionManager;
import ekrut.entity.Report;
import ekrut.entity.ReportType;
import ekrut.entity.User;
import ekrut.net.ReportRequest;
import ekrut.net.ReportResponse;
import ekrut.net.ResultType;
import ekrut.net.UserRequest;
import ekrut.net.UserResponse;

class ClientReportManagerTest {

	private ClientReportManager clientReportManager;
	private EKrutClient ekrutClient;
	
	Map<String, ArrayList<Integer>> inventoryReportData;
	
	@BeforeEach
	public void setup() {
		ekrutClient = mock(EKrutClient.class);
		
		clientReportManager = new ClientReportManager(ekrutClient);
		
		Map<String, ArrayList<Integer>> expectedInventoryReportData = new HashMap<>();

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
	}

	/*
	 * This method is set up a mock for the ekrutClient.sendRequestToServer()method.
	 * This is setting up a mock to simulate the response from the server, which can
	 * be used to test different scenarios in the getReport() function, such as
	 * different result codes being returned by the sendRequest method.
	 */
	private void sendRequest(ReportResponse response) {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock inv)
					throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
				Field res = AbstractClientManager.class.getDeclaredField("response");
				res.setAccessible(true);
				res.set(clientReportManager, response);
				return null;
			}
		}).when(ekrutClient).sendRequestToServer(any(ReportRequest.class));
	}
	
	// Checking functionality getReport: get exists inventory report.
	// Input parameters: date: 2022-01-31, ekrutLocation: "Dimona", area: "South", type: INVENTORY.
	// Expected result: Inventory report of the given parameters and resultType "OK".
    @Test
    public void test_getReport_Success() throws Exception {
        String area = "South";
        String location = "Dimona";
        ReportType reportType = ReportType.INVENTORY;
        LocalDateTime date = LocalDateTime.of(2022, 1, 31, 0, 0, 0);
        
        Report expectedReport = new Report(1, ReportType.INVENTORY, date,
   			 location, area, inventoryReportData, 12);
        
        ReportResponse reportResponse = new ReportResponse(ResultType.OK, expectedReport);
        sendRequest(reportResponse);
        
        Report resultReport = clientReportManager.getReport(area, location, reportType, date);
        assertEquals(expectedReport, resultReport);
    }
    
	// Checking functionality getReport: get not exists report.
	// Input parameters: date: 2022-01-31, ekrutLocation: "Dubai", area: "UAE", type: INVENTORY.
	// Expected result: null report and resultType "NOT_FOUND".
    @Test 
    public void test_GetReport_NotExsitsReport_ResNullReport() throws Exception{
        String area = "UAE";
        String location = "Dubai";
        ReportType reportType = ReportType.INVENTORY;
        LocalDateTime date = LocalDateTime.of(2022, 1, 31, 0, 0, 0);
        
        Report expectedReport = null;
        
        ReportResponse reportResponse = new ReportResponse(ResultType.NOT_FOUND, expectedReport);
        sendRequest(reportResponse);
        
        Report resultReport = clientReportManager.getReport(area, location, reportType, date);
        assertNull(resultReport);
    }
}

