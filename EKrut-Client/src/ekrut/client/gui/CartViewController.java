package ekrut.client.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ekrut.client.EKrutClientUI;
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

public class CartViewController implements Initializable {

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

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		clientOrderManager = EKrutClientUI.getEkrutClient().getClientOrderManager();		
	}
    
    public void AddItemViewToCartVBox(){
//		ArrayList<OrderItem> itemsInCart = clientOrderManager.getActiveOrderItems();
//		ArrayList<ItemController> itemsToAdd = new ArrayList<>();
//		
//		for (OrderItem Item : itemsInCart)
//			itemsToAdd.add(new ItemController(Item));
//		
//		ObservableList<Node> children = orderVBox.getChildren();
//		children.addAll();
    	
    	
    	
    	ItemInCartController item = new ItemInCartController(null, null);
    	ObservableList<Node> children = itemCartVBox.getChildren();
        children.add(item);
    }
    
//    public VBox getItemCartVBox() {
//    	return itemCartVBox;
//    }
//    
    
    @FXML
    void agreeAndPay(ActionEvent event) {
    	BTC.switchStages("PaymentView");
    }

    @FXML
    void back(ActionEvent event) {
    	BTC.switchStages("ItemBrowser");
    }

    @FXML
    void cancelOrder(ActionEvent event) {

    }



}
