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
			if (order.getStatus().equals(OrderStatus.AWAITING_DELIVERY))
				children.add(new ShipmentClientConController(order));
	}
}
