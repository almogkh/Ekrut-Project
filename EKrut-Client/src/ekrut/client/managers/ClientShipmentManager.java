package ekrut.client.managers;

import ekrut.entity.Order;
import ekrut.entity.OrderStatus;
import ekrut.entity.OrderType;
import ekrut.net.ShipmentRequest;
import ekrut.net.ShipmentResponse;
/**
 * 
 * @author Nir Betesh
 *
 */
public class ClientShipmentManager {
	
	
	// C.Nir		 1) complete 'sendRequest'.
	//				 2) complete comments.
	
	/**
	 * Create request <b>to Delivery department</b> to make a shipment.
	 * 
	 * @param order
	 * @throws Exception
	 */
	public boolean createShippingRequest(Order order){
		if (order == null)
			throw new IllegalArgumentException("Null order was provided.");
		if (order.getType() != OrderType.SHIPMENT)
			throw new IllegalArgumentException("Null order was provided."); // Q.Nir Do we need it? and if we do so the exception?
		// YES

		ShipmentRequest shipmentRequest = 
				new ShipmentRequest(order.getOrderId(), order.getClientAddress(), order.getDate());
		
		ShipmentResponse shipmentResponse = sendRequest(shipmentRequest);
		if (!shipmentResponse.getResultCode().equals("OK"))
			return false; // Q.Nir boolean or not to be
		return true;
	}
	
	/**
	 * Create request <b>to Delivery department</b> to approve sending shipment.
	 * 
	 * @param order
	 * @throws Exception
	 */
	public void confirmShipment(Order order) throws Exception {
		if (order == null)
			throw new IllegalArgumentException("Null order was provided");
		
		ShipmentRequest shipmentRequest = 
				new ShipmentRequest(OrderStatus.AWAITING_DELIVERY, order.getOrderId());
		ShipmentResponse shipmentResponse = sendRequest(shipmentRequest);
		
		if (!shipmentResponse.getResultCode().equals("OK"))
			throw new Exception(shipmentResponse.getResultCode()); // Q.Nir exception??
	}
	
	
	// Q.Nir - do we need this class? Because Worker can only see the buyer confirmation
	// but setDone is the action. how it going to work for us?
	
	/**
	 * Create request <b>to customer</b> to approve getting shipment.
	 * 
	 * @param order
	 * @throws Exception
	 */
	public void confirmDelivery(Order order) throws Exception {
		if (order == null)
			throw new IllegalArgumentException("Null order was provided");
		
		ShipmentRequest shipmentRequest = 
				new ShipmentRequest(OrderStatus.DELIVERY_CONFIRMED, order.getOrderId());
		ShipmentResponse shipmentResponse = sendRequest(shipmentRequest);
		
		if (!shipmentResponse.getResultCode().equals("OK"))
			throw new Exception(shipmentResponse.getResultCode()); // Q.Nir exception??
	}
	
	/**
	 * Create request <b>to operates shipments</b> to set done the shipment.
	 * 
	 * @param order
	 * @throws Exception
	 */
	public void setDone(Order order) throws Exception {
		if (order == null)
			throw new IllegalArgumentException("Null order was provided");
		
		ShipmentRequest shipmentRequest = 
				new ShipmentRequest(OrderStatus.DONE, order.getOrderId());
		ShipmentResponse shipmentResponse = sendRequest(shipmentRequest);
		
		if (!shipmentResponse.getResultCode().equals("OK"))
			throw new Exception(shipmentResponse.getResultCode()); // Q.Nir exception??
	}
	
	/**
	 * Send request to server for one of the method in this class.
	 * (see {@link #createShippingRequest()}, {@link #confirmShipment()},
	 * 		{@link #confirmDelivery()}, {@link #setDone()}). 
	 * 
	 * @param shipmentRequest
	 * @return ShipmentResponse response from server.
	 */
	private ShipmentResponse sendRequest(ShipmentRequest shipmentRequest) {
		
		return null;
	}
}
