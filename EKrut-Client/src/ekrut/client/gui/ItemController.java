package ekrut.client.gui;

import java.io.IOException;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientInventoryManager;
import ekrut.client.managers.ClientOrderManager;
import ekrut.entity.InventoryItem;
import ekrut.entity.Item;
import ekrut.entity.OrderItem;
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
	private Label noDigitOrQuantityError;

	private int itemId;
	private Item item;
	private Image image;
	private Integer cartQuantity = 0;
	private Integer itemQuantity;
	private String ekrutLocation;
	private InventoryItem inventoryItem;
	private ClientOrderManager clientOrderManager;

	public ItemController(Item item) {
		this.ekrutLocation = EKrutClientUI.ekrutLocation;
		clientOrderManager = EKrutClientUI.getEkrutClient().getClientOrderManager();
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Item.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		this.item = item;
		this.itemId = item.getItemId(); // Q.Nir - is nedded?
		// Q.Nir - this.itemImage = item.getImg(); // initialize ItemView image = new
		// Image(new ByteArrayInputStream(item.getImg()));
		itemName.setText(item.getItemName());
		itemDiscription.setText(item.getItemDescription());
		itemPrice.setText(Float.toString(item.getItemPrice()));
	}

	public ItemController(InventoryItem inventoryItem) {
		this.ekrutLocation = EKrutClientUI.ekrutLocation;

		clientOrderManager = EKrutClientUI.getEkrutClient().getClientOrderManager();
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Item.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		this.inventoryItem = inventoryItem;
		this.item = inventoryItem.getItem();
		this.itemId = item.getItemId(); // Q.Nir - is nedded?
		// Q.Nir - this.itemImage = item.getImg(); // initialize ItemView image = new
		// Image(new ByteArrayInputStream(item.getImg()));
		itemName.setText(item.getItemName());
		itemDiscription.setText(item.getItemDescription());
		itemPrice.setText(Float.toString(item.getItemPrice()));
	}

	// C.Nir - Check if quntity is available for *MACHINE*
	@FXML
	void addToCart(ActionEvent event) {
		noDigitOrQuantityError.setVisible(false);
		try {
			int textQuantity = Integer.parseInt(quantityTxt.getText());
			if (ekrutLocation != null) {
				if (textQuantity > inventoryItem.getItemQuantity()) {
					setQuantityTxtStyle("#FFB4AB");
					noDigitOrQuantityError.setText("Not Enough Items!");
					noDigitOrQuantityError.setVisible(true);					
				}
				else {
					cartQuantity = textQuantity;
					setQuantityTxtStyle("#FFFFFF");
				}
			}else {
				
			cartQuantity = textQuantity;
			setQuantityTxtStyle("#FFFFFF");
		}
		} catch (NumberFormatException e) {
			setQuantityTxtStyle("#FFB4AB");
			noDigitOrQuantityError.setVisible(true);
		}
		OrderItem OrderItem = new OrderItem(item, cartQuantity);
		clientOrderManager.addItemToOrder(OrderItem);
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
				if (textQuantity + 1 < inventoryItem.getItemQuantity()) {
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
		quantityTxt.setStyle(
				"-fx-border-color: #000000; -fx-background-radius: 20; -fx-border-radius: 20; -fx-background-color: "
						+ color + ";");
	}

	public Integer getCartQuantity() {
		return cartQuantity;
	}

}
