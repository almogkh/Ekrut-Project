package ekrut.client;

import java.io.IOException;

import ekrut.client.gui.BaseTemplateController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
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
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/ekrut/client/gui/BaseTemplate.fxml"));
		Parent root = loader.load();
		BaseTemplateController baseTemplateController = loader.getController();
		baseTemplateController.loadHostSelection();
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(EKrutClientUI.class.getResourceAsStream("/ekrut/client/gui/gui-assets/icon.png")));
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void stop() throws IOException {
		if (ekrutClient != null)
			ekrutClient.closeConnection();
	}

	public static void popupUserNotification(String notificationMsg) {

		Alert alert = new Alert(AlertType.INFORMATION, notificationMsg, ButtonType.OK);
		alert.showAndWait();
		// getEkrutClient().getClientSessionManager().logoutUser();

	}

}
