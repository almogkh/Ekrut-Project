package ekrut.server.managers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import ekrut.entity.Order;
import ekrut.entity.OrderStatus;
import ekrut.entity.OrderType;
import ekrut.net.ResultType;
import ekrut.net.ShipmentRequest;
import ekrut.net.ShipmentResponse;
import ekrut.server.db.DBController;
import ekrut.server.db.OrderDAO;
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

	OrderDAO orderDAO;

	/**
	 * 
	 * Constructs a new {@code ServerShipmentManager} object.
	 * 
	 * @param con the database controller to use for accessing the database
	 */
	public ServerShipmentManager(DBController con) {
		orderDAO = new OrderDAO(con);
	}

	// Q.Nir , TBD - Need to change after Yovel implementation area at fetch.... method?
	// PLEASE, HOW CAN I DO IT?
	// C.Nir - Message need to be sent after confirmation.

	/**
	 * Fetches a list of shipment requests for approval.
	 * 
	 * @param shipmentRequest the {@code ShipmentRequest} object containing the request details
	 * @return a {@code ShipmentResponse} object with the result of the operation
	 * @throws IllegalArgumentException if {@code shipmentRequest} is {@code null}
	 */
	public ShipmentResponse fetchShipmentRequests(ShipmentRequest shipmentRequest, String area) {
		// Check if shipmentRequest is null.
		if (shipmentRequest == null)
			throw new IllegalArgumentException("null shipmentRequest was provided.");

		ArrayList<Order> orderShipmentListForAppoval = orderDAO.fetchOrderShipmentList(area);
		
		// Check if there any shipments on DB.
		if (orderShipmentListForAppoval == null)
			return new ShipmentResponse(ResultType.NOT_FOUND);
		
		return new ShipmentResponse(ResultType.OK, orderShipmentListForAppoval);
	}

	/**
	 * Confirms the shipment of an order by EKurt worker.
	 * 
	 * @param shipmentRequest the {@code ShipmentRequest} object containing the request details
	 * @return a {@code ShipmentResponse} object with the result of the operation
	 * @throws IllegalArgumentException if {@code shipmentRequest} is {@code null}
	 */
	public ShipmentResponse confirmShipment(ShipmentRequest shipmentRequest) {
		// In case the shipment request null an exception will be thrown.
		if (shipmentRequest == null)
			throw new IllegalArgumentException("Null order was provided.");
		
		// Prepare fields in order to calculate due date.
		int orderId = shipmentRequest.getOrderId();
		LocalDateTime date = shipmentRequest.getDate();
		String clientAddress = shipmentRequest.getClientAddress();
		Order order = orderDAO.fetchOrderById(orderId);
		
		// In case order is null return not found result.
		if (order == null)
			return new ShipmentResponse(ResultType.NOT_FOUND);

		// In case order is not for shipping return invalid input result.
		if (order.getType().equals(OrderType.SHIPMENT))
			return new ShipmentResponse(ResultType.INVALID_INPUT);

		// In case order status not submitted return invalid input result.
		if (order.getStatus().equals(OrderStatus.SUBMITTED))
			return new ShipmentResponse(ResultType.INVALID_INPUT);

		// Estimate delivery time.
		LocalDateTime estimateDeliveryTime = ShipmentManagerUtils.estimatedArrivalTime(date, clientAddress);
		// Set due date in order.
		order.setDueDate(estimateDeliveryTime);

		// Try to confirm shipment and update order status to awaiting for delivery.
		if (!orderDAO.updateOrderStatus(orderId, OrderStatus.AWAITING_DELIVERY))
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);
		
		return new ShipmentResponse(ResultType.OK);
	}

	/**
	 * Customer confirmation of shipment arrival.
	 * 
	 * @param shipmentRequest the {@code ShipmentRequest} object containing the request details.
	 * @return a {@code ShipmentResponse} object with the result of the operation.
	 */
	public ShipmentResponse confirmDelivery(ShipmentRequest shipmentRequest) {
		// In case the shipment request null an exception will be thrown.
		if (shipmentRequest == null)
			throw new IllegalArgumentException("Null order was provided.");
		
		// Get order by ID from DB.
		int orderId = shipmentRequest.getOrderId();
		Order order = orderDAO.fetchOrderById(orderId);
		
		// In case order is null return not found result.
		if (order == null)
			return new ShipmentResponse(ResultType.NOT_FOUND);

		// In case order is not for shipping return invalid input result.
		if (order.getType().equals(OrderType.SHIPMENT))
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		// In case order status not awaiting for delivery return unknown result.
		if (order.getStatus().equals(OrderStatus.AWAITING_DELIVERY))
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		// Try to update order status to confirmed.
		if (!orderDAO.updateOrderStatus(orderId, OrderStatus.DELIVERY_CONFIRMED))
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		return new ShipmentResponse(ResultType.OK);
	}

	/**
	 * Worker marks an order as done.
	 * 
	 * @param shipmentRequest the {@code ShipmentRequest} object containing the request details.
	 * @return a {@code ShipmentResponse} object with the result of the operation.
	 */
	public ShipmentResponse setDone(ShipmentRequest shipmentRequest) {
		// In case the shipment request null an exception will be thrown.
		if (shipmentRequest == null)
			throw new IllegalArgumentException("Null order was provided.");
		
		// Get order by ID from DB.
		int orderId = shipmentRequest.getOrderId();
		Order order = orderDAO.fetchOrderById(orderId);
		
		// In case order is null return not found result.
		if (order == null)
			return new ShipmentResponse(ResultType.NOT_FOUND);

		// In case order is not for shipping return invalid input result.
		if (order.getType().equals(OrderType.SHIPMENT))
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		// In case order status not confirmed return unknown result.
		if (order.getStatus().equals(OrderStatus.DELIVERY_CONFIRMED))
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		// Try to update order status to done.
		if (!orderDAO.updateOrderStatus(orderId, OrderStatus.DONE))
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		return new ShipmentResponse(ResultType.OK);
	}
}
