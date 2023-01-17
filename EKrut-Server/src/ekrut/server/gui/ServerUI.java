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

/**
 * The ServerUI class is the main class that runs the EKrut server's GUI.
 * 
 * @author Yovel Gabay
 */

public class ServerUI extends Application {

	private static EKrutServer server;
	private static ServerController controller;

	/**
	 * The main method that launches the server GUI.
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * This method is called when the application starts. It loads the server GUI's
	 * layout and sets the server controller.
	 */
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

	/**
	 * This method is called when the application is closed. It disconnects the
	 * server.
	 */
	@Override
	public void stop() throws IOException {
		disconnect();
	}

	/**
	 * This method initializes the server, sets the connected clients table and
	 * starts the TimeScheduler
	 * 
	 * @param port       the port number of the server
	 * @param DBuserName the username of the database
	 * @param username   the username of the server
	 * @param password   the password of the server
	 * @return true if the server is successfully run, false otherwise
	 */
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

	/*
	 * This method stops the TimeScheduler, stops listening for connections, closes
	 * the server and sets the server to null.
	 */
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
