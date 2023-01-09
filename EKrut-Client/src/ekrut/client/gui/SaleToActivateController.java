package ekrut.client.gui;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

import ekrut.entity.SaleDiscount;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class SaleToActivateController extends HBox {

    @FXML
    private Button activateSaleBtn;

    @FXML
    private Text dayOfSaleTxt;

    @FXML
    private Text endTimeTxt;

    @FXML
    private Text saleTypeTxt;

    @FXML
    private Text startTimeTxt;
    
    private final int DAYS_IN_WEEK = 7;
    private SaleDiscount saleDiscount;
    
    public SaleToActivateController(SaleDiscount saleDiscount) {
    	this.saleDiscount = saleDiscount;
    	ArrayList<String> daysOfsaleList = new ArrayList<>();
    	String dayOfSale = saleDiscount.getDayOfSale();
    	
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SaleToActivate.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		for (Integer i = 0; i < DAYS_IN_WEEK; i++) 
			if(dayOfSale.charAt(i) == 'T') 
				daysOfsaleList.add(getTheDay(i + 1));
			
		String days = daysOfsaleList.toString();
		days = days.substring(1, days.length() - 1);
		
		dayOfSaleTxt.setText(days);
		startTimeTxt.setText(setTime(saleDiscount.getStartTime()));
		endTimeTxt.setText(setTime(saleDiscount.getEndTime()));
		saleTypeTxt.setText(saleDiscount.getType().toString());
		
    }
    
    private String getTheDay(int day) {
    	switch (day) {
		case 1:
			return "Sun";
		case 2:
			return "Mon";
		case 3:
			return "Tue";
		case 4:
			return "Wed";
		case 5:
			return "Thu";
		case 6:
			return "Fri";
		case 7: 
			return "Sat";
		default:
			return null;
		}
    }
    
    public String setTime(LocalTime time) {
		int Hour = time.getHour();
		int Min = time.getMinute();
		return String.format("%02d:%02d", Hour, Min);
    }
    
    @FXML
    void activateSale(ActionEvent event) {

    }

}
