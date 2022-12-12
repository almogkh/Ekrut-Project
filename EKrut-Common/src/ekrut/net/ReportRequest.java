package ekrut.net;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ReportRequest implements Serializable{
	private static final long serialVersionUID = -7271037236386122944L;
	private String area;
	private String reportType;
	private LocalDateTime date;
	
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
}
