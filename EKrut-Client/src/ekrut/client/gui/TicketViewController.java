package ekrut.client.gui;

import java.io.IOException;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientTicketManager;
import ekrut.entity.Ticket;
import ekrut.entity.TicketStatus;
import ekrut.net.ResultType;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class TicketViewController extends HBox{

	Ticket ticket;

    @FXML
    private Label areaPlusLocationLbl;

    @FXML
    private Label assignedForLbl;
    
    @FXML
    private Label ticketMsgLbl;

    @FXML
    private Button markCompletedBtn;
    
    private ClientTicketManager clientTicketManager;

	public TicketViewController(Ticket ticket, boolean disableCompetedBtn) {
		this.ticket = ticket;
    	this.clientTicketManager = EKrutClientUI.getEkrutClient().getClientTicketManager();
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TicketView.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		areaPlusLocationLbl.setText(ticket.getArea() + ", " + ticket.getEkrutLocation());
		assignedForLbl.setText("Assigned to:  " + ticket.getUsername());
		ticketMsgLbl.setText("Restock " + ticket.getItemName());
		markCompletedBtn.setDisable(disableCompetedBtn);
	}
    
    @FXML
    void markCompleted(ActionEvent event) {
    	Alert alert = new Alert(AlertType.CONFIRMATION,
    			"Please confirm "+ ticket.getItemName() +" replenishment", ButtonType.YES, ButtonType.NO);
    	alert.showAndWait();
    	if (alert.getResult() == ButtonType.NO)
    	    return;
    	if (clientTicketManager.updateTicketStatus(ticket, TicketStatus.DONE) != ResultType.OK) {
			Platform.runLater(() ->{
				EKrutClientUI.popupUserNotification("Couldn't update item quantity - reach IT department for assistance.");
				} );
			return;
    	}
    	Parent parent = getParent();
    	ObservableList<Node> vboxChildren = ((VBox) parent).getChildren();
    	vboxChildren.remove(this);
    }
}