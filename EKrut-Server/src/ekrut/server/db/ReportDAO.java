package ekrut.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mysql.cj.MysqlType;

import ekrut.entity.Order;
import ekrut.entity.Report;
import ekrut.entity.ReportType;

/*This is how the DB look like:
 * reports table:
 * 
 * 		ID	 | 		type 	|	 date  	|	 area 	| 	ekrutLocation
 * -------------------------------------------------------------------
 * 	  15678  |     order    |  28.12.22 |    TLV    |     U-TLV
 * 
 * #######################################################################
 * orders report data table:
 *  reportID |  itemID | itemName |	numOfOrders |
 * ------------------------------------------
 * 	  15678  |  12233  |   COKE   |     130     |
 * 
 * #######################################################################
 * orders monthly sales table:
 *   reportID  | totalSales | totalSalesInILS |
 * --------------------------------------------
 * 	  15678    |    3220    |       18550     |
 * 
 * #######################################################################
 * inventory report data table:
 * 	 reportID  | itemID | itemName | quantity | threshold | thresholdAlert
 * ------------------------------------------------------------------------
 * 	  32114    | 13332  |   PEPSI  |	120	  |     10	  |		5
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
 * 	  reportID |  0_1  | 2_3 |  4_5  | 5_6 | 6+
 * --------------------------------------------
 * 	   29993   |  256  | 324 |  122  |  70 | 104
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
	
	
	/**
	 * Return the report ID for a report with the given date, ekrutlLocation,
	 * area, and type.
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
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM reports WHERE date = ?"
				+ " AND ekrutLocation = ? AND area = ? AND type = ?;");
		try {
			ps.setObject(1, date, MysqlType.DATETIME); 
			ps.setString(2, ekrutLocation);
			ps.setString(3, area);
			ps.setString(4, type.toString()); 
			ResultSet rs = con.executeQuery(ps);
			
			if (rs.first()) {
			  reportid = rs.getInt("reportID");
			}
			if (reportid == -1)
				throw new Exception("There is no such report.");

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
			int reportID = getReportID(date, ekrutLocation, area, type);
			Report report = null;
			// We will use the corresponding function according to the report type
			switch(type){
			case ORDER:
				report = fetchOrderReportByID(reportID);
			case INVENTORY:
				report = fetchInventoryReportByID(reportID);
			case CUSTOMER:
				report = fetchCustomerReportByID(reportID);
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
	public Report fetchCustomerReportByID(int reportID) {
		PreparedStatement ps1 = con.getPreparedStatement("SELECT * FROM reports WHERE reportID = ?");
		PreparedStatement ps2 = con.getPreparedStatement("SELECT * FROM customer_reports WHERE reportID = ?");

		try {
			ps1.setInt(1, reportID);
			ResultSet rs1 = con.executeQuery(ps1);
			
			if (!rs1.next()) 
				return null;
						
			ps2.setInt(1, reportID);
			ResultSet rs2 = con.executeQuery(ps2);
			
			ArrayList<Integer> customerReportData = new ArrayList<Integer>();
			// Add each row to customerReportData
			if (rs2.first()) {
				customerReportData.add(rs2.getInt("0-1"));
				customerReportData.add(rs2.getInt("2-3"));
				customerReportData.add(rs2.getInt("4-5"));
				customerReportData.add(rs2.getInt("5-6"));
				customerReportData.add(rs2.getInt("6+"));
			}
			// Create a report instance
			Report report = new Report(rs1.getInt("reportID"),
					ReportType.valueOf(rs1.getString("type")), 
					rs1.getObject(("date"), LocalDateTime.class),
					rs1.getString("area"),
					rs1.getString("ekrutLocation"),
					customerReportData); 	
			
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

	/**
	 * Return an order report with the given report ID.
	 * 
	 * @param reportID the ID of the report to retrieve
	 * @return a {@link Report} object containing the report data, or null if the report does not exist
	 * @throws Exception if there is an error executing the SQL query
	 */
	public Report fetchOrderReportByID(int reportID) {
		PreparedStatement ps1 = con.getPreparedStatement("SELECT * FROM reports WHERE reportID = ?");
		PreparedStatement ps2 = con.getPreparedStatement("SELECT * FROM order_reports WHERE reportID = ?");
		PreparedStatement ps3 = con.getPreparedStatement("SELECT * FROM order_sales_reports WHERE reportID = ?");
		int numberOfSales = -1;
		int numberOfSalesInILS = -1;

		try {
			ps1.setInt(1, reportID);
			ResultSet rs1 = con.executeQuery(ps1);
			
			if (!rs1.next()) 
				return null;
			
			ps2.setInt(1, reportID);
			ResultSet rs2 = con.executeQuery(ps2);
			
			Map<String, Integer> orderReportData = new HashMap<>();
		
			// Put items into the map: for each itemName -> numOfOrders
			while (rs2.next()) {
				orderReportData.put(rs2.getString("itemName"),
									rs2.getInt("numOfOrders"));
			}
			
			ps3.setInt(1, reportID);
			ResultSet rs3 = con.executeQuery(ps3);
			// Get and save numberOfSales and numberOfSalesInILS
			if (rs3.first()) {
				numberOfSales = rs3.getInt("numberOfSales");
				numberOfSalesInILS = rs3.getInt("numberOfSalesInILS");
			}
			else {
				return null; 
			}
			// Create a report instance
			Report report = new Report(rs1.getInt("reportID"),
					ReportType.valueOf(rs1.getString("type")), 
					rs1.getObject(("date"), LocalDateTime.class),
					rs1.getString("area"),
					rs1.getString("ekrutLocation"),
					numberOfSales, 
					numberOfSalesInILS,
					numberOfSalesInILS,
					orderReportData); 

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
	 * Return an inventory report with the given report ID.
	 * 
	 * @param reportID the ID of the report to retrieve
	 * @return a {@link Report} object containing the report data, or null if the report does not exist
	 * @throws Exception if there is an error executing the SQL query
	 */
	public Report fetchInventoryReportByID(Integer reportID) throws Exception {
		PreparedStatement ps1 = con.getPreparedStatement("SELECT * FROM reports WHERE reportID = ?");
		PreparedStatement ps2 = con.getPreparedStatement("SELECT * FROM inventory_reports WHERE reportID = ?");
		try {
			ps1.setInt(1, reportID);
			ResultSet rs1 = con.executeQuery(ps1);
			
			if (!rs1.next()) 
				return null;
			
			ps2.setInt(1, reportID);
			ResultSet rs2 = con.executeQuery(ps2);
			
			Map<String, int[]> inventoryReportData = new HashMap<>();	
			// Put items into the map: for each itemName -> quantity, threshold, thresholdAlert
			while (rs2.next()) {
				inventoryReportData.put(rs2.getString("itemName"),
						new int[] { rs2.getInt("quantity"),
									rs2.getInt("threshold"),
									rs2.getInt("thresholdAlert")});
			} 
			// Create a report instance
			Report report = new Report(rs1.getInt("reportID"),
					ReportType.valueOf(rs1.getString("type")), 
					rs1.getObject(("date"), LocalDateTime.class),
					rs1.getString("area"),
					rs1.getString("ekrutLocation"),
					inventoryReportData); 
			
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
	
	public Object fetchAllMonthlyOrders(LocalDateTime date, String location) {
		// Create a date instance of the month&year but is set to the first day of the month
		LocalDateTime fromDateTime = date;
		fromDateTime = fromDateTime.withDayOfMonth(1);
		fromDateTime = fromDateTime.withHour(0);
		fromDateTime = fromDateTime.withMinute(0);

		PreparedStatement ps1 = con.getPreparedStatement("SELECT orderId FROM orders WHERE"
				+ " date >= '?' AND date < '?' AND location = ?");
		try {
			ps1.setObject(1, fromDateTime, MysqlType.DATETIME);
			ps1.setObject(2, date, MysqlType.DATETIME);
			ps1.setString(3, location);
			ResultSet rs1 = con.executeQuery(ps1);
			
			ArrayList<Order> orders = new ArrayList<>();
			// Fetch all the reports by ID and add them to orders ArrayList
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
	
}
