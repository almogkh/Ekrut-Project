package ekrut.client.gui;

import java.util.ArrayList;

import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientSessionManager;
import ekrut.entity.User;
import ekrut.entity.UserRegistration;
import ekrut.entity.UserType;
import ekrut.net.FetchUserType;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert.AlertType;

/**
 * The view is used by service representatives to register new customers or subscribers
 * 
 * @author Yovel Gabay
 */
public class ServiceRepresentativeViewController {
	@FXML
	private TextField areaField;

	@FXML
	private RadioButton clientRBtn;

	@FXML
	private TextField creditCardField;

	@FXML
	private TextField emailField;

	@FXML
	private Label errorDetails;

	@FXML
	private Label errorExistRegister;

	@FXML
	private Label errorFetchLabel;

	@FXML
	private Label errorRegister;

	@FXML
	private Button fetchDataBtn;

	@FXML
	private TextField firstNameField;

	@FXML
	private TextField lastNameField;

	@FXML
	private RadioButton monthlyChrgeRBtn;

	@FXML
	private Pane paneRegister;

	@FXML
	private TextField phoneField;

	@FXML
	private Button registerBtn;

	@FXML
	private RadioButton subscriberRBtn;

	@FXML
	private TextField usernameField;

	@FXML
	private TextField idField;
	private ClientSessionManager sessionManager;
	private String username;
	private User user;
	private ArrayList<UserRegistration> registerList;

	@FXML
	private void initialize() {
		EKrutClient client = EKrutClientUI.getEkrutClient();
		this.sessionManager = client.getClientSessionManager();
		monthlyChrgeRBtn.setDisable(true);
		hideErrors();
		setText("");
		setDisable(true);

	}

	private void hideErrors() {
		errorDetails.setVisible(false);
		errorRegister.setVisible(false);
		errorExistRegister.setVisible(false);
		errorFetchLabel.setVisible(false);
	}

	private void setText(String str) {
		emailField.setText(str);
		phoneField.setText("");
		areaField.setText(str);
		firstNameField.setText(str);
		lastNameField.setText(str);
		idField.setText(str);
		creditCardField.setText(str);
	}

	private void setDisable(boolean condition) {
		emailField.setDisable(condition);
		phoneField.setDisable(condition);
		areaField.setDisable(condition);
		creditCardField.setDisable(condition);
		clientRBtn.setDisable(condition);
		subscriberRBtn.setDisable(condition);
		registerBtn.setDisable(condition);
		lastNameField.setDisable(condition);
		firstNameField.setDisable(condition);
		idField.setDisable(condition);
	}

	/*
	 * Called when the user clicks the OK button, it is used to fetch the user data
	 * based on the provided username, and display the user's details on the view
	 */
	@FXML
	void fetchData(ActionEvent event) {
		username = usernameField.getText().trim();
		user = sessionManager.fetchUser(FetchUserType.USER_NAME, username).get(0);

		if (user == null) {
			errorFetchLabel.setVisible(true);
			return;
		}

		errorFetchLabel.setVisible(false);
		setDisable(false);
		if (user.getUserType() != UserType.REGISTERED) {
			emailField.setText(user.getEmail());
			emailField.setDisable(false);
			phoneField.setText(user.getPhoneNumber());
			phoneField.setDisable(false);
			areaField.setText(user.getArea());
			areaField.setDisable(false);
			firstNameField.setText(user.getFirstName());
			firstNameField.setDisable(false);
			lastNameField.setText(user.getLastName());
			lastNameField.setDisable(false);
			idField.setText(user.getId());
			idField.setDisable(false);
		}
	}

	@FXML
	void chooseClient(ActionEvent event) {
		monthlyChrgeRBtn.setDisable(true);
		subscriberRBtn.setSelected(false);
	}

	@FXML
	void chooseSub(ActionEvent event) {
		monthlyChrgeRBtn.setDisable(false);
		clientRBtn.setSelected(false);
	}

	@FXML
	void chooseMonthlyCharge(ActionEvent event) {
		subscriberRBtn.setSelected(true);
	}

	private void registerSuccess(String username) {
		new Alert(AlertType.INFORMATION, username + " added to the registration list successfully!", ButtonType.OK)
				.showAndWait();
	}

	/*
	 * Called when the user clicks the register button, it is used to register a new
	 * user, either as a customer or subscriber, based on the information provided
	 * in the text fields.
	 */
	@FXML
	void register(ActionEvent event) {
		hideErrors();
		if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || emailField.getText().isEmpty()
				|| phoneField.getText().isEmpty() || creditCardField.getText().isEmpty()
				|| areaField.getText().isEmpty() || idField.getText().isEmpty()
				|| (!subscriberRBtn.isSelected() && !clientRBtn.isSelected())) {
			errorDetails.setVisible(true);
			return;
		}

		//Check if users exist in registration list
		registerList = sessionManager.getRegistrationList(areaField.getText());
		if (registerList != null) {
			for (UserRegistration register : registerList) {
				if (register.getUsername().equals(username)) {
					errorExistRegister.setVisible(true);
					registerBtn.setDisable(true);
					return;
				}
			}
		}

		user.setFirstName(firstNameField.getText());
		user.setLastName(lastNameField.getText());
		user.setEmail(emailField.getText());
		user.setPhoneNumber(phoneField.getText());
		user.setArea(areaField.getText());
		user.setId(idField.getText());
		UserRegistration userToRegister = new UserRegistration(user.getUsername(), creditCardField.getText(),
				phoneField.getText(), emailField.getText(), monthlyChrgeRBtn.isSelected() ? true : false,
				clientRBtn.isSelected() ? "customer" : "subscriber", areaField.getText());
		if (!sessionManager.createUserToRegister(userToRegister)) {
			errorRegister.setVisible(true);
			return;
		}

		if (user.getUserType() == UserType.REGISTERED)
			sessionManager.updateUser(user);
		registerSuccess(user.getUsername());
		initialize();
	}
}
