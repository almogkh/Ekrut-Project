package ekrut.client.gui;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientInventoryManager;
import ekrut.client.managers.ClientOrderManager;
import ekrut.entity.InventoryItem;
import ekrut.entity.OrderItem;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class OrderCartViewController implements Initializable {

    @FXML
    private Button agreeAndPayBtn;
 
    @FXML
    private Button backBtn;

    @FXML
    private Button cancelOrderBtn;

    @FXML
    public VBox itemCartVBox;

    @FXML
    private Label priceAfterDiscountLbl;

    @FXML
    private Label priceBeforeDiscountLbl;

    private BaseTemplateController BTC = BaseTemplateController.getBaseTemplateController();
	private ClientOrderManager clientOrderManager;
	private ClientInventoryManager clientInventoryManager;
	private ArrayList<InventoryItem> inventoryItems;


	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		clientInventoryManager = EKrutClientUI.getEkrutClient().getClientInventoryManager();
		clientOrderManager = EKrutClientUI.getEkrutClient().getClientOrderManager();
		AddItemViewToCartVBox();
	}
    
    public void AddItemViewToCartVBox(){
		ArrayList<OrderItem> itemsInCart = clientOrderManager.getActiveOrderItems();
		ArrayList<OrderItemInCartController> itemsToAdd = new ArrayList<>();
		
		for (OrderItem orderItem : itemsInCart)
			itemsToAdd.add(new OrderItemInCartController(orderItem));
		
		ObservableList<Node> children = itemCartVBox.getChildren();
		children.addAll(itemsToAdd);
    	
    }
    
    @FXML
    void agreeAndPay(ActionEvent event) {
    	BTC.switchStages("OrderPaymentView");
    }

    @FXML
    void back(ActionEvent event) {
    	BTC.switchStages("OrderItemBrowser");
    }

    @FXML
    void cancelOrder(ActionEvent event) {

    }



}
