package ekrut.client.gui;

import java.util.ArrayList;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientSessionManager;
import ekrut.client.managers.ClientShipmentManager;
import ekrut.entity.Order;
import ekrut.entity.OrderStatus;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * This class is used to display and handle the shipment worker approval
 * process.
 * 
 * It creates ShipmentWorkerAppController objects for each order and adding them
 * to a container.
 * 
 * @author Nir Betesh
 */
public class ShipmentWorkerApprovalController {

	@FXML
	private VBox ShipmentVBox;

	@FXML
	private void initialize() {
		ClientShipmentManager clientShipmentManager = EKrutClientUI.getEkrutClient().getClientShipmentManager();
		ClientSessionManager clientSessionManager = EKrutClientUI.getEkrutClient().getClientSessionManager();
		String area = clientSessionManager.getUser().getArea();
		ObservableList<Node> children = ShipmentVBox.getChildren();

		ArrayList<Order> orders = clientShipmentManager.fetchShipmentRequests(area);

		if (orders == null)
			return;

		for (Order order : orders)
			if (order.getStatus() == OrderStatus.SUBMITTED)
				children.add(new ShipmentWorkerAppController(order));
	}
}
