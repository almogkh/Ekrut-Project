package ekrut.client.gui;

import ekrut.client.EKrutClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

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

	@FXML
	void Continue(ActionEvent event) {
		EKrutClientUI.getEkrutClient().getClientOrderManager().createOrder(addressTxt.getText(), true);
		BaseTemplateController.getBaseTemplateController().switchStages("OrderItemBrowser");
	}

	@FXML
	void back(ActionEvent event) {
		BaseTemplateController.getBaseTemplateController().switchStages("OrderCreation");
	}

}
