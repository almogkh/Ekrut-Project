package ekrut.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap; 
import java.util.Map;
import java.util.Map.Entry;

import com.mysql.cj.MysqlType;
import ekrut.entity.Order;
import ekrut.entity.OrderType;
import ekrut.entity.Report;
import ekrut.entity.ReportType;

/*This is how the DB look like:
 * reports table:
 * 
 * 		ID	 | 		type 	|	 date  	| ekrutLocation | area
 * -------------------------------------------------------------------
 * 	  15678  |     order    |  28.12.22 |       U-TLV   | NORTH
 * 
 * #######################################################################
 * orders report data table:
 *  reportID |   location  |totalOrders | shipment | pickup | remote | totalOrdersInILS |shipmentInILS | pickupInILS | remoteInILS  |
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 	  15678  |     U-TLV   |     130    |    40   |   50   |    40   |        12000     |       4000     |     5000    |    4000     | 
 * 
 * #######################################################################
 * orders top sellers table: 
 *   reportID  | itemName  | totalSales
 * -----------------------------------------
 * 	  15678    |    COKE   |  679
 * 
 * #######################################################################
 * orders monthly data table: 
 *   reportID  | totalOrders  | totalOrdersInILS
 * ---------------------------------------------
 * 	  15678    |      1362    |  340988
 * 
 * 
 * #######################################################################
 * inventory report data table:
 * 	 reportID  |  itemName | quantity | threshold | thresholdAlert
 * ------------------------------------------------------------------------
 * 	  32114    |    PEPSI  |	120	  |     10	  |		5
 *
 * #######################################################################
 * inventory items that reach their threshold
 * 	   date   | itemID | itemName |
 * --------------------------------
 * 	 12.01.23 | 17332  |   FANTA  |
 * 
 * #######################################################################
 * customers report table:
 *  how many customers did x-y orders
 * 	  reportID |  1  |  2  |   3   |  4  | 5   | 6+
 * ------------------------------------------------
 * 	   29993   | 256 | 324 |  122  |  70 | 104 | 152
 * 
 * #######################################################################
 **/

/**
 * Handles all direct database interactions with reports
 * 
 * @author Tal Gaon
 */
public class ReportDAO {
	private DBController con;
	private OrderDAO orderDAO;
	
	/**
	 * Constructs a new reportDAO that uses the provided controller
	 * 
	 * @param con the database controller to use
	 * @param orderDAO to use it to fetch orders
	 */
	public ReportDAO(DBController con, OrderDAO orderDAO) {
		this.con = con;
		this.orderDAO = orderDAO;
	}
	
	public ReportDAO(DBController con) {
		this.con = con;
	}
	
	
	/**
	 * Return the report ID for a report with the given date, ekrutlLocation,
	 * and type.
	 * 
	 * @param date
	 * @param ekrutLocation
	 * @param area
	 * @param type
	 * @return the report ID of the report, or -1 if the report does not exist
	 * @throws Exception if there is an error executing the SQL query
	 */
	public Integer getReportID(LocalDateTime date, String ekrutLocation, String area, ReportType type) throws Exception {
		int reportid = -1;
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM reports WHERE"
				+ " EXTRACT(MONTH FROM date) = EXTRACT(MONTH FROM ?)"
				+ " AND EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM ?)"
				+ " AND ekrutLocation = ? AND type = ? AND area = ?");
		
