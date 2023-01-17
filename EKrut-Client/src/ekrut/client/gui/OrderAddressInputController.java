package ekrut.client.gui;

import java.util.Optional;

import ekrut.client.EKrutClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

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
		if (address.equals(""))
			new Alert(AlertType.ERROR, "You must enter address for shipment!.", ButtonType.OK).showAndWait();
		else {
			Optional<ButtonType> res = new Alert(AlertType.CONFIRMATION,
					"Your address is: " + address + ", for continue in order press OK.", ButtonType.YES, ButtonType.NO)
							.showAndWait();
			res.ifPresent((btn) -> {
				if (btn == ButtonType.YES) {
					EKrutClientUI.getEkrutClient().getClientOrderManager().createOrder(address, true);
					BaseTemplateController.getBaseTemplateController().switchStages("OrderItemBrowser");
				}
			});
		}
	}

	@FXML
	void back(ActionEvent event) {
		BaseTemplateController.getBaseTemplateController().switchStages("OrderCreation");
	}

}
