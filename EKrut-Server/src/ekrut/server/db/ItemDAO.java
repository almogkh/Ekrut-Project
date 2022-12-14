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

	/**
	 * Fetch a single Item object from DB, identified by itemId.
	 * 
	 * @param itemId the unique Item identifier. 
	 * @return	an Item according to the provided itemId.
	 */
	public static Item fetchItem(int itemId) {
		Item item = null;
		try {
			// TBD code below should be somewhere else!
			DBController dbcon = new DBController("jdbc:mysql://localhost/ekrut?serverTimezone=IST", "root", "UntilWhenNov12");
			dbcon.connect();
			
			PreparedStatement ps = dbcon.getPreparedStatement("SELECT * FROM items WHERE itemId = ?;");
			ps.setString(1, Integer.toString(itemId));
			ResultSet rs = dbcon.executeQuery(ps);
			
			// Check if any results are available.
			if(rs.next())
				item =  new Item(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getFloat(4));
			
			ps.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return item;
	}
	
	
}
