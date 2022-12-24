package ekrut.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import com.mysql.cj.MysqlType;
import ekrut.entity.SaleDiscount;
import ekrut.entity.SaleDiscountType;

public class saleDiscountDAO {

	private DBController con;

	public saleDiscountDAO(DBController con) {
		this.con = con;
	}

	public boolean createDiscount(SaleDiscount saleDiscount) {
		if (saleDiscount == null)
			return false;

		PreparedStatement ps = con.getPreparedStatement(
				"INSERT INTO saleDiscount (area,startTime,endTime,dayOfSale,saleType) VALUES(?,?,?,?,?)");
		try {
			ps.setString(1, saleDiscount.getArea());
			ps.setObject(2, saleDiscount.getStartTime(), MysqlType.TIME);
			ps.setObject(3, saleDiscount.getEndTime(), MysqlType.TIME);
			ps.setString(4, saleDiscount.getDayOfSale());
			ps.setString(5, saleDiscount.getType().toString());
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

	// Fetch all templates sales as a list.
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

	// Fetch all activate sales as a list.
	public ArrayList<SaleDiscount> fetchActivateSaleDiscountListByArea(String area) {

		PreparedStatement ps = con.getPreparedStatement("SELECT discountId FROM activeSales WHERE area = ?");
		ArrayList<SaleDiscount> activeSaleList = new ArrayList<>();
		try {
			ps.setString(1, area);
			ResultSet rs = con.executeQuery(ps);

			while (rs.next())
				activeSaleList.add(fetchSaleDiscountById(rs.getInt(1)));

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
