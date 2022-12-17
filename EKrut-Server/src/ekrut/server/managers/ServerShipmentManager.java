package ekrut.server.managers;
import java.time.LocalDateTime;

import ekrut.net.ShipmentRequest;
import ekrut.net.ShipmentResponse;
import ekrut.server.intefaces.ShipmentManagerUtils;

public class ServerShipmentManager {
	
	/**
	 * Create shipping request for 
	 * @param shipmentRequest
	 * @return return if shipping request was successfully passed
	 */
	public ShipmentResponse createShippingRequest(ShipmentRequest shipmentRequest) {
		if (shipmentRequest == null)
			throw new IllegalArgumentException("Null order was provided");
		
		int orderId = shipmentRequest.getOrderId();
		LocalDateTime date = shipmentRequest.getDate();
		String clientAddress = shipmentRequest.getClientAddress();
		
		// estimate delivery time.
		LocalDateTime estimateDeliveryTime = ShipmentManagerUtils.estimatedArrivalTime(date, clientAddress);
	
		// create shipping in DB.
		//ShipmentDAO. // TBD Q.Nir
		
		return null;
	}
	
	

	
	public boolean confirmDelivery() {
		return false;
		
	}
	

	public boolean confirmShipment() {
		return false;
		
	}
	

	public boolean setDone() {
		return false;
	}
}

	

