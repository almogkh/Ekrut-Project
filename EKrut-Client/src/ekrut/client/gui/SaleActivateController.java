package ekrut.client.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientSalesManager;
import ekrut.entity.SaleDiscount;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
    private String ekrutLocation;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	this.ekrutLocation = EKrutClientUI.ekrutLocation;
    	clientSaleDiscount  = EKrutClientUI.getEkrutClient().getClientSalesManager();
    	ArrayList<SaleDiscount> activeSalesList = clientSaleDiscount.fetchSaleTemplates();
    	ArrayList<SaleToActivateController> activeSalesToAdd = new ArrayList<>();
    	
    	for (SaleDiscount sale : activeSalesList) 
    		activeSalesToAdd.add(new SaleToActivateController(sale));
    	
    	ObservableList<Node> children = salesVBox.getChildren();
    	children.addAll(activeSalesToAdd);

    	
    }
    


    @FXML
    void backToMainMenu(ActionEvent event) {

    }




    
}
