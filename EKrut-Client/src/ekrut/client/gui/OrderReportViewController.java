package ekrut.client.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
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

public class OrderReportViewController{

	@FXML
    private CategoryAxis ItemNameAxis;

    @FXML
    private NumberAxis NumberOfItemOrdersAxis;

    @FXML
    private Label areaLbl;

    @FXML
    private Label dateLbl;

    @FXML
    private CategoryAxis locationsAxis;

    @FXML
    private NumberAxis numberOfOrderAxis;

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

    private EKrutClient client = EKrutClientUI.getEkrutClient();
    ClientReportManager  clientReportManager = client.getClientReportManager();
    Report report;
    
    public void setOrderReport(Report report) {
    	this.report = report;
    	//Set head Labels
    	setHeadLabels();
    	// Set orders labels
		setTotalOrdersLbl();
		setTotalOrdersLblInILS();
		// Set charts
		setOrdersPieChat();
		try {
			setOrdersBarChart();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setTopSellersBarChart();
		
    }
    
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
    
    private void setHeadLabels() {
    	// Set area and date labels
    	areaLbl.setText(report.getArea());
    	String date = (String.valueOf(report.getDate().getMonthValue()) + '/' + String.valueOf(report.getDate().getYear()));
    	dateLbl.setText(date);
    	
    }
    // Set Order Pie Chart
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
    	//TBD handle two or more words at location name
    	//Defining the x axis               
		ArrayList<String> locations = clientReportManager.getFacilitiesByArea(report.getArea());
		ArrayList<String> fixLocations = new ArrayList<>();
		for (String location : locations) {
			fixLocations.add(location.replace("_", " "));
		}
   	 	// Convert array list into a array
   	 	String[] locationsArr = fixLocations.toArray(new String[fixLocations.size()]);
   	 	locationsAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(locationsArr))); 
   	 	locationsAxis.setLabel("Locations");  
		
		//Defining the y axis 
   	 	numberOfOrderAxis.setLabel("Total in ILS");
		
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
    	
    	Map<String, Integer> topSellersData = report.getTopSellersData();
    	
    	ArrayList<String> topSellersItems = new ArrayList<>();
    	for (Map.Entry<String, Integer> entry : topSellersData.entrySet()) {
    		topSellersItems.add(entry.getKey());
    	}
  
		
   	 	// Convert array list into a array
   	 	String[] topSellersArr = topSellersItems.toArray(new String[topSellersItems.size()]);
   	 	ItemNameAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(topSellersArr))); 
   	 	ItemNameAxis.setLabel("Items");
		
		NumberOfItemOrdersAxis.setLabel("Total Orders");

    	XYChart.Series series = new XYChart.Series<>(); 
    	series.setName("Item"); 
    	
    	for (Map.Entry<String, Integer> entry : topSellersData.entrySet()) {
		//Prepare XYChart.Series objects by setting data       
			series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())); 
    	}
		topSellersBarChart.getData().addAll(series);
    }
    
}



