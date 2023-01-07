package ekrut.client.gui;

import java.util.Optional;

import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientOrderManager;
import ekrut.net.ResultType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class OrderPaymentViewController {

	@FXML
	private Button backBtn;

	@FXML
	private Button cancelOrderBtn;

	@FXML
	private Button payBtn;

	@FXML
	private ToggleGroup creditCardChoice;

	@FXML
	private RadioButton currentCreditCardRadioBtn;

	@FXML
	private RadioButton newCreditCardRadioBtn;

	@FXML
	private Label priceLbl;

	@FXML
	private TextField newCardNumberTxt;
	
	private ClientOrderManager orderManager;

	@FXML
	private void initialize() {
		this.orderManager = EKrutClientUI.getEkrutClient().getClientOrderManager();
		priceLbl.setText(String.format("%.2f", orderManager.getTotalPrice() - orderManager.getDiscount()));
	}

	@FXML
	void back(ActionEvent event) {
		BaseTemplateController.getBaseTemplateController().switchStages("OrderCartView");
	}

	@FXML
	void cancelOrder(ActionEvent event) {
		Optional<ButtonType> res = new Alert(AlertType.CONFIRMATION,
				"Are you sure you want to cancel the order?",
				ButtonType.YES, ButtonType.NO).showAndWait();
		res.ifPresent((btn) -> {
			if (btn == ButtonType.YES) {
				orderManager.cancelOrder();
				BaseTemplateController.getBaseTemplateController().switchStages("OrderCreation");
			}
		});
	}

	@FXML
	void useOldCard(ActionEvent event) {
		newCardNumberTxt.setText(null);
		newCardNumberTxt.setDisable(true);
	}

	@FXML
	void useNewCard(ActionEvent event) {
		newCardNumberTxt.setDisable(false);
	}
	
	private void orderSuccess() {
		new Alert(AlertType.INFORMATION, "The order was successfully placed!", ButtonType.OK).showAndWait();
		BaseTemplateController.getBaseTemplateController().switchStages("MainMenu");
	}
	
	@FXML
	void confirmOrder(ActionEvent event) {
		if (currentCreditCardRadioBtn.isSelected()) {
			if (orderManager.confirmOrder(null) == ResultType.OK) {
				orderSuccess();
				return;
			}
		} else {
			if (orderManager.confirmOrder(newCardNumberTxt.getText()) == ResultType.OK) {
				orderSuccess();
				return;
			}
		}
		new Alert(AlertType.ERROR, "The order could not be placed.", ButtonType.OK).showAndWait();
	}

}
