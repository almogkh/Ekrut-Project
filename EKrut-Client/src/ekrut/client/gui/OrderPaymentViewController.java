package ekrut.client.gui;

import java.util.Optional;

import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientOrderManager;
import ekrut.entity.User;
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
		EKrutClient client = EKrutClientUI.getEkrutClient();
		this.orderManager = client.getClientOrderManager();
		User user = client.getClientSessionManager().getUser();
		boolean subscriber = user.getCustomerInfo().getSubscriberNumber() != -1;
		float discount = subscriber ? orderManager.getDiscount() : 0;
		priceLbl.setText(String.format("%.2f", orderManager.getTotalPrice() - discount));
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
				BaseTemplateController.getBaseTemplateController().switchStages(
											EKrutClientUI.ekrutLocation == null
											? "OrderCreation"
											: "MainMenu");
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
	
	private void orderSuccess(int orderId) {
		new Alert(AlertType.INFORMATION, "The order was successfully placed! Your order ID is " + orderId,
				ButtonType.OK).showAndWait();
		if (EKrutClientUI.ekrutLocation == null) {
			BaseTemplateController.getBaseTemplateController().switchStages("MainMenu");
		} else {
			EKrutClientUI.getEkrutClient().getClientSessionManager().logoutUser();
			BaseTemplateController.getBaseTemplateController().logout();
		}
	}
	
	@FXML
	void confirmOrder(ActionEvent event) {
		int orderId;
		if (currentCreditCardRadioBtn.isSelected()) {
			if ((orderId = orderManager.confirmOrder(null)) != -1) {
				orderSuccess(orderId);
				return;
			}
		} else {
			if ((orderId = orderManager.confirmOrder(newCardNumberTxt.getText())) != -1) {
				orderSuccess(orderId);
				return;
			}
		}
		new Alert(AlertType.ERROR, "The order could not be placed.", ButtonType.OK).showAndWait();
	}

}
