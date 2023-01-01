package ekrut.client.managers;
import ekrut.net.ReportRequest;
import ekrut.net.ReportResponse;
import ekrut.net.ResultType;

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

	public Report getReport(String area, String location, ReportType reportType, LocalDateTime date) throws Exception { 
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
	
	public ArrayList<String> fetchFacilitiesByArea(String area){
		
		
		
		return null;
	}
	
	
}
	
