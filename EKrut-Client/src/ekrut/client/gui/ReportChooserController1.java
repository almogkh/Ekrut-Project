package ekrut.client.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import ekrut.client.managers.ClientReportManager;
import ekrut.entity.User;
import ekrut.entity.UserType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class ReportChooserController1 implements Initializable{

    @FXML
    private ComboBox<String> areaComboBox;

    @FXML
    private ComboBox<String> locationComboBox;

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private Label nameInitialsLabel;

    @FXML
    private Label reportErrorLabel;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private Button viewReportButton;

    @FXML
    private ComboBox<String> yearComboBox;

    @FXML
    void viewReport(ActionEvent event) {

    }
    
    User user = new User(UserType.AREA_MANAGER, "talga", "tal123", "tal", "gaon", "31231",
			"talgaon4@gmail.com", "0522613732", "north");
    
    public void displayUserDetails(User user) {
    	
    	nameInitialsLabel.setText(user.getFirstName().substring(0, 1).toUpperCase() +
    							 user.getLastName().substring(0, 1).toUpperCase());
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		displayUserDetails(user);
		
 		Map<String, String[]> locations = new HashMap<>();
 		
 		String[] north = {"Haifa", "Akko", "Karmiel"};
 		String[] south = {"Dimona", "Eilat", "Netivot"};
 		String[] UAE = {"Dubai", "Abu-Dabi", "Queit"};
		
		// Can be changed later to English months
 		String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
 		String[] years = {"2021", "2022", "2023"};
 		String[] areas = null;
 		
 		locations.put("North", north);
 		locations.put("South", south);
 		locations.put("UAE", UAE); 
 		
 		if (user.getUserType() == UserType.CEO) {
 			areas = new String[3];
 			areas[0] = "North";
 			areas[1] = "South";
 			areas[2] = "UAE";
 		}
 		else if(user.getUserType() == UserType.AREA_MANAGER) {
 			if (user.getArea() == "north") {
 				areas = new String[1];
 				areas[0] = "North";
 			}
 			else if (user.getArea() == "south") {
 				areas = new String[1];
 				areas[0] = "South";
 			}
 			else {
 				areas = new String[1];
 	 			areas[0] = "UAE";
 			}
 		}
 		
		monthComboBox.getItems().addAll(months);
 		yearComboBox.getItems().addAll(years);
 		areaComboBox.getItems().addAll(areas);
 		
 		
 		areaComboBox.setOnAction(event ->{
 			locationComboBox.setPromptText("Choose Location");
 			locationComboBox.getItems().clear();
 			locationComboBox.setDisable(false);
 			String data = areaComboBox.getSelectionModel().getSelectedItem().toString();
 			locationComboBox.getItems().addAll(locations.get(data));
 		});
	}

}
