package ekrut.net;

import java.io.Serializable;
import java.util.ArrayList;

import ekrut.entity.Report;

public class ReportResponse implements Serializable{
	private static final long serialVersionUID = -3756403993356581023L;
	private ResultType resultType;
	private Report report; 
	private ArrayList<String> facilities;
	
	public ReportResponse(ResultType resultType, Report report) {
		this.resultType = resultType; 
		this.report = report;
	}
	public ReportResponse(ResultType resultType, ArrayList<String> facilities) {
		this.resultType = resultType;
		this.facilities = facilities;
	}
	public ReportResponse(ResultType resultType) {
		this.resultType = resultType;
	}
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
	public ArrayList<String> getFacilities(){
		return facilities;
	}
	public void setFacilities(ArrayList<String> facilities) {
		this.facilities = facilities;
	}
	
}
