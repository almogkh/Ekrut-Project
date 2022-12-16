package ekrut.server.intefaces;

import java.time.LocalDateTime;


public abstract class ShipmentManagerUtils {
	
	public static long flightTimeInHours(String warehouse, String clientAddress) {
		return 5L;	
	}
	
	public static long watingForDroneTimeInHours() {
		return 2L;	
	}
	
	public static long ShipmentLoadTimeInHours() {
		return 3L;	
	}
	
	private static long deliveryDurationTimeInHours(String clientAddress) {
		return flightTimeInHours(null, clientAddress)
				+ watingForDroneTimeInHours()
				+ watingForDroneTimeInHours();
	}
	
	public static LocalDateTime estimatedArrivalTime(LocalDateTime date, String clientAddress) {
		long overallDeliveryHours = deliveryDurationTimeInHours(clientAddress);
		LocalDateTime deliveryDate = date.plusHours(overallDeliveryHours);
		return deliveryDate;
	
	}

	
}
