package ekrut.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mysql.cj.MysqlType;
import ekrut.entity.Report;

/*This is how the DB look like:
 * reports table:
 * 
 * 		ID	 | 		type 	|	 date  	|	 area 	| 	ekrutLocation
 * -------------------------------------------------------------------
 * 	  15678  |     order    |  28.12.22 |    TLV    |     U-TLV
 * 
 * #######################################################################
 * orders report data table:
 *  reportID |  itemID | itemName |	sales# |
 * ------------------------------------------
 * 	  15678  |  12233  |   COKE   |   130  |
 * 
 * #######################################################################
 * orders monthly sales table:
 *   reportID  | totalSales | totalSalesInILS |
 * --------------------------------------------
 * 	  15678    |    3220    |       18550     |
 * 
 * #######################################################################
 * inventory report data table:
 * 	 reportID  | itemID | itemName | quantity | threshold
 * -------------------------------------------------------
 * 	  32114    | 13332  |   PEPSI  |	120	  |     10
 *
 * #######################################################################
 * inventory items that reach their threshold
 * 	   date   | itemID | itemName |
 * --------------------------------
 * 	 12.01.23 | 17332  |   FANTA  |
 * 
 * #######################################################################
 * customers report table:
 *  inventory items that reach their threshold
 * 	  reportID |  0_1  | 2_3 |  4_5  | 5_6 | 6+
 * --------------------------------------------
 * 	   29993   |  256  | 324 |  122  |  70 | 104
 * 
 * #######################################################################
 **/

