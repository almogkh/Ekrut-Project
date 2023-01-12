package ekrut.client.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientSessionManager;
import ekrut.client.managers.ClientShipmentManager;
import ekrut.entity.Order;
import ekrut.entity.OrderStatus;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class ShipmentWorkerApprovalController implements Initializable {

    @FXML
    private VBox ShipmentVBox;
    
    private ClientShipmentManager clientShipmentManager;
    private ClientSessionManager clientSessionManager;
  
 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ArrayList<Order> ordersForShipping = new ArrayList<>();
		ArrayList<Order> orders = new ArrayList<>();
		ArrayList<ShipmentWorkerAppController> ordersToShow = new ArrayList<>();
		
		clientShipmentManager = EKrutClientUI.getEkrutClient().getClientShipmentManager();
		clientSessionManager = EKrutClientUI.getEkrutClient().getClientSessionManager();
		String area = clientSessionManager.getUser().getArea();
	
		orders = clientShipmentManager.fetchShipmentRequests(area);
		
		// Q.Nir - is needed or this 'for loop' can handle null array??
		if (orders.isEmpty())
			return;
		
		for (Order order : orders) 
			if (order.getStatus().equals(OrderStatus.SUBMITTED))
				ordersForShipping.add(order);
		
		// Q.Nir - is needed or this 'for loop' can handle null array??
		if (ordersForShipping.isEmpty())
			return;
		
		
		for (Order order : ordersForShipping) 
			ordersToShow.add(new ShipmentWorkerAppController(order));
		
		ObservableList<Node> children = ShipmentVBox.getChildren();
		children.addAll(ordersToShow);
		
	}
}
