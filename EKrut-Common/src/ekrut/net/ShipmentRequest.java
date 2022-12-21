package ekrut.net;

import java.io.Serializable;
import java.time.LocalDateTime;

import ekrut.entity.OrderStatus;

/**
 * 
 * Represents a shipment request made by a client or worker. 
 * It can be used to request different actions, such as fetching
 * orders for approval, confirming delivery, or marking an order as done.
 * 
 * @author Nir Betesh
 */
public class ShipmentRequest implements Serializable {

	private static final long serialVersionUID = 5704922451119200259L;
	private OrderStatus status;
	private ShipmentRequestType action;
	private int orderId;
	private String clientAddress;
	private LocalDateTime date;
	private String area;

	/**
	 * 
	 * Constructs a {@code ShipmentRequest} object for fetching orders for approval.
	 * 
	 * @param action the type of request being made
	 * @param area   the area for which to fetch orders
	 */
	public ShipmentRequest(ShipmentRequestType action, String area) {
		this.action = action;
		this.area = area;
	}

	/**
	 * 
	 * Constructs a {@code ShipmentRequest} object for confirming delivery or
	 * marking an order as done.
	 * 
	 * @param action      the type of request being made
	 * @param orderStatus the status of the order
	 * @param orderId     the ID of the order
	 */
	public ShipmentRequest(ShipmentRequestType action, OrderStatus orderStatus, int orderId) {
		this.action = action;
		this.status = orderStatus;
		this.orderId = orderId;
	}

	/**
	 * 
	 * Returns the status of the order associated with this request.
	 * 
	 * @return the status of the order
	 */
	public OrderStatus getStatus() {
		return status;
	}

	/**
	 * 
	 * Returns the type of request being made.
	 * 
	 * @return the type of request
	 */
	public ShipmentRequestType getAction() {
		return action;
	}

	/**
	 * 
	 * Returns the ID of the order associated with this request.
	 * 
	 * @return the ID of the order
	 */
	public int getOrderId() {
		return orderId;
	}

	/**
	 * 
	 * Returns the address of the client associated with this request.
	 * 
	 * @return the client address
	 */
	public String getClientAddress() {
		return clientAddress;
	}

	/**
	 * 
	 * Returns the date of shipment creation.
	 * 
	 * @return the date of the request
	 */
	public LocalDateTime getDate() {
		return date;
	}
	
	/**
	 * 
	 * Returns the area of shipment.
	 * 
	 * @return the date of the request
	 */
	public String getArea() {
		return area;
	}

}
