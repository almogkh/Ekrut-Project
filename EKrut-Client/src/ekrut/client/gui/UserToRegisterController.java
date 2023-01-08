package ekrut.client.gui;

import java.io.IOException;

import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientSessionManager;
import ekrut.entity.UserRegistration;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class UserToRegisterController extends HBox {
	
	private UserRegistration user;
	
	@FXML
	private Label emailLbl;

	@FXML
	private Button markCompletedBtn;

	@FXML
	private Label nameLbl;

	@FXML
	private Label phoneLbl;

	@FXML
	private Label subOrCustomerLbl;

	public UserToRegisterController(UserRegistration user) {
		this.user = user;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UserToRegister.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		emailLbl.setText("Email: " + user.getEmail());
		nameLbl.setText("Username: " + user.getUsername());
		phoneLbl.setText("Phone: " + user.getPhoneNumber());
		subOrCustomerLbl.setText(user.getCustomerOrSub());
	}

	@FXML
	void markCompleted(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION, "Please confirm " + user.getUsername(),
				ButtonType.YES, ButtonType.NO);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.NO)
			return;
		EKrutClient client = EKrutClientUI.getEkrutClient();
		ClientSessionManager clientSessionManager = client.getClientSessionManager();
		clientSessionManager.registerUser(user);

		Parent parent = getParent();
		ObservableList<Node> vboxChildren = ((VBox) parent).getChildren();
		vboxChildren.remove(this);
	}

}
