package ekrut.client.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientTicketManager;
import ekrut.entity.Ticket;
import ekrut.entity.UserType;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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
    	UserType usertype = client.getClientSessionManager().getUser().getUserType();
    	String username = client.getClientSessionManager().getUser().getUsername();
    	
    	ArrayList<Ticket> ticketsToShow;
    	if (usertype == UserType.AREA_MANAGER) {
    		createTicketBtn.setVisible(true);
    		String userArea = client.getClientSessionManager().getUser().getArea();
    		ticketsToShow = clientTicketManager.fetchTicketsByArea(userArea); // TBD OFEK: BUG WHEN WRONG AREA
    	} else {
    		createTicketBtn.setVisible(false);
    		ticketsToShow = clientTicketManager.fetchTicketsByUsername(username);
    	}
    		if (ticketsToShow == null)
    			return;
    		ObservableList<Node> ticketsContainerVboxChildren = ticketsContainerVbox.getChildren();
    		for (Ticket ticket : ticketsToShow) {
    			ticketsContainerVboxChildren.add(new TicketViewController(ticket, usertype != UserType.OPERATIONS_WORKER));
    		}
	}
    
    
    
    @FXML
    void createTicket(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/ekrut/client/gui/TicketSubmission.fxml"));
		try {
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		Parent root = loader.getRoot();
		//TicketSubmissionController ticketSubmissionController = loader.getController();
		BaseTemplateController.getBaseTemplateController().setRightWindow(root);
	    }

}
