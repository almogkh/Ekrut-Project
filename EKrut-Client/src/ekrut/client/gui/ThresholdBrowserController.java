package ekrut.client.gui;

import java.util.ArrayList;
import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientInventoryManager;
import ekrut.entity.InventoryItem;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * Threshold Browser Controller, shows the thresholds of the machines in the user's assigned area. 
 * 
 * @author Ofek Malka
 */
public class ThresholdBrowserController {

    @FXML
    private VBox thresholdsVbox;

    /**
     * Fetches the ekrut locations of the current user's area and the inventory items 
     * and threshold values associated with each location.
     */
	@FXML
	private void initialize() {
		EKrutClient client = EKrutClientUI.getEkrutClient();
		ClientInventoryManager CIM = client.getClientInventoryManager();
		String currArea = client.getClientSessionManager().getUser().getArea();
		ObservableList<Node> children = thresholdsVbox.getChildren();
		
		ArrayList<String> ekrutLocationsInArea = CIM.fetchAllEkrutLocationsByArea(currArea);
		for (String ekrutLocation : ekrutLocationsInArea) {
			ArrayList<InventoryItem> inventoryItemsInEkrutLocation = CIM.fetchInventoryItemsByEkrutLocation(ekrutLocation);
			if (inventoryItemsInEkrutLocation.size() == 0) {
				children.add(new ThresholdSingleViewController(CIM, ekrutLocation, 0));
				continue;
			}
			int currThreshold = inventoryItemsInEkrutLocation.get(0).getItemThreshold();
			children.add(new ThresholdSingleViewController(CIM, ekrutLocation, currThreshold));
		}
	}
}
