package ekrut.client.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.entity.Item;
import ekrut.entity.User;
import ekrut.entity.UserType;
import ekrut.net.FetchUserType;
import ekrut.net.ResultType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
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
	private String area;
	private String ekrutLocation;
	private String itemName;
	private String workerUserName;
	ArrayList<Item> itemsArrayList;
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
		ArrayList<User> workersArrayList = client.getClientSessionManager().fetchUser(FetchUserType.ROLE, UserType.OPERATIONS_WORKER.toString()); 
		String[] workers = new String[workersArrayList.size()];
		for (int i=0; i<workersArrayList.size(); i++)
			workers[i] = workersArrayList.get(i).getUsername();
		workerCombo.getItems().addAll(workers);
		
		// Gray out item & worker combo boxes.
		itemCombo.setDisable(true);
		workerCombo.setDisable(true);
		arrowToItem.setOpacity(0.4);
		arrowToWorker.setOpacity(0.4);
		// Set Area name in gui preview
		areaPlusLocationLbl.setText(area + ", " + FACILITY_PLACEHOLDER);
		submitBtn.setDisable(true);
	}
    
    
    @FXML
    void facilityComboSelected(ActionEvent event) {
    	submitBtn.setDisable(true);
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
    	submitBtn.setDisable(true);
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
    	submitBtn.setDisable(false);
    }
    
    private void updateItemChoice() {
    	itemCombo.getItems().clear(); 
    	itemsArrayList = client.getClientInventoryManager().fetchAllItems();
    	if (itemsArrayList != null)
	    	for (Item item : itemsArrayList)
	        	itemCombo.getItems().add(item.getItemName());
    	else
    		System.out.println("BAD!");
    }
    
    @FXML
    void submitBtnAction(ActionEvent event) {
    	if (facilityCombo.getValue() == null || 
    		itemCombo.getValue() == null || 
    		workerCombo.getValue() == null)
    		return;
    	int itemId = -1;
    	for (Item inventoryItem : itemsArrayList)
    		if (inventoryItem.getItemName().toString() == itemCombo.getValue()) {
    			itemId = inventoryItem.getItemId();
    			break;
    		}
    	if (itemId != -1) {
    		if (ResultType.OK == client.getClientTicketManager().CreateTicket(
    				facilityCombo.getValue(), itemId, workerCombo.getValue())) {
    			Alert alert = new Alert(AlertType.INFORMATION, "Ticket created", ButtonType.CLOSE);
    			alert.showAndWait();
    		} else {
    			Alert alert = new Alert(AlertType.ERROR, "Couldn't create ticket :(", ButtonType.CLOSE);
    			alert.showAndWait();
    		}
    	}
    }
}
