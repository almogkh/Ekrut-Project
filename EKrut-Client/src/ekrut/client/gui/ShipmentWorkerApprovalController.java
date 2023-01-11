package ekrut.client.gui;

import java.util.ArrayList;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientSessionManager;
import ekrut.client.managers.ClientShipmentManager;
import ekrut.entity.Order;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class ShipmentWorkerApprovalController {

	@FXML
	private VBox ShipmentVBox;

	@FXML
	private void initialize() {
		ClientShipmentManager clientShipmentManager = EKrutClientUI.getEkrutClient().getClientShipmentManager();
		ClientSessionManager clientSessionManager = EKrutClientUI.getEkrutClient().getClientSessionManager();
		String area = clientSessionManager.getUser().getArea();
		ObservableList<Node> children = ShipmentVBox.getChildren();

		ArrayList<Order> ordersForShipping = clientShipmentManager.fetchShipmentRequests(area);

		if (ordersForShipping == null)
			return;

		for (Order order : ordersForShipping)
			children.add(new ShipmentWorkerAppController(order));
	}
}
