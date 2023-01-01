package ekrut.client.gui;

import java.io.IOException;

import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.entity.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    private Label errorLbl;

	private static final String BLANK_USER_OR_PASS_ERROR = "Username or password cannot remain blank.";
	private static final String INCORRECT_USER_PASS_ERROR = "Incorrect username and password combination.";
	
	public void setServerDetails(String serverIp, String serverPort){
		errorLbl.setVisible(false);
	}
	
	public void setFocus(WindowEvent event) {
		usernameTxt.requestFocus();
	}
	
	private FXMLLoader loader;
	private EKrutClient ekrutClient;
	
	@FXML
	void attemptLogin(ActionEvent event){
		// reset red error labels
		errorLbl.setVisible(false);
		// gather info from the form
		String username = usernameTxt.getText().trim();
		String password = passwordTxt.getText();
		if (username.isEmpty() || password.isEmpty()) {
			errorLbl.setText(BLANK_USER_OR_PASS_ERROR);
			errorLbl.setVisible(true);
			return;
		}
		
		User me = null;

		if (ekrutClient == null)
			ekrutClient = EKrutClientUI.getEkrutClient();
		
		try {
			me = ekrutClient.getClientSessionManager().loginUser(username, password);
		} catch (RuntimeException e1) {
			errorLbl.setText(INCORRECT_USER_PASS_ERROR);
			errorLbl.setVisible(true);
			return;
		}
		if (me == null) {
			errorLbl.setText(INCORRECT_USER_PASS_ERROR);
			errorLbl.setVisible(true);
			return;
		}
		
		// LOGIN SUCCESS!
		if (loader == null) {
			loader = new FXMLLoader(getClass().getResource("/ekrut/client/gui/MainMenu.fxml"));
			try {
				loader.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		Parent root = loader.getRoot();
		BaseTemplateController.getBaseTemplateController().showLogoutBtn();
		BaseTemplateController.getBaseTemplateController().setUser(me);
		BaseTemplateController.getBaseTemplateController().setRightWindow(root);
	}

}
