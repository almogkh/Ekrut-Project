package ekrut.client.gui;

import java.util.ArrayList;
import java.util.Map;

import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientReportManager;
import ekrut.entity.InventoryReportItem;
import ekrut.entity.Report;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


public class InventoryReportViewController {
	

    @FXML
    private TableView<InventoryReportItem> TableInventory;

    @FXML
    private Label areaLbl;

    @FXML
    private Label dateLbl;

    @FXML
    private Label facilityThersholdLbl;

    @FXML
    private TableColumn<InventoryReportItem, Integer> itemIDCol;

    @FXML
    private TableColumn<InventoryReportItem, String> itemNameCol;

    @FXML
    private Label locationLbl;

    @FXML
    private TableColumn<InventoryReportItem, Integer> quantityCol;

    @FXML
    private TableColumn<InventoryReportItem, Integer> thresholdBreachesCol;

	
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
	    
	    public void setTable(){
	    	ObservableList<InventoryReportItem> mylist = FXCollections.observableArrayList();
	    	Map<String, ArrayList<Integer>> mymap = report.getInventoryReportData();
	    	Integer itemid = 444;
	    	for (Map.Entry<String, ArrayList<Integer>> entry : mymap.entrySet()) {
	    		mylist.add(new InventoryReportItem(itemid, entry.getKey(), entry.getValue().get(0), entry.getValue().get(1)));
	    	} 
	    	TableInventory.setItems(mylist);
	    	itemIDCol.setCellValueFactory(new PropertyValueFactory<>("ItemID"));
	    	itemNameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));
	    	quantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
	    	thresholdBreachesCol.setCellValueFactory(new PropertyValueFactory<>("Threshold Breaches"));
	    	
	    }
}
