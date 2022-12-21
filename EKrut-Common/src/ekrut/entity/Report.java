package ekrut.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Tal Gaon
 */
public class Report {
	private Integer reportID;
	private ReportType reportType;
	private LocalDateTime date;
	private String area;
	private String ekrutLocation;
	// Each data structure holds the relevant data to make a report
	private Map<String, ArrayList<Integer>> InventoryReportData;
	private Map<String, Integer> customerReportData;
	private Map<String, Integer> orderReportData;
	private int avgSalesPerCustomer;
	private int monthlyOrders;
	private int monthlyOrdersInILS;
	private int threshold;

	public Report(Integer reportID, ReportType reportType, LocalDateTime date, String ekrutLocation) {
		this.reportID = reportID;
		this.reportType = reportType;
		this.date = date;
		this.ekrutLocation = ekrutLocation;
	}

	// Order report constructor
	public Report(Integer reportID, ReportType reportType, LocalDateTime date, String ekrutLocation, int monthlyOrders,
			int monthlyOrdersInILS, Map<String, Integer> orderReportData) {
		this(reportID, reportType, date, ekrutLocation);
		this.monthlyOrders = monthlyOrders;
		this.monthlyOrdersInILS = monthlyOrdersInILS;
		this.orderReportData = orderReportData;

	}

	// Customer report report constructor
	public Report(Integer reportID, ReportType reportType, LocalDateTime date, String ekrutLocation,
			Map<String, Integer> customerReportData) {
		this(reportID, reportType, date, ekrutLocation);
		this.customerReportData = customerReportData;
	}

	// Inventory report constructor 
	 public Report(Integer reportID, ReportType reportType, LocalDateTime date,
			 String ekrutLocation, Map<String, ArrayList<Integer>> InventoryReportData, int threshold) {
		 this(reportID, reportType, date, ekrutLocation);
		 this.InventoryReportData = InventoryReportData;
	 }

	public Integer getReportID() {
		return reportID;
	}

	public void setReportID(Integer reportID) {
		this.reportID = reportID;
	}

	public ReportType getReportType() {
		return reportType;
	}

	public void setReportType(ReportType reportType) {
		this.reportType = reportType;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public String getArea() {
		return area;
	}

	public String getEkrutLocation() {
		return ekrutLocation;
	}

	public void setEkrutLocation(String ekrutLocation) {
		this.ekrutLocation = ekrutLocation;
	}

	public Map<String, ArrayList<Integer>> getInventoryReportData() {
		return InventoryReportData;
	}

	public void setInventoryReportData(Map<String, ArrayList<Integer>> inventoryReportData) {
		InventoryReportData = inventoryReportData;
	}

	public Map<String, Integer> getCustomerReportData() {
		return customerReportData;
	}

	public void setCustomerReportData(Map<String, Integer> customerReportData) {
		this.customerReportData = customerReportData;
	}

	public Map<String, Integer> getOrderReportData() {
		return orderReportData;
	}

	public void setOrderReportData(Map<String, Integer> orderReportData) {
		this.orderReportData = orderReportData;
	}

	public int getAvgSalesPerCustomer() {
		return avgSalesPerCustomer;
	}

	public void setAvgSalesPerCustomer(int avgSalesPerCustomer) {
		this.avgSalesPerCustomer = avgSalesPerCustomer;
	}

	public int getMonthlyOrders() {
		return monthlyOrders;
	}

	public void setMonthlyOrders(int monthlyOrders) {
		this.monthlyOrders = monthlyOrders;
	}

	public int getMonthlyOrdersInILS() {
		return monthlyOrdersInILS;
	}

	public void setMonthlyOrdersInILS(int monthlyOrdersInILS) {
		this.monthlyOrdersInILS = monthlyOrdersInILS;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

}
