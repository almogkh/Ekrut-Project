package ekrut.net;

import java.io.Serializable;
import ekrut.entity.Report;

public class ReportResponse implements Serializable{
	private static final long serialVersionUID = -3756403993356581023L;
	private ResultType resultType;
	private Report report; 
	
	public ResultType getResultCode() {
		return resultType;
	}
	public void setResultCode(ResultType resultCode) {
		this.resultType = resultCode;
	}
	public Report getReport() {
		return report;
	}
	public void setReport(Report report) {
		this.report = report;
	}
	
}
