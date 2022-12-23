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
	private final String ekrutLocation;
	
	/**
	 * Constructs a new client order manager.
	 * 
	 * @param ekrutLocation The location of the machine on which the client is running. If this is null, that
	 *                      means the client is running the administrative section.
	 */
	public ClientOrderManager(String ekrutLocation) {
		this.ekrutLocation = ekrutLocation;
	}
	
	/**
	 * Creates a new empty <b>pickup</b> order. There must not already be an active order  
	 * 
	 * @param ekrutLocation the machine from which the order is taken
	 */
	public void createOrder() {
		if (activeOrder != null)
			throw new IllegalStateException("An order is already in progress");
		activeOrder = new Order(OrderType.PICKUP, ekrutLocation);
	}
	
	/**
	 * Creates a new empty <b>remote or shipment</b> order. There must not already be an active order (see {@link #isActiveOrder()}).
	 * 
	 * @param param      the machine from which to pickup the order if this is a remote order or the client address
	 *                   if this is a shipment order
	 * @param isShipment is this a shipment order or a remote order
	 */
	public void createOrder(String param, boolean isShipment) {
		if (activeOrder != null)
			throw new IllegalStateException("An order is already in progress");
		
		OrderType type = isShipment ? OrderType.SHIPMENT : OrderType.REMOTE;
		activeOrder = new Order(type, param);
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
		activeOrder = null;
		return response.getResult();
	}
	
	/**
	 * Retrieves the list of orders that belong to this user.
	 * 
	 * @return the list of orders that belong to this user
	 */
	public ArrayList<Order> fetchOrders() {
		OrderResponse response = sendRequest(new OrderRequest(OrderRequestType.FETCH));
		return response.getOrders();
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
