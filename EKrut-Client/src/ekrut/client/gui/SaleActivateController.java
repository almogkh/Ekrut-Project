package ekrut.client.gui;

import java.util.ArrayList;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientSalesManager;
import ekrut.entity.SaleDiscount;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class SaleActivateController {

	@FXML
	private Button backBtn;

	@FXML
	private VBox salesVBox;

	@FXML
	private void initialize() {
		ClientSalesManager clientSaleDiscount = EKrutClientUI.getEkrutClient().getClientSalesManager();
		ArrayList<SaleDiscount> saleTemplates = clientSaleDiscount.fetchSaleTemplates();
		ArrayList<SaleDiscount> activeSales = clientSaleDiscount.fetchActiveSales();

		// Q.Nir - Is needed?
		if (saleTemplates == null)
			return;
		
		ObservableList<Node> children = salesVBox.getChildren();
		
		for (SaleDiscount sale : saleTemplates)
			children.add(new SaleToActivateController(sale, activeSales));
	}
}
