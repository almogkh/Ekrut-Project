package ekrut.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ekrut.entity.InventoryItem;
import ekrut.entity.Item;

public class InventoryItemDAO {

	public static InventoryItem fetchInventoryItem(int itemId, String ekrutLocation) throws Exception {
		InventoryItem inventoryItem = null;
		try {
			// TBD code below should be somewhere else!
			DBController dbcon = new DBController("jdbc:mysql://localhost/ekrut?serverTimezone=IST", "root", "UntilWhenNov12");
			dbcon.connect();
			
			PreparedStatement ps = dbcon.getPreparedStatement("SELECT * FROM inventory_items WHERE itemId = ? AND ekrutLocation = ?;");
			ps.setString(1, Integer.toString(itemId));
			ps.setString(2, ekrutLocation);
			ResultSet rs = dbcon.executeQuery(ps);

			// Since Item in a component of InventoryItem, we'll fetch it.
			Item item = ItemDAO.fetchItem(itemId);
			if (item == null)
				throw new Exception("There are no item with the given itemId.");
			
			// Check if any results are available.
			if(rs.next()) {
				inventoryItem = new InventoryItem(item, rs.getInt(2), rs.getString(3), rs.getInt(4));
			}
			ps.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return inventoryItem;
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
