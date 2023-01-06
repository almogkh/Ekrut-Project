package ekrut.client.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientInventoryManager;
import ekrut.client.managers.ClientOrderManager;
import ekrut.entity.InventoryItem;
import ekrut.entity.Item;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class OrderBrowserController implements Initializable {

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
	private ClientInventoryManager clientInventoryManager;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		BTC = BaseTemplateController.getBaseTemplateController();
		this.ekrutLocation = EKrutClientUI.ekrutLocation;
		clientInventoryManager = EKrutClientUI.getEkrutClient().getClientInventoryManager();
		AddItemViewToOrderVBox();
	}

	// need to consider
	// 1. immidiate order
	// 2. Remote Order
	// 3. Shipment Order

	public void AddItemViewToOrderVBox() {
		if (ekrutLocation != null) {
			ArrayList<InventoryItem> itemsForSale = clientInventoryManager.fetchInventoryItemsByEkrutLocation(ekrutLocation);
			ArrayList<OrderItemController> inventoryItemsToAdd = new ArrayList<>();
			
			for (InventoryItem inventoryItem : itemsForSale)
				inventoryItemsToAdd.add(new OrderItemController(inventoryItem));
			
			ObservableList<Node> children = orderVBox.getChildren();
			children.addAll(inventoryItemsToAdd);
		}
		else {
			ArrayList<Item> itemsForSale = clientInventoryManager.fetchAllItems();
			ArrayList<OrderItemController> itemsToAdd = new ArrayList<>();
			
			for (Item item : itemsForSale)
				itemsToAdd.add(new OrderItemController(item));
			
			ObservableList<Node> children = orderVBox.getChildren();
			children.addAll(itemsToAdd);
		}
	}

	@FXML
	void ViewCart(ActionEvent event) {
		BTC.switchStages("OrderCartView");
		
	}

	@FXML
	void back(ActionEvent event) {
		BTC.switchStages("MainMenu");
	}

	@FXML
	void cancelOrder(ActionEvent event) {

	}

}