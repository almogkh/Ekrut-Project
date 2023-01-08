package ekrut.client.gui;

import java.util.ArrayList;

import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientSessionManager;
import ekrut.entity.UserRegistration;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class UserRegistrationController {
	private EKrutClient client;
	private ClientSessionManager clientSessionManager;

	@FXML
	private VBox usersContainerVbox;

	@FXML
	private void initialize() {

		client = EKrutClientUI.getEkrutClient();
		clientSessionManager = client.getClientSessionManager();

		String userArea = client.getClientSessionManager().getUser().getArea();
		ArrayList<UserRegistration> registrationList = clientSessionManager.getRegistrationList(userArea);

		ObservableList<Node> registerContainerVboxChildren = usersContainerVbox.getChildren();
		for (UserRegistration user : registrationList) {
			registerContainerVboxChildren.add(new UserToRegisterController(user));
		}
	}
}
