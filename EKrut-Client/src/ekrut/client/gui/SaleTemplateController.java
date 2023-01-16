package ekrut.client.gui;

import java.time.LocalTime;
import java.util.ArrayList;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientSalesManager;
import ekrut.entity.SaleDiscount;
import ekrut.entity.SaleDiscountType;
import ekrut.net.ResultType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert.AlertType;

public class SaleTemplateController {

	@FXML
	private Button backBtn;

	@FXML
	private CheckBox sundayCheckB;

	@FXML
	private CheckBox mondayCheckB;

	@FXML
	private CheckBox tuesdayCheckB;

	@FXML
	private CheckBox wednesdayCheckB;

	@FXML
	private CheckBox thursdayCheckB;

	@FXML
	private CheckBox fridayCheckB;

	@FXML
	private CheckBox saturdayCheckB;

	@FXML
	private ComboBox<String> saleTypeChoiceB;

	@FXML
	private ComboBox<String> startTimeInHourChoiceB;

	@FXML
	private ComboBox<String> startTimeInMinChoiceB;

	@FXML
	private ComboBox<String> endTimeInHourChoiceB;

	@FXML
	private ComboBox<String> endTimeInMinChoiceB;

	@FXML
	private Button makeTemplateBtn;

	private static final int HOURS_IN_DAY = 24;
	private static final int MINUTES_IN_HOUR = 60;
	private static final String[] HOURS = new String[HOURS_IN_DAY];
	private static final String[] MINUTES = new String[MINUTES_IN_HOUR];
	private static final String SUCCESS_MSG = "Sale discount tamplate was successfully added.";
	private static final String FAIL_MSG = "Failed to add template.";
	private static final String EXISTS_MSG = "Template already exists!";
	private static final String MISSING_INPUT_MSG = "Sorry, it looks like one or more details are missing."
			+ " Please check that all required fields have been filled out and try again.";
	private static final String LOGIC_TIME_ERROR = "There is no logic in the time that you set to sale discount."
			+ " Please check all the required fields and try again.";
	private static final String[] SALE_TYPE = { "30% off", "One Plus One" };

	private ArrayList<SaleDiscount> templates;

	private ClientSalesManager clientSalesManager;
	
	static {
		for (int i = 0; i < MINUTES_IN_HOUR; i++)
			MINUTES[i] = (i < 10 ? "0" : "") + Integer.toString(i);
		for (int i = 0; i < HOURS_IN_DAY; i++)
			HOURS[i] = (i < 10 ? "0" : "") + Integer.toString(i);
	}

	@FXML
	private void initialize() {
		clientSalesManager = EKrutClientUI.getEkrutClient().getClientSalesManager();
		templates = clientSalesManager.fetchSaleTemplates();


		startTimeInHourChoiceB.getItems().addAll(HOURS);
		endTimeInHourChoiceB.getItems().addAll(HOURS);

		startTimeInMinChoiceB.getItems().addAll(MINUTES);
		endTimeInMinChoiceB.getItems().addAll(MINUTES);
		
		saleTypeChoiceB.getItems().addAll(SALE_TYPE);
	}

	@FXML
	void makeSaleTemplate(ActionEvent event) {
		StringBuilder dayOfSale = new StringBuilder();
		dayOfSale.append(isDaySelected(sundayCheckB));
		dayOfSale.append(isDaySelected(mondayCheckB));
		dayOfSale.append(isDaySelected(tuesdayCheckB));
		dayOfSale.append(isDaySelected(wednesdayCheckB));
		dayOfSale.append(isDaySelected(thursdayCheckB));
		dayOfSale.append(isDaySelected(fridayCheckB));
		dayOfSale.append(isDaySelected(saturdayCheckB));
		String saleDays = dayOfSale.toString();

		if (startTimeInHourChoiceB.getValue() == null|| endTimeInHourChoiceB.getValue() == null
		 || startTimeInMinChoiceB.getValue() == null || endTimeInMinChoiceB.getValue() == null
		 || saleTypeChoiceB.getValue() == null       || saleDays.equals("FFFFFFF")) {
			
			new Alert(AlertType.ERROR, MISSING_INPUT_MSG, ButtonType.OK).showAndWait();
			return;
		}

		int startHour = Integer.parseInt(startTimeInHourChoiceB.getValue());
		int startMin = Integer.parseInt(startTimeInMinChoiceB.getValue());
		int endHour = Integer.parseInt(endTimeInHourChoiceB.getValue());
		int endMin = Integer.parseInt(endTimeInMinChoiceB.getValue());
		LocalTime startTime = LocalTime.of(startHour, startMin);
		LocalTime endTime = LocalTime.of(endHour, endMin);

		// Check if the input time is legal.
		if (startTime.isAfter(endTime)) {
			new Alert(AlertType.ERROR, LOGIC_TIME_ERROR, ButtonType.OK).showAndWait();
			return;
		}
		
		// Q.Nir - Is ok to be specific? its fits only for these two types, if we will
		// add another type it will be problem.
		SaleDiscountType type = saleTypeChoiceB.getValue().equals("One Plus One") ? SaleDiscountType.ONE_PLUS_ONE
				: SaleDiscountType.THIRTY_PERCENT_OFF;

		// Create template.
		SaleDiscount saleDiscount = new SaleDiscount(startTime, endTime, saleDays, type);
		
		// Check if template is already exist in DB.
		if ( templates != null && templates.contains(saleDiscount)) {
			new Alert(AlertType.ERROR, EXISTS_MSG, ButtonType.OK).showAndWait();
			return;
		}
		templates.add(saleDiscount);
		
		// Add template and send notification if template was added to DB
		if (clientSalesManager.createSaleTemplate(saleDiscount) == ResultType.OK)
			new Alert(AlertType.INFORMATION, SUCCESS_MSG, ButtonType.OK).showAndWait();
		else
			new Alert(AlertType.ERROR, FAIL_MSG, ButtonType.OK).showAndWait();
	}

	private char isDaySelected(CheckBox day) {
		return day.isSelected() ? 'T' : 'F';
	}

}
