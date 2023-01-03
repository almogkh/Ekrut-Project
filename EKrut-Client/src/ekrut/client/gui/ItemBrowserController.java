package ekrut.client.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientInventoryManager;
import ekrut.entity.InventoryItem;
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
    
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.ekrutLocation = EKrutClientUI.ekrutLocation;
		clientInventoryManager = EKrutClientUI.getEkrutClient().getClientInventoryManager();
		AddItemViewToOrderVBox();
	}
	
	// need to consider
	// 		1. immidiate order
	// 		2. Remote Order
	//		3. Shipment Order
    
    public void AddItemViewToOrderVBox(){
    	ArrayList<InventoryItem> itemsForSale = clientInventoryManager.getItems("Afula");
    	ArrayList<ItemController> itemsToAdd = new ArrayList<>();
    	for (InventoryItem inventoryItem : itemsForSale) 
    		itemsToAdd.add(new ItemController(inventoryItem));
    		
    	ObservableList<Node> children = orderVBox.getChildren();
    	children.addAll(itemsToAdd);
    }
    
    @FXML
    void ViewCart(ActionEvent event) {
    	
    	BTC.switchStages("CartView");
    	
    	
    	
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/ekrut/client/gui/CArtView.fxml"));
    	Parent root = BTC.load(loader);
    	//ItemBrowserController itemBrowserController = loader.getController();
    	BTC.setRightWindow(root);

    }

    @FXML
    void back(ActionEvent event) {
    	BTC.switchStages("MainMenu");
    }

    @FXML
    void cancelOrder(ActionEvent event) {
    	
    }


}

/*
FXMLLoader loader = new FXMLLoader(getClass().getResource("/ekrut/client/gui/CartView.fxml"));
Parent root = BTC.load(loader);
CustomerReportViewController customerReportViewController = loader.getController();
*/