package ekrut.client.gui;

import ekrut.client.EKrutClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

/**
 * 
 * This class represents the controller for the Order Address Input view in the
 * EKrutClient application. It handles the UI elements and logic for inputting
 * and submitting the address for a remote order.
 * 
 * @author Nir Beresh
 * @author Almog Khaikin
 */
public class OrderAddressInputController {

	@FXML
	private Button ContinueBtn;

	@FXML
	private TextField addressTxt;

	@FXML
	private Button backBtn;

	// Create order after
	@FXML
	void Continue(ActionEvent event) {
		String address = addressTxt.getText();
		if (address.trim().isEmpty()) {
			new Alert(AlertType.ERROR, "You must enter address for shipment!", ButtonType.OK).showAndWait();
		} else {
			boolean validAddress = true;
			for (int i = 0; i < address.length(); i++) {
				char c = address.charAt(i);
				if (!Character.isLetterOrDigit(c) && c != '.' && c != ',' && c != ' ') {
					validAddress = false;
					break;
				}
			}
			if (!validAddress) {
				new Alert(AlertType.ERROR, "Address contains invalid characters,please try again", ButtonType.OK)
						.showAndWait();
				addressTxt.setText("");
			} else {
				EKrutClientUI.getEkrutClient().getClientOrderManager().createOrder(address, true);
				BaseTemplateController.getBaseTemplateController().switchStages("OrderItemBrowser");
			}
		}
	}

	@FXML
	void back(ActionEvent event) {
		BaseTemplateController.getBaseTemplateController().switchStages("OrderCreation");
	}

}
