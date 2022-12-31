package ekrut.client.gui;

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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ObservableList<PieChart.Data> PieChartData =
				FXCollections.observableArrayList(
						new PieChart.Data("Haifa", 15),
						new PieChart.Data("Nahariya", 25),
						new PieChart.Data("Akko", 20),
						new PieChart.Data("Nazereth", 30),
						new PieChart.Data("Afula", 10));
		
		ordersPieChart.setData(PieChartData);
		ordersPieChart.setTitle("pie orders");
		
		//Defining the x axis               
		CategoryAxis xAxis = new CategoryAxis();   
		        
		xAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(
		   "Haifa", "Akko", "Nahariya"))); 
		xAxis.setLabel("Order Type");  

		//Defining the y axis 
		NumberAxis yAxis = new NumberAxis(); 
		yAxis.setLabel("Total in ILS");
		
		//OrdersHistogram = new BarChart<>(xAxis, yAxis);  
		ordersPieChart.setTitle("Orders total sum sepretad into types"); 
		
		
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


		topSellersBarChart.getData().addAll(series11);
	}
}


