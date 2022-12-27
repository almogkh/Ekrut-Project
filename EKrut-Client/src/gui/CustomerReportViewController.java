package gui;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.print.DocFlavor.URL;

import ekrut.client.managers.ClientReportManager;
import ekrut.entity.Report;
import ekrut.entity.ReportType;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class CustomerReportViewController implements Initializable{

	 	@FXML
	    private BarChart<?, ?> CustomerChart;

	    @FXML
	    private NumberAxis NumberOfCustomers;

	    @FXML
	    private CategoryAxis OrdersCategories;

	    @FXML
	    private Label serverConnectionStatusLbl;

	    @FXML
	    private Label serverIpPortLbl;

		@Override
		public void initialize(java.net.URL location, ResourceBundle resources) {
			
			XYChart.Series set1 = new XYChart.Series<>();
			
			Map<String, Integer> testReportMap = new HashMap<>();
			testReportMap.put("1", 25);
	        testReportMap.put("2", 22);
	        testReportMap.put("3", 26);
	        testReportMap.put("4", 16);
	        testReportMap.put("5", 14);
	        testReportMap.put("6+", 28);
	        
			set1.getData().add(new XYChart.Data("1",testReportMap.get("1")));
			set1.getData().add(new XYChart.Data("2",testReportMap.get("2")));
			set1.getData().add(new XYChart.Data("3",testReportMap.get("3")));
			set1.getData().add(new XYChart.Data("4",testReportMap.get("4")));
			set1.getData().add(new XYChart.Data("5",testReportMap.get("5")));
			set1.getData().add(new XYChart.Data("6+",testReportMap.get("6+")));
			CustomerChart.getData().addAll(set1);
		}
}
