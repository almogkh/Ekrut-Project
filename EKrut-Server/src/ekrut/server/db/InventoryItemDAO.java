package ekrut.server.db;

import ekrut.entity.InventoryItem;
import ekrut.entity.Item;

public class InventoryItemDAO {

	public static InventoryItem fetchInventoryItem(Item item, String ekrutLocation) {
		return null;
	}
	
	public static InventoryItem[] fetchAllItemsByLocation(String ekrutLocation) {
		return null;
	}
	
	public static Boolean updateItemQuantity(InventoryItem inventoryItem) {
		return false;
	}
	
	public static Boolean updateThresholdByArea() {
		return false;
	}
}
