package ekrut.client.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import ekrut.client.EKrutClient;
import ekrut.client.managers.ClientReportManager;
import ekrut.entity.Order;
import ekrut.entity.Report;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class OrderReportViewController implements Initializable{

    @FXML
    private Label areaLbl;

    @FXML
    private Label dateLbl;

    @FXML
    private BarChart<?, ?> ordersBarChart;

    @FXML
    private PieChart ordersPieChart;

    @FXML
    private BarChart<?, ?> topSellersBarChart;

    @FXML
    private Label totalOrdersInILSLbl;

    @FXML
    private Label totalOrdersLbl;

    
    private EKrutClient client;
    ClientReportManager  clientReportManager = new ClientReportManager(client);
    Report report;
    
    
    private void setTotalOrdersLbl() {
    	// Convert 1859 to "1,859"
    	String totalOrders = String.format("%,d", report.getTotalOrders());
    	totalOrdersLbl.setText(totalOrders);
    }
    
    private void setTotalOrdersLblInILS() {
    	// Convert 1859 to "1,859"
    	String totalOrdersInILS = String.format("%,d", report.getTotalOrdersInILS());
    	totalOrdersLbl.setText(totalOrdersInILS);
    }
    
    private void setOrdersPieChat() {
    	ObservableList<PieChart.Data> PieChartData =
				FXCollections.observableArrayList();
    	Map<String, ArrayList<Integer>> orderData = report.getOrderReportData();
    	// We save order number data at index 0
    	for (Map.Entry<String, ArrayList<Integer>> entry : orderData.entrySet()) {
    		Integer orderNum = entry.getValue().get(0);
    		PieChartData.add(new PieChart.Data(entry.getKey(), orderNum));
    	}
    	ordersPieChart.setData(PieChartData);
    }
    
    private void setOrdersBarChart() throws Exception {
    	//Defining the x axis               
		CategoryAxis xAxis = new CategoryAxis();   
		ArrayList<String> locations = clientReportManager.getFacilitiesByArea(report.getArea());
   	 	// Convert array list into a array
   	 	String[] locationsArr = locations.toArray(new String[locations.size()]);
		xAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(locationsArr))); 
		xAxis.setLabel("Order Type");  
		
		//Defining the y axis 
		NumberAxis yAxis = new NumberAxis(); 
		yAxis.setLabel("Total in ILS");
		
		//OrdersHistogram = new BarChart<>(xAxis, yAxis);  
		
		//Prepare XYChart.Series objects by setting data        
		Map<String, ArrayList<Integer>> orderData = report.getOrderReportData();
		
		XYChart.Series series1 = new XYChart.Series<>(); 
		series1.setName("shipment"); 

		XYChart.Series series2 = new XYChart.Series<>(); 
		series2.setName("remote"); 

		XYChart.Series series3 = new XYChart.Series<>(); 
		series3.setName("pickup"); 

    	// We save order number data at index 0
    	for (Map.Entry<String, ArrayList<Integer>> entry : orderData.entrySet()) {
    		String location = entry.getKey();
    		Integer shipmentNum = entry.getValue().get(5);
    		Integer pickUpNum = entry.getValue().get(6);
    		Integer remoteNum = entry.getValue().get(7);

    		series1.getData().add(new XYChart.Data<>(location, shipmentNum));
    		series2.getData().add(new XYChart.Data<>(location, pickUpNum)); 
    		series3.getData().add(new XYChart.Data<>(location, remoteNum)); 

    	}
		ordersBarChart.getData().addAll(series1, series2, series3);
		
    }
    
    private void setTopSellersBarChart() {
    	    	
    	XYChart.Series series4 = new XYChart.Series<>(); 
    	series4.setName("Item"); 
    	
    	Map<String, Integer> topSellersData = report.getTopSellersData();
    	
    	for (Map.Entry<String, Integer> entry : topSellersData.entrySet()) {
		//Prepare XYChart.Series objects by setting data        
			series4.setName("Item"); 
			series4.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())); 
    	}
		topSellersBarChart.getData().addAll(series4);
    }
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Set orders labels
		setTotalOrdersLbl();
		setTotalOrdersLblInILS();
		
		// Set charts
		setOrdersPieChat();
		
		try {
			setOrdersBarChart();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		setTopSellersBarChart();
	}
}


	/*		ObservableList<PieChart.Data> PieChartData =
				FXCollections.observableArrayList(
						new PieChart.Data("Haifa", 15),
						new PieChart.Data("Nahariya", 25),
						new PieChart.Data("Akko", 20),
						new PieChart.Data("Nazereth", 30),
						new PieChart.Data("Afula", 10));
		
		ordersPieChart.setData(PieChartData);
		
		//Defining the x axis               
		CategoryAxis xAxis = new CategoryAxis();   
		        
		xAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(
		   "Haifa", "Akko", "Nahariya"))); 
		xAxis.setLabel("Order Type");  

		//Defining the y axis 
		NumberAxis yAxis = new NumberAxis(); 
		yAxis.setLabel("Total in ILS");
		
		//OrdersHistogram = new BarChart<>(xAxis, yAxis);  
		
		
		//Prepare XYChart.Series objects by setting data        
		XYChart.Series series1 = new XYChart.Series<>(); 
		series1.setName("shipment"); 
		series1.getData().add(new XYChart.Data<>("Haifa", 1200)); 
		series1.getData().add(new XYChart.Data<>("Akko", 900)); 
		series1.getData().add(new XYChart.Data<>("Nahariya", 1400)); 

		XYChart.Series series2 = new XYChart.Series();  
		series2.setName("remote"); 
		series2.getData().add(new XYChart.Data<>("Haifa", 500)); 
		series2.getData().add(new XYChart.Data<>("Akko", 2000));  
		series2.getData().add(new XYChart.Data<>("Nahariya", 1100)); 

		XYChart.Series series3 = new XYChart.Series(); 
		series3.setName("pickup"); 
		series3.getData().add(new XYChart.Data<>("Haifa", 1700)); 
		series3.getData().add(new XYChart.Data<>("Akko", 850)); 
		series3.getData().add(new XYChart.Data<>("Nahariya", 930)); 

		ordersBarChart.getData().addAll(series1, series2, series3);
		
		//Prepare XYChart.Series objects by setting data        
		XYChart.Series series11 = new XYChart.Series<>(); 
		series11.setName("Item"); 
		series11.getData().add(new XYChart.Data<>(1200, "Bamba")); 
		series11.getData().add(new XYChart.Data<>(900, "Bisli")); 
		series11.getData().add(new XYChart.Data<>(1400, "Coke"));
		series11.getData().add(new XYChart.Data<>(1400, "Pepsi")); 
		series11.getData().add(new XYChart.Data<>(1300, "Pasta")); 


		topSellersBarChart.getData().addAll(series11);*/


