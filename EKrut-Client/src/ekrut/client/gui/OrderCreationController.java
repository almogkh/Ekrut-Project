package ekrut.client.gui;

import java.util.ArrayList;

import ekrut.client.EKrutClientUI;
import ekrut.entity.OrderType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

/**
 * 
 * OrderCreationController is a class that handles the creation of a new order.
 * It allows the user to select the order type (self pickup or shipment), the
 * pickup area, and the pickup location.
 * 
 * @author Nir Beresh
 * @author Almog Khaikin
 */
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
		BaseTemplateController btc = BaseTemplateController.getBaseTemplateController();
		if (type == OrderType.REMOTE) {
			EKrutClientUI.getEkrutClient().getClientOrderManager().createOrder(pickupLocation.getValue(), false);
			btc.switchStages("OrderItemBrowser");
		} else {
			btc.switchStages("OrderAddressInput");
		}
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
		if (choice == null || choice.equals(cachedAreaChoice))
			return;
		cachedAreaChoice = choice;
		ArrayList<String> locations = EKrutClientUI.getEkrutClient().getClientReportManager()
				.getFacilitiesByArea(choice);
		pickupLocation.getItems().setAll(locations);
		pickupLocation.setDisable(false);
	}

	@FXML
	void updateUI(ActionEvent event) {
		String choice = orderTypeBox.getValue();
		if (choice.equals(cachedTypeChoice))
			return;
		cachedTypeChoice = choice;
		resetUI();
		if (choice.equals("Self pickup")) {
			createOrderBtn.setText("Create order");
			pickupArea.setDisable(false);
			pickupArea.getItems().addAll("North", "South", "UAE");
		} else {
			createOrderBtn.setText("Choose shipping address");
			createOrderBtn.setDisable(false);
		}
	}

	@FXML
	void enableButton(ActionEvent event) {
		createOrderBtn.setDisable(false);
	}

}
