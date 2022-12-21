package ekrut.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import ekrut.server.intefaces.IItemQuantityFetcher;

/**
 * Fetches the quantity of an item from the inventory system.
 */
public class ItemQuantityFetcher implements IItemQuantityFetcher {

	DBController con;

	public ItemQuantityFetcher(DBController con) {
		this.con = con;
	}

	/**
	 * Fetches the quantity of an item from the inventory system.
	 *
	 * @param itemId        the ID of the item
	 * @param ekrutLocation the ekrut location of the item
	 * @return the quantity of the item, or -1 if the item could not be found or there was an error
	 * @throws RuntimeException if there is a problem closing the Prepared Statement object
	 */
	public int fetchQuantity(int itemId, String ekrutLocation) {
		PreparedStatement ps = con.getPreparedStatement(
				"SELECT quantity FROM inventory_items WHERE itemId = ? AND ekrutLocation = ?;");
		try {
			ps.setInt(1, itemId);
			ps.setString(2, ekrutLocation);
			ResultSet rs = con.executeQuery(ps);
			// Check if any results are available.
			if (rs.next()) {
				return rs.getInt("quantity");
			}
			return -1;
		} catch (SQLException e1) {
			return -1;
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

}