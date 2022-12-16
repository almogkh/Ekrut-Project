package ekrut.net;

import java.io.Serializable;

public class ShipmentResponse implements Serializable {
	private static final long serialVersionUID = -3750120930210878137L;
	private String resultCode;
	
	public ShipmentResponse(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultCode() {
		return resultCode;
	}
	
}
