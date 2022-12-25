package ekrut.client.gui;

import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class HostSelectionController {

    @FXML
    private Label blankLbl;

    @FXML
    private Label incorrectValLbl;

    @FXML
    private Button connectBtn;

    @FXML
    private TextField hostTxt;

    @FXML
    private TextField portTxt;

    private FXMLLoader loader;
    
    @FXML
    void connect(ActionEvent event) {
    	blankLbl.setVisible(false);
    	incorrectValLbl.setVisible(false);
    	String server = hostTxt.getText().trim();
    	String portText = portTxt.getText().trim();
    	
    	
    	
		int port;

		if (portText.isEmpty() || server.isEmpty()) {
			blankLbl.setVisible(true);
			return;
		} else {
			try {
				port = Integer.parseInt(portText);
			} catch (NumberFormatException e) {
				incorrectValLbl.setVisible(true);
				return;
			}

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			if (loader == null) {
				loader = new FXMLLoader(getClass().getResource("/gui/ClientLogin.fxml"));
				//loader.load();
			}
			Parent root = loader.getRoot();
			stage.getScene().setRoot(root); // If server was launch, change scene.
		}
    		
    	
    }

}
