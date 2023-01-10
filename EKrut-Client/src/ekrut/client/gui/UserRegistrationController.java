package ekrut.client.gui;

import java.util.ArrayList;

import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientSessionManager;
import ekrut.entity.UserRegistration;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class UserRegistrationController {
	private EKrutClient client;
	private ClientSessionManager clientSessionManager;

	@FXML
	private VBox usersContainerVbox;
	

    @FXML
    private Label nullRegistrationList;

	@FXML
	private void initialize() {

		client = EKrutClientUI.getEkrutClient();
		clientSessionManager = client.getClientSessionManager();
		nullRegistrationList.setVisible(false);
		String userArea = client.getClientSessionManager().getUser().getArea();
		ArrayList<UserRegistration> registrationList = clientSessionManager.getRegistrationList(userArea);
		if (registrationList==null)
			nullRegistrationList.setVisible(true);
		else {
			ObservableList<Node> registerContainerVboxChildren = usersContainerVbox.getChildren();
		for (UserRegistration user : registrationList) {
			registerContainerVboxChildren.add(new UserToRegisterController(user));
		}
		}
		
	}
}
