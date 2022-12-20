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
	// Each data structure holds the relevant data to make a report
	private Map<String, int[]> InventoryReportData;
	private ArrayList<Integer> customerReportData;
	private Map<String, Integer> orderReportData;
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
	
	// Order report constructor
	public Report(Integer reportID, String reportType, LocalDateTime date, String area,
			String ekrutLocation, int monthlyOrders, int avgSalesPerCustomer,
			int monthlyOrdersInILS, Map<String, Integer> orderReportData) {
		this(reportID, reportType, date, area, ekrutLocation);
		this.monthlyOrders = monthlyOrders;
		this.avgSalesPerCustomer = avgSalesPerCustomer;
		this.monthlyOrdersInILS = monthlyOrdersInILS;
		this.orderReportData = orderReportData; //?

	}
	
	// Customer report constructor
	public Report(Integer reportID, String reportType, LocalDateTime date, String area,
		String ekrutLocation, ArrayList<Integer> customerReportData) {
		this(reportID, reportType, date, area, ekrutLocation);
		this.customerReportData = customerReportData;
	}
	
	// Inventory report constructor
	public Report(Integer reportID, String reportType, LocalDateTime date, String area,
			String ekrutLocation, Map<String, int[]> InventoryReportData) {
			this(reportID, reportType, date, area, ekrutLocation);
			this.InventoryReportData = InventoryReportData;

	}


}
