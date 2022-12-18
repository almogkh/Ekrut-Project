package gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
		
	public void setFocus(WindowEvent event) {
		//dbPasswordTxt.requestFocus();
	}
	
	
	@FXML
	void attemptLogin(ActionEvent event) throws IOException {
		usernameOrPasswdBlankLbl.setVisible(false);
		incorrectUserPassLbl.setVisible(false);
		String username = usernameTxt.getText().trim();
		String password = passwordTxt.getText().trim(); // to trim or not to trim?
		if (username.isEmpty() || password.isEmpty()) {
			usernameOrPasswdBlankLbl.setVisible(true);
			return;
		}
		
		
		
	}

}
