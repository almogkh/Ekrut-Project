package ekrut.client.gui;

import ekrut.client.EKrutClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class HostSelectionController {

    @FXML
    private Button connectBtn;

    @FXML
    private TextField serverTxt;

    @FXML
    private TextField portTxt;

    @FXML
    private Label redErrorLbl;

    private static final String INVALID_PORT_ERROR = "Port or IP values are incorrent.";
    private static final String BLANK_VAL_ERROR = "Server or Port cannot remain blank";
    private static final String CANNOT_CONNECT_ERROR = "Unable to connect to server.";
    
    @FXML
    void connect(ActionEvent event) {
    	redErrorLbl.setVisible(false);
    	String server = serverTxt.getText().trim();
    	String portText = portTxt.getText().trim();
		int port;
		if (portText.isEmpty() || server.isEmpty()) {
			redErrorLbl.setText(BLANK_VAL_ERROR);
			redErrorLbl.setVisible(true);
			return;
		} else {
			try {
				port = Integer.parseInt(portText);
			} catch (NumberFormatException e) {
				redErrorLbl.setText(INVALID_PORT_ERROR);
				redErrorLbl.setVisible(true);
				return;
			}
			
			if (!EKrutClientUI.connectToServer(server, port)) {
				redErrorLbl.setText(CANNOT_CONNECT_ERROR);
				redErrorLbl.setVisible(true);
				return;
			}		
			BaseTemplateController.getBaseTemplateController().openLoginStage();
		}
    }
}
