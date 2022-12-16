package ekrut.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.mysql.cj.MysqlType;

import ekrut.entity.Item;
import ekrut.entity.Order;
import ekrut.entity.OrderItem;
import ekrut.entity.OrderStatus;
import ekrut.entity.OrderType;

public class OrderDAO {

	private DBController con;
	
	public OrderDAO(DBController con) {
		this.con = con;
	}
	
	public boolean createOrder(Order order) {
		con.beginTransaction();
		
		PreparedStatement p1 = con.getPreparedStatement("INSERT INTO orders " +
                                                        "(date,status,type,dueDate,clientAddress,location) " +
                                                        "VALUES(?,?,?,?,?,?)", true);
		
		PreparedStatement p2 = con.getPreparedStatement("INSERT INTO orderItems (orderId,itemId,itemQuantity) " +
                                                        "VALUES(?,?,?)");
		try {
			p1.setObject(1, order.getDate(), MysqlType.DATETIME);
			p1.setString(2, order.getStatus().toString());
			p1.setString(3, order.getType().toString());
			if (order.getType() == OrderType.SHIPMENT) {
				p1.setObject(4, order.getDueDate(), MysqlType.DATETIME);
				p1.setString(5, order.getClientAddress());
				p1.setNull(6, Types.VARCHAR);
			} else {
				p1.setNull(4, MysqlType.DATETIME.getJdbcType());
				p1.setNull(5, Types.VARCHAR);
				p1.setString(6, order.getEkrutLocation());
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
	
	public Order fetchOrderById(int orderId) {
		PreparedStatement p1 = con.getPreparedStatement("SELECT * FROM orders WHERE orderId = ?");
		PreparedStatement p2 = con.getPreparedStatement("SELECT * FROM orderItems WHERE orderId = ?");
		
		try {
			p1.setInt(1, orderId);
			ResultSet rs1 = p1.executeQuery();
			if (!rs1.next()) 
				return null;
			
			Order order = new Order(rs1.getInt(1), rs1.getObject(2, LocalDateTime.class),
					                OrderStatus.valueOf(rs1.getString(3)), OrderType.valueOf(rs1.getString(4)),
					                rs1.getObject(5, LocalDateTime.class), rs1.getString(6), rs1.getString(7));
			ArrayList<OrderItem> items = order.getItems();
			
			p2.setInt(1, orderId);
			ResultSet rs2 = p2.executeQuery();
			
			while (rs2.next()) {
				Item item = ItemDAO.fetchItem(rs2.getInt(2));
				OrderItem orderItem = new OrderItem(item, rs2.getInt(3));
				items.add(orderItem);
			}
			
			return order;
			
		} catch (SQLException e) {
			return null;
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
