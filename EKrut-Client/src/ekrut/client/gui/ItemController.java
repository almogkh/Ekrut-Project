package ekrut.client.gui;

import java.io.IOException;
import ekrut.entity.Item;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ItemController extends HBox {

	@FXML
	private Button AddBtn;

	@FXML
	private Text itemPrice;

	@FXML
	private Text itemDiscription;

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

	@FXML
	private Label noDigitError;

	private Integer cartQuantity = 0;

	Image image;

	public ItemController(Item item) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Item.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		/*
		  // Q.Nir - this.itemImage = item.getImg(); // initialize ItemView image = new
		  Image(new ByteArrayInputStream(item.getImg()));
		  itemName.setText(item.getItemName());
		  itemDiscription.setText(item.getItemDescription());
		  itemPrice.setText(Float.toString(item.getItemPrice()));
		 */
	}

	@FXML
	void addToCart(ActionEvent event) {
		noDigitError.setVisible(false);
		try {
			int textQuantity = Integer.parseInt(quantityTxt.getText());
			cartQuantity = textQuantity;
			setQuantityTxtStyle("#FFFFFF");
		} catch (NumberFormatException e) { 
			noDigitError.setVisible(true);
		}
	}

	@FXML
	void minusItem(ActionEvent event) {
		noDigitError.setVisible(false);
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
			noDigitError.setVisible(true);
		}
	}

	@FXML
	void plusItem(ActionEvent event) {
		noDigitError.setVisible(false);
		try {
			int textQuantity = Integer.parseInt(quantityTxt.getText());
			quantityTxt.setText(Integer.toString(textQuantity + 1));
			textQuantity++;
			if (cartQuantity == textQuantity)
				setQuantityTxtStyle("#FFFFFF");
			else
				setQuantityTxtStyle("#FFB4AB");
		} catch (NumberFormatException e) { 
			noDigitError.setVisible(true);
		}

	}

	private void setQuantityTxtStyle(String color) {
		quantityTxt.setStyle(
				"-fx-border-color: #000000; -fx-background-radius: 20; -fx-border-radius: 20; -fx-background-color: "
						+ color + ";");
	}

	public Integer getCartQuantity() {
		return cartQuantity;
	}

}
