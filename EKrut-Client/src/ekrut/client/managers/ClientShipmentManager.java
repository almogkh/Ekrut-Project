package ekrut.client.managers;

import java.util.ArrayList;

import ekrut.client.EKrutClient;
import ekrut.entity.Order;
import ekrut.entity.OrderStatus;
import ekrut.net.ResultType;
import ekrut.net.ShipmentRequest;
import ekrut.net.ShipmentRequestType;
import ekrut.net.ShipmentResponse;
/**
 * This class represents the client side manager for handling shipments. It provides methods for fetching
 * orders for shipment in a specific area, confirming shipment, and confirming delivery of an order.
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
	 * Creates a request to the delivery department to approve sending a shipment for the specified order.
	 * 
	 * @param order the order to send a shipment request for
	 */
	public void confirmShipment(Order order) {
		if (order == null)
			return;
		
		// Prepare ShipmentRequest for sending.
		ShipmentRequest shipmentRequest = 
				new ShipmentRequest(ShipmentRequestType.UPDATE_STATUS, OrderStatus.SUBMITTED, order.getOrderId());
		sendRequest(shipmentRequest);
	}

	/**
	 * Confirms the delivery of the specified order by sending a {@link ShipmentRequest} with the status set to 
	 * {@link OrderStatus#DELIVERY_CONFIRMED} and the order ID to the {@link #sendRequest(ShipmentRequest)} method.
	 * If the {@link ShipmentResponse#getResultCode()} is not {@link ResultType#OK},
	 * an {@link Exception} is thrown with the {@link ResultType} as the message.
	 * 
	 * @param order the order to confirm the delivery of
	 */
	public void confirmDelivery(Order order) {
		if (order == null)
			return;
		
		ShipmentRequest shipmentRequest = 
				new ShipmentRequest(ShipmentRequestType.UPDATE_STATUS ,OrderStatus.AWAITING_DELIVERY, order.getOrderId());

		sendRequest(shipmentRequest);
	}
		
	/**
	 * Sets the status of the specified order to "done" by sending a {@link ShipmentRequest} with the
	 * status set to {@link OrderStatus#DONE} and the order ID to the {@link #sendRequest(ShipmentRequest)} method.
	 * In case the {@link ShipmentResponse#getResultCode()} is not {@link ResultType#OK}, an {@link Exception} is thrown.
	 * 
	 * @param order the order to set the status to "done"
	 */
	public void setDone(Order order) {
		if (order == null)
			return;
		
		ShipmentRequest shipmentRequest = 
				new ShipmentRequest(ShipmentRequestType.UPDATE_STATUS ,OrderStatus.DELIVERY_CONFIRMED, order.getOrderId());
		sendRequest(shipmentRequest);
	}
}
