package ekrut.client.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientOrderManager;
import ekrut.entity.Order;
import ekrut.entity.OrderStatus;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class ShipmentClientConfirmationController implements Initializable {

    @FXML
    private VBox ShipmentVBox;
    
    private ArrayList<Order> orders = new ArrayList<>();
    private ArrayList<Order> ordersForShipping = new ArrayList<>();
    private ClientOrderManager clientOrderManager;  
 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ArrayList<ShipmentClientConController> ordersToShow = new ArrayList<>();
		clientOrderManager = EKrutClientUI.getEkrutClient().getClientOrderManager();
		orders = clientOrderManager.fetchOrders();
		
		if (orders == null)
			return;
				
		for (Order order : orders) 
			if (order.getStatus().equals(OrderStatus.AWAITING_DELIVERY))
				ordersForShipping.add(order);
		
		// Q.Nir - is needed or this 'for loop' can handle null array??
		if (ordersForShipping == null)
			return;

		for (Order order : ordersForShipping)
			ordersToShow.add(new ShipmentClientConController(order));
		
		ObservableList<Node> children = ShipmentVBox.getChildren();
		children.addAll(ordersToShow);
		
	}

    
}
