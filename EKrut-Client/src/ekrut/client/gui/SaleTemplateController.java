package ekrut.client.gui;

import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientSalesManager;
import ekrut.entity.SaleDiscount;
import ekrut.entity.SaleDiscountType;
import ekrut.net.ResultType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert.AlertType;

public class SaleTemplateController implements Initializable {

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

	private final int HUORS_IN_DAY = 24;
	private final int MINUTES_IN_HOUR = 60;

	private Integer startHour;
	private Integer startMin;
	private Integer endHour;
	private Integer endMin;
	private String[] hours = new String[HUORS_IN_DAY];
	private String[] minutes = new String[MINUTES_IN_HOUR];
	private String[] saleType = { "30% off", "One Plus One" };
	private StringBuilder dayOfSale = new StringBuilder("FFFFFFF");
	private ArrayList<SaleDiscount> templateList = new ArrayList<>();
	private String successMsg = "Sale discount tamplate was successfully added.";
	private String faileMsg = "Failed to add template.";
	private String existMsg = "Template already exist!.";
	private String missingInputErrorMsg = "Sorry, it looks like one or more details are missing."
			+ " Please check that all required fields have been filled out and try again.";
	private String logicTimeError = "There is no logic in the time that you set to sale discount."
			+ " Please check all the required fields and try again.";

	private ClientSalesManager clientSalesManager;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		clientSalesManager = EKrutClientUI.getEkrutClient().getClientSalesManager();
		templateList = clientSalesManager.fetchSaleTemplates();

		for (Integer i = 0; i < HUORS_IN_DAY; i++) {
			if (i - 10 < 0)
				hours[i] = '0' + i.toString();
			else
				hours[i] = i.toString();
		}
		startTimeInHourChoiceB.getItems().addAll(hours);
		endTimeInHourChoiceB.getItems().addAll(hours);

		for (Integer i = 0; i < MINUTES_IN_HOUR; i++) {
			if (i - 10 < 0)
				minutes[i] = '0' + i.toString();
			else
				minutes[i] = i.toString();
		}
		startTimeInMinChoiceB.getItems().addAll(minutes);
		endTimeInMinChoiceB.getItems().addAll(minutes);
		
		saleTypeChoiceB.getItems().addAll(saleType);
	}

	@FXML
	void makeSaleTemplate(ActionEvent event) {

		dayOfSale.setCharAt(0, isDaySelected(sundayCheckB));
		dayOfSale.setCharAt(1, isDaySelected(mondayCheckB));
		dayOfSale.setCharAt(2, isDaySelected(tuesdayCheckB));
		dayOfSale.setCharAt(3, isDaySelected(wednesdayCheckB));
		dayOfSale.setCharAt(4, isDaySelected(thursdayCheckB));
		dayOfSale.setCharAt(5, isDaySelected(fridayCheckB));
		dayOfSale.setCharAt(6, isDaySelected(saturdayCheckB));

		if (startTimeInHourChoiceB.getValue() == null   || endTimeInHourChoiceB.getValue() == null
			|| startTimeInMinChoiceB.getValue() == null || endTimeInMinChoiceB.getValue() == null
			|| dayOfSale.toString().equals("FFFFFFF")   || saleTypeChoiceB.getValue() == null) {

			new Alert(AlertType.ERROR, missingInputErrorMsg, ButtonType.OK).showAndWait();
			return;
		}

		startHour = Integer.parseInt(startTimeInHourChoiceB.getValue());
		startMin = Integer.parseInt(startTimeInMinChoiceB.getValue());
		endHour = Integer.parseInt(endTimeInHourChoiceB.getValue());
		endMin = Integer.parseInt(endTimeInMinChoiceB.getValue());

		// Check if the input time is llegal.
		if (startHour > endHour) {
			new Alert(AlertType.ERROR, logicTimeError, ButtonType.OK).showAndWait();
			return;
		}

		if (startHour == endHour && startMin > endMin) {
			new Alert(AlertType.ERROR, logicTimeError, ButtonType.OK).showAndWait();
			return;
		}

		LocalTime startTime = LocalTime.of(startHour, startMin);
		LocalTime endTime = LocalTime.of(endHour, endMin);
		
		// Q.Nir - Is ok to be specific? its fits only for these two typse, if we will
		// add another type it will be problem.
		SaleDiscountType type = saleTypeChoiceB.getValue().equals("One Plus One") ? SaleDiscountType.ONE_PLUS_ONE
				: SaleDiscountType.THIRTY_PERCENT_OFF;

		// Create template.
		SaleDiscount saleDiscount = new SaleDiscount(startTime.toString(), endTime.toString(), dayOfSale.toString(), type);
		
		// Check if template is already exist in DB.
		if (templateList.contains(saleDiscount)) {
			new Alert(AlertType.ERROR, existMsg, ButtonType.OK).showAndWait();
			return;
		}
		templateList.add(saleDiscount);
		
		// Add template and send notification if template was added to DB
		if (clientSalesManager.createSaleTemplate(saleDiscount) == ResultType.OK)
			new Alert(AlertType.INFORMATION, successMsg, ButtonType.OK).showAndWait();
		else
			new Alert(AlertType.ERROR, faileMsg, ButtonType.OK).showAndWait();
	}

	public char isDaySelected(CheckBox day) {
		return day.isSelected() ? 'T' : 'F';
	}

}
