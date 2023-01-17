package ekrut.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientSessionManager;
import ekrut.entity.User;
import ekrut.entity.UserType;
import ekrut.net.FetchUserType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * 
 * This class represents the controller for the Login via Touch in the
 * EKrutClient application. It handles the UI elements and logic for logging in
 * as a registered subscriber of the application using touch ID.
 * 
 * @author Nir Betesh
 */
public class LoginViaTouchController {

	@FXML
	private Button loginBtn;

	@FXML
	private Label errorLbl;

	@FXML
	private ComboBox<String> usersCBox;

	private ClientLoginController clientLoginController;
	private ClientSessionManager clientSessionManager;
	private Map<String, String> usersAndPasswords = new HashMap<>();

	/**
	 * This method initializes the controller by fetching all the customer users and
	 * adding the subscribed customers' username to the combobox.
	 */
	@FXML
	public void initialize() {
		clientSessionManager = EKrutClientUI.getEkrutClient().getClientSessionManager();
		ArrayList<User> customerUsers = new ArrayList<>();
		customerUsers = clientSessionManager.fetchUser(FetchUserType.ROLE, UserType.CUSTOMER.toString());
		for (User user : customerUsers)
			if (user.getCustomerInfo().getSubscriberNumber() != -1) {
				usersAndPasswords.put(user.getUsername(), user.getPassword());
				usersCBox.getItems().add(user.getUsername());
			}
	}

	@FXML
	void LoginViaTouch(ActionEvent event) {
		String username = usersCBox.getValue();
		if (username == null)
			return;
		String password = usersAndPasswords.get(username);

		clientLoginController.usernameTxt.setText(username);
		clientLoginController.passwordTxt.setText(password);
		clientLoginController.attemptLogin(null);
		((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
	}

	/**
	 * This method sets the client login controller for the class.
	 *
	 * @param clientLoginController the client login controller to set
	 */
	public void setClientLoginController(ClientLoginController clientLoginController) {
		this.clientLoginController = clientLoginController;
	}
}
