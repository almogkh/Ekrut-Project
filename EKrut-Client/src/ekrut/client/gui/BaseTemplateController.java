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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

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

	private static BaseTemplateController baseTemplateController;

	public static BaseTemplateController getBaseTemplateController() {
		return baseTemplateController;
	}

	void showLogoutBtn() {
		logoutBtn.setVisible(true);
	}

	public void setRightWindow(Parent root) {
		ObservableList<Node> childern = rightVbox.getChildren();
		childern.setAll(root);
	}

	public void logout() {
		openLoginStage();

		ObservableList<Node> vboxChildren = navigationVbox.getChildren();
		vboxChildren.clear();
		infoPane.setVisible(false);
		logoutBtn.setVisible(false);
	}
	
    @FXML
    void logout(ActionEvent event) {
    	try {
			EKrutClientUI.getEkrutClient().getClientSessionManager().logoutUser();
			logout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadHostSelection() throws IOException {
		baseTemplateController = this;
		infoPane.setVisible(false);
		logoutBtn.setVisible(false);
		switchStages("HostSelection");

	}

	public void loadCreateOrder() {
		if (EKrutClientUI.ekrutLocation != null) {
			switchStages("OrderItemBrowser");
		} else {
			if (EKrutClientUI.getEkrutClient().getClientOrderManager().isActiveOrder())
				switchStages("OrderItemBrowser");
			else
				switchStages("OrderCreation");
		}
	}

	public void loadPickupOrder() {
		switchStages("OrderPickupLocation");
	}

	public void loadApproveShipment() {
		switchStages("ShipmentWorkerApproval");
	}
	
	public void LoadSetShipmentAsDone() {
		switchStages("ShipmentSetDone");
	}

	public void loadConfirmDelivery() {
		switchStages("ShipmentClientConfirmation");
	}
	
	private void loadActivateSale() {
		switchStages("SaleActivate");
	}
	
	public void loadSaleTemplate() {
		switchStages("SaleTemplate");
	}
	
	public void loadUsersRegistration() {
		switchStages("ServiceRepresentativeView");
	}
	
    // C.Nir - can use in *local* method switchStages() all fo this can be repalce in one row.
	public void loadViewTickets() {
		switchStages("TicketBrowser");
	}

	public void loadRegistrationRequests() {
		switchStages("UsersRegistration");
	}
	
	public void loadViewReports() {
		switchStages("ReportChooser");
	}
	
	public void loadThresholdSelector() {		
		switchStages("ThresholdBrowser");
	}

	private static void setHyperlinkStyle(Hyperlink hyperLink) {
		Font font = Font.font("Arial Rounded MT Bold", 18);
		hyperLink.setFont(font);
		hyperLink.setWrapText(true);
		hyperLink.setTextAlignment(TextAlignment.CENTER);
		hyperLink.setStyle("-fx-text-fill: white;"
				//+ "-fx-underline: false;"
				//+ "-fx-border-color: black;"
				//+ "-fx-border-radius: 20;"
				//+ "-fx-background-color: rgba(255, 255, 255, 0.5);"
				+ "-fx-background-color: #2F88FF;"
				//+ "-fx-background-radius: 20;"
				+ "-fx-pref-width: 200;"
				+ "-fx-alignment: center;");
	}

	private ArrayList<Hyperlink> getHyperlinks(UserType userType, boolean customer) {
		ArrayList<Hyperlink> allHyperlinks = new ArrayList<>();
		
		Hyperlink remoteOrderHyp = new Hyperlink("Remote Order");
    	remoteOrderHyp.setOnAction((ActionEvent event) -> loadCreateOrder());
    	
    	Hyperlink approveShipmentHyp = new Hyperlink("Approve Shipment");
    	approveShipmentHyp.setOnAction((ActionEvent event) -> loadConfirmDelivery());
    	
    	Hyperlink viewShipRequestsHyp = new Hyperlink("Shipment Requests");
    	viewShipRequestsHyp.setOnAction((ActionEvent event) -> loadApproveShipment());
    	
		Hyperlink shipmentStatus = new Hyperlink("Shipment Status");
		shipmentStatus.setOnAction((ActionEvent event) -> LoadSetShipmentAsDone());

    	Hyperlink viewTicketsHyp = new Hyperlink("Tickets");
    	viewTicketsHyp.setOnAction((ActionEvent event) -> loadViewTickets());

    	Hyperlink registrationRequestsHyp = new Hyperlink("Registration Requests");
    	registrationRequestsHyp.setOnAction((ActionEvent event) -> loadRegistrationRequests());
    	
    	Hyperlink viewReportsHyp = new Hyperlink("Monthly Reports");
    	viewReportsHyp.setOnAction((ActionEvent event) -> loadViewReports());
		
		Hyperlink thresholdSelector = new Hyperlink("Set Thresholds");
		thresholdSelector.setOnAction((ActionEvent event) -> loadThresholdSelector());
		
		Hyperlink makeSaleTemplate = new Hyperlink("Make Sale Template");
		makeSaleTemplate.setOnAction((ActionEvent event) -> loadSaleTemplate());
		
		Hyperlink activateSale = new Hyperlink("Sale Activation");
		activateSale.setOnAction((ActionEvent event) -> loadActivateSale());
		
		Hyperlink usersRegistration = new Hyperlink("Users Registration");
		usersRegistration.setOnAction((ActionEvent event) -> loadUsersRegistration());
		
		switch (userType) {
		case REGISTERED:
		case CUSTOMER:
			break;
		case SHIPMENT_OPERATOR:
			allHyperlinks.add(viewShipRequestsHyp);
			allHyperlinks.add(shipmentStatus);
			break;
		case MARKETING_MANAGER:
			allHyperlinks.add(makeSaleTemplate);
			break;
		case MARKETING_WORKER:
			allHyperlinks.add(activateSale);
			break;
		case OPERATIONS_WORKER:
			allHyperlinks.add(viewTicketsHyp);
			break;
		case CEO:
			allHyperlinks.add(viewReportsHyp);
			break;
		case AREA_MANAGER:
			allHyperlinks.add(viewTicketsHyp);
			allHyperlinks.add(registrationRequestsHyp);
			allHyperlinks.add(viewReportsHyp);
			allHyperlinks.add(thresholdSelector);
			break;
		case SERVICE_REPRESENTATIVE:
			allHyperlinks.add(usersRegistration);
			break;
		}
		if (customer) {
			allHyperlinks.add(remoteOrderHyp);    		
			allHyperlinks.add(approveShipmentHyp);
		}
		return allHyperlinks;
	}

	public void setUser(User me) {
		infoPane.setVisible(true);
		logoutBtn.setVisible(true);
		nameInitialsLbl.setText((me.getFirstName().substring(0, 1) + me.getLastName().substring(0, 1)).toUpperCase());
		roleLbl.setText(me.getUserType().toString().replace("_", " "));
		navigationVbox.setVisible(true);
		ObservableList<Node> vboxChildren = navigationVbox.getChildren();
		if (EKrutClientUI.ekrutLocation != null) {
			if (me.isCustomer()) {
				Hyperlink createOrderHyp = new Hyperlink("Create New Order");
				createOrderHyp.setOnAction((ActionEvent event) -> loadCreateOrder());
				Hyperlink pickupOrderHyp = new Hyperlink("Pickup Order");
				pickupOrderHyp.setOnAction((ActionEvent event) -> loadPickupOrder());
				vboxChildren.addAll(createOrderHyp, pickupOrderHyp);
			}
		} else {
			vboxChildren.addAll(getHyperlinks(me.getUserType(), me.isCustomer()));
		}
		for (Node node : vboxChildren) {
			setHyperlinkStyle((Hyperlink) node);
		}
	}

    // Switch stages if any info dose not needed.
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
		setRightWindow(root);
    }
   
    // Load fxml and return parent, good for case that information is needed
    public Parent load(FXMLLoader loader) {
    	try {
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return loader.getRoot();
    }
    
    public void openLoginStage() {
    	FXMLLoader loader;
			loader = new FXMLLoader(getClass().getResource("/ekrut/client/gui/ClientLogin.fxml"));
		Parent root = load(loader);
		ClientLoginController clientLoginController = loader.getController();
		setRightWindow(root);
		
		if (EKrutClientUI.ekrutLocation != null) {
			Stage touchStage = new Stage();
			FXMLLoader loaderTouch = new FXMLLoader(getClass().getResource("/ekrut/client/gui/LoginViaTouch.fxml"));
			Parent rootTouch = load(loaderTouch);
			LoginViaTouchController loginViaTouchController = loaderTouch.getController();
			loginViaTouchController.setClientLoginController(clientLoginController);
			Scene scene = new Scene(rootTouch);
			touchStage.getIcons().add(new Image(EKrutClientUI.class.getResourceAsStream("/ekrut/client/gui/gui-assets/icon.png")));
			touchStage.setScene(scene);
			touchStage.show();
		}
    	
    }
}
