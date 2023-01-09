package ekrut.client.gui;

import java.util.ArrayList;
import java.util.Optional;

import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientOrderManager;
import ekrut.entity.OrderItem;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;

public class OrderCartViewController {

    @FXML
    private Button agreeAndPayBtn;
 
    @FXML
    private Button backBtn;

    @FXML
    private Button cancelOrderBtn;

    @FXML
    public VBox itemCartVBox;

    @FXML
    private Label priceAfterDiscountLbl;

    @FXML
    private Label priceBeforeDiscountLbl;

    private BaseTemplateController BTC = BaseTemplateController.getBaseTemplateController();
	private ClientOrderManager orderManager;
	private boolean subscriber;

	@FXML
	private void initialize() {
		EKrutClient client = EKrutClientUI.getEkrutClient();
		orderManager = client.getClientOrderManager();
		subscriber = client.getClientSessionManager().getUser().getCustomerInfo().getSubscriberNumber() != -1;
		if (!subscriber)
			priceAfterDiscountLbl.setVisible(false);
		AddItemViewToCartVBox();
		updatePrice();
	}
    
    private void AddItemViewToCartVBox(){
		ArrayList<OrderItem> itemsInCart = orderManager.getActiveOrderItems();
		ObservableList<Node> children = itemCartVBox.getChildren();
		
		for (OrderItem orderItem : itemsInCart)
			children.add(new OrderItemInCartController(this, orderItem));
    	
    }
    
    void updatePrice() {
    	float price = orderManager.getTotalPrice();
    	priceBeforeDiscountLbl.setText(String.format("%.2f", price));
    	if (subscriber)
    		priceAfterDiscountLbl.setText(String.format("%.2f", price - orderManager.getDiscount()));
    }
    
    @FXML
    void agreeAndPay(ActionEvent event) {
    	BTC.switchStages("OrderPaymentView");
    }

    @FXML
    void back(ActionEvent event) {
    	BTC.switchStages("OrderItemBrowser");
    }

    @FXML
    void cancelOrder(ActionEvent event) {
    	Optional<ButtonType> res = new Alert(AlertType.CONFIRMATION,
				"Are you sure you want to cancel the order?",
				ButtonType.YES, ButtonType.NO).showAndWait();
    	res.ifPresent((btn) -> {
			if (btn == ButtonType.YES) {
				orderManager.cancelOrder();
				BTC.switchStages("OrderCreation");
			}
		});
    }
 }
