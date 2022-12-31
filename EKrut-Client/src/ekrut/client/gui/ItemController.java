package ekrut.client.gui;

import java.io.IOException;

import ekrut.entity.Item;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ItemController extends HBox {

	@FXML
	private Button AddBtn;

	@FXML
	private Text itemPrice;

	@FXML
	private Text itemDiscription;

	@FXML
	private HBox itemHBox;

	@FXML
	private ImageView itemImage;

	@FXML
	private Text itemName;

	@FXML
	private Button minusBtn;

	@FXML
	private Button plusBtn;

	@FXML
	private TextField quantityTxt;

	@FXML
	private Text saleType;

	@FXML
	private Label quantityInCart;

	private Integer currentQuantity = 0;
	private Integer quantityToAdd = 0;

	public ItemController() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Item.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public ItemController(Item item) {
		// Q.Nir - this.itemImage = item.getImg();
		itemName.setText(item.getItemName());
		itemDiscription.setText(item.getItemDescription());
		itemPrice.setText(Float.toString(item.getItemPrice()));
	}

	@FXML
	void addToCart(ActionEvent event) {
		int formQuantity = Integer.parseInt(quantityTxt.getText());
		currentQuantity = formQuantity;
		setQuantityTxtStyle("#FFFFFF");

	}

	@FXML
	void minusItem(ActionEvent event) {
		int formQuantity = Integer.parseInt(quantityTxt.getText());
		if (formQuantity > 0) {
			quantityTxt.setText(Integer.toString(formQuantity - 1));
			formQuantity--;
			if (currentQuantity == formQuantity)
				setQuantityTxtStyle("#FFFFFF");
			else
				setQuantityTxtStyle("#FFB4AB");
		}
	}

	@FXML
	void plusItem(ActionEvent event) {
		int formQuantity = Integer.parseInt(quantityTxt.getText());
		quantityTxt.setText(Integer.toString(formQuantity + 1));
		formQuantity++;
		if (currentQuantity == formQuantity)
			setQuantityTxtStyle("#FFFFFF");
		else
			setQuantityTxtStyle("#FFB4AB");
	}

	private void setQuantityTxtStyle(String color) {
		quantityTxt.setStyle("-fx-border-color: #000000; " + "-fx-background-radius: 20; " + "-fx-border-radius: 20; "
				+ "-fx-background-color: " + color + ";");
	};

}
