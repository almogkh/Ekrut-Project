package ekrut.server.db;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.mysql.cj.MysqlType;

import ekrut.entity.InventoryItem;
import ekrut.entity.Item;
import ekrut.server.intefaces.IItemQuantityFetcher;

/**
 * InventoryItem DAO control all Inventory Item related DB interactions.
 * 
 * @author Ofek Malka
 *
 * SQL tables that might be affected by this class:
 * 	ekrut_machines, inventory_items, threshold_breaches.
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
	 * Adds a record to the threshold_breaches table in a database with the given date, item name, ekrut location, and area.
	 *
	 * @param date a LocalDateTime object representing the date and time of the threshold breach
	 * @param itemName a String representing the name of the item associated with the threshold breach
	 * @param ekrutLocation a String representing the location of the ekrut associated with the threshold breach
	 * @param area a String representing the area associated with the threshold breach
	 * @return a boolean value indicating whether the insertion was successful or not
	 * @throws RuntimeException if an SQLException is thrown while trying to close the prepared statement
	 * @see java.time.LocalDateTime
	 */
	public boolean addThresholdBreach(LocalDateTime date, String itemName, String ekrutLocation, String area) {
		PreparedStatement ps = con.getPreparedStatement(
				"INSERT INTO threshold_breaches (date, itemName, ekrutLocation, area) VALUES(?, ?, ?, ?);");
		try {
			ps.setObject(1, date, MysqlType.DATETIME);
			ps.setString(2, itemName);
			ps.setString(3, ekrutLocation);
			ps.setString(4, area);
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
	 * Adds a new ekrut location to the ekrut_machines table in the database.
	 *
	 * @param area the area where the ekrut location is located
	 * @param ekrutLocation the name of the ekrut location
	 * @param threshold the threshold for the ekrut location
	 * @return true if the ekrut location was added successfully, false otherwise
	 */
	public boolean addEkrutLocation(String area, String ekrutLocation, int threshold) {
		PreparedStatement ps = con.getPreparedStatement(
				"INSERT INTO ekrut_machines (area, ekrutLocation, threshold) VALUES(?, ?, ?);");
		try {
			ps.setString(1, area);
			ps.setString(2, ekrutLocation);
			ps.setInt(3, threshold);
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
	
	public boolean addItemToEkrutLocation(String area, String ekrutLocation, int itemId) {
		return addItemToEkrutLocation(area, ekrutLocation, itemId, 0);
	}

	/**
	 * This method adds an item to an ekrutLocation in the inventory.
	 * 
	 * @param area The area where the ekrutLocation is located.
	 * @param ekrutLocation The ekrutLocation where the item should be added.
	 * @param itemId The id of the item to be added.
	 * @param quantity The quantity of the item to be added.
	 * @return true if the item was successfully added to the ekrutLocation, false otherwise.
	*/
	public boolean addItemToEkrutLocation(String area, String ekrutLocation, int itemId, int quantity) {
		PreparedStatement ps1 = con.getPreparedStatement(
				"SELECT TRUE FROM ekrut_machines WHERE area = ? AND ekrutLocation = ?;");
		PreparedStatement ps2 = con.getPreparedStatement(
				"INSERT INTO inventory_items (area, ekrutLocation, itemId, quantity) "
				+ "VALUES(?, ?, ?, ?);");
		try {
			ps1.setString(1, area);
			ps1.setString(2, ekrutLocation);
			ps2.setString(1, area);
			ps2.setString(2, ekrutLocation);
			ps2.setInt(3, itemId);
			ps2.setInt(4, quantity);
			ResultSet rs1 = ps1.executeQuery();
			if (!rs1.next())
				return false;
			return 1 == con.executeUpdate(ps2);
		} catch (SQLException e) {
			return false;
		} finally {
			try {
				ps1.close();
				ps2.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 *	Updates the threshold for the given ekrut location in the given area.
	 *	
	 *	@param area the area in which the ekrut location is located
	 *	@param ekrutLocation the ekrut location whose threshold is being updated
	 *	@param newThreshold the new threshold value
	 *	@return true if the update was successful, false otherwise
	 *	@throws RuntimeException if there is an error executing the update query
	 */
	public boolean updateEkrutLocationThreshold(String ekrutLocation, int newThreshold) {
		PreparedStatement ps = con.getPreparedStatement(
				"UPDATE ekrut_machines SET threshold = ? WHERE ekrutLocation = ?;");
		try {
			ps.setInt(1, newThreshold);
			ps.setString(2, ekrutLocation);
			return 1 == con.executeUpdate(ps);
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
	* This method updates the quantity of the inventory item with the given item ID in the specified ekrut location.
	* The new quantity is specified in the input.
	* 
	* @param area the area where the ekrut location is located
	* @param ekrutLocation the ekrut location where the inventory item is located
	* @param itemId the ID of the item to be updated
	* @param quantity the new quantity of the item
	* @return true if the update was successful, false otherwise
	*/
	public boolean updateItemQuantity(String ekrutLocation, int itemId, int quantity) {
		// Check such item exists in ekrutLocation
		if (fetchInventoryItem(itemId, ekrutLocation).getItemQuantity() == -1) {
			String area = fetchAreaByEkrutLocation(ekrutLocation);
			if (area == null) 
				return false;
			return addItemToEkrutLocation(area, ekrutLocation, itemId, quantity);
		} else {
			PreparedStatement ps = con.getPreparedStatement(
					"UPDATE inventory_items SET quantity = ? WHERE ekrutLocation = ? AND itemId = ?;");
			try {
				ps.setInt(1, quantity);
				ps.setString(2, ekrutLocation);
				ps.setInt(3, itemId);
				return 1 == con.executeUpdate(ps);
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
	
	/**
	 * Fetches all locations of ekrut machines from the ekrut_machines table.
	 *
	 * @param ekrutLocation a location of an ekrut machine; this parameter is not used
	 * @return a list of all ekrut locations, or null if an error occurs
	 * @throws RuntimeException if an error occurs while closing the Prepared Statement
	 */
	public ArrayList<String> fetchAllEkrutLocationsByArea(String area){
		PreparedStatement ps = con.getPreparedStatement("SELECT ekrutLocation FROM ekrut_machines WHERE area = ?");
		try {
			// ekrutLocation
			ps.setString(1, area);
			ResultSet rs = con.executeQuery(ps);
			ArrayList<String> ekrutLocations = new ArrayList<>();
			while (rs.next())
				ekrutLocations.add(rs.getString("ekrutLocation"));
			return ekrutLocations;
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
	
	private void loadItemImage(Item item) {
		try (InputStream is = getClass().getResourceAsStream("/resources/images/" + item.getItemId() + ".jpg");
			 ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			if (is != null) {
				byte[] arr = new byte[4096];
				int bytes;
				while ((bytes = is.read(arr)) != -1)
					os.write(arr, 0, bytes);
				item.setImg(os.toByteArray());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Fetches all items in the inventory system at a given ekrut location.
	 *
	 * @param ekrutLocation the ekrut location of the items to fetch
	 * @return a list of InventoryItems, or null if there are no items at the given ekrut location or if there was an error
	 * @throws RuntimeException if there is a problem closing the Prepared Statement object
	 */
	public ArrayList<InventoryItem> fetchAllItemsByEkrutLocation(String ekrutLocation) {
		PreparedStatement ps = con.getPreparedStatement(
				"SELECT ii.area, ii.ekrutLocation, ii.itemId, ii.quantity, em.threshold "
				+ "FROM inventory_items ii, ekrut_machines em "
				+ "WHERE ii.ekrutLocation = em.ekrutLocation AND  "
				+ "ii.area = em.area AND "
				+ "ii.ekrutLocation = ?;");
		try {
			ps.setString(1, ekrutLocation);
			ResultSet rs = con.executeQuery(ps);
			ArrayList<InventoryItem> inventoryItems = new ArrayList<>();
			while (rs.next()) {
				Item item = itemDAO.fetchItem(rs.getInt("itemId"));
				int itemActualQuantity = itemQuantityFetcher.fetchQuantity(rs.getInt("itemId"), ekrutLocation);
				if (item != null && itemActualQuantity != -1) {
					loadItemImage(item);
					inventoryItems.add(new InventoryItem(item, itemActualQuantity, ekrutLocation, rs.getString("area"), rs.getInt("threshold")));
				}
			}
			return inventoryItems;
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
	 * Fetches an InventoryItem from the inventory system.
	 *
	 * @param itemId the ID of the item
	 * @param ekrutLocation the ekrut location of the item
	 * @return the InventoryItem, or null if the item could not be found or there was an error
	 * @throws RuntimeException if there is a problem closing the Prepared Statement object
	 */
	public InventoryItem fetchInventoryItem(int itemId, String ekrutLocation) {
		PreparedStatement ps = con.getPreparedStatement("SELECT area, threshold FROM ekrut_machines WHERE ekrutLocation = ?;");
		
		try {
			ps.setString(1, ekrutLocation);
			ResultSet rs = con.executeQuery(ps);

			// Since Item in a component of InventoryItem, we'll fetch it.
			Item item = itemDAO.fetchItem(itemId);
			if (item == null)
				throw new RuntimeException("There are no item with the given itemId.");
			loadItemImage(item);

			// Check if any results are available.
			if (rs.next()) {
				int itemActualQuantity = itemQuantityFetcher.fetchQuantity(itemId, ekrutLocation);
				return new InventoryItem(item, itemActualQuantity, ekrutLocation, rs.getString("area"), rs.getInt("threshold"));
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
	
	
	
	private String fetchAreaByEkrutLocation(String ekrutLocation) {
		PreparedStatement ps = con.getPreparedStatement("SELECT area FROM ekrut_machines WHERE ekrutLocation = ?;");
		try {
			ps.setString(1, ekrutLocation);
			ResultSet rs = con.executeQuery(ps);
			if (rs.next())
				return rs.getString("area");
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
	
	
	
	
	
}
