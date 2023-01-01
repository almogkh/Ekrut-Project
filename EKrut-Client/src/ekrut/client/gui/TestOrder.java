
package ekrut.client.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class TestOrder extends Application{
	
	public static void main(String[] args) {
		launch(args);
	}

	
	
	
//	@Override
//	public void start(Stage primaryStage) throws Exception {
//		FXMLLoader loader = new FXMLLoader(getClass().getResource("PaymentView.fxml"));
//		Parent root = loader.load();
//		Scene scene = new Scene(root);
//		primaryStage.setTitle("EKrut cart");
//		primaryStage.setScene(scene);
//		primaryStage.show();
//	}	
	
//	@Override
//	public void start(Stage primaryStage) throws Exception {
//		FXMLLoader loader = new FXMLLoader(getClass().getResource("CartView.fxml"));
//		Parent root = loader.load();
//		CartViewController controller = loader.getController();
//		Scene scene = new Scene(root);
//		controller.AddItemViewToCartVBox();
//		primaryStage.setTitle("EKrut cart");
//		primaryStage.setScene(scene);
//		primaryStage.show();
//	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemBrowser.fxml"));
		Parent root = loader.load();
		ItemBrowserController controller = loader.getController();
		Scene scene = new Scene(root);
		controller.AddItemViewToOrderVBox();
		primaryStage.setTitle("EKrut cart");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
