package ekrut.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Report implements Serializable{

	private static final long serialVersionUID = -5874512121114020139L;
	
	private Integer reportID;
	private ReportType reportType;
	private LocalDateTime date;
	private String area;
	private String ekrutLocation;

	// Each data structure holds the relevant data to make a report
	private Map<String, ArrayList<Integer>> inventoryReportData;
	private Map<String, Integer> customerReportData;
	private Map<String, ArrayList<Integer>> orderReportData;
	private Map<String, Integer> topSellersData;
	private Map<Integer, Integer> customersOrdersByDate;
	
	private Integer threshold; 
	private Integer totalOrders;
	private Integer totalOrdersInILS;
	private Integer totalShipmentOrders;
	private Integer totalShipmentOrdersInILS;
	
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
			String ekrutLocation, String area, Integer totalOrders, Integer totalOrdersInILS, Integer totalShipmentOrders, 
			Integer totalShipmentOrdersInILS, Map<String, ArrayList<Integer>> orderReportData, Map<String, Integer> topSellersData) {
		this(reportID, reportType, date, ekrutLocation, area);
		this.totalOrders = totalOrders; 
		this.totalOrdersInILS = totalOrdersInILS;
		this.totalShipmentOrders = totalShipmentOrders;
		this.totalShipmentOrdersInILS = totalShipmentOrdersInILS;
		this.orderReportData = orderReportData;
		this.topSellersData = topSellersData;
	}

	// Customer report report constructor
	public Report(Integer reportID, ReportType reportType, LocalDateTime date, String ekrutLocation, String area,
			Map<String, Integer> customerReportData, Map<Integer, Integer> customersOrdersByDate) {
		this(reportID, reportType, date, ekrutLocation, area);
		this.customerReportData = customerReportData;
		this.customersOrdersByDate = customersOrdersByDate;
	} 

	// Inventory report constructor 
	 public Report(Integer reportID, ReportType reportType, LocalDateTime date,
			 String ekrutLocation, String area, Map<String, ArrayList<Integer>> inventoryReportData, Integer threshold) {
		 this(reportID, reportType, date, ekrutLocation, area);
		 this.inventoryReportData = inventoryReportData;
		 this.threshold = threshold;
	 }

	public Integer getReportID() {
		return reportID;
	}

	public ReportType getReportType() {
		return reportType;
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

	public Map<String, ArrayList<Integer>> getInventoryReportData() {
		return inventoryReportData;
	}

	public Map<String, Integer> getCustomerReportData() {
		return customerReportData;
	}

	public Map<String, ArrayList<Integer>>  getOrderReportData() {
		return orderReportData;
	}

	public Integer getThreshold() {
		return threshold;
	}

	public Map<String, Integer> getTopSellersData() {
		return topSellersData;
	}

	public Integer getTotalOrders() {
		return totalOrders;
	}

	public Integer getTotalOrdersInILS() {
		return totalOrdersInILS;
	}

	public Map<Integer, Integer> getCustomersOrdersByDate() {
		return customersOrdersByDate;
	}

	public Integer getTotalShipmentOrders() {
		return totalShipmentOrders;
	}

	public Integer getTotalShipmentOrdersInILS() {
		return totalShipmentOrdersInILS;
	}

	public void setReportID(Integer reportID) {
		this.reportID = reportID;
	}

	public void setReportType(ReportType reportType) {
		this.reportType = reportType;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(area, date, ekrutLocation, inventoryReportData, reportID, reportType, threshold);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Report other = (Report) obj;
		return Objects.equals(area, other.area) 
				&& Objects.equals(date.toLocalDate(), other.date.toLocalDate()) 
				&& Objects.equals(ekrutLocation, other.ekrutLocation)
				&& Objects.equals(inventoryReportData, other.inventoryReportData)
				&& Objects.equals(reportID, other.reportID)
				&& reportType == other.reportType && Objects.equals(threshold, other.threshold);
	}
}
