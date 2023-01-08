package ekrut.client.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientSessionManager;
import ekrut.entity.UserRegistration;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class UserRegistrationController implements Initializable {
	private EKrutClient client;
	private ClientSessionManager clientSessionManager;

	@FXML
	private VBox usersContainerVbox;

	public void initialize(URL location, ResourceBundle resources) {

		client = EKrutClientUI.getEkrutClient();
		clientSessionManager = client.getClientSessionManager();

		String userArea = client.getClientSessionManager().getUser().getArea();
		ArrayList<UserRegistration> registraionList = clientSessionManager.getRegistrationList(userArea)
				.getUserRegistrationList();

		ObservableList<Node> registerContainerVboxChildren = usersContainerVbox.getChildren();
		for (UserRegistration user : registraionList) {
			registerContainerVboxChildren.add(new UserToRegisterController(user));
		}
	}
}
