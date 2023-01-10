package ekrut.client.gui;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientSalesManager;
import ekrut.entity.SaleDiscount;
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
    
    private final int DAYS_IN_WEEK = 7;
    private SaleDiscount saleDiscount;
    private ClientSalesManager clientSalesManager; 
	private String activeSuccessMsg = "Sale discount was activate successfully.";
	private String activeFaileMsg = "Failed to activate sale discunt. Please check your premissions";
	private String unactiveSuccessMsg = "Sale discount was unactivate successfully.";
	private String unactiveFaileMsg = "Failed to unactivate sale discunt. Please check your premissions";
	private ArrayList<String> daysOfsaleList;

    public SaleToActivateController(SaleDiscount saleDiscount, ArrayList<SaleDiscount> activeSales) {
    	this.saleDiscount = saleDiscount;
    	clientSalesManager = EKrutClientUI.getEkrutClient().getClientSalesManager();
    	daysOfsaleList = new ArrayList<>();
    	
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
		for (Integer i = 0; i < DAYS_IN_WEEK; i++) 
			if(dayOfSale.charAt(i) == 'T') 
				daysOfsaleList.add(getTheDay(i + 1));
			
		String days = daysOfsaleList.toString();
		days = days.substring(1, days.length() - 1);
		
		dayOfSaleTxt.setText(days);
		startTimeTxt.setText(setTime(saleDiscount.getStartTime()));
		endTimeTxt.setText(setTime(saleDiscount.getEndTime()));
		
		String saleType = saleDiscount.getType().toString().equals("ONE_PLUS_ONE") ? "One Plus One" : "30% Off";
		saleTypeTxt.setText(saleType);
		
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
    	if(clientSalesManager.activateSaleForArea(saleDiscount.getDiscountId()) == ResultType.OK) {
    		new Alert(AlertType.INFORMATION, activeSuccessMsg, ButtonType.OK).showAndWait();
    		activateSaleBtn.setVisible(false);
    		deactivateSaleBtn.setVisible(true);
    	}
    	else
    		new Alert(AlertType.ERROR, activeFaileMsg, ButtonType.OK).showAndWait();
    }
    
    @FXML
    void deactivateSale(ActionEvent event) {
    	if(clientSalesManager.deactivateSaleForArea(saleDiscount.getDiscountId()) == ResultType.OK) {
    		new Alert(AlertType.INFORMATION, unactiveSuccessMsg, ButtonType.OK).showAndWait();
    		deactivateSaleBtn.setVisible(false);
    		activateSaleBtn.setVisible(true);
    	}
    	else
    		new Alert(AlertType.ERROR, unactiveFaileMsg, ButtonType.OK).showAndWait();
    }


}
