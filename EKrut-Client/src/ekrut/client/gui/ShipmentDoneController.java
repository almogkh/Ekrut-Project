
package ekrut.client.gui;

import java.io.IOException;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientShipmentManager;
import ekrut.entity.Order;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ShipmentDoneController extends HBox  {

    @FXML
    private Button setDoneBtn;
    
    @FXML
    private Text orderIdTxt;
    
    private Order order;
    private ClientShipmentManager clientShipmentManager;
    
    public ShipmentDoneController(Order order) {
    	this.order = order;
    	clientShipmentManager = EKrutClientUI.getEkrutClient().getClientShipmentManager();
    	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ShipmentDone.fxml"));
    	fxmlLoader.setRoot(this);
    	fxmlLoader.setController(this);
    
    	try {
    		fxmlLoader.load();
    	} catch (IOException e){
    		throw new RuntimeException(e);
    	}
    	orderIdTxt.setText(order.getOrderId().toString());
    }
    
    @FXML
    void setDone(ActionEvent event) {
    	clientShipmentManager.setDone(order);
		((VBox)getParent()).getChildren().remove(this);
    }

}
