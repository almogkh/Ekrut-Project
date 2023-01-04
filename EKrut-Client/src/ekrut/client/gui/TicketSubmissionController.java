package ekrut.client.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.entity.InventoryItem;
import ekrut.entity.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class TicketSubmissionController implements Initializable {
    @FXML
    private ComboBox<String> facilityCombo;
    @FXML
    private ComboBox<String> itemCombo;
    @FXML
    private ComboBox<String> workerCombo;
    
    

    @FXML
    private ImageView arrowToItem;
    @FXML
    private ImageView arrowToWorker;
    
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
	private String[] items;
	private String area;
	private String ekrutLocation;
	private String itemName;
	private String workerUserName;
	private static final String WORKER_PLACEHOLDER = "<Worker's Name>";
	private static final String ITEM_PLACEHOLDER = "<Item Name>";
	private static final String FACILITY_PLACEHOLDER = "<Machine ID>";

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Getting the connected area manager's ekrutLocations. 
		client = EKrutClientUI.getEkrutClient();
		User user = client.getClientSessionManager().getUser();
		area = user.getArea();
		
		// Setting facility combo box with all ekrutLocations (this combo box won't change)
		ArrayList<String> ekrutLocationsArrayList = client.getClientInventoryManager().fetchAllEkrutLocationsByArea(area);
		String[] ekrutLocations = ekrutLocationsArrayList.toArray(new String[0]);
		facilityCombo.getItems().addAll(ekrutLocations);
		
		// Setting all available marketing workers.
		//ArrayList<User> workersArrayList = client.getClientSessionManager().fetchAllUsersByRole(UserType.MARKETING_WORKER); 
		//workers = new String[workersArrayList.lengh];
		//for (int i=0; i<workersArrayList.lengh; i++)
		//	workers[i] = workersArrayList.toArray(new String[0]);
		
		// Gray out item & worker combo boxes.
		itemCombo.setDisable(true);
		workerCombo.setDisable(true);
		arrowToItem.setOpacity(0.4);
		arrowToWorker.setOpacity(0.4);
		// Set Area name in gui preview
		areaPlusLocationLbl.setText(area + ", " + FACILITY_PLACEHOLDER);
	}
    
    
    @FXML
    void facilityComboSelected(ActionEvent event) {
    	workerCombo.setValue(null);
    	itemCombo.setValue(null);
    	workerCombo.setDisable(true);
    	itemCombo.setDisable(false);
    	arrowToItem.setOpacity(1);
    	arrowToWorker.setOpacity(0.4);
    	assignedForLbl.setText("Assigned for:  " + WORKER_PLACEHOLDER);
    	ticketMsgLbl.setText("Restock " + ITEM_PLACEHOLDER);
    	ekrutLocation = facilityCombo.getValue();
    	areaPlusLocationLbl.setText(area + ", " + ekrutLocation);
    	updateItemChoice();
    }

    @FXML
    void itemComboSelected(ActionEvent event) {
    	workerCombo.setValue(null);
    	workerCombo.setDisable(false);
    	arrowToWorker.setOpacity(1);
    	assignedForLbl.setText("Assigned for:  " + WORKER_PLACEHOLDER);
    	itemName = itemCombo.getValue();
    	ticketMsgLbl.setText("Restock " + itemName);
    }
    
    @FXML
    void workerComboSelected(ActionEvent event) {
    	workerUserName = workerCombo.getValue();
    	assignedForLbl.setText("Assigned for:  " + workerUserName);
    }
    
    
    
    
    
    
    
    
    
    
    private void updateItemChoice() {
    	itemCombo.getItems().clear(); 
    	ArrayList<InventoryItem> items = client.getClientInventoryManager().fetchInventoryItemsByEkrutLocation(ekrutLocation);
    	if (items != null)
	    	for (InventoryItem item : items)
	        	itemCombo.getItems().add(item.getItem().getItemName());
    }
    
    @FXML
    void submitBtnAction(ActionEvent event) {
    	System.out.println(ekrutLocation);
    	System.out.println(itemName);
    	System.out.println(workerUserName);
    	System.out.println(items);
    }
}
