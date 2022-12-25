package ekrut.client;

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class EKrutClientUI extends Application {

	private static EKrutClient ekrutClient;
	public static String ekrutLocation;
	
	public static void main(String[] args) {
		if (args.length == 1)
			ekrutLocation = args[0];
		launch(args);
	}
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// ekrutClient = new EKrutClient();
	}
	
	
	
	@Override
	public void stop() throws IOException {
		if (ekrutClient != null)
			// logout?
			ekrutClient.closeConnection();
	}
	
	
	
	public void popupUserNotification(String notificationMsg) {
		Alert alert = new Alert(AlertType.INFORMATION, notificationMsg, ButtonType.OK);
		alert.showAndWait();
	}

}
