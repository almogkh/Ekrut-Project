package ekrut.client.gui;

import java.io.IOException;
import java.util.Optional;

import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientOrderManager;
import ekrut.entity.OrderItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * 
 * The OrderItemInCartController class represents an OrderItem in the Order Cart
 * View. It provides the user with the ability to delete the OrderItem, update
 * its quantity, and display the item's name and price.
 * 
 * @author Nir Betesh
 * @author Almog Khaikin
 */
public class OrderItemInCartController extends HBox {

	@FXML
	private HBox itemInCartHBox;

	@FXML
	private Button deleteItemBtn;

	@FXML
	private Label itemNameLbl;

	@FXML
	private Label itemPriceLbl;

	@FXML
	private Label noDigitOrQuantityError;

	@FXML
	private TextField quantityTxt;

	@FXML
	private Button updateBtn;

	private BaseTemplateController BTC;
	private OrderCartViewController controller;
	private ClientOrderManager orderManager;
	private OrderItem orderItem;

	/**
	 * This constructor creates an instance of the class and sets the necessary data
	 * for it to function properly.
	 * 
	 * @param controller The OrderCartViewController that this
	 *                   OrderItemInCartController belongs to.
	 * 
	 * @param orderItem  The OrderItem that this OrderItemInCartController
	 *                   represents.
	 */
	public OrderItemInCartController(OrderCartViewController controller, OrderItem orderItem) {
		BTC = BaseTemplateController.getBaseTemplateController();
		this.controller = controller;
		this.orderManager = EKrutClientUI.getEkrutClient().getClientOrderManager();
		this.orderItem = orderItem;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("OrderItemInCart.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		StringBuilder itemName = new StringBuilder(orderItem.getItem().getItemName());
		int length = 96 - itemName.length();
		for (int i = 0; i < length; i++)
			itemName.append('.');
		itemNameLbl.setText(itemName.toString());
		itemPriceLbl.setText(Float.toString(orderItem.getItem().getItemPrice()));
		quantityTxt.setText(Integer.toString(orderItem.getItemQuantity()));
	}

	@FXML
	void deleteItem(ActionEvent event) {
		Optional<ButtonType> res = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete this item?",
				ButtonType.YES, ButtonType.NO).showAndWait();
		res.ifPresent((btn) -> {
			if (btn == ButtonType.YES) {
				orderManager.removeItemFromOrder(orderItem.getItem());
				controller.updatePrice();
				((VBox) getParent()).getChildren().remove(this);
			}
		});

	}

	@FXML
	void back(ActionEvent event) {
		BTC.switchStages("OrderItemBrowser");
	}
}
