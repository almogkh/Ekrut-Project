package ekrut.client.managers;
import ekrut.net.ReportRequest;
import ekrut.net.ReportResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;

import ekrut.client.EKrutClient;
import ekrut.entity.Report;
import ekrut.entity.ReportType; 

/**
 * This class manages reports on the client side.
 * 
 * @author Tal Gaon
 *
 */

public class ClientReportManager extends AbstractClientManager<ReportRequest, ReportResponse> { 

	public ClientReportManager(EKrutClient client) {
		super(client, ReportResponse.class);
	}
	
	//redo document
	/**
	 * Returns the report according to the inputs if such a report exists. 
	 * @param String area, String reportType, LocalDateTime date.
	 * @throws Exception when the servers response is anything but "OK".
	 * @return report.
	 */
	public Report getReport(String area, String location, ReportType reportType, LocalDateTime date) throws Exception { 
		// Prepare a getReportRequest to send to server.

		ReportRequest reportRequest = new ReportRequest(area, location, reportType, date);
		// Sending getReportRequest and receiving getReportResponse.
		ReportResponse reportResponse = sendRequest(reportRequest);
	
		// Return the report that attached to the response.
		return reportResponse.getReport();  
	} 
	
	public ArrayList<String> getFacilitiesByArea(String area) {
		ReportRequest reportRequest = new ReportRequest(area);
		ReportResponse reportResponse = sendRequest(reportRequest);
		return reportResponse.getFacilities();  
	}
}
