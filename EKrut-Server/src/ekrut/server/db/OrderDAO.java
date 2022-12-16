package ekrut.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.mysql.cj.MysqlType;

import ekrut.entity.Order;
import ekrut.entity.OrderItem;
import ekrut.entity.OrderType;

public class OrderDAO {

	private DBController con;
	
	public OrderDAO(DBController con) {
		this.con = con;
	}
	
	public boolean createOrder(Order order) {
		con.beginTransaction();
		
		PreparedStatement p1 = con.getPreparedStatement("INSERT INTO orders " +
                                                        "(date,status,dueDate,clientAddress,location) " +
                                                        "VALUES(?,?,?,?,?)", true);
		
		PreparedStatement p2 = con.getPreparedStatement("INSERT INTO orderItems (orderId,itemId,itemQuantity) " +
                                                        "VALUES(?,?,?)");
		try {
			p1.setObject(1, order.getDate(), MysqlType.DATETIME);
			p1.setString(2, order.getStatus().toString());
			if (order.getType() == OrderType.SHIPMENT) {
				p1.setObject(3, order.getDueDate(), MysqlType.DATETIME);
				p1.setString(4, order.getClientAddress());
				p1.setNull(5, Types.VARCHAR);
			} else {
				p1.setNull(3, MysqlType.DATETIME.getJdbcType());
				p1.setNull(4, Types.VARCHAR);
				p1.setString(5, order.getEkrutLocation());
			}
			
			int count = con.executeUpdate(p1);
			if (count != 1) {
				con.abortTransaction();
				return false;
			}
			
			ResultSet rs = p1.getGeneratedKeys();
			if (!rs.next()) {
				con.abortTransaction();
				return false;
			}
			
			int orderId = rs.getInt(1);
			rs.close();
			
			order.setOrderId(orderId);
			
			for (OrderItem oi : order.getItems()) {
				p2.setInt(1, orderId);
				p2.setInt(2, oi.getItem().getItemId());
				p2.setInt(3, oi.getItemQuantity());
				p2.addBatch();
			}
			
			int[] results = p2.executeBatch();
			for (int i : results) {
				if (i != 1) {
					con.abortTransaction();
					return false;
				}
			}
			
			con.commitTransaction();
			return true;
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				p1.close();
				p2.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
