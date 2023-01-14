package ekrut.client.gui;

import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientOrderManager;
import ekrut.net.ResultType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class OrderPickupController {

	@FXML
	private Button getOrderBtn;

	@FXML
	private TextField orderIdTxt;

	private final static String NOT_VALID_INPUT_ERROR_MSG = "Invalid input. Please enter a valid number.";
	private final static String UNKONWUN_ERROR_MSG = "Order not found. Please try again.";
	private final static String PICKUP_SUCCESS_MSG = "Your order is on its way to you!\n"
			+ "Thank you for buying from EKrut,\n" + "Hope to see you soon!";

	private ClientOrderManager clientOrderManager;

	@FXML
	private void initialize() {
		clientOrderManager = EKrutClientUI.getEkrutClient().getClientOrderManager();
	}

	@FXML
	void getOrder(ActionEvent event) {
		int orderId;
		
		try {
			orderId = Integer.parseInt(orderIdTxt.getText());
		} catch (NumberFormatException e) {
			new Alert(AlertType.ERROR, NOT_VALID_INPUT_ERROR_MSG, ButtonType.OK).showAndWait();
			return;
		}

		if (clientOrderManager.pickupOrder(orderId) == ResultType.OK) {
			new Alert(AlertType.INFORMATION, PICKUP_SUCCESS_MSG, ButtonType.OK).showAndWait();
			return;
		}

		new Alert(AlertType.ERROR, UNKONWUN_ERROR_MSG, ButtonType.OK).showAndWait();
	}
}
