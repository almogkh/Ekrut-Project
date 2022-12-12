package ekrut.net;

import java.io.Serializable;
import ekrut.entity.Report;

public class ReportResponse implements Serializable{
	private static final long serialVersionUID = -3756403993356581023L;
	private int resultCode;
	private Report report;
	
	public int getResultCode() {
		return resultCode;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	public Report getReport() {
		return report;
	}
	public void setReport(Report report) {
		this.report = report;
	}
	
}
