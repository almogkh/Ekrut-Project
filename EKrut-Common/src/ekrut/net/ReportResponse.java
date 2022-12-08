package ekrut.net;

import java.io.Serializable;
import ekrut.entity.Report;

public class ReportResponse implements Serializable{
	private static final long serialVersionUID = -3756403993356581023L;
	private int resultCode;
	private Report report;
}
