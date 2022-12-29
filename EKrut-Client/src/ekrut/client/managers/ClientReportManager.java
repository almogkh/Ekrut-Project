package ekrut.client.managers;
import ekrut.net.ReportRequest;
import ekrut.net.ReportRequestType;
import ekrut.net.ReportResponse;
import ekrut.net.ResultType;

import java.time.LocalDateTime;
import java.util.ArrayList;

import ekrut.entity.Report; 

/**
 * This class manages reports on the client side.
 * 
 * @author Tal Gaon
 *
 */

public class ClientReportManager {
	
	//redo document
	/**
	 * Returns the report according to the inputs if such a report exists. 
	 * @param String area, String reportType, LocalDateTime date.
	 * @throws Exception when the servers response is anything but "OK".
	 * @return report.
	 */
	public Report getReport(String area, String location, String reportType, LocalDateTime date) throws Exception { 
	// Prepare a getReportRequest to send to server.
	ReportRequest reportRequest = new ReportRequest(area, location, reportType, date);
	// Sending getReportRequest and receiving getReportResponse.
	ReportResponse reportResponse = sendRequest(reportRequest);
 
	ResultType resultType = reportResponse.getResultCode(); 
	// ResultCode is not "OK" meaning we encountered an error.
	if (!resultType.equals(ResultType.OK))
		throw new Exception(resultType.toString()); // TBD CHANGE TO SPESIFIC EXCEPTION
	// Return the report that attached to the response.
	return reportResponse.getReport();  
	}
	
	public ArrayList<String> getFacilitiesByArea(String area) throws Exception{
		
		ReportRequest reportRequest = new ReportRequest(area);
		
		ReportResponse reportResponse = sendRequest(reportRequest);
		
		ResultType resultType = reportResponse.getResultCode(); 
		// ResultCode is not "OK" meaning we encountered an error.
		if (!resultType.equals(ResultType.OK))
			throw new Exception(resultType.toString()); // TBD CHANGE TO SPESIFIC EXCEPTION
		
		return reportResponse.getFacilities();  
	}
	
	public ReportResponse sendRequest(ReportRequest getReportsRequest) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ArrayList<String> fetchFacilitiesByArea(String area){
		
		
		
		return null;
	}
	
	
}
	
