package ekrut.client.managers;
import java.io.IOException;
import ekrut.net.ReportRequest;
import ekrut.net.ReportResponse;


public class ClientReportManager {
	// method that send request to the server
	ReportRequest request = new ReportRequest();
	
	/*
	private boolean sendReportRequest(ReportRequest request) {
		try {
			//sendToServer(request);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false; 
	}
	*/
	private void handleReportResponse(ReportResponse response) {
		if(response.getResultCode() == 0) {
			//handle error
		}
		else {
			// do something? maybe only the viewer get the response..
		}
	}
	
	/* those methods are filters for the arrayList of reports*/
	
}
	// method that handle message from server(reportRespone)
	// filter reports? (at view class)
	
