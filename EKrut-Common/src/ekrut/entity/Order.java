package ekrut.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class Order implements Serializable {

	private static final long serialVersionUID = -1777296007338157917L;

	private int orderId;
	private boolean isValidId;
	private LocalDate date;
	private OrderStatus status;
	private OrderType type;
	private LocalDate dueDate;
	private String clientAddress;
	private String ekrutLocation;
	private ArrayList<OrderItem> items;

	public Order(OrderType type, String ekrutLocation) {
		this(type, null, ekrutLocation);
	}
	
	public Order(OrderType type, String clientAddress, String ekrutLocation) {
		this.type = type;
		this.clientAddress = clientAddress;
		this.ekrutLocation = ekrutLocation;
		this.items = new ArrayList<>();
	}

	public Integer getOrderId() {
		if (isValidId)
			return orderId;
		return null;
	}
	
	public void setOrderId(int orderId) {
		if (isValidId)
			throw new IllegalStateException("Order ID already set");
		this.orderId = orderId;
		this.isValidId = true;
	}

	public LocalDate getDate() {
		return date;
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

	public OrderType getType() {
		return type;
	}

	public LocalDate getDueDate() {
		return dueDate;
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
}
