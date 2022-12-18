package ekrut.entity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Report {
	private Integer reportID;
	private String reportType;
	private LocalDateTime date;
	private String area;
	private String ekrutLocation;
	/* each data structure holds the relevant data to make a report
	 * Only the data structure that corresponds to the type of report will be initialized,
	 * this makes the possibility of expanding a report if necessary more convenient*/
	private Map<String, Map<String, int[]>> InventoryReportData = new HashMap<>();
	private Map<String, List<Map<String, int[]>>> orderReportData = new HashMap<>();
	private Map<String, Map<String, List<int[]>>> clientsReportData = new HashMap<>();
	private int avgSalesPerCustomer;
	
	public Report(Integer reportID, String reportType, LocalDateTime date, String area, String ekrutLocation) {
		this.reportID = reportID;
		this.reportType = reportType;
		this.date = date;
		this.area = area;
		this.ekrutLocation = ekrutLocation;
	}

	public void setInventoryReportData(Map<String, Map<String, int[]>> inventoryReportData) {
		InventoryReportData = inventoryReportData;
	}

	public void setOrderReportData(Map<String, List<Map<String, int[]>>> orderReportData) {
		this.orderReportData = orderReportData;
	}

	public void setClientsReportsData(Map<String, Map<String, List<int[]>>> clientsReportsData) {
		this.clientsReportData = clientsReportsData;
	}

}
