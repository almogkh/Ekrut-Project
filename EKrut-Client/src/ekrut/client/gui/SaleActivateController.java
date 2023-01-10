package ekrut.client.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientSalesManager;
import ekrut.entity.SaleDiscount;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class SaleActivateController implements Initializable {

	@FXML
	private Button backBtn;

	@FXML
	private VBox salesVBox;

	private ClientSalesManager clientSaleDiscount;
	private ArrayList<SaleDiscount> activeSalesInThisArea = new ArrayList<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		clientSaleDiscount = EKrutClientUI.getEkrutClient().getClientSalesManager();
		ArrayList<SaleDiscount> activeSalesList = clientSaleDiscount.fetchSaleTemplates();
		ArrayList<SaleToActivateController> activeSalesToAdd = new ArrayList<>();
		activeSalesInThisArea = clientSaleDiscount.fetchActiveSales();
		
		// Q.Nir - Is needed?
		if (activeSalesList == null)
			return;
		
		for (SaleDiscount sale : activeSalesList)
			activeSalesToAdd.add(new SaleToActivateController(sale, activeSalesInThisArea));

		ObservableList<Node> children = salesVBox.getChildren();
		children.addAll(activeSalesToAdd);
	}
}
