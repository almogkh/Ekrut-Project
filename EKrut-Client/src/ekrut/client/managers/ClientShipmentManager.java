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
	 * @throws IllegalArgumentException if a null string is provided as the area
	 */
	public ArrayList<Order> fetchShipmentRequests(String area) {
		if (area == null)
			return null;
		
		// Prepare ShipmentRequest for sending.
		ShipmentRequest shipmentRequest = new ShipmentRequest(ShipmentRequestType.FETCH_SHIPMENT_ORDERS, area);
		// Send
		ShipmentResponse shipmentResponse = sendRequest(shipmentRequest);
		
		// In case resultType isn't "OK" exception will throws.
		ResultType resultType = shipmentResponse.getResultCode();
		if (resultType != ResultType.OK)
			return null; // Q.Nir exception??
		
		return shipmentResponse.getOrdersForShipment();
	}

	/**
	 * Creates a request to the delivery department to approve sending a shipment for the specified order.
	 * 
	 * @param order the order to send a shipment request for
	 * @throws IllegalArgumentException if a null order is provided
	 */
	public void confirmShipment(Order order) {
		if (order == null)
			return;
		System.out.println("1");
		
		// Prepare ShipmentRequest for sending.
		ShipmentRequest shipmentRequest = 
				new ShipmentRequest(ShipmentRequestType.UPDATE_STATUS, OrderStatus.SUBMITTED, order.getOrderId());
		ShipmentResponse shipmentResponse = sendRequest(shipmentRequest);
		
		System.out.println("2");

		// In case resultType isn't "OK" exception will throws.
		ResultType resultType = shipmentResponse.getResultCode();
		if (resultType != ResultType.OK)
			return; 
	}

	/**
	 * Confirms the delivery of the specified order by sending a {@link ShipmentRequest} with the status set to 
	 * {@link OrderStatus#DELIVERY_CONFIRMED} and the order ID to the {@link #sendRequest(ShipmentRequest)} method.
	 * If the {@link ShipmentResponse#getResultCode()} is not {@link ResultType#OK},
	 * an {@link Exception} is thrown with the {@link ResultType} as the message.
	 * 
	 * @param order the order to confirm the delivery of
	 * @throws IllegalArgumentException if the provided order is null
	 */
	public void confirmDelivery(Order order) {
		if (order == null)
			return;
		
		ShipmentRequest shipmentRequest = 
				new ShipmentRequest(ShipmentRequestType.UPDATE_STATUS ,OrderStatus.AWAITING_DELIVERY, order.getOrderId());

		ShipmentResponse shipmentResponse = sendRequest(shipmentRequest);
		
		// In case resultType isn't "OK" exception will throws.
		ResultType resultType = shipmentResponse.getResultCode();
		if (resultType != ResultType.OK)
			return; 
	}
		
	/**
	 * Sets the status of the specified order to "done" by sending a {@link ShipmentRequest} with the
	 * status set to {@link OrderStatus#DONE} and the order ID to the {@link #sendRequest(ShipmentRequest)} method.
	 * In case the {@link ShipmentResponse#getResultCode()} is not {@link ResultType#OK}, an {@link Exception} is thrown.
	 * 
	 * @param order the order to set the status to "done"
	 * @throws IllegalArgumentException if the provided order is null
	 */
	public void setDone(Order order) {
		if (order == null)
			return;
		
		ShipmentRequest shipmentRequest = 
				new ShipmentRequest(ShipmentRequestType.UPDATE_STATUS ,OrderStatus.DELIVERY_CONFIRMED, order.getOrderId());
		ShipmentResponse shipmentResponse = sendRequest(shipmentRequest);
		
		// In case resultType isn't "OK" exception will throws.
		ResultType resultType = shipmentResponse.getResultCode();
		if (resultType != ResultType.OK)
			return; 
		}
}
