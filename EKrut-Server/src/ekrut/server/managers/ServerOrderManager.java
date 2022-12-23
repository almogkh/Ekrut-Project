package ekrut.server.managers;

import java.time.LocalDateTime;
import java.util.ArrayList;

import ekrut.entity.Order;
import ekrut.entity.OrderStatus;
import ekrut.entity.OrderType;
import ekrut.entity.User;
import ekrut.net.OrderRequest;
import ekrut.net.OrderRequestType;
import ekrut.net.OrderResponse;
import ekrut.net.ResultType;
import ekrut.server.db.OrderDAO;
import ocsf.server.ConnectionToClient;

/**
 * Manages order management on the server side.
 * 
 * @author Almog Khaikin
 */
public class ServerOrderManager {

	private OrderDAO orderDAO;
	private ServerSessionManager sessionManager;
	
	public ServerOrderManager(OrderDAO orderDAO, ServerSessionManager sessionManager) {
		this.orderDAO = orderDAO;
		this.sessionManager = sessionManager;
	}
	
	/**
	 * Creates a new order in the database.
	 * 
	 * @param request the order creation request
	 * @param con     the connection through which the request was received
	 * @return        a response detailing whether the operation was successful
	 */
	public OrderResponse createOrder(OrderRequest request, ConnectionToClient con) {
		if (request.getAction() != OrderRequestType.CREATE || request.getOrder() == null)
			return new OrderResponse(ResultType.INVALID_INPUT);
		
		User user = getAssociatedUser(con);
		if (user == null)
			return new OrderResponse(ResultType.LOGIN_REQUIRED);
		
		Order order = request.getOrder();
		order.setDate(LocalDateTime.now());
		order.setStatus(OrderStatus.SUBMITTED);
		order.setUsername(user.getUsername());
		
		if (!orderDAO.createOrder(order))
			return new OrderResponse(ResultType.UNKNOWN_ERROR);
		
		// We need to return the order ID for remote orders.
		return new OrderResponse(ResultType.OK, order.getOrderId());	
	}
	
	/**
	 * Retrieves all the orders that belong to this user.
	 * 
	 * @param request the fetch request
	 * @param con     the connection through which the request was received
	 * @return        a response containing the list of orders or an error if one occurred
	 */
	public OrderResponse fetchOrders(OrderRequest request, ConnectionToClient con) {
		if (request.getAction() != OrderRequestType.FETCH)
			return new OrderResponse(ResultType.INVALID_INPUT);
		
		User user = getAssociatedUser(con);
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
	 * @param con     the connection through which the request was received
	 * @return        the result of the operation
	 */
	public OrderResponse pickupOrder(OrderRequest request, ConnectionToClient con) {
		if (request.getAction() != OrderRequestType.PICKUP)
			return new OrderResponse(ResultType.INVALID_INPUT);
		
		User user = getAssociatedUser(con);
		if (user == null)
			return new OrderResponse(ResultType.LOGIN_REQUIRED);
		
		Order order = orderDAO.fetchOrderById(request.getOrderId());
		if (order == null)
			return new OrderResponse(ResultType.NOT_FOUND);
		
		if (order.getType() != OrderType.PICKUP || order.getUsername() != user.getUsername())
			return new OrderResponse(ResultType.INVALID_INPUT);
		
		orderDAO.updateOrderStatus(request.getOrderId(), OrderStatus.DONE);
		return new OrderResponse(ResultType.OK);
	}
	
	private User getAssociatedUser(ConnectionToClient con) {
		return null;
	}
}
