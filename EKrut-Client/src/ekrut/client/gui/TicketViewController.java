package ekrut.client.gui;

import java.io.IOException;

import ekrut.entity.Ticket;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class TicketViewController extends HBox{


	public TicketViewController(Ticket ticket) {
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
		ticketMsgLbl.setText("Add " + ticket.getThreshold() + " units of " + ticket.getItemName()); // TBD OFEK threshold???
	}
	
    @FXML
    private Label areaPlusLocationLbl;

    @FXML
    private Label assignedForLbl;
    
    @FXML
    private Label ticketMsgLbl;

    @FXML
    private Button markCompletedBtn;

    @FXML
    void markCompleted(ActionEvent event) {
    	System.out.println("cmpltd");
    }

}