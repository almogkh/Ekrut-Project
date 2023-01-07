package ekrut.client.gui;

import java.io.IOException;

import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientOrderManager;
import ekrut.entity.OrderItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
		orderManager.removeItemFromOrder(orderItem.getItem());
		controller.updatePrice();
		((VBox) getParent()).getChildren().remove(this);
	}

    @FXML
    void back(ActionEvent event) {
    	BTC.switchStages("OrderItemBrowser");
    }
}
