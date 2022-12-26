package ekrut.client;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
	
	
	
	public static EKrutClient getEkrutClient() {
		return ekrutClient;
	}



	public static boolean connectToServer(String ip, int port) {
		ekrutClient = new EKrutClient(ip, port);
		try {
			ekrutClient.openConnection();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/ekrut/client/gui/HostSelection.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	
	
	@Override
	public void stop() throws IOException {
		if (ekrutClient != null)
			ekrutClient.closeConnection();
	}
	
	
	
	public void popupUserNotification(String notificationMsg) {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.INFORMATION, notificationMsg, ButtonType.OK);
			alert.showAndWait();
		});
	}

}
