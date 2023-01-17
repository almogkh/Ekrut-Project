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

	/**
	 * The OrderPickupController class is responsible for handling the interaction
	 * with the user when they are trying to pickup an order. It receives an order
	 * ID input from the user, and sends it to the ClientOrderManager for
	 * processing. Depending on the result of the processing, it displays an
	 * appropriate message to the user.
	 * 
	 * @author Nir Betesh
	 * @author Almog Khaikin
	 */
	@FXML
	private Button getOrderBtn;

	@FXML
	private TextField orderIdTxt;

	private final static String NOT_VALID_INPUT_ERROR_MSG = "Invalid input. Please enter a valid number.";
	private final static String UNKONWUN_ERROR_MSG = "Order ID  was not found. Please try again.\n"
			+ "If you dont have an order ID from any reason,\n" + "please call our customer service.\n"
			+ "Have a good day!\n" + "EKrut.";
	private final static String PICKUP_SUCCESS_MSG = "Your order is on its way to you!\n"
			+ "Thank you for buying from EKrut,\n" + "Hope to see you soon!";
	private final static String PICKUP_DENIED = "This order has already been picked-up";

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

		ResultType resultType = clientOrderManager.pickupOrder(orderId);
		if (resultType == ResultType.OK) {
			new Alert(AlertType.INFORMATION, PICKUP_SUCCESS_MSG, ButtonType.OK).showAndWait();
			return;
		}

		if (resultType == ResultType.PERMISSION_DENIED) {
			new Alert(AlertType.INFORMATION, PICKUP_DENIED, ButtonType.OK).showAndWait();
			return;
		}

		new Alert(AlertType.ERROR, UNKONWUN_ERROR_MSG, ButtonType.OK).showAndWait();
	}
}
