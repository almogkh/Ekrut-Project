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

/**
 * Handles all direct database interactions with orders
 * 
 * @author Almog Khaikin
 */
public class OrderDAO {

	private DBController con;
	private ItemDAO itemDAO;
	
	/**
	 * Constructs a new OrderDAO that uses the provided controller
	 * 
	 * @param con   the database controller to use
	 */
	public OrderDAO(DBController con) {
		this.con = con;
		this.itemDAO = new ItemDAO(con);
	}
	
	/**
	 * Changes the default ItemDAO to use for item related database operations.
	 * Useful for testing with mocks.
	 * 
	 * @param dao   the ItemDAO to use
	 */
	public void setItemDAO(ItemDAO dao) {
		this.itemDAO = dao;
	}
	
	/**
	 * Creates a new order entry in the database and sets the order object's orderId field.
	 * 
	 * @param order   the order that should be added to the database
	 * @return        true if the operation succeeded, false otherwise
	 */
	public boolean createOrder(Order order) {
		con.beginTransaction();
		
		// Pass true so that we can get the new order ID
		PreparedStatement p1 = con.getPreparedStatement("INSERT INTO orders " +
                                                        "(date,status,type,dueDate,clientAddress,location) " +
                                                        "VALUES(?,?,?,?,?,?)", true);
		
		PreparedStatement p2 = con.getPreparedStatement("INSERT INTO orderitems (orderId,itemId,itemQuantity) " +
                                                        "VALUES(?,?,?)");
		try {
			p1.setObject(1, order.getDate(), MysqlType.DATETIME);
			p1.setString(2, order.getStatus().toString());
			p1.setString(3, order.getType().toString());
			// Ekrut location is irrelevant for shipment type orders
			if (order.getType() == OrderType.SHIPMENT) {
				p1.setObject(4, order.getDueDate(), MysqlType.DATETIME);
				p1.setString(5, order.getClientAddress());
				p1.setNull(6, Types.VARCHAR);
			} else {
				// No due date and client address for non-shipment orders
				p1.setNull(4, MysqlType.DATETIME.getJdbcType());
				p1.setNull(5, Types.VARCHAR);
				p1.setString(6, order.getEkrutLocation());
			}
			
			int count = con.executeUpdate(p1);
			if (count != 1) {
				con.abortTransaction();
				return false;
			}
			
			// Get the new order's ID
			ResultSet rs = p1.getGeneratedKeys();
			if (!rs.next()) {
				con.abortTransaction();
				return false;
			}
			
			int orderId = rs.getInt(1);
			rs.close();
			
			// Save the ID in the order object for later use
			order.setOrderId(orderId);
			
			// Insert all the items in the order
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
	
	/**
	 * Returns the order with the specified ID.
	 * 
	 * @param orderId   the ID of the order we want to retrieve
	 * @return          the desired order if found, null otherwise
	 */
	public Order fetchOrderById(int orderId) {
		PreparedStatement p1 = con.getPreparedStatement("SELECT * FROM orders WHERE orderId = ?");
		PreparedStatement p2 = con.getPreparedStatement("SELECT * FROM orderitems WHERE orderId = ?");
		
		try {
			p1.setInt(1, orderId);
			ResultSet rs1 = p1.executeQuery();
			// No order with such an ID was found
			if (!rs1.next()) 
				return null;
			
			// Construct the main order object
			Order order = new Order(rs1.getInt(1), rs1.getObject(2, LocalDateTime.class),
					                OrderStatus.valueOf(rs1.getString(3)), OrderType.valueOf(rs1.getString(4)),
					                rs1.getObject(5, LocalDateTime.class), rs1.getString(6), rs1.getString(7));
			ArrayList<OrderItem> items = order.getItems();
			
			p2.setInt(1, orderId);
			ResultSet rs2 = p2.executeQuery();
			// Get the order items
			while (rs2.next()) {
				Item item = itemDAO.fetchItem(rs2.getInt(2));
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
	
	/**
	 * Updates the status of an order in the database.
	 * 
	 * @param orderId  the ID of the order whose status should be updated
	 * @param status   the new order status to set
	 * @return         true if the operation succeeded, false otherwise
	 */
	public boolean updateOrderStatus(int orderId, OrderStatus status) {
		PreparedStatement p = con.getPreparedStatement("UPDATE orders SET status = ? WHERE orderId = ?");
		
		try {
			p.setString(1, status.toString());
			p.setInt(2, orderId);
			
			if (p.executeUpdate() != 1)
				return false;
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
			
		} finally {
			try {
				p.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		return true;
	}
}
