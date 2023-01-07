package ekrut.client.gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;


public class OrderAddressInputController {

    @FXML
    private Button ContinueBtn;

    @FXML
    private Button backBtn;


    
    @FXML
    void Continue(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/ekrut/client/gui/ItemBrowser.fxml"));
			try {
				loader.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		Parent root = loader.getRoot();
		BaseTemplateController.getBaseTemplateController().setRightWindow(root);
    }
  
    @FXML
    void back(ActionEvent event) {

    }

}
