package ekrut.net;

import java.io.Serializable;
import java.util.ArrayList;
import ekrut.entity.Order;

/**
 * 
 * Represents a response to a shipment request.
 */
public class ShipmentResponse implements Serializable {

	private static final long serialVersionUID = -3750120930210878137L;

	/** The result code of the response. */
	private ResultType resultCode;

	/** A list of orders for shipment. */
	private ArrayList<Order> OrderListForShipment;

	/**
	 * 
	 * Constructs a new {@code ShipmentResponse} object with the specified result
	 * code.
	 * 
	 * @param resultCode the result code of the response
	 */
	public ShipmentResponse(ResultType resultCode) {
		this.resultCode = resultCode;
	}

	/**
	 * 
	 * Returns the result code of the response.
	 * 
	 * @return the result code of the response
	 */
	public ResultType getResultCode() {
		return resultCode;
	}

	/**
	 * 
	 * Returns the list of orders for shipment.
	 * 
	 * @return the list of orders for shipment
	 */
	public ArrayList<Order> getOrdersForShipment() {
		return OrderListForShipment;
	}
}