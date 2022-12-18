package ekrut.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.mysql.cj.MysqlType;
import ekrut.entity.Report;

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
		// TODO Auto-generated method stub
		return null;
	}


	private Report fetchOrderReportByID(int reportID) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Fetches an inventory report based on the given report ID.
	 *
	 * @param reportID The ID of the report.
	 * @return The report, or null if no report was found or an error occurred.
	 * @throws Exception If an error occurred while fetching the report.
	 */
	public Report fetchInventoryReportByID(Integer reportID) throws Exception {
		PreparedStatement ps1 = con.getPreparedStatement("SELECT * FROM reports WHERE reportID = ?");
		PreparedStatement ps2 = con.getPreparedStatement("SELECT * FROM inventory_reports WHERE reportID = ?");

		try {
			ps1.setInt(1, reportID);
			ResultSet rs1 = con.executeQuery(ps1);
			
			if (!rs1.next()) 
				return null;
			
			/*Create instance of report using report constructor
			 *Columns are: reportID, reportType, date, reportArea, ekrutLocaion*/
			Report report = new Report(rs1.getInt(1), rs1.getString(2),
					rs1.getObject(3, LocalDateTime.class), rs1.getString(4), rs1.getString(5)); 
			
			ps2.setInt(1, reportID);
			ResultSet rs2 = con.executeQuery(ps2);
			Map<String, int[]> InventoryReportData = new HashMap<>();
		
			// Put items into the map: for each itemID -> quantity, threshold
			while (rs2.next()) {
				InventoryReportData.put(rs2.getString(4), new int[] { rs2.getInt(5), rs2.getInt(6)});
			}
			report.setInventoryReportData(InventoryReportData);

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
	
	
	
	
	
	
}
