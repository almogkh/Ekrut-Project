package ekrut.server.gui;

import java.io.File;
import java.io.IOException;

import ekrut.server.EKrutServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerUI extends Application {

	private static EKrutServer server;
	private static ServerController controller;
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		//File file= new File("C:\\Users\\97250\\git\\Ekrut-Project\\EKrut-Server\\bin\\ekrut\\server\\gui\\Server.fxml");
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource(("/ekrut/server/gui/Server.fxml")));

		Parent root = loader.load();
		controller = loader.getController();
		
		Scene scene = new Scene(root);
		primaryStage.setTitle("Server");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	

	@Override
	public void stop() throws IOException {
		if (server != null)
			server.close();
	}

	public static boolean runServer(int port, String username, String password) {
		server = new EKrutServer(port, username, password);
		controller.setTable(server.getSession());
		try {
			server.listen();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static void disconnect() {
		if (server == null) {
			server.stopListening();
		} else {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Server Disconnected");
	}
}
