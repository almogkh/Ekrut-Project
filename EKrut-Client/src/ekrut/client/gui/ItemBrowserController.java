package ekrut.client.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientInventoryManager;
import ekrut.client.managers.ClientOrderManager;
import ekrut.entity.InventoryItem;
import ekrut.entity.Order;
import ekrut.entity.OrderItem;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ItemBrowserController implements Initializable {

	@FXML
	private VBox orderVBox;

	@FXML
	private Label priceLbl;

	@FXML
	private Button viewCartBtn;

	@FXML
	private Button cancelOrderBtn;

	private BaseTemplateController BTC = BaseTemplateController.getBaseTemplateController();

	private String ekrutLocation;
	private ClientInventoryManager clientInventoryManager;
	private ClientOrderManager clientOrderManager;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.ekrutLocation = EKrutClientUI.ekrutLocation;
		clientInventoryManager = EKrutClientUI.getEkrutClient().getClientInventoryManager();
		clientOrderManager =  EKrutClientUI.getEkrutClient().getClientOrderManager();
		clientOrderManager.createOrder();
		AddItemViewToOrderVBox();
	}

	// need to consider
	// 1. immidiate order
	// 2. Remote Order
	// 3. Shipment Order

	public void AddItemViewToOrderVBox() {
		if (ekrutLocation != null) {
			ArrayList<InventoryItem> itemsForSale = clientInventoryManager.getItems(ekrutLocation);
			ArrayList<ItemController> itemsToAdd = new ArrayList<>();
			
			for (InventoryItem inventoryItem : itemsForSale)
				itemsToAdd.add(new ItemController(inventoryItem));
			
			ObservableList<Node> children = orderVBox.getChildren();
			children.addAll(itemsToAdd);
		}
		else {
			// Shipmet - get all items - wait for Ofek method
		}
		
	}

	@FXML
	void ViewCart(ActionEvent event) {
		BTC.switchStages("CartView");
	}

	@FXML
	void back(ActionEvent event) {
		BTC.switchStages("MainMenu");
	}

	@FXML
	void cancelOrder(ActionEvent event) {

	}

}