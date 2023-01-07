package ekrut.client.gui;

import java.util.ArrayList;

import ekrut.client.EKrutClientUI;
import ekrut.entity.OrderType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class OrderCreationController {

    @FXML
    private ComboBox<String> orderTypeBox;

    @FXML
    private ComboBox<String> pickupArea;

    @FXML
    private Label pickupAreaLbl;

    @FXML
    private ComboBox<String> pickupLocation;

    @FXML
    private Label pickupLocationLbl;
    
    @FXML
    private Button createOrderBtn;
    
    private String cachedTypeChoice = "";
    private String cachedAreaChoice = "";
    
    @FXML
    private void initialize() {
    	orderTypeBox.getItems().addAll("Self pickup", "Shipment");
    }

    @FXML
    void createOrder(ActionEvent event) {
    	OrderType type = cachedTypeChoice.equals("Shipment") ? OrderType.SHIPMENT : OrderType.REMOTE;
    	String param = type == OrderType.SHIPMENT ? EKrutClientUI.getEkrutClient().getClientSessionManager()
    												.getUser().getArea() : pickupLocation.getValue();
    	EKrutClientUI.getEkrutClient().getClientOrderManager().createOrder(param, type == OrderType.SHIPMENT);
    	BaseTemplateController btc = BaseTemplateController.getBaseTemplateController();
    	btc.switchStages("OrderItemBrowser");
    }

    private void resetUI() {
    	createOrderBtn.setDisable(true);
    	pickupArea.getItems().clear();
    	pickupArea.setValue(null);
    	pickupArea.setDisable(true);
    	pickupLocation.getItems().clear();
    	pickupLocation.setValue(null);
    	pickupLocation.setDisable(true);
    }
    
    @FXML
    void updateLocations(ActionEvent event) {
    	String choice = pickupArea.getValue();
    	if (choice.equals(cachedAreaChoice))
    		return;
    	cachedAreaChoice = choice;
    	ArrayList<String> locations = EKrutClientUI.getEkrutClient().getClientReportManager()
    								.getFacilitiesByArea(choice);
    	pickupLocation.getItems().setAll(locations);
    }

    @FXML
    void updateUI(ActionEvent event) {
    	String choice = orderTypeBox.getValue();
    	if (choice.equals(cachedTypeChoice))
    		return;
    	cachedTypeChoice = choice;
    	resetUI();
    	if (choice.equals("Self pickup")) {
    		pickupArea.setDisable(false);
    		pickupArea.getItems().addAll("North", "South", "UAE");
    	} else {
    		createOrderBtn.setDisable(false);
    	}
    }
    
    @FXML
    void enableButton(ActionEvent event) {
    	createOrderBtn.setDisable(false);
    }

}
