package ekrut.client.gui;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CartViewController {

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
    
    public void AddItemViewToCartVBox(){
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
