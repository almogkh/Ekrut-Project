package ekrut.net;

import java.io.Serializable;
import java.time.LocalDateTime;

import ekrut.entity.OrderStatus;

public class ShipmentRequest implements Serializable {

	private static final long serialVersionUID = 5704922451119200259L;
	private OrderStatus action;
	private int orderId;
	private String clientAddress;
	private LocalDateTime date;
	
	public ShipmentRequest(int orderId, String clientAddress, LocalDateTime date) {
		this.action = OrderStatus.AWAITING_DELIVERY;
		this.orderId = orderId;
		this.clientAddress = clientAddress;
		this.date = date;
	}
	
	
	public ShipmentRequest(OrderStatus awaitingDelivery, int orderId) {
		this.action = awaitingDelivery;
		this.orderId = orderId;
	}
	

	public OrderStatus getAction() {
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
