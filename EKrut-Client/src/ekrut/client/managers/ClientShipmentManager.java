package ekrut.client.managers;

import java.util.ArrayList;

import ekrut.client.EKrutClient;
import ekrut.entity.Order;
import ekrut.entity.OrderStatus;
import ekrut.net.ShipmentRequest;
import ekrut.net.ShipmentRequestType;
import ekrut.net.ShipmentResponse;

/**
 * This class represents the client side manager for handling shipments. It
 * provides methods for fetching orders for shipment in a specific area,
 * confirming shipment, and confirming delivery of an order.
 * 
 * @author Nir Betesh
 */
public class ClientShipmentManager extends AbstractClientManager<ShipmentRequest, ShipmentResponse> {

	public ClientShipmentManager(EKrutClient client) {
		super(client, ShipmentResponse.class);
	}

	/**
	 * Fetches a list of orders that are ready for shipment in the specified area.
	 * 
	 * @param area the area to fetch orders for
	 * @return a list of orders ready for shipment
	 */
	public ArrayList<Order> fetchShipmentRequests(String area) {
		if (area == null)
			return null;

		// Prepare ShipmentRequest for sending.
		ShipmentRequest shipmentRequest = new ShipmentRequest(ShipmentRequestType.FETCH_SHIPMENT_ORDERS, area);
		// Send
		ShipmentResponse shipmentResponse = sendRequest(shipmentRequest);

		return shipmentResponse.getOrdersForShipment();
	}

	/**
	 * Creates a request to the delivery department to approve sending a shipment
	 * for the specified order.
	 * 
	 * @param order the order to send a shipment request for
	 */
	public void confirmShipment(Order order) {
		if (order == null)
			return;

		// Prepare ShipmentRequest for sending.
		ShipmentRequest shipmentRequest = new ShipmentRequest(ShipmentRequestType.UPDATE_STATUS, OrderStatus.SUBMITTED,
				order.getOrderId());
		sendRequest(shipmentRequest);
	}

	/**
	 * Confirm delivery of an order.
	 * 
	 * @param order the order that is being confirmed for delivery.
	 */
	public void confirmDelivery(Order order) {
		if (order == null)
			return;

		ShipmentRequest shipmentRequest = new ShipmentRequest(ShipmentRequestType.UPDATE_STATUS,
				OrderStatus.AWAITING_DELIVERY, order.getOrderId());

		sendRequest(shipmentRequest);
	}

	/**
	 * This function is used to set the status of the given order to
	 * "DELIVERY_CONFIRMED".
	 * 
	 * This will indicate that the delivery of the order has been confirmed by the
	 * client.
	 * 
	 * @param order The order for which the status needs to be updated
	 */
	public void setDone(Order order) {
		if (order == null)
			return;

		ShipmentRequest shipmentRequest = new ShipmentRequest(ShipmentRequestType.UPDATE_STATUS,
				OrderStatus.DELIVERY_CONFIRMED, order.getOrderId());
		sendRequest(shipmentRequest);
	}
}
