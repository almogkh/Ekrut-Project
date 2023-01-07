package ekrut.server.db;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ekrut.entity.Item;

/**
 * Item DAO control all Item related DB interactions.
 * 
 * @author Ofek Malka
 *
 */
public class ItemDAO {
	
	private DBController con;
	
	public ItemDAO(DBController con) {
		this.con = con;
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
	 * Fetch a single Item object from DB, identified by itemId.
	 * 
	 * @param itemId the unique Item identifier. 
	 * @return	an Item according to the provided itemId.
	 */
	public Item fetchItem(int itemId) {
		PreparedStatement ps = con.getPreparedStatement(
				"SELECT itemId, itemName, itemDescription, itemPrice FROM items WHERE itemId = ?;");
		try {
			ps.setInt(1, itemId);
			ResultSet rs = con.executeQuery(ps);
			if(rs.next()) {
				Item item = new Item(rs.getInt("itemId"),
						rs.getString("itemName"), 
						rs.getString("itemDescription"),
						rs.getFloat("itemPrice"));
				loadItemImage(item);
				return item;
			}
			return null;
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
	
	public ArrayList<Item> fetchAllItems() {
		ArrayList<Item> allItems = new ArrayList<>();
		PreparedStatement ps = con.getPreparedStatement(
				"SELECT * FROM items;");
		try {
			ResultSet rs = con.executeQuery(ps);
			while(rs.next()) {
				Item item = new Item(
						rs.getInt("itemId"),
						rs.getString("itemName"), 
						rs.getString("itemDescription"),
						rs.getFloat("itemPrice"));
				loadItemImage(item);
				allItems.add(item);
			}
			return allItems;
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
	
}
