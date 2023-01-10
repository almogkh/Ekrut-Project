package ekrut.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import ekrut.entity.SaleDiscount;
import ekrut.entity.SaleDiscountType;

/**
 * The saleDiscountDAO class is a data access object for handling sale discount
 * data in the DB. It provides methods for creating, fetching, and managing sale discount data in DB.
 * 
 * @author Nir Betesh
 */
public class SaleDiscountDAO {

	private DBController con;

	public SaleDiscountDAO(DBController con) {
		this.con = con;
	}

	/**
	 * Creates a discount template in the DB.
	 * 
	 * @param saleDiscount the discount template to create
	 * @return true if the discount template was successfully created, false otherwise
	*/
	public boolean createDiscountTemplate(SaleDiscount saleDiscount) {
		if (saleDiscount == null)
			return false;

		PreparedStatement ps = con.getPreparedStatement(
				"INSERT INTO sale_discount (startTime,endTime,dayOfSale,saleType) VALUES(?,?,?,?)");
		try {
			ps.setString(1, saleDiscount.getStartTime().toString());
			ps.setString(2, saleDiscount.getEndTime().toString());
			ps.setString(3, saleDiscount.getDayOfSale());
			ps.setString(4, saleDiscount.getType().toString());
			
			// Check executeUpdate result.
			int count = con.executeUpdate(ps);
			if (count != 1)
				return false;
			
		} catch (SQLException e) {
			return false;
		} finally {
			try {
				// Close statement
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return true;
	}

	/**
	 * Fetches a sale discount from the DB by its id.
	 * 
	 * @param discountId the id of the discount to be fetched.
	 * @return a SaleDiscount object with the data of the discount with the given id, or null if the discount was not found.
	 */
	public SaleDiscount fetchSaleDiscountById(int discountId) {

		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM sale_discount WHERE discountId = ?");

		try {
			ps.setInt(1, discountId);
			ResultSet rs = con.executeQuery(ps);
			SaleDiscount saleDiscount = new SaleDiscount(rs.getInt(1), rs.getObject(2, LocalTime.class),
					rs.getObject(3, LocalTime.class), rs.getString(4), SaleDiscountType.valueOf(rs.getString(5)));

			return saleDiscount;

		} catch (SQLException e) {
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
	 * Fetches all templates sales as a list.
	 * 
	 * @return a list of SaleDiscount objects representing the sale discount templates in the database.
	 */
	public ArrayList<SaleDiscount> fetchSaleDiscountTemplatList() {

		ArrayList<SaleDiscount> saleDiscountTemplateList = new ArrayList<>();
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM sale_discount");

		try {
			ResultSet rs = con.executeQuery(ps);
			while (rs.next())
				saleDiscountTemplateList.add(new SaleDiscount(rs.getInt(1), rs.getObject(2, LocalTime.class),
						rs.getObject(3, LocalTime.class), rs.getString(4), SaleDiscountType.valueOf(rs.getString(5))));

			return saleDiscountTemplateList;

		} catch (SQLException e) {
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
	 * Fetches all activate sales as a list for a given area.
	 * 
	 * @param area the area for which to fetch the active sales.
	 * @return a list of SaleDiscount objects representing the active sales for the given area.
	 */
	public ArrayList<SaleDiscount> fetchActivateSaleDiscountListByArea(String area) {

		PreparedStatement ps = con.getPreparedStatement(
				"SELECT sd.discountId, sd.startTime, sd.endTime, sd.dayOfSale, sd.saleType, a.area "
				+ "FROM active_sales a, sale_discount sd " 
				+ "WHERE a.discountId = sd.discountId AND area = ?");
		ArrayList<SaleDiscount> activeSaleList = new ArrayList<>();

		try {
			ps.setString(1, area);
			ResultSet rs = con.executeQuery(ps);

			while (rs.next())
				activeSaleList.add(new SaleDiscount(rs.getInt(1), rs.getObject(2, LocalTime.class),
						rs.getObject(3, LocalTime.class), rs.getString(4),
						SaleDiscountType.valueOf(rs.getString(5)), rs.getString(6)));
			return activeSaleList;
		} catch (SQLException e) {
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
	 * Activates a sale for an area.
	 * 
	 * @param discountId the ID of the sale that should be activated
	 * @param area       the area in which to activate the sale
	 * @return           true if the operation succeeded, false otherwise
	 */
	public boolean activateSaleForArea(int discountId, String area) {
		PreparedStatement ps = con.getPreparedStatement("INSERT INTO active_sales (discountId,area) " +
                                                        "VALUES(?,?)");
		
		try {
			ps.setInt(1, discountId);
			ps.setString(2, area);
			
			if (ps.executeUpdate() != 1)
				return false;
			return true;
			
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
	 * Deactivates a sale for an area.
	 * 
	 * @param discountId the ID of the sale that should be deactivated
	 * @param area       the area in which to deactivate the sale
	 * @return           true if the operation succeeded, false otherwise
	 */
	public boolean deactivateSaleForArea(int discountId, String area) {
		PreparedStatement ps = con.getPreparedStatement("DELETE FROM active_sales " +
                                                        "WHERE discountId = ? AND area = ?");
		
		try {
			ps.setInt(1, discountId);
			ps.setString(2, area);
			
			if (ps.executeUpdate() != 1)
				return false;
			return true;
			
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
}
