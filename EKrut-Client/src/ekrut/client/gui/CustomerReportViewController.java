package ekrut.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientReportManager;
import ekrut.entity.Report;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.chart.LineChart;


public class CustomerReportViewController {

	   @FXML
	    private BarChart<?, ?> CustomersActivityBarChart;

	    @FXML
	    private Label areaLbl;

	    @FXML
	    private CategoryAxis barChartCategoryAxis;

	    @FXML
	    private NumberAxis barChartNumberAxis;

	    @FXML
	    private Label dateLbl;

	    @FXML
	    private CategoryAxis lineChartCategoryAxis;

	    @FXML
	    private NumberAxis lineChartNumberAxis;

	    @FXML
	    private Label locationLbl;

	    @FXML
	    private LineChart<?, ?> monthlyActivityLineChart;

    private EKrutClient client = EKrutClientUI.getEkrutClient();
    ClientReportManager  clientReportManager = client.getClientReportManager();
    Report report;
    
    // Set the customer report
    public void setCustomerReport(Report report) {
    	this.report = report;
    	setHeadLabels();
		// Set charts
		setCustomersActivityBarChart();
		setMonthlyActivityLineChart();
    }
    
    private void setMonthlyActivityLineChart() {
    	ArrayList<String> days = new ArrayList<>();
    	
    	for (int i = 1; i <= 31; i++) { 
    		days.add(String.valueOf(i));
    	} 
   	    
     	String[] daysArr = days.toArray(new String[days.size()]);
     	lineChartCategoryAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(daysArr))); 
     	
     	XYChart.Series series = new XYChart.Series();
    	series.setName("Item"); 
    	monthlyActivityLineChart.setLegendVisible(false);

     	Map<Integer, Integer> customersOrdersByDate = report.getCustomersOrdersByDate();

     	for(Map.Entry<Integer, Integer> entry : customersOrdersByDate.entrySet()) {
     		series.getData().add(new XYChart.Data(String.valueOf(entry.getKey()), entry.getValue()));
     	}     		
     	
     	monthlyActivityLineChart.getData().add(series);
     	
     	monthlyActivityLineChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
     	series.getNode().setStyle("-fx-stroke: #FFD6DC");
    }
    
    private void setCustomersActivityBarChart() {
		Map<String, Integer> CustomersActivityData = report.getCustomerReportData();
  
    	String[] categories = {"1", "2", "3", "4", "5", "6+"};		

    	barChartCategoryAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(categories))); 
    	CustomersActivityBarChart.setLegendVisible(false);
    	
    	XYChart.Series series = new XYChart.Series(); 
    	
    	for (Map.Entry<String, Integer> entry : CustomersActivityData.entrySet()) {
		//Prepare XYChart.Series objects by setting data       
			series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())); 
    	}
    	CustomersActivityBarChart.getData().addAll(series);
    	CustomersActivityBarChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
    }
    
    private void setHeadLabels() {
    	
    	areaLbl.setText(report.getArea());
    	locationLbl.setText(report.getEkrutLocation());
    	String date = (String.valueOf(report.getDate().getMonthValue()) + '/' + String.valueOf(report.getDate().getYear()));
    	dateLbl.setText(date);
    }
}
