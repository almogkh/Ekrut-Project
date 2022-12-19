package ekrut.net;

import java.io.Serializable;
import java.util.ArrayList;

import ekrut.entity.Order;

public class ShipmentResponse implements Serializable {
	private static final long serialVersionUID = -3750120930210878137L;
	private ResultType resultCode;
	private ArrayList<Order> OrderListForShipment;
	
	public ShipmentResponse(ResultType resultCode) {
		this.resultCode = resultCode;
	}

	public ResultType getResultCode() {
		return resultCode;
	}
	
	public ArrayList<Order> getOrdersForShipment(){
		return OrderListForShipment;
	}
}
