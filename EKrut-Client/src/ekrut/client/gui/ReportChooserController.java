package ekrut.client.gui;

import java.time.LocalDateTime;

import java.util.ArrayList;
import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientReportManager;
import ekrut.entity.Report;
import ekrut.entity.ReportType;
import ekrut.entity.User;
import ekrut.entity.UserType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.text.Text;


/**
 * ReportChooserController is the controller class for the report chooser
 * screen.
 * 
 * It allows to select the parameters for the report they want to view,
 * such as the area, location, month, and year.
 * 
 * It also handles the fetching of the report from the server and displaying it.
 * 
 * @author Tal Gaon
 */
public class ReportChooserController {

	@FXML
	private ComboBox<String> areaComboBox;

	@FXML
	private ComboBox<String> locationComboBox;

	@FXML
	private ComboBox<String> monthComboBox;

	@FXML
	private Text locationAsterisk;

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

	private EKrutClient client;
	private User user;
	private ClientReportManager clientReportManager;

	@FXML
	private void initialize() {
		client = EKrutClientUI.getEkrutClient();
		user = client.getClientSessionManager().getUser();
		clientReportManager = client.getClientReportManager();

		// Set months, years, types combo boxes
		String[] months = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
		monthComboBox.getItems().addAll(months);

		String[] years = { "2021", "2022", "2023" };
		yearComboBox.getItems().addAll(years);

		String[] types = { "Orders Report", "Inventory Report", "Customer Activity Report" };
		typeComboBox.getItems().addAll(types);

		setAreaComboBox();

	}

	@FXML
	void viewReport(ActionEvent event) throws Exception {

		// Handle empty combo box first
		if ((typeComboBox.getValue() == null || areaComboBox.getValue() == null || monthComboBox.getValue() == null
				|| yearComboBox.getValue() == null)
				|| !typeComboBox.getValue().equals("Orders Report") && locationComboBox.getValue() == null) {
			reportErrorLabel.setText("Error, you have to choose all the parameters");
		}
		// Try to fetch report
		else {
			// Create a lcoalDateTime instance
			LocalDateTime date = LocalDateTime.now();

			date = date.withYear(Integer.parseInt(yearComboBox.getValue()));
			date = date.withMonth(Integer.parseInt(monthComboBox.getValue()));

			String location = locationComboBox.getValue();
			ReportType type;

			if (typeComboBox.getValue().equals("Orders Report")) {
				location = areaComboBox.getValue();
				type = ReportType.ORDER;
			} else if (typeComboBox.getValue().equals("Inventory Report")) {
				type = ReportType.INVENTORY;
			} else {
				type = ReportType.CUSTOMER;
			}

			Report report = clientReportManager.getReport(areaComboBox.getValue(), location, type, date);

			if (report == null) {
				reportErrorLabel.setText("Error, there is not such report");
			} else {
				FXMLLoader loader;
				if (type.equals(ReportType.ORDER)) {
					loader = new FXMLLoader(getClass().getResource("/ekrut/client/gui/OrderReportView.fxml"));
					loader.load();
					OrderReportViewController orderReportViewController = loader.getController();
					orderReportViewController.setOrderReport(report);

				} else if (type.equals(ReportType.INVENTORY)) {
					loader = new FXMLLoader(getClass().getResource("/ekrut/client/gui/InventoryReportView.fxml"));
					loader.load();
					InventoryReportViewController inventoryReportViewController = loader.getController();
					inventoryReportViewController.setInventoryReport(report);
				} else {
					loader = new FXMLLoader(getClass().getResource("/ekrut/client/gui/CustomerReportView.fxml"));
					loader.load();
					CustomerReportViewController customerReportViewController = loader.getController();
					customerReportViewController.setCustomerReport(report);
				}
				Parent root = loader.getRoot();
				BaseTemplateController.getBaseTemplateController().setRightWindow(root);
			}
		}
	}

	private void setAreaComboBox() {
		String[] areas = null;
		// Set area comboBox
		if (user.getUserType().equals(UserType.CEO)) {
			areas = new String[3];
			areas[0] = "North";
			areas[1] = "South";
			areas[2] = "UAE";
		} else if (user.getUserType().equals(UserType.AREA_MANAGER)) {
			areas = new String[] { user.getArea() };
		}
		areaComboBox.getItems().addAll(areas);
	}

	private void setLocationComboBox(String area) throws Exception {
		ArrayList<String> locations = clientReportManager.getFacilitiesByArea(area);

		// Convert array list into a array
		String[] locationsArr = locations.toArray(new String[locations.size()]);
		locationComboBox.getItems().addAll(locationsArr);
	}

	// If the Report type is an order report than disable locations
	@FXML
	void setLocationsByType(ActionEvent event) throws Exception {
		String type = typeComboBox.getValue();
		areaComboBox.setDisable(false);

		if (type.equals("Orders Report")) {
			locationComboBox.setPromptText("Not Available");
			locationComboBox.setDisable(true);
			locationAsterisk.setVisible(false);
			locationComboBox.getItems().clear();
		} else {
			locationComboBox.setDisable(false);
			locationAsterisk.setVisible(true);
			if (areaComboBox.getValue() != null) {
				setLocationComboBox(areaComboBox.getValue());
			}
		}
	}

	// Set the locations by the chosen area if the report type is not order
	@FXML
	void setLocationsByArea(ActionEvent event) throws Exception {
		String type = typeComboBox.getValue();
		String area = areaComboBox.getValue();
		if (!type.equals("Orders Report")) {
			locationComboBox.getItems().clear();
			locationComboBox.setPromptText("Choose Location");
			locationComboBox.setDisable(false);
			setLocationComboBox(area);
		}
	}
}