		try {
			ps.setObject(1, date, MysqlType.DATETIME); 
			ps.setObject(2, date, MysqlType.DATETIME); 
			ps.setString(3, ekrutLocation);
			ps.setString(4, type.toString()); 
			ps.setString(5, area);
			ResultSet rs = con.executeQuery(ps);

			if (rs.first()) {
			  reportid = rs.getInt("reportID");
			}
			
			if (reportid == -1)
				return null;

			return reportid; 
			
			}catch (SQLException e) {
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
	 * Fetches a report based on the given parameters.
	 *
	 * @param date
	 * @param ekrutLocation
	 * @param area 
	 * @param type ("order", "inventory", or "customer").
	 * @return The report, or null if an error occurred.
	 * */
	public Report fetchReport(LocalDateTime date, String ekrutLocation, String area, ReportType type) {
		try {
			Integer reportID = getReportID(date, ekrutLocation, area, type);

			// If there is not such report
			if (reportID == null)
				return null;
				
			Report report = null;
			// We will use the corresponding function according to the report type
			switch(type){
			case ORDER:
				report = fetchOrderReportByID(reportID);
				break;
			case INVENTORY:
				report = fetchInventoryReportByID(reportID);
				break;
			case CUSTOMER:
				report = fetchCustomerReportByID(reportID);
				break;
			default:
				break;
			}
				
			return report;
			
		//TBD.tal what to do here?
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Return a customer report with the given report ID.
	 * 
	 * @param reportID
	 * @return a {@link Report} object containing the report data, or null if the report does not exist
	 * @throws Exception if there is an error executing the SQL query

	 */
	public Report fetchCustomerReportByID(Integer reportID) {
		PreparedStatement ps1 = con.getPreparedStatement("SELECT * FROM reports WHERE reportID = ?");
		PreparedStatement ps2 = con.getPreparedStatement("SELECT * FROM customers_report_data WHERE reportID = ?");
		PreparedStatement ps3 = con.getPreparedStatement("SELECT * FROM monthly_orders_by_day WHERE reportID = ?");

		try {
			ps1.setInt(1, reportID);
			ResultSet rs1 = con.executeQuery(ps1);

			if (!rs1.next()) 
				return null;
						
			ps2.setInt(1, reportID);
			ResultSet rs2 = con.executeQuery(ps2);

			Map<String, Integer> customersHistogram = new HashMap<>();

			// Add each row to customerReportData
			if (rs2.first()) {
				customersHistogram.put("1", rs2.getInt("1"));
				customersHistogram.put("2", rs2.getInt("2"));
				customersHistogram.put("3", rs2.getInt("3"));
				customersHistogram.put("4", rs2.getInt("4"));
				customersHistogram.put("5", rs2.getInt("5"));
				customersHistogram.put("6+", rs2.getInt("6+"));
			}

			ps3.setInt(1, reportID);
			ResultSet rs3 = con.executeQuery(ps3);
			
			Map<Integer, Integer> customersOrdersByDate = new HashMap<>();
			
			if(rs3.first()) {
				for(int i = 1; i <= 31 ; i++) {
					customersOrdersByDate.put(i, rs3.getInt(String.valueOf(i)));
				}
			}

			// Create a report instance
			Report report = new Report(rs1.getInt("reportID"),
					ReportType.valueOf(rs1.getString("type")), 
					rs1.getObject(("date"), LocalDateTime.class),
					rs1.getString("ekrutLocation"),
					rs1.getString("area"),
					customersHistogram,
					customersOrdersByDate); 	
			
			return report;
			 
			}catch (SQLException e) {
				return null;
			} finally {
				try {
					ps1.close();
					ps2.close();
					ps3.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
	}

	/**
	 * This method fetches a report from the database based on its ID.
	 * 
	 * @param reportID the ID of the report to fetch
	 * @return a Report object representing the report with the specified ID, or null if no such report exists in the database
	 * @throws SQLException if a database error occurs while executing the SQL queries
	 */
	public Report fetchOrderReportByID(Integer reportID) {
		PreparedStatement ps1 = con.getPreparedStatement("SELECT * FROM reports WHERE reportID = ?");
		PreparedStatement ps2 = con.getPreparedStatement("SELECT * FROM orders_report_data WHERE reportID = ?");
		PreparedStatement ps3 = con.getPreparedStatement("SELECT * FROM top_sellers WHERE reportID = ?");
		PreparedStatement ps4 = con.getPreparedStatement("SELECT * FROM monthly_orders WHERE reportID = ?");
		
		try {
			
			ps1.setInt(1, reportID);
			ResultSet rs1 = con.executeQuery(ps1);
			
			if (!rs1.next()) 
				return null;
			
			ps2.setInt(1, reportID);
			ResultSet rs2 = con.executeQuery(ps2);

			Map<String, ArrayList<Integer>> orderReportData = new HashMap<>();
			
			while (rs2.next()) {
				ArrayList<Integer> temp = new ArrayList<>();
				temp.add(rs2.getInt("totalOrders"));
				temp.add(rs2.getInt("pickup"));
				temp.add(rs2.getInt("remote"));
				temp.add(rs2.getInt("totalOrdersInILS"));
				temp.add(rs2.getInt("pickupInILS"));
				temp.add(rs2.getInt("remoteInILS"));
				orderReportData.put(rs2.getString("ekrutLocation"), temp);
			}

			ps3.setInt(1, reportID);
			ResultSet rs3 = con.executeQuery(ps3);

			//fetch top sellers
			Map<String, Integer> topSellersData = new HashMap<>();
			
			while(rs3.next()) {
				topSellersData.put(rs3.getString("itemName"),
						rs3.getInt("totalSales")); 
			}

			ps4.setInt(1, reportID);
			ResultSet rs4 = con.executeQuery(ps4);
			
			if (!rs4.next()) 
				return null;

			Report report = new Report(rs1.getInt("reportID"),
					ReportType.valueOf(rs1.getString("type")), 
					rs1.getObject(("date"), LocalDateTime.class),
					rs1.getString("ekrutLocation"),
					rs1.getString("area"),
					rs4.getInt("totalOrders"),
					rs4.getInt("totalOrdersInILS"),
					rs4.getInt("totalShipmentOrders"),
					rs4.getInt("totalShipmentOrdersInILS"),
					orderReportData, 
					topSellersData);

			return report;
			 
			}catch (SQLException e) {
				return null;
			} finally {
				try {
					ps1.close();
					ps2.close();
					ps3.close();
					ps4.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
	}
		
	
	
	/**
	 * Return an inventory report with the given report ID.
	 * 
	 * @param reportID the ID of the report to retrieve
	 * @return a {@link Report} object containing the report data, or null if the report does not exist
	 * @throws Exception if there is an error executing the SQL query
	 */
	public Report fetchInventoryReportByID(Integer reportID) throws Exception {
		PreparedStatement ps1 = con.getPreparedStatement("SELECT * FROM reports WHERE reportID = ?");
		PreparedStatement ps2 = con.getPreparedStatement("SELECT * FROM inventory_report_data WHERE reportID = ?");
		try {
			ps1.setInt(1, reportID);
			ResultSet rs1 = con.executeQuery(ps1);
			
			if (!rs1.next()) 
				return null;
			
			ps2.setInt(1, reportID);
			ResultSet rs2 = con.executeQuery(ps2);
			
			Integer threshold = null;
					
			Map<String, ArrayList<Integer>> inventoryReportData = new HashMap<>();	
			// Put items into the map: for each itemName -> quantity, thresholdBreaches
			while (rs2.next()) {
				ArrayList<Integer> temp = new ArrayList<>();
				temp.add(rs2.getInt("quantity"));
				temp.add(rs2.getInt("thresholdBreaches"));
				inventoryReportData.put(rs2.getString("itemName"), temp);
				threshold = rs2.getInt("threshold");
			} 
			// Create a report instance
			Report report = new Report(rs1.getInt("reportID"),
					ReportType.valueOf(rs1.getString("type")), 
					rs1.getObject(("date"), LocalDateTime.class),
					rs1.getString("ekrutLocation"),
					rs1.getString("area"),
					inventoryReportData,
					threshold); 
			
			return report;
			 
			}catch (SQLException e) {
				return null;
			} finally {
				try {
					ps1.close();
					ps2.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
	}
	
	/** TODO redo
	 * Fetches all orders from the database that have the same month and year as the given date,
	 * and the same location as the given location.
	 * 
	 * @param date the reference date to filter the orders by month and year
	 * @param location the location to filter the orders by
	 * @return a list of all orders that meet the criteria, or null if an exception is thrown
	 */
	public ArrayList<Order> fetchAllMonthlyOrdersByLocation(LocalDateTime date, String location) {

		PreparedStatement ps1 = con.getPreparedStatement("SELECT orderId FROM orders WHERE"
				+ " EXTRACT(MONTH FROM date) = EXTRACT(MONTH FROM ?) AND"
				+ " EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM ?) AND location = ?");
		try {
			ps1.setObject(1, date, MysqlType.DATETIME);
			ps1.setObject(2, date, MysqlType.DATETIME);
			ps1.setString(3, location);
			ResultSet rs1 = con.executeQuery(ps1);
			
			ArrayList<Order> orders = new ArrayList<>();
			// Fetch all the orders by ID and add them to orders ArrayList
			while (rs1.next()) {
				orders.add(orderDAO.fetchOrderById(rs1.getInt("reportId")));
			} 
			return orders;
			 
			}catch (SQLException e) {
				return null;
			} finally {
				try {
					ps1.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			
			}
	}
	//TODO document
	public ArrayList<Order> fetchAllMonthlyShipmentOrders(LocalDateTime date) {

		PreparedStatement ps1 = con.getPreparedStatement("SELECT orderId FROM orders WHERE"
				+ " EXTRACT(MONTH FROM date) = EXTRACT(MONTH FROM ?) AND"
				+ " EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM ?) AND type = ?");
		try {
			ps1.setObject(1, date, MysqlType.DATETIME);
			ps1.setObject(2, date, MysqlType.DATETIME);
			ps1.setString(3, OrderType.SHIPMENT.toString());
			ResultSet rs1 = con.executeQuery(ps1);
			
			ArrayList<Order> orders = new ArrayList<>();
			// Fetch all the orders by ID and add them to orders ArrayList
			while (rs1.next()) {
				orders.add(orderDAO.fetchOrderById(rs1.getInt("reportId")));
			} 
			return orders;
			 
			}catch (SQLException e) {
				return null;
			} finally {
				try {
					ps1.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			
			}
	}

	/**
	 * Fetches the username of all orders from the database that have the same month and year as the given date,
	 * and the same location as the given location.
	 * 
	 * @param date the reference date to filter the orders by month and year
	 * @param location the location to filter the orders by
	 * @return a list of all usernames of orders that meet the criteria, or null if an exception is thrown
	 */
	//TODO make 2 new methods. the first one filter out all the orders that is shipment and the second is get all the shipment orders at given area
	public ArrayList<String> getAllCustomersOrdersByName(LocalDateTime date, String location) {
		
		PreparedStatement ps1 = con.getPreparedStatement("SELECT username FROM orders WHERE"
				+ " EXTRACT(MONTH FROM date) = EXTRACT(MONTH FROM ?) AND"
				+ " EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM ?) AND location = ?");
		try {
			ps1.setObject(1, date, MysqlType.DATETIME);
			ps1.setString(2, location);
			
			ResultSet rs1 = con.executeQuery(ps1);
			
			ArrayList<String> customersOrders = new ArrayList<>();
			
			while (rs1.next()) {
				customersOrders.add(rs1.getString("username"));
			}
			
			return customersOrders;
			
			}catch (SQLException e) {
				return null;
			} finally {
				try {
					ps1.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
	}
	//get all orders without shipment orders
	public ArrayList<String> getAllCustomersOrdersByNameWithOutShipment(LocalDateTime date, String location) {
		
		PreparedStatement ps1 = con.getPreparedStatement("SELECT username FROM orders WHERE"
				+ " EXTRACT(MONTH FROM date) = EXTRACT(MONTH FROM ?) AND"
				+ " EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM ?) AND location = ? AND type != shipment");
		try {
			ps1.setObject(1, date, MysqlType.DATETIME);
			ps1.setString(2, location);
			
			ResultSet rs1 = con.executeQuery(ps1);
			
			ArrayList<String> customersOrders = new ArrayList<>();
			
			while (rs1.next()) {
				customersOrders.add(rs1.getString("username"));
			}
			
			return customersOrders;
			
			}catch (SQLException e) {
				return null;
			} finally {
				try {
					ps1.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
	}

	// Add document
	public ArrayList<LocalDateTime> getAllCustomersOrdersByDateWithOutShipment(LocalDateTime date, String location) {
		
		PreparedStatement ps1 = con.getPreparedStatement("SELECT username FROM orders WHERE"
				+ " EXTRACT(MONTH FROM date) = EXTRACT(MONTH FROM ?) AND"
				+ " EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM ?) AND location = ? AND type != shipment");
		try {
			ps1.setObject(1, date, MysqlType.DATETIME);
			ps1.setString(2, location);
			
			ResultSet rs1 = con.executeQuery(ps1);
			
			ArrayList<LocalDateTime> customersOrdersByDate = new ArrayList<>();
			
			while (rs1.next()) {
				customersOrdersByDate.add(rs1.getObject(("date"), LocalDateTime.class));
			}
			
			return customersOrdersByDate;
			
			}catch (SQLException e) {
				return null;
			} finally {
				try {
					ps1.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
	}
	
	
	/**
	 * Fetches the item names of all threshold breaches from the database that have the same month and year as the given date,
	 * and the same location as the given location.
	 * 
	 * @param date the reference date to filter the threshold breaches by month and year
	 * @param ekrutLocation the location to filter the threshold breaches by
	 * @return a list of all item names of threshold breaches that meet the criteria, or null if an exception is thrown
	 */
	public ArrayList<String> getThresholdAlert(LocalDateTime date, String ekrutLocation){

		PreparedStatement ps1 = con.getPreparedStatement("SELECT itemName FROM threshold_breaches WHERE"
				+ " EXTRACT(MONTH FROM date) = EXTRACT(MONTH FROM ?)"
				+ " AND EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM ?) AND location = ?");
		try {
			ps1.setObject(1, date, MysqlType.DATETIME);
			ps1.setObject(2, date, MysqlType.DATETIME);
			ps1.setString(3, ekrutLocation);
			
			ResultSet rs1 = con.executeQuery(ps1);
			
			ArrayList<String> thresholdAlerts = new ArrayList<>();	
			
			while (rs1.next()) {
				thresholdAlerts.add(rs1.getString("itemName"));
			}
			
			return thresholdAlerts;
			
			}catch (SQLException e) {
				return null;
			} finally { 
				try {
					ps1.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
	}
	
	/**
	 * Creates a new report in the database with the given report object's type, date, and location.
	 * Sets the report object's report ID to the ID of the new report in the database.
	 * 
	 * @param report the report object containing the type, date, and location to use for the new report
	 * @return true if the report is successfully created, false otherwise
	 */
	public boolean createReport(Report report) {
		con.beginTransaction();
		
		// Pass true so that we can get the new report ID
		PreparedStatement p1 = con.getPreparedStatement("INSERT INTO reports " +
                                                        "(type,date,location) " +
                                                        "VALUES(?,?,?)", true);
		
		try {
			p1.setString(1, report.getReportType().toString());
			p1.setObject(2, report.getDate(), MysqlType.DATETIME);
			p1.setString(3, report.getEkrutLocation().toString());
	
			int res = p1.executeUpdate();
			if(res != 1) {
				con.abortTransaction();
				return false; 
			}
			// Get the new report ID
			ResultSet rs = p1.getGeneratedKeys();
			if (!rs.next()) {
				con.abortTransaction();
				return false;
			} 
			// TBD check, probably this is'nt required
			int reportID = rs.getInt(1);
			rs.close();
			
			// Save the ID in the report object for later use
			report.setReportID(reportID);
			
			con.commitTransaction();
			
			return true;
			
		} catch (SQLException e) {
			con.abortTransaction();
			return false;
		} finally {
			try {
				p1.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	} 
	//TODO redoing document
	/**
	 * This method creates a new order report in the database based on the provided report object.
	 * 
	 * @param report the report to be created in the database
	 * @return true if the report was successfully created, false otherwise
	 * @throws SQLException if a database error occurs while executing the SQL queries
	 */
	public boolean createOrderReport(Report report) {
		
		con.beginTransaction();
		
		PreparedStatement ps1 = con.getPreparedStatement("INSERT INTO topSellers"
								+ "(reportID,itemName,totalSales) " +
									"VALUES(?,?,?)");
		
		PreparedStatement ps2 = con.getPreparedStatement("INSERT INTO orders_report_data"
								+ "(reportID,ekrutLocation,totalOrders,pickup,remote,totalOrdersInILS,pickupInILS,remoteInILS) " +
                                  "VALUES(?,?,?,?,?,?,?,?)");
		
		PreparedStatement ps3 = con.getPreparedStatement("INSERT INTO monthly_orders"
								+ "(reportID,totalOrders,totalOrdersInILS,totalShipmentOrders,totalShipmentOrdersInILS) " +
								"VALUES(?,?,?)");
		
		
		Integer reportID = report.getReportID();
		
		try {
			//Insert topSellers
			for (Map.Entry<String, Integer> entry : report.getTopSellersData().entrySet()) {
				ps1.setInt(1,  reportID);
				ps1.setString(2, entry.getKey());
				ps1.setInt(3, entry.getValue());
				ps1.addBatch();
			}

			int[] results1 = ps1.executeBatch();
			for (int i : results1) {
				if (i != 1) {
					con.abortTransaction();
					return false;
				}
			}
				
			for (Map.Entry<String, ArrayList<Integer>>  entry : report.getOrderReportData().entrySet()) { 
				ps2.setInt(1, reportID);
				ps2.setString(2, entry.getKey());
				ps2.setInt(3, entry.getValue().get(0));
				ps2.setInt(4, entry.getValue().get(1));
				ps2.setInt(5, entry.getValue().get(2));
				ps2.setInt(6, entry.getValue().get(3));
				ps2.setInt(7, entry.getValue().get(4));
				ps2.setInt(8, entry.getValue().get(5));
				ps2.addBatch(); 
			}
			
			
			int[] results2 = ps2.executeBatch();
			for (int i : results2) {
				if (i != 1) {
					con.abortTransaction();
					return false;
				}
			}
			
			ps3.setInt(1,  reportID);
			ps3.setInt(2, report.getTotalOrders());
			ps3.setInt(3, report.getTotalOrdersInILS());
			ps3.setInt(4, report.getTotalShipmentOrders());
			ps3.setInt(5, report.getTotalShipmentOrdersInILS());

			int res = ps3.executeUpdate();
			if(res != 1) {
				con.abortTransaction();
				return false; 
			}
			con.commitTransaction();
			return true;
			
		} catch (SQLException e) {
			con.abortTransaction();
			return false;
		} finally {
			try {
				ps1.close();
				ps2.close();
				ps3.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
	/**
	 * Creates a customer report and stores it in the database.
	 *
	 * @param report the report to be created and stored
	 * @return true if the report was successfully created and stored, false otherwise
	 */	
	public boolean createCustomerReport(Report report) {
		
		con.beginTransaction();
		
		PreparedStatement ps1 = con.getPreparedStatement("INSERT INTO customerReports"
													+ " (reportID,1,2,3,4,5,6+)"
													+ " VALUES(?,?,?,?,?,?,?)");
		
		PreparedStatement ps2 = con.getPreparedStatement("INSERT INTO customerReports"
								+ " (reportID,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31)"
								+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		
		Integer reportID = report.getReportID();
		
		try {

			ps1.setInt(1,  reportID);
			ps1.setInt(2, report.getCustomerReportData().get("1"));
			ps1.setInt(3, report.getCustomerReportData().get("2"));
			ps1.setInt(4, report.getCustomerReportData().get("3"));
			ps1.setInt(5, report.getCustomerReportData().get("4"));
			ps1.setInt(6, report.getCustomerReportData().get("5"));
			ps1.setInt(7, report.getCustomerReportData().get("6+"));

			int res1 = ps1.executeUpdate();
			if(res1 != 1) {
				con.abortTransaction();
				return false; 
			}
			//fix
			ps2.setInt(1,  reportID);
			for (int i = 1; i <= 31; i++) {
				ps2.setInt(i + 1, report.getCustomersOrdersByDate().get(i));
			}
			
			int res2 = ps2.executeUpdate();
			if(res2 != 1) {
				con.abortTransaction();
				return false; 
			}	
			
			con.commitTransaction();
			return true;
			
		} catch (SQLException e) {
			con.abortTransaction();
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
	 * Creates an inventory report in the database using the given report object.
	 * 
	 * @param report the report object containing the report data
	 * @return true if the report was created successfully, false otherwise
	 */
	public boolean createInventoryReport(Report report) {
	
	con.beginTransaction();
		
	PreparedStatement ps1 = con.getPreparedStatement("INSERT INTO inventoryReports"
													+ "(reportID,itemName,quantity,threshold,thresholdBreaches) " +
                                                    "VALUES(?,?,?,?,?)");
	Integer reportID = report.getReportID();
	
	try {
		 // Iterate over the entries in the Map contained in the report object
		for (Map.Entry<String, ArrayList<Integer>> entry : report.getInventoryReportData().entrySet()) {
			 // Get the item name and threshold data from the entry
			String itemName = entry.getKey();
			ArrayList<Integer> thresholdData = entry.getValue();
			
			if (thresholdData.size() != 2) {
				return false;
			}
			//TresholdData(0) is threshold of the item's facility, tresholdData(1) is how many breaches that item "cause"
			int threshold = thresholdData.get(0);
			int thresholdBreaches = thresholdData.get(1);
			
			ps1.setInt(1, reportID);
			ps1.setString(2, itemName);
			ps1.setInt(3, threshold);
			ps1.setInt(4, thresholdBreaches);
			ps1.addBatch();

		}
		
		int[] results = ps1.executeBatch();
		for (int i : results) {
			if (i != 1) {
				con.abortTransaction();
				return false;
			}
		}
		con.commitTransaction();
		return true;
		
	} catch (SQLException e) {
		con.abortTransaction();
		return false;
	} finally {
		try {
			ps1.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	}
	
	/**
	 * Retrieves a list of facilities in a given area.
	 *
	 * @param area a string representing the area to search for facilities
	 * @return an ArrayList of strings, each representing a facility in the given area.
	 *         If no facilities are found or there is an error, the method returns null.
	 */
	public ArrayList<String> fetchFacilitiesByArea(String area){

		PreparedStatement ps1 = con.getPreparedStatement("SELECT ekrutLocation FROM ekrut_machines WHERE area = ?");
		
		try {
			ps1.setString(1, area);
			
			ResultSet rs1 = con.executeQuery(ps1);
			
			ArrayList<String> facilities = new ArrayList<>();	
			
			 
			while (rs1.next()) {
				facilities.add(rs1.getString("ekrutLocation"));
			}
			
			return facilities;
			
			}catch (SQLException e) {
				return null;
			} finally { 
				try {
					ps1.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
	}
	
}
	

	
