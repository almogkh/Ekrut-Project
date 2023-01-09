package ekrut.server.gui;

import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import ekrut.entity.ConnectedClient;
import ekrut.net.ResultType;
import ekrut.server.EKrutServer;
import ekrut.server.managers.ServerSessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

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
	private Button importDataBTN;

	@FXML
	private TableColumn<ConnectedClient, String> IPColumn;
	
	@FXML
	private TextField PortTXTfield;

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

	@FXML
	private Label ErrorImportData;

	private PrintStream replaceConsole;
	private EKrutServer server;

	public String getLocalIp(){
		String ip = null;
		boolean virtual = false;
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			outer: while (interfaces.hasMoreElements()) {
				NetworkInterface ni = interfaces.nextElement();
				// Not interested in interfaces that are down
				if (!ni.isUp())
					continue;

				
				Enumeration<InetAddress> addresses = ni.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress ia = addresses.nextElement();
					// We don't want a loopback address
					if (ia.isLoopbackAddress())
						continue;
					// Prefer non-virtual interfaces over virtual ones
					if (virtual && !ni.isVirtual()) {
						virtual = false;
						ip = ia.getHostAddress();
						break outer;
					} else if (ip == null) { // Prefer an address over nothing
						ip = ia.getHostAddress();
						virtual = ni.isVirtual();
					}
				}
			}
		} catch (SocketException e) {
		}
		if (ip == null) {
			try {
				// Fall back on an alternate method
				ip = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				// Better than nothing
				ip = "127.0.0.1";
			}
		}
		return ip;
	}

	@FXML
	public void initialize() {
		this.connectionIP.setText(getLocalIp());
		this.consoleStreamIntoGUI();
		this.PortTXTfield.setText("5555");
		this.DBNameTXTfield.setText("jdbc:mysql://localhost/ekrut?serverTimezone=IST");
		this.DBUserNameTXTfield.setText("root");
		this.DBPasswordTXTfield.setText("1qazZ2wsxX!@");
		this.DisconnectBTN.setVisible(false);
		this.importDataBTN.setVisible(false);
		this.ConnectedGreenIMG.setVisible(false);
		this.connectionStatus.setText("Not connected");
		this.ErrorConnection.setVisible(false);
		this.ErrorImportData.setVisible(false);
	}

	public void setTable(ServerSessionManager session) {
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
		if (!ServerUI.runServer(Integer.parseInt(this.PortTXTfield.getText()),this.DBNameTXTfield.getText(), this.DBUserNameTXTfield.getText(), this.DBPasswordTXTfield.getText())) {
			this.ErrorConnection.setVisible(true);
			this.ConnectToServerBTN.setVisible(true);
			this.DisconnectBTN.setVisible(false);
			return;
		}
		this.ConnectToServerBTN.setVisible(false);
		this.DisconnectBTN.setVisible(true);
		this.disableDataInput(true);
		this.connectionStatus.setText("Connected");
		this.ConnectedGreenIMG.setVisible(true);
		this.ErrorConnection.setVisible(false);
		this.importDataBTN.setVisible(true);
		
	}

	@FXML
	void Disconnect(final ActionEvent event) {
		ServerUI.disconnect();
		this.ConnectToServerBTN.setVisible(true);
		this.DisconnectBTN.setVisible(false);
		this.disableDataInput(false);
		this.connectionStatus.setText("Not connected");
		this.ConnectedGreenIMG.setVisible(false);
		

	}
	@FXML
	void importData(final ActionEvent event) {
		if(server.importUsers().getResultCode()==ResultType.UNKNOWN_ERROR) {
			System.out.println("not good");
			this.ErrorImportData.setVisible(true);
			return;
		}
		System.out.println("good");
		this.importDataBTN.setDisable(true);
		
	}
	void disableDataInput(final boolean Condition) {
		PortTXTfield.setDisable(Condition);
		DBNameTXTfield.setDisable(Condition);
		DBUserNameTXTfield.setDisable(Condition);
		DBPasswordTXTfield.setDisable(Condition);
		ConnectedGreenIMG.setDisable(Condition);
	}
	
}
