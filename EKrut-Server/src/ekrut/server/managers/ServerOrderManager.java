package ekrut.server.managers;

import java.time.LocalDateTime;
import java.util.ArrayList;

import ekrut.entity.Customer;
import ekrut.entity.InventoryItem;
import ekrut.entity.Order;
import ekrut.entity.OrderItem;
import ekrut.entity.OrderStatus;
import ekrut.entity.OrderType;
import ekrut.entity.SaleDiscount;
import ekrut.entity.SaleDiscountType;
import ekrut.entity.User;
import ekrut.net.InventoryItemRequest;
import ekrut.net.InventoryItemRequestType;
import ekrut.net.OrderRequest;
import ekrut.net.OrderRequestType;
import ekrut.net.OrderResponse;
import ekrut.net.ResultType;
import ekrut.server.db.DBController;
import ekrut.server.db.OrderDAO;
import ekrut.server.db.TicketDAO;
import ekrut.server.intefaces.IPaymentProcessor;
import ekrut.server.intefaces.StubPaymentProcessor;

/**
 * Manages order management on the server side.
 * 
 * @author Almog Khaikin
 */
public class ServerOrderManager extends AbstractServerManager<OrderRequest, OrderResponse> {

	private DBController dbCon;
	private OrderDAO orderDAO;
	private TicketDAO ticketDAO;
	private ServerSalesManager salesManager;
	private ServerInventoryManager inventoryManager;
	private IPaymentProcessor paymentProcessor;
	
	public ServerOrderManager(DBController dbCon, ServerSalesManager salesManager,
							ServerInventoryManager inventoryManager) {
		super(OrderRequest.class, new OrderResponse(ResultType.UNKNOWN_ERROR));
		this.dbCon = dbCon;
		this.orderDAO = new OrderDAO(dbCon);
		this.ticketDAO = new TicketDAO(dbCon);
		this.salesManager = salesManager;
		this.inventoryManager = inventoryManager;
		this.paymentProcessor = new StubPaymentProcessor();
	}
	
	@Override
	protected OrderResponse handleRequest(OrderRequest request, User user) {
		switch (request.getAction()) {
		case CREATE:
			return createOrder(request, user);
		case FETCH:
			return fetchOrders(request, user);
		case PICKUP:
			return pickupOrder(request, user);
		default:
			return new OrderResponse(ResultType.UNKNOWN_ERROR);
		}
	}
	
	private float computeDiscount(Order order, User user) {
		float discount = 0;
		LocalDateTime now = LocalDateTime.now();
		String area;
		if (order.getType() != OrderType.PICKUP) {
			area = user.getArea();
		} else {
			area = ticketDAO.fetchAreaByEkrutLocation(order.getEkrutLocation());
		}
		ArrayList<SaleDiscount> sales = salesManager.fetchSalesByArea(area).getSales();
		
		if (sales == null || sales.size() == 0)
			return discount;
		
		for (SaleDiscount sale : sales) {
			// Verify the active sale applies to right now
			if (sale.getDayOfSale().charAt(now.getDayOfWeek().getValue() % 7) != 'T')
				continue;
			if (sale.getStartTime().isAfter(now.toLocalTime()) || sale.getEndTime().isBefore(now.toLocalTime()))
				continue;
			
			if (sale.getType() == SaleDiscountType.THIRTY_PERCENT_OFF) {
				for (OrderItem item : order.getItems())
					discount += item.getItemQuantity() * item.getItem().getItemPrice() * 0.3;
			} else { // one plus one free
				for (OrderItem item : order.getItems())
					discount += (item.getItemQuantity() / 2) * item.getItem().getItemPrice();
			}
		}
		
		return discount;
	}
	
