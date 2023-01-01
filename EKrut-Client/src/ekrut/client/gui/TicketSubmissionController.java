package ekrut.client.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.entity.InventoryItem;
import ekrut.entity.User;
import ekrut.net.InventoryItemRequestType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class TicketSubmissionController implements Initializable {
    @FXML
    private ComboBox<String> facilityCombo;
    @FXML
    private ComboBox<String> itemCombo;
    @FXML
    private ComboBox<String> workerCombo;
    
    @FXML
    private Label areaPlusLocationLbl;
    @FXML
    private Label assignedForLbl;
    @FXML
    private Label ticketMsgLbl;
    @FXML
    private Button markCompletedBtn;

    @FXML
    private Button submitBtn;
	private EKrutClient client;
	private String ekrutLocation = null;
	private String itemName = null;
	private String workerUserName = null;
	private String[] items;
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		itemCombo.setDisable(true);
		workerCombo.setDisable(true);
		client = EKrutClientUI.getEkrutClient();
		User user = client.getClientSessionManager().getUser();
		String area = user.getArea();
		ArrayList<String> ekrutLocationsArrayList = client.getClientInventoryManager().fetchAllEkrutLocationsByArea(area);
		String[] ekrutLocations = ekrutLocationsArrayList.toArray(new String[0]);
		facilityCombo.getItems().addAll(ekrutLocations);
		
		// TBD OFEK: NEED YOVEL TO MAKE "FETCH ALL USERS BY ROLE" METHOD. please.
		
		
	}
    
    
    @FXML
    void facilityComboSelected(ActionEvent event) {
    	workerCombo.setValue(null);
    	itemCombo.setValue(null);
    	workerCombo.setDisable(true);
    	itemCombo.setDisable(false);
    	ekrutLocation = facilityCombo.getValue();
    	updateItemChoice();	
    }

    @FXML
    void itemComboSelected(ActionEvent event) {
    	itemName = itemCombo.getValue();
    	workerCombo.setValue("Select Worker");
    	workerCombo.setDisable(false);
    }
    
    @FXML
    void workerComboSelected(ActionEvent event) {
    	workerUserName = workerCombo.getValue();
    	
    }
    
    
    
    
    
    
    
    
    private void updateItemChoice() {
    	itemCombo.getItems().clear(); 
    	ArrayList<InventoryItem> items = client.getClientInventoryManager().getItems(ekrutLocation);
    	if (items != null)
	    	for (InventoryItem item : items)
	        	itemCombo.getItems().add(item.getItem().getItemName());
    }
    
    
    
    
    
    
    @FXML
    void submitBtnAction(ActionEvent event) {
    }


}
