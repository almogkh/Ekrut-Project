package ekrut.client.gui;

import java.io.IOException;

import ekrut.client.EKrutClientUI;
import ekrut.entity.InventoryItem;
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
	private Button minusBtn;

	@FXML
	private Button plusBtn;

	@FXML
	private TextField quantityTxt;

	@FXML
	private Button updateBtn;

	private BaseTemplateController BTC;
	private String ekrutLocation;
	private InventoryItem inventoryItem;
	private final static String DOTS_AFTER_ITEM_NAME = "....................................."
                                                     + ".....................................";
	
	// order manager
	private Integer cartQuantity = 0;


	public OrderItemInCartController(OrderItem orderItem) {
		this.ekrutLocation = EKrutClientUI.ekrutLocation;
		BTC = BaseTemplateController.getBaseTemplateController();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("OrderItemInCart.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		itemNameLbl.setText(orderItem.getItem().getItemName() + DOTS_AFTER_ITEM_NAME);
		itemPriceLbl.setText(Float.toString(orderItem.getItem().getItemPrice()));	
		quantityTxt.setText(Integer.toString(orderItem.getItemQuantity()));
	}


	@FXML
	void deleteItem(ActionEvent event) {
		((VBox) getParent()).getChildren().remove(this);
	}

	@FXML
	void minusItem(ActionEvent event) {
		noDigitOrQuantityError.setVisible(false);
		try {
			int textQuantity = Integer.parseInt(quantityTxt.getText());
			if (textQuantity > 0) {
				quantityTxt.setText(Integer.toString(textQuantity - 1));
				textQuantity--;
				if (cartQuantity == textQuantity)
					setQuantityTxtStyle("#FFFFFF");
				else
					setQuantityTxtStyle("#FFB4AB");
			}
		} catch (NumberFormatException e) {
			noDigitOrQuantityError.setVisible(true);
		}
	}

	@FXML
	void plusItem(ActionEvent event) {
		noDigitOrQuantityError.setVisible(false);
		try {
			int textQuantity = Integer.parseInt(quantityTxt.getText());
			if (ekrutLocation != null) {
				if (textQuantity + 1 <= inventoryItem.getItemQuantity()) {
					quantityTxt.setText(Integer.toString(textQuantity + 1));
					textQuantity++;
				} else {
					noDigitOrQuantityError.setText("Not Enough Items!");
					noDigitOrQuantityError.setVisible(true);
				}
			} else {
				quantityTxt.setText(Integer.toString(textQuantity + 1));
				textQuantity++;
			}
			if (cartQuantity == textQuantity)
				setQuantityTxtStyle("#FFFFFF");
			else
				setQuantityTxtStyle("#FFB4AB");
		} catch (NumberFormatException e) {
			noDigitOrQuantityError.setText("Invalid Input!!!");
			noDigitOrQuantityError.setVisible(true);
		}

	}

	private void setQuantityTxtStyle(String color) {
		quantityTxt.setStyle("-fx-border-color: #000000; -fx-background-radius: 20; " +
							"-fx-border-radius: 20; -fx-background-color: " + color + ";");
	}
	
	@FXML
	void updateItemQuantity(ActionEvent event) {
		noDigitOrQuantityError.setVisible(false);
		try {
			int textQuantity = Integer.parseInt(quantityTxt.getText());
			cartQuantity = textQuantity;
			setQuantityTxtStyle("#FFFFFF");
		} catch (NumberFormatException e) { 
			noDigitOrQuantityError.setVisible(true);
		}
	}
	

    @FXML
    void back(ActionEvent event) {
    	BTC.switchStages("OrderItemBrowser");
    }

    @FXML
    void cancelOrder(ActionEvent event) {
    	// popup to confirm cancelation?
    }

}
