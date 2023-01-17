package ekrut.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientReportManager;
import ekrut.entity.Report;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

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

    @FXML
    private Label totalShipmentOrdersInILSLbl;

    @FXML
    private Label totalShipmentOrdersLbl;
    
    private EKrutClient client = EKrutClientUI.getEkrutClient();
    ClientReportManager  clientReportManager = client.getClientReportManager();
    Report report;
    
    public void setOrderReport(Report report) {
    	this.report = report;
    	//Set head Labels
    	setHeadLabels();
    	// Set orders labels
    	setOrdersLabels(totalOrdersLbl, report.getTotalOrders());
    	setOrdersLabels(totalOrdersInILSLbl, report.getTotalOrdersInILS());
    	setOrdersLabels(totalShipmentOrdersLbl, report.getTotalShipmentOrders());
    	setOrdersLabels(totalShipmentOrdersInILSLbl, report.getTotalShipmentOrdersInILS());
    	
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
    // Get label and Integer and set the Integer after adding commas into the label
    private void setOrdersLabels(Label label, Integer total) {
    	// Convert 1859 to "1,859"
    	String stringToSet = String.format("%,d", total);
    	label.setText(stringToSet);
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
    	//Defining the x axis
		ArrayList<String> locations = clientReportManager.getFacilitiesByArea(report.getArea());
		ArrayList<String> fixLocations = new ArrayList<>();
		for (String location : locations) {
			fixLocations.add(location);
		}
   	 	// Convert array list into a array
   	 	String[] locationsArr = fixLocations.toArray(new String[fixLocations.size()]);
   	 	locationsAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(locationsArr))); 
   	 	locationsAxis.setLabel("Locations");  
		
		//Defining the y axis 
   	 	numberOfOrderAxis.setLabel("Total in ILS");
		
		//Prepare XYChart.Series objects by setting data        
		Map<String, ArrayList<Integer>> orderData = report.getOrderReportData();
		
		XYChart.Series series1 = new XYChart.Series<>(); 
		series1.setName("remote"); 

		XYChart.Series series2 = new XYChart.Series<>(); 
		series2.setName("pickup"); 

    	// We save order number data at index 0
    	for (Map.Entry<String, ArrayList<Integer>> entry : orderData.entrySet()) {
    		String location = entry.getKey();
    		Integer pickUpNum = entry.getValue().get(4);
    		Integer remoteNum = entry.getValue().get(5);

    		series1.getData().add(new XYChart.Data<>(location, pickUpNum)); 
    		series2.getData().add(new XYChart.Data<>(location, remoteNum)); 

    	}
		ordersBarChart.getData().addAll(series1, series2);
		ordersBarChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
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
		
		topSellersBarChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
    }
}



