package ekrut.client.managers;

import java.util.ArrayList;

import ekrut.entity.Item;
import ekrut.entity.Order;
import ekrut.entity.OrderItem;
import ekrut.entity.OrderType;
import ekrut.net.OrderRequest;
import ekrut.net.OrderRequestType;
import ekrut.net.OrderResponse;
import ekrut.net.ResultType;

/**
 * This class manages orders on the client side.
 * 
 * @author Almog Khaikin
 *
 */
public class ClientOrderManager {

	private Order activeOrder;
	
	/**
	 * Creates a new empty <b>pickup</b> order. There must not already be an active order  
	 * 
	 * @param ekrutLocation the machine from which the order is taken
	 */
	public void createOrder(String ekrutLocation) {
		if (activeOrder != null)
			throw new IllegalStateException("An order is already in progress");
		activeOrder = new Order(OrderType.PICKUP, ekrutLocation);
	}
	
	/**
	 * Creates a new empty <b>remote</b> order. There must not already be an active order (see {@link #isActiveOrder()}).
	 * 
	 * @param ekrutLocation the machine from which the order is taken
	 * @param clientAddress the address to which the order should be shipped
	 */
	public void createOrder(String ekrutLocation, String clientAddress) {
		if (activeOrder != null)
			throw new IllegalStateException("An order is already in progress");
		activeOrder = new Order(OrderType.REMOTE, clientAddress, ekrutLocation);
	}
	
	/**
	 * Adds an item to an order. There must already be an active order (see {@link #isActiveOrder()}).
	 * 
	 * @param item the order item to add
	 */
	public void addItemToOrder(OrderItem item) {
		if (activeOrder == null)
			throw new IllegalStateException("No active order");
		
		// Check if this item is already in the order and if so, just update the quantity
		for (OrderItem i : activeOrder.getItems()) {
			if (i.getItem().equals(item.getItem())) {
				i.setItemQuantity(item.getItemQuantity());
				return;
			}
		}
		
		activeOrder.getItems().add(item);
	}
	
	/**
	 * Removes an item from an order. There must already be an active order (see {@link #isActiveOrder()}).
	 * 
	 * @param item the item to remove
	 */
	public void removeItemFromOrder(Item item) {
		if (activeOrder == null)
			throw new IllegalStateException("No active order");
		
		ArrayList<OrderItem>  items = activeOrder.getItems();
		for (OrderItem i : items)
			if (i.getItem().equals(item)) {
				items.remove(i);
				return;
			}
	}
	
	/**
	 * Gets the list of items in the current order.
	 * 
	 * @return the list of items in the current active order
	 */
	public ArrayList<OrderItem> getActiveOrderItems() {
		if (activeOrder == null)
			throw new IllegalStateException("No active order");
		
		return activeOrder.getItems();
	}
	
	/**
	 * Cancels the active order. If no order is active, this method does nothing.
	 */
	public void cancelOrder() {
		activeOrder = null;
	}
	
	private OrderResponse sendRequest(OrderRequest request) {
		return null;
	}
	
	/**
	 * Sends an order creation request to the server.
	 * 
	 * @return the result of the operation
	 */
	public ResultType confirmOrder() {
		if (activeOrder == null)
			throw new IllegalStateException("No active order");
		
		OrderResponse response = sendRequest(new OrderRequest(OrderRequestType.CREATE, activeOrder));
		return response.getResult();
	}
	
	/**
	 * Sends a request to the server to pickup an order that was ordered remotely.
	 * 
	 * @param orderId   the ID of the order that should be picked up
	 * @return          the result of the operation
	 */
	public ResultType pickupOrder(int orderId) {
		OrderResponse response = sendRequest(new OrderRequest(OrderRequestType.PICKUP, orderId));
		return response.getResult();
	}
	
	/**
	 * Returns whether or not there is an active order
	 * 
	 * @return true if there is an active order, false otherwise
	 */
	public boolean isActiveOrder() {
		return activeOrder != null;
	}
}
