package ekrut.client.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientUI_test extends Application {

	public static void main(String[] args) { launch(args); }

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Load first screen
		FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientLogin.fxml"));
		Parent root = loader.load();
		ClientLoginController controller = loader.getController();
		Scene scene = new Scene(root);

		primaryStage.setTitle("EKrut Client");
		primaryStage.setScene(scene);
		primaryStage.setOnShown(controller::setFocus);
		controller.setServerDetails("192.168.88.84", "5555", true);

		primaryStage.show();
	}
	
	@Override
	public void stop() throws IOException {
	}

}
