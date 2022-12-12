package ekrut.net;
import ekrut.entity.Report;
import java.io.Serializable;
import java.util.ArrayList;

public class OrderResponse implements Serializable{
	private static final long serialVersionUID = 738294735018991515L;
	private int resultCode;
	private ArrayList<Report> reportsList = new ArrayList<Report>();
}
