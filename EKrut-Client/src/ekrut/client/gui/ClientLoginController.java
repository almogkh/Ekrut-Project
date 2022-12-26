package ekrut.client.gui;

import java.io.IOException;

import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.entity.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
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
	
	public void setServerDetails(String serverIp, String serverPort){
		usernameOrPasswdBlankLbl.setVisible(false);
		incorrectUserPassLbl.setVisible(false);
		serverIpPortLbl.setText("Server (" + serverIp + ":" + serverPort + "):");
		//serverConnectionStatusLbl.setText("Connected");
		//serverConnectionStatusLbl.setTextFill(GREEN);
	}
	
	public void setFocus(WindowEvent event) {
		usernameTxt.requestFocus();
	}
	
	private FXMLLoader loader;
	private EKrutClient ekrutClient;
	
	@FXML
	void attemptLogin(ActionEvent event) throws Exception {
		usernameOrPasswdBlankLbl.setVisible(false);
		incorrectUserPassLbl.setVisible(false);
		String username = usernameTxt.getText().trim();
		String password = passwordTxt.getText();
		if (username.isEmpty() || password.isEmpty()) {
			usernameOrPasswdBlankLbl.setVisible(true);
			return;
		}
		User me = null;
		
		
		if (ekrutClient == null)
			ekrutClient = EKrutClientUI.getEkrutClient();
		
		me = ekrutClient.getClientSessionManager().loginUser(username, password);
		
		System.out.println(me);
		if (me == null) {
			incorrectUserPassLbl.setVisible(true);
			return;
		}
		// LOGIN SUCCESS!
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		if (loader == null) {
			loader = new FXMLLoader(getClass().getResource("/ekrut/client/gui/MainMenu.fxml"));
			try {
				loader.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		Parent root = loader.getRoot();
		//ClientLoginController clientLoginController = loader.getController();
		//clientLoginController.setServerDetails(server, portText);
		stage.getScene().setRoot(root);
	}

}
