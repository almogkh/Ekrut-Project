package gui;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class OrderReportViewController implements Initializable{

    @FXML
    private BarChart<?, ?> OrdersHistogram;

    @FXML
    private PieChart OrdersPieChart;

    @FXML
    private Pane TopSellersPane;

    @FXML
    private Pane TotalOrdersInILSPane;

    @FXML
    private Pane TotalOrdersPane;

    @FXML
    private Label serverConnectionStatusLbl;

    @FXML
    private Label serverIpPortLbl;

    @FXML
    private Label totalOrdersInILSLabel;

    @FXML
    private Label totalOrdersLabel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ObservableList<PieChart.Data> PieChartData =
				FXCollections.observableArrayList(
						new PieChart.Data("Haifa", 15),
						new PieChart.Data("Nahariya", 25),
						new PieChart.Data("Akko", 20),
						new PieChart.Data("Nazereth", 30),
						new PieChart.Data("Afula", 10));
		
		OrdersPieChart.setData(PieChartData);
		OrdersPieChart.setTitle("pie orders");
		
		//Defining the x axis               
		CategoryAxis xAxis = new CategoryAxis();   
		        
		xAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(
		   "Haifa", "Akko", "Nahariya"))); 
		xAxis.setLabel("Order Type");  

		//Defining the y axis 
		NumberAxis yAxis = new NumberAxis(); 
		yAxis.setLabel("Total in ILS");
		
		//OrdersHistogram = new BarChart<>(xAxis, yAxis);  
		OrdersHistogram.setTitle("Orders total sum sepretad into types"); 
		
		
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

		OrdersHistogram.getData().addAll(series1, series2, series3);
	}

}
