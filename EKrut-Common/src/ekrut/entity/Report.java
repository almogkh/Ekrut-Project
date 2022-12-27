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
	private Map<String, ArrayList<Integer>> orderReportData;
	private Map<String, Integer> topSellersData;
	private int threshold;
	private int totalOrders;
	private int totalOrdersInILS;

	public Report(Integer reportID, ReportType reportType, LocalDateTime date, String ekrutLocation, String area) {
		this.reportID = reportID;
		this.reportType = reportType;
		this.date = date;
		this.ekrutLocation = ekrutLocation;
		this.area = area;
	}

	// Order report constructor
	public Report(
			Integer reportID, ReportType reportType, LocalDateTime date,
			String ekrutLocation, String area, int totalOrders, int totalOrdersInILS,
			Map<String, ArrayList<Integer>> orderReportData, Map<String, Integer> topSellersData) {
		this(reportID, reportType, date, ekrutLocation, area);
		this.totalOrders = totalOrders; 
		this.totalOrdersInILS = totalOrdersInILS;
		this.orderReportData = orderReportData;
		this.topSellersData = topSellersData;
	}

	// Customer report report constructor
	public Report(Integer reportID, ReportType reportType, LocalDateTime date, String ekrutLocation, String area,
			Map<String, Integer> customerReportData) {
		this(reportID, reportType, date, ekrutLocation, area);
		this.customerReportData = customerReportData;
	}

	// Inventory report constructor 
	 public Report(Integer reportID, ReportType reportType, LocalDateTime date,
			 String ekrutLocation, String area, Map<String, ArrayList<Integer>> InventoryReportData, int threshold) {
		 this(reportID, reportType, date, ekrutLocation, area);
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

	public Map<String, ArrayList<Integer>>  getOrderReportData() {
		return orderReportData;
	}
	public void setOrderReportData(Map<String, ArrayList<Integer>> orderReportData) {
		this.orderReportData = orderReportData;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public Map<String, Integer> getTopSellersData() {
		return topSellersData;
	}

	public void setTopSellersData(Map<String, Integer> topSellersData) {
		this.topSellersData = topSellersData;
	}

	public int getTotalOrders() {
		return totalOrders;
	}

	public void setTotalOrders(int totalOrders) {
		this.totalOrders = totalOrders;
	}

	public int getTotalOrdersInILS() {
		return totalOrdersInILS;
	}

	public void setTotalOrdersInILS(int totalOrdersInILS) {
		this.totalOrdersInILS = totalOrdersInILS;
	}

}
