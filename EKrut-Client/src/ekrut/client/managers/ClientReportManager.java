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

	/**
	 * Retrieves a report from the server based on the specified area, location,
	 * report type, and date.
	 * 
	 * @param area       the area for which the report should be generated
	 * @param location   the location for which the report should be generated
	 * @param reportType the type of report to be generated
	 * @param date       the date for which the report should be generated
	 * @return the report requested
	 * @throws Exception if an error occurs while retrieving the report
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
