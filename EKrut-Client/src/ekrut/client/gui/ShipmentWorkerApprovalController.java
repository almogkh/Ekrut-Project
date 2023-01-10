package ekrut.client.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientSessionManager;
import ekrut.client.managers.ClientShipmentManager;
import ekrut.entity.Order;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class ShipmentWorkerApprovalController implements Initializable {

    @FXML
    private VBox ShipmentVBox;
    
    private ArrayList<Order> ordersForShipping = new ArrayList<>();
    private ClientShipmentManager clientShipmentManager;
    private ClientSessionManager clientSessionManager;
  
 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ArrayList<ShipmentWorkerAppController> ordersToShow = new ArrayList<>();
		clientShipmentManager = EKrutClientUI.getEkrutClient().getClientShipmentManager();
		clientSessionManager = EKrutClientUI.getEkrutClient().getClientSessionManager();
		String area = clientSessionManager.getUser().getArea();
	
		ordersForShipping = clientShipmentManager.fetchShipmentRequests(area);
		
		// Q.Nir - is needed or this 'for loop' can handle null array??
		if (ordersForShipping == null)
			return;

		for (Order order : ordersForShipping) 
			ordersToShow.add(new ShipmentWorkerAppController(order));
		
		ObservableList<Node> children = ShipmentVBox.getChildren();
		children.addAll(ordersToShow);
		
	}
}
