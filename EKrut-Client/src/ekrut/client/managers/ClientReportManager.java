package ekrut.client.managers;
import ekrut.net.ReportRequest;
import ekrut.net.ReportResponse;

import java.time.LocalDateTime;

import ekrut.entity.Report; 

/**
 * This class manages reports on the client side.
 * 
 * @author Tal Gaon
 *
 */

public class ClientReportManager {
	
	/**
	 * Returns the report according to the inputs if such a report exists. 
	 * 
	 * @param String area, String reportType, LocalDateTime date.
	 * @throws Exception when the servers response is anything but "OK".
	 * @return report.
	 */
	public Report getReport(String area, String reportType, LocalDateTime date) throws Exception {
	// Prepare a getReportRequest to send to server.
	ReportRequest reportRequest = new ReportRequest(area, reportType, date);
	// Sending getReportRequest and receiving getReportResponse.
	ReportResponse reportResponse = sendRequest(reportRequest);
	// ResultCode is not "OK" meaning we encountered an error. 
	String resultCode = reportResponse.getResultCode();
	if (!resultCode.equals("OK"))
		throw new Exception(resultCode); // TBD CHANGE TO SPESIFIC EXCEPTION
	// Return the report that attached to the response.
	return reportResponse.getReport();  
	}
	
	
	
	
 
	private ReportResponse sendRequest(ReportRequest getReportsRequest) {
		// TODO Auto-generated method stub
		ReportResponse reportsResponse = new ReportResponse();
		return reportsResponse;
	}
	
}
	
