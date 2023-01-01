package ekrut.client.gui;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ItemBrowserController {

	@FXML
	private VBox orderVBox;

	@FXML
	private Label priceLbl;

	@FXML
	private Button checkOutBtn;

    @FXML
    private Button viewCartBtn;
    
    @FXML
    private Button cancelOrderBtn;
    
    public void AddItemViewToOrderVBox(){
    	ItemController item = new ItemController(null);
    	ObservableList<Node> children = orderVBox.getChildren();
        children.add(item);
    }
    
    @FXML
    void cancelOrder(ActionEvent event) {

    }
    
    @FXML
    void back(ActionEvent event) {

    }
}
