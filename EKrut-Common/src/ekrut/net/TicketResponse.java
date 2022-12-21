package ekrut.net;

import java.io.Serializable;

public class TicketResponse implements Serializable{
	
	private static final long serialVersionUID = 5458389358466759549L;
	private int resultCode;
	
	public TicketResponse(int resultCode) {
		this.resultCode = resultCode;
	}

	public int getResultCode() {
		return resultCode;
	}	
	
	
}
