package ekrut.net;

import java.io.Serializable;
import java.time.LocalDateTime;

import ekrut.entity.OrderStatus;

public class ShipmentRequest implements Serializable {

	private static final long serialVersionUID = 5704922451119200259L;
	private OrderStatus status;
	private ShipmentRequestType action;
	private int orderId;
	private String clientAddress;
	private LocalDateTime date;
	private String area;
	
	
	// Worker Confirm deliveries.
	public ShipmentRequest(ShipmentRequestType action, String area) {
		this.action = action;
		this.area = area;
	}
	
	public ShipmentRequest(ShipmentRequestType action, OrderStatus orderStatus, int orderId) {
		this.action = action;
		this.status = orderStatus;
		this.orderId = orderId;
	}
	
	public OrderStatus getStatus() {
		return status;
	}

	public ShipmentRequestType getAction() {
		return action;
	}

	public int getOrderId() {
		return orderId;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public LocalDateTime getDate() {
		return date;
	}
	
	
}
