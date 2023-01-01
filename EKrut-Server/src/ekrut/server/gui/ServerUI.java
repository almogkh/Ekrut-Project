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

	public static boolean runServer(int port,String DBuserName, String username, String password) {
		server = new EKrutServer(port,DBuserName, username, password);
		controller.setTable(server.getSession());
		try {
			if(!server.getDbCon().connect()) {
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
