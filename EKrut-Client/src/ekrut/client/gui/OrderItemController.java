package ekrut.client.gui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientOrderManager;
import ekrut.client.managers.ClientSalesManager;
import ekrut.entity.InventoryItem;
import ekrut.entity.Item;
import ekrut.entity.OrderItem;
import ekrut.entity.SaleDiscount;
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

public class OrderItemController extends HBox {

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

	private Item item;
	private int cartQuantity;
	private String ekrutLocation;
	private InventoryItem inventoryItem;
	private ClientOrderManager orderManager;
	private OrderBrowserController controller;
	private ClientSalesManager clientSalesManager;
	private ArrayList<SaleDiscount> sale = new ArrayList<>();
	private boolean subscriber;


		
	// Q.Nir - is OrderBrowserController controller needed?
	public OrderItemController(OrderBrowserController controller, Item item) {
		EKrutClient client = EKrutClientUI.getEkrutClient();
		orderManager = client.getClientOrderManager();
		subscriber = client.getClientSessionManager().getUser().getCustomerInfo().getSubscriberNumber() != -1;
		this.item = item;
		this.ekrutLocation = EKrutClientUI.ekrutLocation;
		this.orderManager = EKrutClientUI.getEkrutClient().getClientOrderManager();
		this.clientSalesManager = EKrutClientUI.getEkrutClient().getClientSalesManager();
		sale = clientSalesManager.fetchActiveSales(ekrutLocation);
		
		this.controller = controller;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("OrderItem.fxml"));

		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		if (item.getImg() != null)
			itemImage.setImage(new Image(new ByteArrayInputStream(item.getImg())));
		
		itemName.setText(item.getItemName());
		itemDiscription.setText(item.getItemDescription());
		itemPrice.setText(Float.toString(item.getItemPrice()));
		for (OrderItem i : orderManager.getActiveOrderItems()) {
			if (i.getItem().equals(item)) {
				cartQuantity = i.getItemQuantity();
				quantityTxt.setText(Integer.toString(i.getItemQuantity()));
				break;
			}
		}
		if (sale != null && subscriber) {
			String saleDiscountType = sale.get(0).getType().toString().equals("ONE_PLUS_ONE") ? "One Plus One" : "30% Off";
			saleType.setText(saleDiscountType);
			saleType.setVisible(true);
		}
		
	}

	public OrderItemController(OrderBrowserController controller, InventoryItem inventoryItem) {
		this(controller, inventoryItem.getItem());
		this.inventoryItem = inventoryItem;
	}

	@FXML
	void addToCart(ActionEvent event) {
		noDigitOrQuantityError.setVisible(false);
		try {
			int textQuantity = Integer.parseInt(quantityTxt.getText());
			// If its from EKrut machine
			if (ekrutLocation != null) {
				if (textQuantity > inventoryItem.getItemQuantity()) {
					setQuantityTxtStyle("#FFB4AB");
					noDigitOrQuantityError.setText("Not Enough Items!");
					noDigitOrQuantityError.setVisible(true);
				} else {
					cartQuantity = textQuantity;
					setQuantityTxtStyle("#FFFFFF");
				}
			} else {

				cartQuantity = textQuantity;
				setQuantityTxtStyle("#FFFFFF");
			}
		} catch (NumberFormatException e) {
			setQuantityTxtStyle("#FFB4AB");
			noDigitOrQuantityError.setVisible(true);
		}
		OrderItem orderItem = new OrderItem(item, cartQuantity);
		orderManager.addItemToOrder(orderItem);
		controller.updateTotalPrice();
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
				textQuantity = CheckQuantityAvilability(textQuantity);
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

	private int CheckQuantityAvilability(int textQuantity) {
		if (textQuantity + 1 > inventoryItem.getItemQuantity()) {
			noDigitOrQuantityError.setText("Not Enough Items!");
			noDigitOrQuantityError.setVisible(true);
			return textQuantity;
		} else {
			quantityTxt.setText(Integer.toString(textQuantity + 1));
			return textQuantity + 1;
		}
	}

}
