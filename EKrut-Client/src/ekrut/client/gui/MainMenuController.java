package ekrut.client.gui;

import ekrut.entity.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.WindowEvent;

public class MainMenuController{

	@FXML
	private Label userGreetingLbl;
	
	@FXML
	private Label userPrivilegeLbl;

	@FXML
	private Label ekrutLocationLbl;
	
	@FXML
    private Button createNewOrderBtn;
    
    @FXML
    private Button pickupOrderBtn;
	
	@FXML
    private Button RegistrationRequestsBtn;

    @FXML
    private Button approveShipmentArrivalBtn;

    @FXML
    private Button viewMonthlyReportsBtn;

    @FXML
    private Button viewReportsBtn;

    @FXML
    private Button viewShipmentRequestsBtn;

    @FXML
    private Button viewTicketsBtn;

    User me;
    
    
    public void setUser(User me) {
    	this.me = me;
    	userGreetingLbl.setText("Hello " + me.getFirstName());
    	userPrivilegeLbl.setText("Login privilege: " + me.getUserType().toString());
    }
 
    
}
