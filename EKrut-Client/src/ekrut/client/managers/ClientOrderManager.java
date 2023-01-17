package ekrut.client.managers;

import java.time.LocalDateTime;
import java.util.ArrayList;

import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.entity.Customer;
import ekrut.entity.Item;
import ekrut.entity.Order;
import ekrut.entity.OrderItem;
import ekrut.entity.OrderType;
import ekrut.entity.SaleDiscount;
import ekrut.entity.SaleDiscountType;
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
public class ClientOrderManager extends AbstractClientManager<OrderRequest, OrderResponse> {

	private Order activeOrder;
	private final String ekrutLocation;
	private ClientSalesManager salesManager;
	private float cachedPrice;
	private float cachedDiscount;
	private boolean dirtyPrice;
	private boolean dirtyDiscount;
	
	/**
	 * Constructs a new client order manager.
	 * 
	 * @param ekrutLocation The location of the machine on which the client is running. If this is null, that
	 *                      means the client is running the administrative section.
	 */
	public ClientOrderManager(EKrutClient client, String ekrutLocation) {
		super(client, OrderResponse.class);
		this.ekrutLocation = ekrutLocation;
		this.salesManager = client.getClientSalesManager();
		client.getClientSessionManager().registerOnLogoutHandler(this::cancelOrder);
	}
	
	/**
	 * Creates a new empty <b>pickup</b> order. There must not already be an active order  
	 * 
	 * @param ekrutLocation the machine from which the order is taken
	 */
	public void createOrder() {
		if (activeOrder != null)
			return;
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
			return;
		
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
			return;
		
		if (item.getItemQuantity() == 0) {
			removeItemFromOrder(item.getItem());
			return;
		}
		
		dirtyPrice = dirtyDiscount = true;
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
			return;
		
		dirtyPrice = dirtyDiscount = true;
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
			return null;
		
		return activeOrder.getItems();
	}
	
	/**
	 * Cancels the active order. If no order is active, this method does nothing.
	 */
	public void cancelOrder() {
		activeOrder = null;
		cachedPrice = cachedDiscount = 0;
		dirtyPrice = dirtyDiscount = false;
	}
	
	/**
	 * Sends an order creation request to the server.
	 * 
	 * @param creditCardNumber a string representing the credit card with which the payment
	 * 	                       the payment should be made or null to use the registered credit
	 *                         card number
	 * @return the new order ID if successful or -1 if the operation failed
	 */
	public int confirmOrder(String creditCardNumber) {
		if (activeOrder == null)
			return -1;
		
		if (creditCardNumber != null)
			activeOrder.setCreditCard(creditCardNumber);
		OrderResponse response = sendRequest(new OrderRequest(OrderRequestType.CREATE, activeOrder));
		if (response.getResult() == ResultType.OK) {
			Customer info = EKrutClientUI.getEkrutClient().getClientSessionManager().getUser().getCustomerInfo();
			if (info.getSubscriberNumber() != -1)
				info.setOrderedAsSub(true);
			cancelOrder();
			return response.getOrderId();
		}
		return -1;
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
	
	public float getTotalPrice() {
		if (activeOrder == null)
			return 0;
		if (!dirtyPrice)
			return cachedPrice;
		cachedPrice = activeOrder.getSumAmount();
		dirtyPrice = false;
		return cachedPrice;
	}
	
	public float getDiscount() {
		if (!dirtyDiscount)
			return cachedDiscount;
		float discount = 0;
		LocalDateTime now = LocalDateTime.now();
		ArrayList<SaleDiscount> sales;
		if (activeOrder.getType() != OrderType.PICKUP)
			sales = salesManager.fetchActiveSales();
		else
			sales = salesManager.fetchActiveSales(ekrutLocation);
		
		if (sales == null || sales.size() == 0)
			return discount;
		
		for (SaleDiscount sale : sales) {
			// Verify the active sale applies to right now
			if (sale.getDayOfSale().charAt(now.getDayOfWeek().getValue() % 7) != 'T')
				continue;
			if (sale.getStartTime().isAfter(now.toLocalTime()) || sale.getEndTime().isBefore(now.toLocalTime()))
				continue;
			
			if (sale.getType() == SaleDiscountType.THIRTY_PERCENT_OFF) {
				for (OrderItem item : activeOrder.getItems())
					discount += item.getItemQuantity() * item.getItem().getItemPrice() * 0.3;
			} else { // one plus one free
				for (OrderItem item : activeOrder.getItems())
					discount += (item.getItemQuantity() / 2) * item.getItem().getItemPrice();
			}
		}
		
		cachedDiscount = discount;
		dirtyDiscount = false;
		
		return discount;
	}
	
	/**
	 * Returns whether or not there is an active order
	 * 
	 * @return true if there is an active order, false otherwise
	 */
	public boolean isActiveOrder() {
		return activeOrder != null;
	}
	
	public OrderType getOrderType() {
		if (activeOrder == null)
			return null;
		return activeOrder.getType();
	}
	
	public String getEkrutLocation() {
		if (activeOrder == null)
			return null;
		return activeOrder.getEkrutLocation();
	}
}
