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
	private Map<String, int[]> InventoryReportData;
	private ArrayList<Integer> customerReportData;
	private Map<String, Integer> orderReportData;
	private int avgSalesPerCustomer;
	private int monthlyOrders;
	private int monthlyOrdersInILS;

	
	public Report(Integer reportID, ReportType reportType, LocalDateTime date, String area, String ekrutLocation) {
		this.reportID = reportID;
		this.reportType = reportType;
		this.date = date;
		this.area = area;
		this.ekrutLocation = ekrutLocation;
	}
	
	// Order report constructor
	public Report(Integer reportID, ReportType reportType, LocalDateTime date, String area,
			String ekrutLocation, int monthlyOrders, int avgSalesPerCustomer,
			int monthlyOrdersInILS, Map<String, Integer> orderReportData) {
		this(reportID, reportType, date, area, ekrutLocation);
		this.monthlyOrders = monthlyOrders;
		this.avgSalesPerCustomer = avgSalesPerCustomer;
		this.monthlyOrdersInILS = monthlyOrdersInILS;
		this.orderReportData = orderReportData;

	}
	
	// Customer report constructor
	public Report(Integer reportID, ReportType reportType, LocalDateTime date, String area,
		String ekrutLocation, ArrayList<Integer> customerReportData) {
		this(reportID, reportType, date, area, ekrutLocation);
		this.customerReportData = customerReportData;
	}
	
	// Inventory report constructor
	public Report(Integer reportID, ReportType reportType, LocalDateTime date, String area,
			String ekrutLocation, Map<String, int[]> InventoryReportData) {
			this(reportID, reportType, date, area, ekrutLocation);
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

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getEkrutLocation() {
		return ekrutLocation;
	}

	public void setEkrutLocation(String ekrutLocation) {
		this.ekrutLocation = ekrutLocation;
	}

	public Map<String, int[]> getInventoryReportData() {
		return InventoryReportData;
	}

	public void setInventoryReportData(Map<String, int[]> inventoryReportData) {
		InventoryReportData = inventoryReportData;
	}

	public ArrayList<Integer> getCustomerReportData() {
		return customerReportData;
	}

	public void setCustomerReportData(ArrayList<Integer> customerReportData) {
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


}
