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

public class InventoryReportViewController {
	
    @FXML
    private CategoryAxis ItemNameCategoryAxis;

    @FXML
    private NumberAxis ThersholdbreachesAxis;

    @FXML
    private Label areaLbl;

    @FXML
    private Label dateLbl;

    @FXML
    private Label facilityThersholdLbl;

    @FXML
    private BarChart<?, ?> itemThresholdBreachesBarChart;

    @FXML
    private Label locationLbl;


	private EKrutClient client = EKrutClientUI.getEkrutClient();
		ClientReportManager  clientReportManager = client.getClientReportManager();
		Report report;
	    
	    public void setInventoryReport(Report report) {
	    	this.report = report; 
	    	//Set head Labels
	    	setHeadLabels();
	    	setTable();
	    }
	    
	    // Set all the Labels in the head
	    private void setHeadLabels() {
	    	
	    	areaLbl.setText(report.getArea());
	    	locationLbl.setText(report.getEkrutLocation());
	    	String date = (String.valueOf(report.getDate().getMonthValue()) + '/' + String.valueOf(report.getDate().getYear()));
	    	dateLbl.setText(date);
	    	facilityThersholdLbl.setText(String.valueOf(report.getThreshold()));
	    }  
	    /* This class sets up a table to display inventory report data.
	     * It first retrieves a map of inventory report data from a report object.
	     * The key of the map is the name of the item and the value is an ArrayList of integers.
	     * The method creates a new ArrayList of the names of the items and converts it to an array.
	     * The x-axis of the table is set to the array of item names.*/
	    public void setTable(){
	    	Map<String, ArrayList<Integer>> InventoryReportData = report.getInventoryReportData();
	    	ArrayList<String> itemsName = new ArrayList<>();
	    	for (Map.Entry<String, ArrayList<Integer>> entry : InventoryReportData.entrySet()) {
	    		itemsName.add(entry.getKey());
	    	}
	   	 	// Convert array list into a array
	   	 	String[] itemsNameArr = itemsName.toArray(new String[itemsName.size()]);
	   	 	
	    	ItemNameCategoryAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(itemsNameArr))); 
	    	
	    	itemThresholdBreachesBarChart.setLegendVisible(false);
	    	
	    	XYChart.Series series = new XYChart.Series(); 
	    	
	    	for (Map.Entry<String, ArrayList<Integer>> entry : InventoryReportData.entrySet()) {
			//Prepare XYChart.Series objects by setting data       
				series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue().get(0))); 
	    	}
	    	
	    	itemThresholdBreachesBarChart.getData().addAll(series);
	    	
	    	itemThresholdBreachesBarChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
	    }
}
