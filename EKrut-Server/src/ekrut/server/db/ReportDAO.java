package ekrut.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import ekrut.entity.Report;

public class ReportDAO {
	
	
	/**
	 * Obtains the reportID that corresponds to the entered data.
	 * 
	 * @param String date, String ekrutLocation, String area, String type.
	 * @return	reportID.
	 */
	public static Integer getReportID(String date, String ekrutLocation, String area, String type) throws Exception {
		int reportid = -1;
		try {
			// TBD code below should be somewhere else!
			DBController dbcon = new DBController("jdbc:mysql://localhost/ekrut?serverTimezone=IST", "root", "UntilWhenAUG16");
			dbcon.connect();
			
			PreparedStatement ps = dbcon.getPreparedStatement("SELECT * FROM reports WHERE date = ?"
					+ " AND ekrutLocation = ? AND area = ? AND type = ?;");
			ps.setString(1, date); // TBD.tal check how to handle localDateTime
			ps.setString(2, ekrutLocation);
			ps.setString(3, area);
			ps.setString(4, type);
			ResultSet rs = dbcon.executeQuery(ps);
			
			if (rs.first()) {
			  reportid = rs.getInt("reportID");
			}
			if (reportid == -1)
				throw new Exception("There are no such report.");
			ps.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return reportid; 
	}
	public static Report fetchReportByID(Integer reportID) {
		/*
		 * TODO: create an object of a report by get all the necessary data from the DB
		 * We will probably need a methods that will handle
		 * the creation of a report according to each type separately*/
		return null;
	}
}
