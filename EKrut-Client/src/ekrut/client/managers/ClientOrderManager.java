package ekrut.client.managers;

import ekrut.entity.Order;
import ekrut.entity.OrderItem;
import ekrut.entity.OrderType;

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
	 * Cancels the active order. If no order is active, this method does nothing.
	 */
	public void cancelOrder() {
		activeOrder = null;
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
