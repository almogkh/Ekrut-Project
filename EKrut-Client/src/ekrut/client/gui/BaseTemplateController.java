package ekrut.client.gui;

import java.io.IOException;
import ekrut.entity.User;
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
    
    
    
    
    @FXML
    void logout(ActionEvent event) {
    }
    
    static BaseTemplateController getBaseTemplateController() {
    	return baseTemplateController;
    }
    
    public void setRightWindow(Parent root) {
    	ObservableList<Node> childern = rightVbox.getChildren();
    	childern.setAll(root);
    }
    
    public void loadHostSelection() throws IOException {
    	baseTemplateController = this;
    	infoPane.setVisible(false);
    	logoutBtn.setVisible(false);
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/ekrut/client/gui/HostSelection.fxml"));
    	Parent root = loader.load();
		setRightWindow(root);
    }
    
    public void setUser(User me) {
    	this.me = me;
    	infoPane.setVisible(true);
    	logoutBtn.setVisible(true);
    	nameInitialsLbl.setText((me.getFirstName().substring(0, 1) +
    			me.getLastName().substring(0, 1)).toUpperCase());
    	roleLbl.setText(me.getUserType().toString().replace("_", " "));
    	ObservableList<Node> vboxChildren = navigationVbox.getChildren();
    	switch (me.getUserType()) {
    	case SUBSCRIBER:
    		vboxChildren.add(new Hyperlink("Create Order"));
    		vboxChildren.add(new Hyperlink("Pickup Order"));
    		break;
    	case CUSTOMER:
    		break;
    	case MARKETING_MANAGER:
    		break;
    	case MARKETING_WORKER:
    		break;
    	case SHIPMENT_OPERATOR:
    		break;
    	case SHIPMENT_WORKER:
    		break;
    	case AREA_MANAGER:
    		break;
    	case CEO:
    		break;
    	default:
    		break;
    	
    	}
    }
    
    
    
}


