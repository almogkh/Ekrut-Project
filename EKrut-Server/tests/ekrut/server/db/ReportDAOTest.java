package ekrut.server.db;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ekrut.entity.Report;
import ekrut.entity.ReportType;

class ReportDAOTest {

	Report JanUAEReport;
	ReportDAO reportDAO;
	String url;
	String username;
	String password;

	@BeforeEach
	void setUp() throws Exception {

		DBController DBcontroller = new DBController(url, username, password);
		ReportDAO reportDAO = new ReportDAO(DBcontroller, null);
	}
	
	@Test
	public void fetchMonthlyOrdersReport_UAE_JAN() {
		LocalDateTime jan_2022 = LocalDateTime.now();
		jan_2022 = jan_2022.withMonth(1);
		jan_2022 = jan_2022.withYear(2022);
		jan_2022 = jan_2022.withDayOfMonth(31);
		
		Report result = reportDAO.fetchOrderReportByID(1);
		
		
	}


}
