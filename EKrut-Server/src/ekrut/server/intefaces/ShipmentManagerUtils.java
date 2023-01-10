package ekrut.server.intefaces;

import java.time.LocalDateTime;

/**
 * An abstract class that provides utility methods for managing shipments.
 * This version will have the ability to change according to system requirements in the future.
 * @author Nir Betesh
 */
public abstract class ShipmentManagerUtils {

    /**
     * Calculates the flight time in hours for a shipment from the given warehouse to the given client address.
     * 
     * @param warehouse the warehouse location
     * @param clientAddress the client's address
     * @return the flight time in hours
     */
    public static long flightTimeInHours(String warehouse, String clientAddress) {
        return 5L;
    }

    /**
     * Calculates the waiting time for a drone in hours.
     * 
     * @return the waiting time in hours
     */
    public static long watingForDroneTimeInHours() {
        return 2L;
    }

    /**
     * Calculates the time required to load a shipment onto a drone in hours.
     * 
     * @return the loading time in hours
     */
    public static long ShipmentLoadTimeInHours() {
        return 3L;
    }

    /**
     * Calculates the total delivery duration time in hours for a shipment to the given client address.
     * 
     * @param clientAddress the client's address
     * @return the total delivery duration time in hours
     */
    private static long deliveryDurationTimeInHours(String clientAddress) {
        return flightTimeInHours(null, clientAddress)
                + watingForDroneTimeInHours()
                + watingForDroneTimeInHours();
    }

    /**
     * Calculates the estimated arrival time for a shipment given the current date and the client's address.
     * 
     * @param date the current date and time
     * @param clientAddress the client's address
     * @return the estimated arrival time
     */
    public static LocalDateTime estimatedArrivalTime(LocalDateTime date, String clientAddress) {
        long overallDeliveryHours = deliveryDurationTimeInHours(clientAddress);    
        return date.plusHours(overallDeliveryHours);
    }
}
