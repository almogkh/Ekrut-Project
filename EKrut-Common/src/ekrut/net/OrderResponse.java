package ekrut.net;

import ekrut.entity.Order;
import java.io.Serializable;
import java.util.ArrayList;

public class OrderResponse implements Serializable{
	
	private static final long serialVersionUID = 738294735018991515L;
	
	private final ResultType result;
	private ArrayList<Order> orders;
	private Order order;
	private int orderId;
	
	public OrderResponse(ResultType result) {
		this.result = result;
	}
	
	public OrderResponse(ResultType result, int orderId) {
		this.result = result;
		this.orderId = orderId;
	}
	
	public OrderResponse(ResultType result, Order order) {
		this.result = result;
		this.order = order;
	}
	
	public OrderResponse(ResultType result, ArrayList<Order> orders) {
		this.result = result;
		this.orders = orders;
	}

	public ResultType getResult() {
		return result;
	}

	public ArrayList<Order> getOrders() {
		return orders;
	}

	public Order getOrder() {
		return order;
	}

	public int getOrderId() {
		return orderId;
	}
	
}
