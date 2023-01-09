package ekrut.client.gui;

import ekrut.client.EKrutClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class OrderAddressInputController {

    @FXML
    private Button ContinueBtn;

    @FXML
    private TextField addressTxt;

    @FXML
    private Button backBtn;

    @FXML
    void Continue(ActionEvent event) {
    	EKrutClientUI.getEkrutClient().getClientOrderManager().createOrder(addressTxt.getText(), true);
		BaseTemplateController.getBaseTemplateController().switchStages("OrderItemBrowser");
    }

    @FXML
    void back(ActionEvent event) {
    	BaseTemplateController.getBaseTemplateController().switchStages("OrderCreation");
    }

}
