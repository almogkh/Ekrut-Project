package ekrut.net;

import java.io.Serializable;
import java.time.LocalDateTime;

import ekrut.entity.ReportType;

public class ReportRequest implements Serializable{
	private static final long serialVersionUID = -7271037236386122944L;
	private ReportRequestType reportRequestType;
	private String area;
	private String location;
	private ReportType reportType;
	private LocalDateTime date;
	
	public ReportRequest(String area, String location, ReportType reportType, LocalDateTime date) {
		this.reportRequestType = ReportRequestType.FETCH_REPORT; 
		this.area = area;
		this.location = location;
		this.reportType = reportType;
		this.date = date;
	}
	public ReportRequest(String area) {
		this.area = area;
		this.reportRequestType = ReportRequestType.FETCH_FACILITIES; 
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
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

	public ReportRequestType getReportRequestType() {
		return reportRequestType;
	}

	public void setReportRequestType(ReportRequestType reportRequestType) {
		this.reportRequestType = reportRequestType;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