	/**
	 * Creates a new order.
	 * This method computes the amount that needs to be paid given any active sales, attempts to charge
	 * the user's credit card and inserts the order into the database.
	 * 
	 * @param request the order creation request
	 * @param user    the user that sent the request
	 * @return        a response detailing whether the operation was successful
	 */
	private OrderResponse createOrder(OrderRequest request, User user) {
		if (request.getAction() != OrderRequestType.CREATE || request.getOrder() == null)
			return new OrderResponse(ResultType.INVALID_INPUT);
		
		if (user == null)
			return new OrderResponse(ResultType.LOGIN_REQUIRED);
		
		Order order = request.getOrder();
		order.setDate(LocalDateTime.now());
		order.setStatus(order.getType() == OrderType.PICKUP ? OrderStatus.DONE : OrderStatus.SUBMITTED);
		order.setUsername(user.getUsername());
		
		Customer info = user.getCustomerInfo();
		if (info == null)
			return new OrderResponse(ResultType.UNKNOWN_ERROR);
		boolean subscriber = info.getSubscriberNumber() != -1;

		// Compute the total amount the user has to pay
		float debitAmount = order.getSumAmount();
		if (subscriber)
			debitAmount -= computeDiscount(order, user);
		
		// First order from subscriber gets a 20% discount
		if (subscriber && !info.hasOrderedAsSub())
			debitAmount *= 0.8f;
		
		String creditCardNumber = order.getCreditCard();
		if (creditCardNumber == null)
			creditCardNumber = info.getCreditCard();
		
		// Subscribers can choose to pay once a month for all of their orders
		if (subscriber && info.isMonthlyCharge()) {
			if (!paymentProcessor.addToCharges(creditCardNumber, debitAmount))
				return new OrderResponse(ResultType.INVALID_INPUT);
		} else {
			if (!paymentProcessor.submitPayment(creditCardNumber, debitAmount))
				return new OrderResponse(ResultType.INVALID_INPUT);
		}
		
		int retries = 5;
		do {
			dbCon.beginTransaction();
			try {
				if (!orderDAO.createOrder(order, subscriber ? user.getUsername() : null)) {
					dbCon.abortTransaction();
					return new OrderResponse(ResultType.UNKNOWN_ERROR);
				}
				
				if (order.getEkrutLocation() != null) {
					ArrayList<InventoryItem> items = inventoryManager.fetchInventoryItemsByEkrutLocation(
						new InventoryItemRequest(InventoryItemRequestType.FETCH_ALL_INVENTORYITEMS_IN_MACHINE, 
												order.getEkrutLocation())).getInventoryItems();
					for (OrderItem item : order.getItems()) {
						for (InventoryItem invItem : items) {
							if (item.getItem().equals(invItem.getItem())) {
								inventoryManager.updateInventoryQuantity(new InventoryItemRequest(
															InventoryItemRequestType.UPDATE_ITEM_QUANTITY,
															item.getItem().getItemId(),
															invItem.getItemQuantity() - item.getItemQuantity(),
															order.getEkrutLocation()));
								break;
							}
						}
					}
				}
				
				dbCon.commitTransaction();
				// We need to return the order ID for remote orders.
				return new OrderResponse(ResultType.OK, order.getOrderId());	
			} catch (DeadlockException e) {
				dbCon.abortTransaction();
				retries--;
			}
		} while (retries > 0);
		
		return new OrderResponse(ResultType.UNKNOWN_ERROR);
	}
	
	/**
	 * Retrieves all the orders that belong to this user.
	 * 
	 * @param request the fetch request
	 * @param user    the user that sent the request
	 * @return        a response containing the list of orders or an error if one occurred
	 */
	private OrderResponse fetchOrders(OrderRequest request, User user) {
		if (request.getAction() != OrderRequestType.FETCH)
			return new OrderResponse(ResultType.INVALID_INPUT);
		
		if (user == null)
			return new OrderResponse(ResultType.LOGIN_REQUIRED);
		
		ArrayList<Order> orders = orderDAO.fetchOrdersByUsername(user.getUsername());
		if (orders == null)
			return new OrderResponse(ResultType.UNKNOWN_ERROR);
		
		return new OrderResponse(ResultType.OK, orders);
	}
	
	/**
	 * Handles pickup of an order that was ordered remotely.
	 * 
	 * @param request the order pickup request
	 * @param user    the user that sent the request
	 * @return        the result of the operation
	 */
	private OrderResponse pickupOrder(OrderRequest request, User user) {
		if (request.getAction() != OrderRequestType.PICKUP)
			return new OrderResponse(ResultType.INVALID_INPUT);
		
		if (user == null)
			return new OrderResponse(ResultType.LOGIN_REQUIRED);
		
		Order order = orderDAO.fetchOrderById(request.getOrderId());
		if (order == null)
			return new OrderResponse(ResultType.NOT_FOUND);
		
		if (order.getType() != OrderType.REMOTE || !order.getUsername().equals(user.getUsername()))
			return new OrderResponse(ResultType.INVALID_INPUT);
		
		if (!orderDAO.updateOrderStatus(request.getOrderId(), OrderStatus.DONE))
			return new OrderResponse(ResultType.UNKNOWN_ERROR);
		return new OrderResponse(ResultType.OK);
	}
}
