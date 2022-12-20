package ekrut.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
	// TBD.tal make the memory allocation in constructors
	private Map<String, int[]> InventoryReportData = new HashMap<>();
	private ArrayList<Integer> customerReportData = new ArrayList<Integer>();
	private Map<String, Integer> orderReportData = new HashMap<>();
	private Map<String, LocalDateTime> thresholdReportData = new HashMap<>();
	private int avgSalesPerCustomer;
	private int monthlyOrders;
	private int monthlyOrdersInILS;
	
	public Report(Integer reportID, String reportType, LocalDateTime date, String area, String ekrutLocation) {
		this.reportID = reportID;
		this.reportType = reportType;
		this.date = date;
		this.area = area;
		this.ekrutLocation = ekrutLocation;
	}

	public void setInventoryReportData(Map<String, int[]> inventoryReportData) {
		InventoryReportData = inventoryReportData;
	}

	public void setMonthlyOrders(int monthlyOrders) {
		this.monthlyOrders = monthlyOrders;
	}

	public void setMonthlyOrdersInILS(int monthlyOrdersInILS) {
		this.monthlyOrdersInILS = monthlyOrdersInILS;
	}

	public void setOrderReportData(Map<String, Integer> orderReportData) {
		this.orderReportData = orderReportData;
	}

	public void setCustomerReportData(ArrayList<Integer> customerReportData) {
		this.customerReportData = customerReportData; 
	}
	public void setThresholdReportData(Map<String, LocalDateTime> thresholdReportData) {
		this.thresholdReportData = thresholdReportData;
	}

}