public class ReportDAO {
	private DBController con;
	/**
	 * Constructs a new reportDAO that uses the provided controller
	 * 
	 * @param con, the database controller to use
	 */
	public ReportDAO(DBController con) {
		this.con = con;
	}
	
	
	/**
	 * Obtains the reportID that corresponds to the entered data.
	 * 
	 * @param String date, String ekrutLocation, String area, String type.
	 * @return	reportID.
	 */
	public Integer getReportID(String date, String ekrutLocation, String area, String type) throws Exception {
		int reportid = -1;
		PreparedStatement ps = con.getPreparedStatement("SELECT * FROM reports WHERE date = ?"
				+ " AND ekrutLocation = ? AND area = ? AND type = ?;");
		try {
			ps.setObject(1, date, MysqlType.DATETIME);
			ps.setString(2, ekrutLocation);
			ps.setString(3, area);
			ps.setString(4, type);
			ResultSet rs = con.executeQuery(ps);
			
			if (rs.first()) {
			  reportid = rs.getInt("reportID");
			}
			if (reportid == -1)
				throw new Exception("There are no such report.");
			ps.close();
			rs.close();
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
	 * @param date The date of the report.
	 * @param ekrutLocation The location of the report.
	 * @param area The area of the report.
	 * @param type The type of the report ("order", "inventory", or "customer").
	 * @return The report, or null if an error occurred.
	 * */
	public Report fetchReport(String date, String ekrutLocation, String area, String type) {
		try {
			int reportID = getReportID(date, ekrutLocation, area, type);
			Report report = null;
			if(type == "order") {
				report = fetchOrderReportByID(reportID);
			}
			else if(type == "inventory") {
				report = fetchInventoryReportByID(reportID);
			}
			else {
				report = fetchCustomerReportByID(reportID);
			}
			return report;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private Report fetchCustomerReportByID(int reportID) {
		PreparedStatement ps1 = con.getPreparedStatement("SELECT * FROM reports WHERE reportID = ?");
		PreparedStatement ps2 = con.getPreparedStatement("SELECT * FROM customer_reports WHERE reportID = ?");

		try {
			ps1.setInt(1, reportID);
			ResultSet rs1 = con.executeQuery(ps1);
			
			if (!rs1.next()) 
				return null;
			
			/*Create instance of report using report constructor
			 *Columns are: reportID, reportType, date, reportArea, ekrutLocaion*/
			Report report = new Report(rs1.getInt("reportID"),
					rs1.getString("type"), 
					rs1.getObject(("date"), LocalDateTime.class),
					rs1.getString("area"),
					rs1.getString("ekrutLocation"));  
			
			ps2.setInt(1, reportID);
			ResultSet rs2 = con.executeQuery(ps2);
			ArrayList<Integer> customerReportData = new ArrayList<Integer>();
		

			if (rs2.first()) {
				customerReportData.add(rs2.getInt("0-1"));
				customerReportData.add(rs2.getInt("2-3"));
				customerReportData.add(rs2.getInt("4-5"));
				customerReportData.add(rs2.getInt("5-6"));
				customerReportData.add(rs2.getInt("6+"));
			}
				
			report.setCustomerReportData(customerReportData);
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


	private Report fetchOrderReportByID(int reportID) {
		PreparedStatement ps1 = con.getPreparedStatement("SELECT * FROM reports WHERE reportID = ?");
		PreparedStatement ps2 = con.getPreparedStatement("SELECT * FROM order_reports WHERE reportID = ?");
		PreparedStatement ps3 = con.getPreparedStatement("SELECT * FROM order_sales_reports WHERE reportID = ?");


		try {
			ps1.setInt(1, reportID);
			ResultSet rs1 = con.executeQuery(ps1);
			
			if (!rs1.next()) 
				return null;
			
			/*Create instance of report using report constructor
			 *Columns are: reportID, reportType, date, reportArea, ekrutLocaion*/
			Report report = new Report(rs1.getInt("reportID"),
					rs1.getString("type"), 
					rs1.getObject(("date"), LocalDateTime.class),
					rs1.getString("area"),
					rs1.getString("ekrutLocation")); 
			//set month order and month order in ILS
			//TBD.tal set this in a constructor
			
			ps2.setInt(1, reportID);
			ResultSet rs2 = con.executeQuery(ps2);
			Map<String, Integer> orderReportData = new HashMap<>();
		
			// Put items into the map: for each itemName -> how many sales
			while (rs2.next()) {
				orderReportData.put(rs2.getString("itemName"),
									rs2.getInt("sales"));
			}
			report.setOrderReportData(orderReportData);
			
			ResultSet rs3 = con.executeQuery(ps3);
			if (rs3.first()) {
				report.setMonthlyOrders(rs3.getInt("numberOfSales"));
				report.setMonthlyOrders(rs3.getInt("numberOfSalesInILS"));
			}

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
	 * Fetches an inventory report based on the given report ID.
	 *
	 * @param reportID The ID of the report.
	 * @return The report, or null if no report was found or an error occurred.
	 * @throws Exception If an error occurred while fetching the report.
	 */
	public Report fetchInventoryReportByID(Integer reportID) throws Exception {
		LocalDateTime dateTimeNow = LocalDateTime.now();
		PreparedStatement ps1 = con.getPreparedStatement("SELECT * FROM reports WHERE reportID = ?");
		PreparedStatement ps2 = con.getPreparedStatement("SELECT * FROM inventory_reports WHERE reportID = ?");
		PreparedStatement ps3 = con.getPreparedStatement("SELECT * FROM threshold_reports WHERE reportID = ?");

		try {
			ps1.setInt(1, reportID);
			ResultSet rs1 = con.executeQuery(ps1);
			
			if (!rs1.next()) 
				return null;
			
			/*Create instance of report using report constructor
			 *Columns are: reportID, reportType, date, reportArea, ekrutLocaion*/
			Report report = new Report(rs1.getInt("reportID"),
					rs1.getString("type"), 
					rs1.getObject(("date"), LocalDateTime.class),
					rs1.getString("area"),
					rs1.getString("ekrutLocation"));  
			
			ps2.setInt(1, reportID);
			ResultSet rs2 = con.executeQuery(ps2);
			Map<String, int[]> inventoryReportData = new HashMap<>();
		
			// Put items into the map: for each itemName -> quantity, threshold
			while (rs2.next()) {
				inventoryReportData.put(rs2.getString("itemName"),
						new int[] { rs2.getInt("quantity"),
									rs2.getInt("threshold")});
			} 
			report.setInventoryReportData(inventoryReportData);
			
			ResultSet rs3 = con.executeQuery(ps3);
			Map<String, LocalDateTime> thresholdReportData = new HashMap<>();
			while (rs3.next()) {
				LocalDateTime reportDate = rs3.getObject("date", LocalDateTime.class);
				if(reportDate.getMonth() == dateTimeNow.getMonth() &&
						reportDate.getYear() == dateTimeNow.getYear()) {
					thresholdReportData.put(rs2.getString("itemName"), rs3.getObject("date", LocalDateTime.class));
				}
			}
			report.setThresholdReportData(thresholdReportData);
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
}
