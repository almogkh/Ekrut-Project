package ekrut.client;

import java.io.IOException;

import ekrut.client.gui.BaseTemplateController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * The EKrutClientUI class is the main class that runs the EKrut client's GUI.
 * It extends the javafx Application class and contains methods to connect to
 * the server, launch the GUI and handle user notifications.
 * 
 * @author Ofek Malka
 */
public class EKrutClientUI extends Application {

	private static EKrutClient ekrutClient;
	public static String ekrutLocation;

	/**
	 * The main method that launches the GUI
	 * 
	 * @param args Arguments passed to the program. Only one argument is expected,
	 *             the location of the client
	 */
	public static void main(String[] args) {
		if (args.length == 1)
			ekrutLocation = args[0];
		launch(args);
	}

	/**
	 * Gets the EKrutClient object
	 * 
	 * @return the EKrutClient object
	 */
	public static EKrutClient getEkrutClient() {
		return ekrutClient;
	}

	/**
	 * Connects to the server
	 * 
	 * @param ip   the IP address of the server
	 * @param port the port number of the server
	 * @return true if the connection is successful, false otherwise
	 */
	public static boolean connectToServer(String ip, int port) {
		ekrutClient = new EKrutClient(ip, port);
		try {
			ekrutClient.openConnection();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Launches the GUI
	 * 
	 * @param primaryStage the primary stage of the GUI
	 * @throws Exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/ekrut/client/gui/BaseTemplate.fxml"));
		Parent root = loader.load();
		BaseTemplateController baseTemplateController = loader.getController();
		baseTemplateController.loadHostSelection();
		Scene scene = new Scene(root);
		primaryStage.getIcons()
				.add(new Image(EKrutClientUI.class.getResourceAsStream("/ekrut/client/gui/gui-assets/icon.png")));
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Closes the connection when the program is closed
	 * 
	 * @throws IOException
	 */
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
