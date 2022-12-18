package ekrut.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mysql.cj.MysqlType;

import ekrut.entity.Item;
import ekrut.entity.Order;
import ekrut.entity.OrderItem;
import ekrut.entity.OrderStatus;
import ekrut.entity.OrderType;
import ekrut.entity.Report;

public class ReportDAO {
	private DBController con;
	/**
	 * Constructs a new OrderDAO that uses the provided controller
	 * 
	 * @param con   the database controller to use
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
	
	public Object fetchInventoryReportByID(Integer reportID) throws Exception {
		/*
		 * TODO: create an object of a report by get all the necessary data from the DB
		 * We will probably need a methods that will handle
		 * the creation of a report according to each type separately*/
		PreparedStatement ps1 = con.getPreparedStatement("SELECT * FROM reports WHERE reportID = ?");
		PreparedStatement ps2 = con.getPreparedStatement("SELECT * FROM inventory_reports WHERE reportID = ?");

		try {
			ps1.setInt(1, reportID);
			ResultSet rs1 = con.executeQuery(ps1);
			
			if (!rs1.next()) 
				return null;
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
