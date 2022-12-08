package ekrut.net;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ReportRequest implements Serializable{
	private static final long serialVersionUID = -7271037236386122944L;
	private String area;
	private String reportType;
	private LocalDateTime date;
}
