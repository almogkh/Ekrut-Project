package ekrut.client.gui;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientSalesManager;
import ekrut.entity.SaleDiscount;
import ekrut.entity.SaleDiscountType;
import ekrut.net.ResultType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class SaleToActivateController extends HBox {

    @FXML
    private Button activateSaleBtn;
    
    @FXML
    private Button deactivateSaleBtn;

    @FXML
    private Text dayOfSaleTxt;

    @FXML
    private Text endTimeTxt;

    @FXML
    private Text saleTypeTxt;

    @FXML
    private Text startTimeTxt;
    
    private static final int DAYS_IN_WEEK = 7;
    private static final String ACTIVATION_SUCCESS_MSG = "Sale discount was activated successfully.";
    private static final String ACTIVATION_FAIL_MSG = "Failed to activate sale discount. Please check your permissions";
    private static final String DEACTIVATION_SUCCESS_MSG = "Sale discount was deactivated successfully.";
    private static final String DEACTIVATION_FAIL_MSG = "Failed to deactivate sale discount. Please check your permissions";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    private SaleDiscount saleDiscount;
    private ClientSalesManager clientSalesManager; 

    public SaleToActivateController(SaleDiscount saleDiscount, ArrayList<SaleDiscount> activeSales) {
    	this.saleDiscount = saleDiscount;
    	clientSalesManager = EKrutClientUI.getEkrutClient().getClientSalesManager();
    	ArrayList<String> daysOfSale = new ArrayList<>();
    	
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SaleToActivate.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		if (activeSales.contains(saleDiscount)) {
    		activateSaleBtn.setVisible(false);
    		deactivateSaleBtn.setVisible(true);
		}
		
		String dayOfSale = saleDiscount.getDayOfSale();
		for (int i = 0; i < DAYS_IN_WEEK; i++) 
			if(dayOfSale.charAt(i) == 'T') 
				daysOfSale.add(getTheDay(i + 1));
			
		String days = daysOfSale.toString();
		days = days.substring(1, days.length() - 1);
		
		dayOfSaleTxt.setText(days);
		startTimeTxt.setText(setTime(saleDiscount.getStartTime()));
		endTimeTxt.setText(setTime(saleDiscount.getEndTime()));
		
		String saleType = saleDiscount.getType() == SaleDiscountType.ONE_PLUS_ONE ? "One Plus One" : "30% Off";
		saleTypeTxt.setText(saleType);
    }
    
    private String getTheDay(int day) {
    	switch (day) {
		case 1: return "Sun";
		case 2: return "Mon";
		case 3: return "Tue";
		case 4: return "Wed";
		case 5: return "Thu";
		case 6: return "Fri";
		case 7: return "Sat";
		default: return null;
		}
    }
    
    private String setTime(LocalTime time) {
    	return time.format(FORMATTER);
    }
    
    @FXML
    void activateSale(ActionEvent event) {
    	if(clientSalesManager.activateSaleForArea(saleDiscount.getDiscountId()) == ResultType.OK) {
    		new Alert(AlertType.INFORMATION, ACTIVATION_SUCCESS_MSG, ButtonType.OK).showAndWait();
    		activateSaleBtn.setVisible(false);
    		deactivateSaleBtn.setVisible(true);
    	}
    	else
    		new Alert(AlertType.ERROR, ACTIVATION_FAIL_MSG, ButtonType.OK).showAndWait();
    }
    
    @FXML
    void deactivateSale(ActionEvent event) {
    	if(clientSalesManager.deactivateSaleForArea(saleDiscount.getDiscountId()) == ResultType.OK) {
    		new Alert(AlertType.INFORMATION, DEACTIVATION_SUCCESS_MSG, ButtonType.OK).showAndWait();
    		deactivateSaleBtn.setVisible(false);
    		activateSaleBtn.setVisible(true);
    	}
    	else
    		new Alert(AlertType.ERROR, DEACTIVATION_FAIL_MSG, ButtonType.OK).showAndWait();
    }


}
