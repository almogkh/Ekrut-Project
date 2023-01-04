package ekrut.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import ekrut.client.EKrutClientUI;
import ekrut.entity.User;
import ekrut.entity.UserType;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;


public class BaseTemplateController {
	
    @FXML
    private Button logoutBtn;

    @FXML
    private Label nameInitialsLbl;

    @FXML
    private VBox navigationVbox;

    @FXML
    private VBox rightVbox;

    @FXML
    private Label roleLbl;
    
    @FXML
    private Pane infoPane;

    User me;
    static BaseTemplateController baseTemplateController;

    static BaseTemplateController getBaseTemplateController() {
    	return baseTemplateController;
    }
    
    void showLogoutBtn() {
    	logoutBtn.setVisible(true);
    }
    
    public void setRightWindow(Parent root) {
    	ObservableList<Node> childern = rightVbox.getChildren();
    	childern.setAll(root);
    }

    // C.Nir - can use in *local* method switchStages() all fo this can be repalce in one row.
    @FXML
    void logout(ActionEvent event) {
    	try {
			EKrutClientUI.getEkrutClient().getClientSessionManager().logoutUser();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/ekrut/client/gui/ClientLogin.fxml"));
			try {
				loader.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			Parent root = loader.getRoot();
			setRightWindow(root);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	ObservableList<Node> vboxChildren = navigationVbox.getChildren();
    	vboxChildren.clear();
    	infoPane.setVisible(false);
    	logoutBtn.setVisible(false);
    }
    
    public void loadHostSelection(){
    	baseTemplateController = this; // ????????
    	infoPane.setVisible(false);
    	logoutBtn.setVisible(false);
    	
        // C.Nir - can use in *local* method switchStages() all fo this can be repalce in one row.
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/ekrut/client/gui/HostSelection.fxml"));
    	Parent root = null;
		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		setRightWindow(root);
    }
    
    public void loadCreateOrder(){
    	switchStages("ItemBrowser");
    }
    
    public void loadPickupOrder(){
    }
    
    public void loadApproveShipment(){
    }
    
    public void loadViewShipment(){
    }
    
    // C.Nir - can use in *local* method load();
    public void loadViewTickets(){
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/ekrut/client/gui/TicketBrowser.fxml"));
		try {
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Parent root = loader.getRoot();
		TicketBrowserController ticketBrowserController = loader.getController();
		setRightWindow(root);
    	
    }
    
    public void loadregistrationRequests(){
    }
    
    // C.Nir - can use in *local* method switchStages() all fo this can be repalce in one row.
    public void loadViewReports(){
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/ekrut/client/gui/ReportChooser.fxml"));
		try {
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Parent root = loader.getRoot();
		//ReportChooserController reportChooserController = loader.getController();
		setRightWindow(root);

    }
    
    private static void setHyperlinkStyle(Hyperlink hyperLink) {
    	Font font = Font.font("Arial Rounded MT Bold", 18);
    	//hyperLink.getFont().getName()
    	hyperLink.setFont(font);
    	hyperLink.setWrapText(true);
    	hyperLink.setTextAlignment(TextAlignment.CENTER);
    }
    
    private ArrayList<Hyperlink> getHyperlinks(UserType userType) {	
    	ArrayList<Hyperlink> allHyperlinks = new ArrayList<>();
    	
    	Hyperlink remoteOrderHyp = new Hyperlink("Remote order");
    	remoteOrderHyp.setOnAction((ActionEvent event) -> loadCreateOrder());
    	
    	Hyperlink pickupOrder = new Hyperlink("Pickup Order");
    	pickupOrder.setOnAction((ActionEvent event) -> loadCreateOrder());
    	
    	Hyperlink approveShipmentHyp = new Hyperlink("Approve shipment");
    	approveShipmentHyp.setOnAction((ActionEvent event) -> loadApproveShipment());
    	
    	Hyperlink getExistingOrder = new Hyperlink("Get Order by ID");
    	getExistingOrder.setOnAction((ActionEvent event) -> loadCreateOrder());
    	
    	Hyperlink viewShipRequestsHyp = new Hyperlink("Shipment requests");
    	viewShipRequestsHyp.setOnAction((ActionEvent event) -> loadViewShipment());

    	Hyperlink viewTicketsHyp = new Hyperlink("Tickets");
    	viewTicketsHyp.setOnAction((ActionEvent event) -> loadViewTickets());

    	Hyperlink registrationRequestsHyp = new Hyperlink("Registration requests");
    	registrationRequestsHyp.setOnAction((ActionEvent event) -> loadregistrationRequests());
    	
    	Hyperlink viewReportsHyp = new Hyperlink("Monthly reports");
    	viewReportsHyp.setOnAction((ActionEvent event) -> loadViewReports());
    	
    	switch (userType) {
	    	case CUSTOMER:
	    	case SUBSCRIBER:
	    		allHyperlinks.add(remoteOrderHyp);    		
	    		allHyperlinks.add(pickupOrder);	    		
	    		allHyperlinks.add(approveShipmentHyp);			    		
	    		allHyperlinks.add(getExistingOrder);
	    		break;
	    	case SHIPMENT_OPERATOR:
	    	case SHIPMENT_WORKER:
	    		allHyperlinks.add(viewShipRequestsHyp);
	    		break;
	    	case MARKETING_MANAGER:
	    		break;
	    	case MARKETING_WORKER:
	    		break;
	    	case CEO:
	    	case AREA_MANAGER:
	    		allHyperlinks.add(viewTicketsHyp);	    		
	    		allHyperlinks.add(registrationRequestsHyp);		
	    		allHyperlinks.add(viewReportsHyp);
	    		//
	    		// add order btn
	    		//
	    		break;
    	}
    	return allHyperlinks;
    }
    
    public void setUser(User me) {
    	this.me = me;
    	infoPane.setVisible(true);
    	logoutBtn.setVisible(true);
    	nameInitialsLbl.setText((me.getFirstName().substring(0, 1) +
    			me.getLastName().substring(0, 1)).toUpperCase());
    	roleLbl.setText(me.getUserType().toString().replace("_", " "));
    	navigationVbox.setVisible(true);
    	ObservableList<Node> vboxChildren = navigationVbox.getChildren();
    	if (EKrutClientUI.ekrutLocation != null) {
    		Hyperlink createOrderHyp = new Hyperlink("Create new order");
    		createOrderHyp.setOnAction((ActionEvent event) -> loadCreateOrder());
    		Hyperlink pickupOrderHyp = new Hyperlink("Pickup existing order");
    		pickupOrderHyp.setOnAction((ActionEvent event) -> loadPickupOrder());
    		vboxChildren.addAll(createOrderHyp, pickupOrderHyp);
    	}else{
    		vboxChildren.addAll(getHyperlinks(me.getUserType()));
    	}
    	for (Node node : vboxChildren) {
    		setHyperlinkStyle((Hyperlink) node);
    	}
    }
    
    // Q.Nir - is needed?
    // switch stages if any info dose not needed.
    public void switchStages(String fxmlName) {
    	String path = "/ekrut/client/gui/";
    	String fileExtention = ".fxml";
    	FXMLLoader loader = new FXMLLoader(getClass().getResource(path + fxmlName + fileExtention));
    	try {
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Parent root = loader.getRoot();
		BaseTemplateController.getBaseTemplateController().setRightWindow(root);
    }
    
    // load fxml and return parent
    public Parent load(FXMLLoader loader) {
    	try {
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return loader.getRoot();
    }




}


