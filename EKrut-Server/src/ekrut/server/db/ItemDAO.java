package ekrut.server.db;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

	/**
	 * Fetch a single Item object from DB, identified by itemId.
	 * 
	 * @param itemId the unique Item identifier. 
	 * @return	an Item according to the provided itemId.
	 */
	public Item fetchItem(int itemId) {
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM items WHERE itemId = ?;");
		try {
			ps.setInt(1, itemId);
			ResultSet rs = con.executeQuery(ps);
			if(rs.next())
				return new Item(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getFloat(4));
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
}
