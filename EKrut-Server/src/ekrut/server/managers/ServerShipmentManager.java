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

	// Q.Nir , TBD - Need to change after Yovel implementation?
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
		if (shipmentRequest == null)
			throw new IllegalArgumentException("null shipmentRequest was provided.");

		ArrayList<Order> orderShipmentListForAppoval = orderDAO.fetchOrderShipmentList(area);
		
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
		if (shipmentRequest == null)
			throw new IllegalArgumentException("Null order was provided.");

		int orderId = shipmentRequest.getOrderId();
		LocalDateTime date = shipmentRequest.getDate();
		String clientAddress = shipmentRequest.getClientAddress();
		Order order = orderDAO.fetchOrderById(orderId);

		// In case the order null an exception will be thrown
		if (order == null)
			return new ShipmentResponse(ResultType.NOT_FOUND);

		if (order.getType() != OrderType.SHIPMENT)
			return new ShipmentResponse(ResultType.INVALID_INPUT);

		if (order.getStatus() != OrderStatus.SUBMITTED)
			return new ShipmentResponse(ResultType.INVALID_INPUT);

		// Estimate delivery time.
		LocalDateTime estimateDeliveryTime = ShipmentManagerUtils.estimatedArrivalTime(date, clientAddress);
		order.setDueDate(estimateDeliveryTime);

		// Q.Nir , do we need to add another method in case the worker cancel?
		// and for this we will need to add CANCELED_SHIPMENT in in OrderStatus
		// what's happen in case the worker cancel the shipment? we will choose to cancel
		// and continue with regular order process? or we will delete the whole order?
		if (!orderDAO.updateOrderStatus(orderId, OrderStatus.AWAITING_DELIVERY))
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);
		
		return new ShipmentResponse(ResultType.OK);
	}

	/**
	 * Confirms the delivery of an order.
	 * 
	 * @param shipmentRequest the {@code ShipmentRequest} object containing the request details
	 * @return a {@code ShipmentResponse} object with the result of the operation
	 */
	public ShipmentResponse confirmDelivery(ShipmentRequest shipmentRequest) {
		int orderId = shipmentRequest.getOrderId();
		Order order = orderDAO.fetchOrderById(orderId);
		if (order == null)
			return new ShipmentResponse(ResultType.NOT_FOUND);

		if (order.getType() != OrderType.SHIPMENT)
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		if (order.getStatus() != OrderStatus.AWAITING_DELIVERY)
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		if (!orderDAO.updateOrderStatus(orderId, OrderStatus.DELIVERY_CONFIRMED))
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		return new ShipmentResponse(ResultType.OK);
	}

	/**
	 * Marks an order as done.
	 * 
	 * @param shipmentRequest the {@code ShipmentRequest} object containing the
	 *                        request details
	 * @return a {@code ShipmentResponse} object with the result of the operation
	 */
	public ShipmentResponse setDone(ShipmentRequest shipmentRequest) {
		int orderId = shipmentRequest.getOrderId();
		Order order = orderDAO.fetchOrderById(orderId);
		if (order == null)
			return new ShipmentResponse(ResultType.NOT_FOUND);

		if (order.getType() != OrderType.SHIPMENT)
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		if (order.getStatus() != OrderStatus.DELIVERY_CONFIRMED)
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		if (!orderDAO.updateOrderStatus(orderId, OrderStatus.DONE))
			return new ShipmentResponse(ResultType.UNKNOWN_ERROR);

		return new ShipmentResponse(ResultType.OK);
	}
}
