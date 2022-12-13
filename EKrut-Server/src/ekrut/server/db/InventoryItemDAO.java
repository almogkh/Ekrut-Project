package ekrut.server.db;

import ekrut.entity.InventoryItem;
import ekrut.entity.Item;

public class InventoryItemDAO {

	public static InventoryItem fetchInventoryItem(int itemId, String ekrutLocation) {
		return null;
	}
	
	public static InventoryItem[] fetchAllItemsByLocation(String ekrutLocation) {
		return null;
	}
	
	public static Boolean updateItemThreshold(int itemId, String ekrutLocation,int threshold) {
		return false;
	}
	
	// TBD NESSEARY?
	public static Boolean updateThresholdByArea() {
		return false;
	}

	public static Boolean updateItemQuantity(int itemId, int quantity, String ekrutLocation) {
		return true;
	}


}
