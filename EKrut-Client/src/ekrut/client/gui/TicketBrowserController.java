package ekrut.client.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientTicketManager;
import ekrut.entity.Ticket;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.VBox;

public class TicketBrowserController implements Initializable {
	
	private EKrutClient client;
	private ClientTicketManager clientTicketManager;

    @FXML
    private Button createTicketBtn;

    @FXML
    private VBox ticketsContainerVbox;

    @Override
	public void initialize(URL location, ResourceBundle resources) {
    	client = EKrutClientUI.getEkrutClient();
    	clientTicketManager = client.getClientTicketManager();
    	try {
			ArrayList<Ticket> ticketsToShow = clientTicketManager.fetchTicketsByArea("UAEe");
			ObservableList<Node> ticketsContainerVboxChildren = ticketsContainerVbox.getChildren();
			for (Ticket ticket : ticketsToShow) {
				ticketsContainerVboxChildren.add(new TicketViewController(ticket));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    
    
    @FXML
    void createTicket(ActionEvent event) {

    }

}
