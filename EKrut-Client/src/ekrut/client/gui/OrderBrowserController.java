package ekrut.client.gui;

import java.util.ArrayList;
import java.util.Optional;

import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientInventoryManager;
import ekrut.client.managers.ClientOrderManager;
import ekrut.entity.InventoryItem;
import ekrut.entity.Item;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class OrderBrowserController {

	@FXML
	private VBox orderVBox;

	@FXML
	private Label priceLbl;

	@FXML
	private Button viewCartBtn;

	@FXML
	private Button cancelOrderBtn;

	private BaseTemplateController BTC;
	private String ekrutLocation;
	private ClientInventoryManager inventoryManager;
	private ClientOrderManager orderManager;
	private boolean subscriber;

	@FXML
	private void initialize() {
		EKrutClient client = EKrutClientUI.getEkrutClient();
		subscriber = client.getClientSessionManager().getUser().getCustomerInfo().getSubscriberNumber() != -1;
		BTC = BaseTemplateController.getBaseTemplateController();
		this.ekrutLocation = EKrutClientUI.ekrutLocation;
		inventoryManager = EKrutClientUI.getEkrutClient().getClientInventoryManager();
		orderManager = EKrutClientUI.getEkrutClient().getClientOrderManager();
		if (ekrutLocation != null)
			orderManager.createOrder();
		AddItemViewToOrderVBox();
		updateTotalPrice();
	}

	private void AddItemViewToOrderVBox() {
		if (ekrutLocation != null) {
			ArrayList<InventoryItem> itemsForSale = inventoryManager.fetchInventoryItemsByEkrutLocation(ekrutLocation);
			ArrayList<OrderItemController> inventoryItemsToAdd = new ArrayList<>();
			
			for (InventoryItem inventoryItem : itemsForSale)
				// Q.Nir - why are you use 'this'?
				inventoryItemsToAdd.add(new OrderItemController(this, inventoryItem));
			
			ObservableList<Node> children = orderVBox.getChildren();
			children.addAll(inventoryItemsToAdd);
		}
		else {
			ArrayList<Item> itemsForSale = inventoryManager.fetchAllItems();
			ArrayList<OrderItemController> itemsToAdd = new ArrayList<>();
			
			for (Item item : itemsForSale)
				itemsToAdd.add(new OrderItemController(this, item));
			
			ObservableList<Node> children = orderVBox.getChildren();
			children.addAll(itemsToAdd);
		}
	}
	
	void updateTotalPrice() {
		if (subscriber) 
			priceLbl.setText(String.format("%.2f", orderManager.getTotalPrice() - orderManager.getDiscount()));
		else
			priceLbl.setText(String.format("%.2f", orderManager.getTotalPrice()));
	}

	@FXML
	void ViewCart(ActionEvent event) {
		BTC.switchStages("OrderCartView");
		
	}

	@FXML
	void cancelOrder(ActionEvent event) {
		Optional<ButtonType> res = new Alert(AlertType.CONFIRMATION,
										"Are you sure you want to cancel the order?",
										ButtonType.YES, ButtonType.NO).showAndWait();
		res.ifPresent((btn) -> {
			if (btn == ButtonType.YES) {
				orderManager.cancelOrder();
				BTC.switchStages(ekrutLocation == null ? "OrderCreation" : "MainMenu");
			}
		});
	}

}