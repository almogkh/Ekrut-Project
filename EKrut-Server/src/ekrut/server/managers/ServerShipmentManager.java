package ekrut.server.managers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import ekrut.entity.Order;
import ekrut.entity.OrderStatus;
import ekrut.entity.OrderType;
import ekrut.entity.User;
import ekrut.net.ResultType;
import ekrut.net.ShipmentRequest;
import ekrut.net.ShipmentResponse;
import ekrut.server.PopupUserNotifier;
import ekrut.server.db.DBController;
import ekrut.server.db.OrderDAO;
import ekrut.server.db.UserDAO;
import ekrut.server.intefaces.IUserNotifier;
import ekrut.server.intefaces.ShipmentManagerUtils;

/**
 * The {@code ServerShipmentManager} class is responsible for managing the
 * server shipping requests. This includes fetching shipment requests for
 * approval, confirming the shipment, confirming the delivery, and marking the
 * order as done.
 * 
 * @author Nir Betesh
 */
public class ServerShipmentManager {

	private OrderDAO orderDAO;
	private UserDAO userDAO;
	private IUserNotifier userNotifier;
	/**
	 * 
	 * Constructs a new {@code ServerShipmentManager} object.
	 * 
	 * @param con the database controller to use for accessing the database
	 */
	public ServerShipmentManager(DBController con, ServerSessionManager serverSessionManager) {
		orderDAO = new OrderDAO(con);
		userDAO = new UserDAO(con);
		userNotifier = new PopupUserNotifier(con, serverSessionManager);
	}


	/**
	 * Fetches a list of shipment requests for approval.
	 * 
	 * @param shipmentRequest the {@code ShipmentRequest} object containing the
	 *                        request details
	 * @return a {@code ShipmentResponse} object with the result of the operation
	 * @throws IllegalArgumentException if {@code shipmentRequest} is {@code null}
	 */
	public ShipmentResponse fetchShipmentRequests(ShipmentRequest shipmentRequest, String area) {
		// Check if shipmentRequest is null.
		if (shipmentRequest == null)
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		ArrayList<Order> orderShipmentListForAppoval = orderDAO.fetchOrderShipmentListByArea(area);

		// Check if there any shipments on DB.
		if (orderShipmentListForAppoval == null)
			return new ShipmentResponse(ResultType.NOT_FOUND);

		return new ShipmentResponse(ResultType.OK, orderShipmentListForAppoval);
	}

	/**
	 * Confirms the shipment of an order by EKurt worker.
	 * Sends (Currently a simulation) estimated arrival time for user on email and SMS.
	 * 
	 * @param shipmentRequest the {@code ShipmentRequest} object containing the request details
	 * @return a {@code ShipmentResponse} object with the result of the operation
	 * @throws IllegalArgumentException if {@code shipmentRequest} is {@code null}
	 */
	public ShipmentResponse confirmShipment(ShipmentRequest shipmentRequest) {
		// In case the shipment request null an exception will be thrown.
		if (shipmentRequest == null)
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		// Prepare fields in order to calculate due date.
		int orderId = shipmentRequest.getOrderId();
		
		Order order = orderDAO.fetchOrderById(orderId);
		// In case order is null return not found result.
		if (order == null)
			return new ShipmentResponse(ResultType.NOT_FOUND);
		
		LocalDateTime date = order.getDate();
		String clientAddress = order.getClientAddress();

		// In case order is not for shipping return invalid input result.
		if (order.getType() != OrderType.SHIPMENT)
			return new ShipmentResponse(ResultType.INVALID_INPUT);

		// In case order status not submitted return invalid input result.
		if (order.getStatus() != OrderStatus.SUBMITTED)
			return new ShipmentResponse(ResultType.INVALID_INPUT);
		
		// Try to 1update order status to awaiting for delivery.
		if (!orderDAO.updateOrderStatus(orderId, OrderStatus.AWAITING_DELIVERY))
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);
		
		// Estimate delivery time.
		LocalDateTime estimateDeliveryTime = ShipmentManagerUtils.estimatedArrivalTime(date, clientAddress);
		// Set due date in order.
		order.setDueDate(estimateDeliveryTime);
		orderDAO.updateOrderDueDate(order.getOrderId(), estimateDeliveryTime);
		
		// Send message to customer when his shippment was approved.
		String username = order.getUsername();
		
		// Q.Nir - Need to check if success??
		User user = userDAO.fetchUserByUsername(username);	
		String notificationMsg = "Hi " + user.getFirstName() + ",\n\n"
							   + "We wanted to let you know that your delivery is approved by our shipment department,\n"
							   + "and your shipment is currently in the checkout process!.\n"
							   + "your shipment expected to arrive by " + estimateDeliveryTime.toString() + " o'clock.\n"
							   + "When your shipment has arrived, please confirm that you have received the shipment in the application.\n"
							   + "Keep an eye out for it and let us know if you have any questions or concerns.\n\n"
							   + "Best regards,\nEKrut";
		userNotifier.sendNotification(notificationMsg, user.getEmail(), user.getPhoneNumber());		
		return new ShipmentResponse(ResultType.OK);
	}

	/**
	 * Customer confirmation of shipment arrival.
	 * 
	 * @param shipmentRequest the {@code ShipmentRequest} object containing the
	 *                        request details.
	 * @return a {@code ShipmentResponse} object with the result of the operation.
	 */
	public ShipmentResponse confirmDelivery(ShipmentRequest shipmentRequest) {
		// In case the shipment request null an exception will be thrown.
		if (shipmentRequest == null)
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		// Get order by ID from DB.
		int orderId = shipmentRequest.getOrderId();
		Order order = orderDAO.fetchOrderById(orderId);

		// In case order is null return not found result.
		if (order == null)
			return new ShipmentResponse(ResultType.NOT_FOUND);

		// In case order is not for shipping return invalid input result.
		if (order.getType() != OrderType.SHIPMENT)
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		// In case order status not awaiting for delivery return unknown result.
		if (order.getStatus() != OrderStatus.AWAITING_DELIVERY)
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		// Try to update order status to confirmed.
		if (!orderDAO.updateOrderStatus(orderId, OrderStatus.DELIVERY_CONFIRMED))
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		return new ShipmentResponse(ResultType.OK);
	}

	/**
	 * Worker marks an order as done.
	 * 
	 * @param shipmentRequest the {@code ShipmentRequest} object containing the
	 *                        request details.
	 * @return a {@code ShipmentResponse} object with the result of the operation.
	 */
	public ShipmentResponse setDone(ShipmentRequest shipmentRequest) {
		// In case the shipment request null an exception will be thrown.
		if (shipmentRequest == null)
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		// Get order by ID from DB.
		int orderId = shipmentRequest.getOrderId();
		Order order = orderDAO.fetchOrderById(orderId);

		// In case order is null return not found result.
		if (order == null)
			return new ShipmentResponse(ResultType.NOT_FOUND);

		// In case order is not for shipping return invalid input result.
		if (order.getType() != OrderType.SHIPMENT)
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		// In case order status not confirmed return unknown result.
		if (order.getStatus() != OrderStatus.DELIVERY_CONFIRMED)
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		// Try to update order status to done.
		if (!orderDAO.updateOrderStatus(orderId, OrderStatus.DONE))
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		return new ShipmentResponse(ResultType.OK);
	}
}
