package ekrut.client.gui;

import java.util.ArrayList;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientOrderManager;
import ekrut.entity.Order;
import ekrut.entity.OrderStatus;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * This class is used to display a list of orders that are awaiting delivery
 * confirmation from the client.
 * 
 * It creates ShipmentClientConController objects for each order and adds them
 * to a container.
 * 
 * @author Nir Betesh
 */
public class ShipmentClientConfirmationController {

	@FXML
	private VBox ShipmentVBox;

	@FXML
	private void initialize() {
		ClientOrderManager clientOrderManager = EKrutClientUI.getEkrutClient().getClientOrderManager();
		ArrayList<Order> orders = clientOrderManager.fetchOrders();
		ObservableList<Node> children = ShipmentVBox.getChildren();

		if (orders == null)
			return;

		for (Order order : orders)
			if (order.getStatus() == OrderStatus.AWAITING_DELIVERY)
				children.add(new ShipmentClientConController(order));
	}
}
