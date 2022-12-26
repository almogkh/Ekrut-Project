package ekrut.client.gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;

public class ClientLoginController {
	
	@FXML
	private TextField usernameTxt;
	
	@FXML
	private TextField passwordTxt;
	
	@FXML
	private Button loginBtn;
	
	@FXML
	private Label incorrectUserPassLbl;
	
	@FXML
	private Label usernameOrPasswdBlankLbl;
	
	@FXML
	private Label serverIpPortLbl;
	
	@FXML
	private Label serverConnectionStatusLbl;
	
	private final Color GREEN = Color.web("#23a423");
	private final Color RED = Color.web("#e13838");
	
	public void setServerDetails(String serverIp, String serverPort, boolean connectionStatus){
		usernameOrPasswdBlankLbl.setVisible(false);
		incorrectUserPassLbl.setVisible(false);
		serverIpPortLbl.setText("Server (" + serverIp + ":" + serverPort + "):");
		serverConnectionStatusLbl.setText(connectionStatus ? "Connected" : "Disconnected");
		serverConnectionStatusLbl.setTextFill(connectionStatus ? GREEN : RED);
	}
	
	public void setFocus(WindowEvent event) {
		usernameTxt.requestFocus();
	}
	
	@FXML
	void attemptLogin(ActionEvent event) throws IOException {
		usernameOrPasswdBlankLbl.setVisible(false);
		incorrectUserPassLbl.setVisible(false);
		String username = usernameTxt.getText().trim();
		String password = passwordTxt.getText();
		if (username.isEmpty() || password.isEmpty()) {
			usernameOrPasswdBlankLbl.setVisible(true);
			return;
		}
		
		if (username.equals(password)) { // TBD IMPLEMENT REAL USERNAME & PASSWORD CHECKING
			incorrectUserPassLbl.setVisible(true);
			return;
		}
		// LOGIN SUCCESS!
	}

}
