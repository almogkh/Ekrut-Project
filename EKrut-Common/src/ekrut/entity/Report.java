package ekrut.entity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Report {
	private String reportID;
	private String reportType;
	private LocalDateTime date;
	private String area;
	/* each data structure holds the relevant data to make a report
	 * Only the data structure that corresponds to the type of report will be initialized,
	 * this makes the possibility of expanding a report if necessary more convenient*/
	private Map<String, Map<String, int[]>> InventoryReportData = new HashMap<>();
	private Map<String, List<Map<String, int[]>>> orderReportData = new HashMap<>();
	private Map<String, Map<String, List<int[]>>> clientsReportData = new HashMap<>();
	private int avgSalesPerCustomer;
	
	public Report(String reportID, String reportType, LocalDateTime date, String area) {
		this.reportID = reportID;
		this.reportType = reportType;
		this.date = date;
		this.area = area;
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
