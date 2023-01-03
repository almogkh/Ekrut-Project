package ekrut.server.gui;

import java.io.PrintStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import ekrut.entity.ConnectedClient;
import ekrut.entity.User;
import ekrut.entity.UserType;
import ekrut.server.EKrutServer;
import ekrut.server.managers.ServerSessionManager;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Callback;
import ocsf.server.ConnectionToClient;

public class ServerController {

	@FXML
	private Button ConnectToServerBTN;

	@FXML
	private TableView<ConnectedClient> ConnectedClients;

	@FXML
	private ImageView ConnectedGreenIMG;

	@FXML
	private ImageView ConnectedRedIMG;

	@FXML
	private TextArea Console;

	@FXML
	private TextField DBNameTXTfield;

	@FXML
	private PasswordField DBPasswordTXTfield;

	@FXML
	private TextField DBUserNameTXTfield;

	@FXML
	private Button DisconnectBTN;

	@FXML
	private TableColumn<ConnectedClient, String> IPColumn;

	@FXML
	private TextField IPTXTfield;

	@FXML
	private TableColumn<ConnectedClient, String> RoleColumn;

	@FXML
	private TableColumn<ConnectedClient, String> UsernameColumn;

	@FXML
	private Label connectionStatus;

	@FXML
	private Label connectionIP;

	@FXML
	private Label ErrorConnection;

	private PrintStream replaceConsole;
	private ServerSessionManager session;

	public String getLocalIp(){
		String ip = null;
		
		// Attempt to determine the 'real' IP address.
		try(final DatagramSocket socket = new DatagramSocket()){
			  try {
				socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
				ip = socket.getLocalAddress().getHostAddress();
			} catch (UnknownHostException e) {}
			} catch (SocketException e1) {}
		if (ip == null) {
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		return ip;
	}

	@FXML
	public void initialize() {
		this.IPTXTfield.setText(this.getLocalIp());
		this.consoleStreamIntoGUI();
		this.DBNameTXTfield.setText("jdbc:mysql://localhost/ekrut?serverTimezone=IST");
		this.DBUserNameTXTfield.setText("root");
		this.DBPasswordTXTfield.setText("UntilWhenNov12");
		this.DisconnectBTN.setVisible(false);
		this.ConnectedGreenIMG.setVisible(false);
		this.connectionStatus.setText("Not connected");
		this.connectionIP.setVisible(false);
		this.ErrorConnection.setVisible(false);
	}

	public void setTable(ServerSessionManager session) {
		this.session = session;
		this.ConnectedClients.setItems(session.getConnectedClientList());
		this.IPColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));
		this.RoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
		this.UsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
	}

	@FXML
	void consoleStreamIntoGUI() {
		System.setOut(this.replaceConsole = new PrintStream(new Console(this.Console)));
		System.setErr(this.replaceConsole);
	}

	@FXML
	void Connect(final ActionEvent event) {
		this.ErrorConnection.setVisible(false);
		if (!ServerUI.runServer(
				5555, 
				this.DBNameTXTfield.getText(), 
				this.DBUserNameTXTfield.getText(), 
				this.DBPasswordTXTfield.getText())) {
			this.ErrorConnection.setVisible(true);
			this.ConnectToServerBTN.setVisible(true);
			this.DisconnectBTN.setVisible(false);
			return;
		}
		this.ConnectToServerBTN.setVisible(false);
		this.DisconnectBTN.setVisible(true);
		this.disableDataInput(false);
		this.connectionStatus.setText("Connected");
		this.connectionIP.setVisible(true);
		this.connectionIP.setText(getLocalIp());
		this.ConnectedGreenIMG.setVisible(true);
		this.ErrorConnection.setVisible(false);
	}

	@FXML
	void Disconnect(final ActionEvent event) {
		ServerUI.disconnect();
		this.ConnectToServerBTN.setVisible(true);
		this.DisconnectBTN.setVisible(false);
		this.disableDataInput(true);
		this.connectionStatus.setText("Not connected");
		this.connectionIP.setVisible(false);
		this.ConnectedGreenIMG.setVisible(false);

	}

	void disableDataInput(final boolean Condition) {
		IPTXTfield.setVisible(Condition);
		DBNameTXTfield.setVisible(Condition);
		DBUserNameTXTfield.setVisible(Condition);
		DBPasswordTXTfield.setVisible(Condition);
		ConnectedGreenIMG.setVisible(Condition);
	}
}
