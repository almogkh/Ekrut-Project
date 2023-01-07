package ekrut.client.gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class PaymentViewController implements Initializable {

	// private final

	@FXML
	private Button backBtn;

	@FXML
	private Button cancelOrderBtn;

	@FXML
	private Button payBtn;

	@FXML
	private ToggleGroup creditCardChoice;

	@FXML
	private RadioButton currentCreditCardRadioBtn;

	@FXML
	private RadioButton newCreditCardRadioBtn;

	@FXML
	private Label hidenCardNumbersLbl;

	@FXML
	private Label lastFourDigitLbl;

	@FXML
	private Label priceLbl;

	@FXML
	private ComboBox<String> monthCBox;

	@FXML
	private ComboBox<String> yearCBox;

	@FXML
	private TextField newCardNumberTxt;

	@FXML
	private TextField newCvcTxt;

	// Q.Nir - there is another way to do this with out using initialize?
	public void initialize(URL location, ResourceBundle resources) {
		// initialize experation card years
		for (Integer i = 2040; i > 2021; i--)
			yearCBox.getItems().add(i.toString());

		// initialize experation card months
		for (Integer i = 1; i < 13; i++) {
			if (i < 10)
				monthCBox.getItems().add("0" + i.toString());
			else
				monthCBox.getItems().add(i.toString());
		}
	}

	@FXML
	void back(ActionEvent event) {

	}

	@FXML
	void cancelOrder(ActionEvent event) {

	}

	@FXML
	void months(ActionEvent event) {

	}

	@FXML
	void useOldCard(ActionEvent event) {
		setEditableCard(0.3, true);

	}

	@FXML
	void useNewCard(ActionEvent event) {
		setEditableCard(1, false);
	}

	private void setEditableCard(double opacity, boolean state) {
		newCardNumberTxt.setDisable(state);
		newCvcTxt.setDisable(state);
		monthCBox.setDisable(state);
		yearCBox.setDisable(state);
		hidenCardNumbersLbl.setOpacity(1.3 - opacity);
		lastFourDigitLbl.setOpacity(1.3 - opacity);
	}

	@FXML
	void years(ActionEvent event) {

	}

}
