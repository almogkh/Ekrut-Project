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
    private Label ekrutLocationLbl;

    @FXML
    private Label ekrutmachineLbl;

    @FXML
    private Label errorLbl;

    @FXML
    private Label serverLbl;


	private final Color GREEN = Color.web("#23a423");
	private final Color RED = Color.web("#e13838");
	private static final String BLANK_USER_OR_PASS_ERROR = "Username or password cannot remain blank.";
	private static final String INCORRECT_USER_PASS_ERROR = "Incorrect username and password combination.";
	
	public void setServerDetails(String serverIp, String serverPort){
		if (EKrutClientUI.ekrutLocation == null) {
			ekrutLocationLbl.setVisible(false);
			ekrutmachineLbl.setVisible(false);
		}else {
			ekrutLocationLbl.setVisible(true);
			ekrutmachineLbl.setText(EKrutClientUI.ekrutLocation.replace("_", " "));
			ekrutmachineLbl.setVisible(true);
		}
		errorLbl.setVisible(false);
		serverLbl.setText("Server (" + serverIp + ":" + serverPort + "):");
	}
	
	public void setFocus(WindowEvent event) {
		usernameTxt.requestFocus();
	}
	
	private FXMLLoader loader;
	private EKrutClient ekrutClient;
	
	@FXML
	void attemptLogin(ActionEvent event) throws Exception {
		// reset red error labels
		errorLbl.setVisible(false);
		
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
		
		me = ekrutClient.getClientSessionManager().loginUser(username, password);
		
		if (me == null) {
			errorLbl.setText(INCORRECT_USER_PASS_ERROR);
			errorLbl.setVisible(true);
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
		MainMenuController mainMenuController = loader.getController();

		mainMenuController.setUser(me);

		//mainMenuController.setLabels(me.getFirstName(), me.getUserType().toString(), 
		//		EKrutClientUI.ekrutLocation);

		
		if (EKrutClientUI.ekrutLocation == null) {
			// client runs as On-Line (remote) client
		}else {
			// client runs as ekrut machine!
			
		}
		stage.getScene().setRoot(root);
	}

}
