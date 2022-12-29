package ekrut.net;

import java.io.Serializable;
import ekrut.entity.Order;

public class OrderRequest implements Serializable{
	
	private static final long serialVersionUID = -4824573021802436936L;
	
	private final OrderRequestType action;
	private int orderId;
	private Order order;
	
	public OrderRequest(OrderRequestType action) {
		this.action = action;
	}
	
	public OrderRequest(OrderRequestType action, Order order) {
		this.action = action;
		this.order = order;
	}

	public OrderRequest(OrderRequestType action, int orderId) {
		this.action = action;
		this.orderId = orderId;
	}

	public OrderRequestType getAction() {
		return action;
	}

	public int getOrderId() {
		return orderId;
	}

	public Order getOrder() {
		return order;
	}
}
