package ekrut.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Order implements Serializable {

	private static final long serialVersionUID = -1777296007338157917L;

	private Integer orderId;
	private LocalDateTime date;
	private OrderStatus status;
	private OrderType type;
	private LocalDateTime dueDate;
	private String clientAddress;
	private String ekrutLocation;
	private String username;
	private ArrayList<OrderItem> items;

	public Order(OrderType type, String param) {
		this.type = type;
		if (type == OrderType.PICKUP || type == OrderType.REMOTE)
			this.ekrutLocation = param;
		else
			this.clientAddress = param;
	}

	public Order(int orderId, LocalDateTime date, OrderStatus status, OrderType type, LocalDateTime dueDate,
			String clientAddress, String ekrutLocation, String username) {
		this.orderId = orderId;
		this.date = date;
		this.status = status;
		this.type = type;
		this.dueDate = dueDate;
		this.clientAddress = clientAddress;
		this.ekrutLocation = ekrutLocation;
		this.username = username;
	}

	public Integer getOrderId() {
		return orderId;
	}
	
	public void setOrderId(int orderId) {
		if (this.orderId != null)
			throw new IllegalStateException("Order ID already set");
		this.orderId = orderId;
	}

	public LocalDateTime getDate() {
		return date;
	}
	
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	
	public int getSumAmount() {
		int sum = 0;
		
		for (OrderItem item : items) {
			sum += item.getItemQuantity() * item.getItem().getItemPrice();
		}
		return sum;
	}

	public OrderStatus getStatus() {
		return status;
	}
	
	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public OrderType getType() {
		return type;
	}

	public LocalDateTime getDueDate() {
		return dueDate;
	}
	
	public void setDueDate(LocalDateTime dueDate) {
		this.dueDate = dueDate;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public String getEkrutLocation() {
		return ekrutLocation;
	}

	public ArrayList<OrderItem> getItems() {
		return items;
	}

	public void setItems(ArrayList<OrderItem> items) {
		this.items = items;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
