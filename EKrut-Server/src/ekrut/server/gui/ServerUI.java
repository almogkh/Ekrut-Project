package ekrut.server.gui;

import java.io.IOException;

import ekrut.server.EKrutServer;
import ekrut.server.TimeScheduler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ServerUI extends Application {

	private static EKrutServer server;
	private static ServerController controller;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(("/ekrut/server/gui/Server.fxml")));

		Parent root = loader.load();
		controller = loader.getController();

		Scene scene = new Scene(root);
		primaryStage.getIcons()
				.add(new Image(ServerUI.class.getResourceAsStream("/ekrut/server/gui/gui-assets/icon-server.png")));
		primaryStage.setTitle("Server");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void stop() throws IOException {
		disconnect();
	}

	public static boolean runServer(int port, String DBuserName, String username, String password) {
		server = new EKrutServer(port);
		server.init(DBuserName, username, password);
		controller.setTable(server.getSession());
		try {
			TimeScheduler.startTimer();
			if (!server.connect()) {
				System.out.println("Can't connect to DB");
				return false;
			}

			server.listen();
			System.out.println("Server listening for connections on port " + server.getPort());

			return true;
		} catch (IOException e) {
			System.out.println("ERROR - Could not listen for clients!");
			return false;
		}
	}
	public static EKrutServer getServer() {
		return server;
	}

	
	public static void disconnect() {
		TimeScheduler.stopTimer();
		if (server == null)
			return;
		if (server.isListening()) {
			server.stopListening();
		}
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		server = null;
		System.out.println("Server Disconnected");
	}
}
