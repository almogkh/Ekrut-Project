package ekrut.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import com.mysql.cj.MysqlType;
import ekrut.entity.SaleDiscount;
import ekrut.entity.SaleDiscountType;

/**
 * The saleDiscountDAO class is a data access object for handling sale discount
 * data in the database. It provides methods for creating, fetching, and
 * managing sale discount data in the database.
 * 
 * @author Nir Betesh
 */
public class SaleDiscountDAO {

	private DBController con;

	public SaleDiscountDAO(DBController con) {
		this.con = con;
	}

	/**
	 * Creates a sale discount in the database.
	 * 
	 * @param saleDiscount the SaleDiscount object to be added to the database.
	 * @return true if the discount was successfully created, false otherwise.
	 * @throws SQLException if there is a problem executing the SQL query or
	 *                      processing the result set.
	 */
	public boolean createDiscount(SaleDiscount saleDiscount) {
		if (saleDiscount == null)
			return false;

		PreparedStatement ps = con.getPreparedStatement(
				"INSERT INTO saleDiscount (startTime,endTime,dayOfSale,saleType) VALUES(?,?,?,?)");
		try {
			ps.setObject(1, saleDiscount.getStartTime(), MysqlType.TIME);
			ps.setObject(2, saleDiscount.getEndTime(), MysqlType.TIME);
			ps.setString(3, saleDiscount.getDayOfSale());
			ps.setString(4, saleDiscount.getType().toString());
			int count = con.executeUpdate(ps);
			if (count != 1)
				return false;
		} catch (SQLException e) {
			return false;
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return true;
	}

	/**
	 * Fetches a sale discount from the database by its id.
	 * 
	 * @param discountId the id of the discount to be fetched.
	 * @return a SaleDiscount object with the data of the discount with the given
	 *         id, or null if the discount was not found.
	 * @throws SQLException if there is a problem executing the SQL query or
	 *                      processing the result set.
	 */
	public SaleDiscount fetchSaleDiscountById(int discountId) {

		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM saleDiscount WHERE discountId = ?");

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
	 * @return a list of SaleDiscount objects representing the sale discount
	 *         templates in the database.
	 * @throws SQLException if there is a problem executing the SQL query or
	 *                      processing the result set.
	 */
	public ArrayList<SaleDiscount> fetchSaleDiscountTemplatList() {

		ArrayList<SaleDiscount> saleDiscountTemplateList = new ArrayList<>();
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM saleDiscount");

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
	 * @return a list of SaleDiscount objects representing the active sales for the
	 *         given area.
	 * @throws SQLException if there is a problem executing the SQL query or
	 *                      processing the result set.
	 */
	public ArrayList<SaleDiscount> fetchActivateSaleDiscountListByArea(String area) {

		PreparedStatement ps = con.getPreparedStatement(
				"SELECT sd.discountId, sd.startTime, sd.endTime, sd.dayOfSale, sd.saleType, a.area "
				+ "FROM active_sales a, sale_discount sd " 
				+ "WHERE a.discountId = sd.discountId and area = ?");
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
}
