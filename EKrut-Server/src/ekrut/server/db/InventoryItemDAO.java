package ekrut.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import ekrut.entity.InventoryItem;
import ekrut.entity.Item;

/**
 * InventoryItem DAO control all Inventory Item related DB interactions.
 * 
 * @author Ofek Malka
 *
 */
public class InventoryItemDAO {

	private DBController con;
	private ItemDAO itemDAO;

	public InventoryItemDAO(DBController con) {
		this.con = con;
		itemDAO = new ItemDAO(con); 
	}

	/**
	 * Fetch a single InventoryItem object from DB, identified by itemId and ekrutLocation.
	 * 
	 * @param itemId the unique Item identifier. 
	 * @param ekrutLocation the unique machine identifier. 
	 * @return	an InventoryItem according to the provided parameters.
	 */
	public InventoryItem fetchInventoryItem(int itemId, String ekrutLocation) {
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM inventory_items WHERE itemId = ? AND ekrutLocation = ?;");
		try {
			ps.setInt(1, itemId);
			ps.setString(2, ekrutLocation);
			ResultSet rs = con.executeQuery(ps);

			// Since Item in a component of InventoryItem, we'll fetch it.
			Item item = itemDAO.fetchItem(itemId);
			if (item == null)
				throw new RuntimeException("There are no item with the given itemId.");

			// Check if any results are available.
			if (rs.next())
				return new InventoryItem(item, rs.getInt(2), rs.getString(3), rs.getInt(4));
			return null;
		} catch (SQLException e1) {
			return null;
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Fetch array list of InventoryItem object(s) from DB, identified by ekrutLocation.
	 * 
	 * @param ekrutLocation the unique machine identifier. 
	 * @return	{@link ArrayList}&lt;{@link InventoryItem}&gt; list with all the inventory items assigned to the provided machine
	 */
	public ArrayList<InventoryItem> fetchAllItemsByLocation(String ekrutLocation) {
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM inventory_items WHERE ekrutLocation = ?;");
		try {
			ps.setString(1, ekrutLocation);
			ResultSet rs = con.executeQuery(ps);
			ArrayList<InventoryItem> inventoryItems = new ArrayList<>();
			while(rs.next()) {
				Item item = itemDAO.fetchItem(rs.getInt(1));
				if (item != null)
					inventoryItems.add(new InventoryItem(item, rs.getInt(2), rs.getString(3), rs.getInt(4)));
			}
			return inventoryItems.size() != 0? inventoryItems : null;
		} catch (SQLException e1) {
			return null;
		}finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Updates the <b>threshold</b> of an inventory item in DB, , identified by itemId and ekrutLocation.
	 * 
	 * @param itemId the unique Item identifier. 
	 * @param ekrutLocation the unique machine identifier. 
	 * @return	True if update is successful, otherwise False.
	 */
	public Boolean updateItemThreshold(int itemId, String ekrutLocation, int threshold) {
		con.beginTransaction();
		PreparedStatement ps = con.getPreparedStatement(
				"UPDATE inventory_items SET threshold = ? WHERE itemId = ? AND ekrutLocation = ?;");
		try {
			ps.setInt(1, threshold);
			ps.setInt(2, itemId);
			ps.setString(3, ekrutLocation);
			int count = con.executeUpdate(ps);
			if (count != 1) {
				con.abortTransaction();
				return false;
			}
			con.commitTransaction();
			return true;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Updates the <b>quantity</b> of an inventory item in DB, , identified by itemId and ekrutLocation.
	 * 
	 * @param itemId the unique Item identifier. 
	 * @param ekrutLocation the unique machine identifier. 
	 * @return	True if update is successful, otherwise False.
	 */
	public Boolean updateItemQuantity(int itemId, int quantity, String ekrutLocation) {
		con.beginTransaction();
		PreparedStatement ps = con.getPreparedStatement(
				"UPDATE inventory_items SET quantity = ? WHERE itemId = ? AND ekrutLocation = ?;");
		try {
			ps.setInt(1, quantity);
			ps.setInt(2, itemId);
			ps.setString(3, ekrutLocation);
			int count = con.executeUpdate(ps);
			if (count != 1) {
				con.abortTransaction();
				return false;
			}
			con.commitTransaction();
			return true;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
