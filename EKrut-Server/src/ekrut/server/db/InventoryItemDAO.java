package ekrut.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import ekrut.entity.InventoryItem;
import ekrut.entity.Item;
import ekrut.server.intefaces.IItemQuantityFetcher;

/**
 * InventoryItem DAO control all Inventory Item related DB interactions.
 * 
 * @author Ofek Malka
 *
 */
public class InventoryItemDAO {

	private DBController con;
	private ItemDAO itemDAO;
	private IItemQuantityFetcher itemQuantityFetcher;

	public InventoryItemDAO(DBController con) {
		this.con = con;
		this.itemDAO = new ItemDAO(con);
		this.itemQuantityFetcher = new ItemQuantityFetcher(con);
	}

	/**
	 * Constructs an InventoryItemDAO object with injectable ItemQuantityFetcher
	 * dependency.
	 *
	 * @param con                 a DBController object for interacting with the database
	 * @param itemQuantityFetcher an iItemQuantityFetcher object for fetching items quantity via sensor
	 */
	public InventoryItemDAO(DBController con, IItemQuantityFetcher itemQuantityFetcher) {
		this.con = con;
		this.itemDAO = new ItemDAO(con);
		this.itemQuantityFetcher = itemQuantityFetcher;
	}

	/**
	 * Creates a new InventoryItem in the inventory system.
	 *
	 * @param itemId        the ID of the new InventoryItem
	 * @param quantity      the initial quantity of the new InventoryItem
	 * @param ekrutLocation the ekrut location of the new InventoryItem
	 * @param itemThreshold the threshold of the new InventoryItem
	 * @return a boolean indicating whether the creation was successful
	 * @throws RuntimeException if there is a problem closing the PreparedStatement object
	 */
	public boolean CreateInventoryItem(int itemId, int quantity, String ekrutLocation, int itemThreshold) {
		PreparedStatement ps = con.getPreparedStatement(
				"INSERT INTO inventory_items (itemId, quantity, ekrutLocation, itemThreshold) " + "VALUES(?, ?, ?, ?)");
		try {
			ps.setInt(1, itemId);
			ps.setInt(2, quantity);
			ps.setString(3, ekrutLocation);
			ps.setInt(4, itemThreshold);
			return 1 == con.executeUpdate(ps);
		} catch (SQLException e) {
			return false;
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Fetch a single InventoryItem object from DB, identified by itemId and
	 * ekrutLocation.
	 * 
	 * @param itemId        the unique Item identifier.
	 * @param ekrutLocation the unique machine identifier.
	 * @return an InventoryItem according to the provided parameters.
	 */
	public InventoryItem fetchInventoryItem(int itemId, String ekrutLocation) {
		PreparedStatement ps = con
				.getPreparedStatement("SELECT * FROM inventory_items WHERE itemId = ? AND ekrutLocation = ?;");
		try {
			ps.setInt(1, itemId);
			ps.setString(2, ekrutLocation);
			ResultSet rs = con.executeQuery(ps);

			// Since Item in a component of InventoryItem, we'll fetch it.
			Item item = itemDAO.fetchItem(itemId);
			if (item == null)
				throw new RuntimeException("There are no item with the given itemId.");

			// Check if any results are available.
			if (rs.next()) {
				int itemActualQuantity = itemQuantityFetcher.fetchQuantity(itemId, ekrutLocation);
				return new InventoryItem(item, itemActualQuantity, rs.getString("ekrutLocation"),
						rs.getInt("itemThreshold"));
			}
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
	 * Fetch array list of InventoryItem object(s) from DB, identified by
	 * ekrutLocation.
	 * 
	 * @param ekrutLocation the unique machine identifier.
	 * @return {@link ArrayList}&lt;{@link InventoryItem}&gt; list with all the
	 *         inventory items assigned to the provided machine
	 */
	public ArrayList<InventoryItem> fetchAllItemsByLocation(String ekrutLocation) {
		PreparedStatement ps = con.getPreparedStatement("SELECT itemId FROM inventory_items WHERE ekrutLocation = ?;");
		try {
			ps.setString(1, ekrutLocation);
			ResultSet rs = con.executeQuery(ps);
			ArrayList<InventoryItem> inventoryItems = new ArrayList<>();
			while (rs.next())
				inventoryItems.add(fetchInventoryItem(rs.getInt("itemId"), ekrutLocation));
			return inventoryItems.size() != 0 ? inventoryItems : null;
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
	 * Updates the threshold of an inventory item in the database.
	 * 
	 * @param oldInventoryItem the inventory item to update
	 * @param newThreshold     the new threshold value to set
	 * @return true if the update was successful, false otherwise
	 * @throws RuntimeException if there is a database error
	 */
	public boolean updateItemThreshold(int itemId, String ekrutLocation, int newThreshold) {
		con.beginTransaction();
		PreparedStatement ps = con.getPreparedStatement(
				"UPDATE inventory_items SET threshold = ? WHERE itemId = ? AND ekrutLocation = ?;");
		try {
			ps.setInt(1, newThreshold);
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
	 * Updates the threshold of all InventoryItems in a specific area.
	 *
	 * @param threshold the new threshold value for the InventoryItems
	 * @param area      the area for which the InventoryItems' thresholds should be updated
	 * @return a boolean indicating whether the update was successful
	 * @throws RuntimeException if there is a problem with the SQL query or closing the Prepared Statement object
	 */
	public boolean updateItemsThresholdByArea(String area, int threshold) {
		con.beginTransaction();
		PreparedStatement ps = con.getPreparedStatement("UPDATE inventory_items "
				+ "SET inventory_items.itemThreshold = ? " + "WHERE inventory_items.ekrutLocation IN "
				+ "(SELECT ekrutLocation FROM machine_in_area WHERE area = ?);");
		try {
			ps.setInt(1, threshold);
			ps.setString(2, area);
			con.executeUpdate(ps);
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
	 * Updates the quantity of an InventoryItem in the inventory system.
	 *
	 * @param itemId        the ID of the InventoryItem to be updated
	 * @param quantity      the new quantity for the InventoryItem
	 * @param ekrutLocation the ekrut location of the InventoryItem to be updated
	 * @return a boolean indicating whether the update was successful
	 * @throws RuntimeException if there is a problem with the SQL query or closing the Prepared Statement object
	 */
	public boolean updateItemQuantity(int itemId, int quantity, String ekrutLocation) {
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
