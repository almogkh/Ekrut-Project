package ekrut.entity;

import ekrut.entity.ConnectedClient;
import ekrut.entity.UserType;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class InventoryReportItem {
	private IntegerProperty ItemID;
	private StringProperty ItemName;
	private IntegerProperty quantity;
	private IntegerProperty thresholdBreaches;



	public InventoryReportItem(final Integer itemID, final String itemName, final Integer quantity, final Integer thresholdBreaches) {
		this.setItemID(new SimpleIntegerProperty(itemID));
		this.setItemName(new SimpleStringProperty(itemName));
		this.setQuantity(new SimpleIntegerProperty(quantity));
		this.setThresholdBreaches(new SimpleIntegerProperty(thresholdBreaches));
	}


	
	public IntegerProperty getItemID() {
		return ItemID;
	}



	public void setItemID(IntegerProperty itemID) {
		ItemID = itemID;
	}



	public StringProperty getItemName() {
		return ItemName;
	}



	public void setItemName(StringProperty itemName) {
		ItemName = itemName;
	}



	public IntegerProperty getQuantity() {
		return quantity;
	}



	public void setQuantity(IntegerProperty quantity) {
		this.quantity = quantity;
	}



	public IntegerProperty getThresholdBreaches() {
		return thresholdBreaches;
	}



	public void setThresholdBreaches(IntegerProperty thresholdBreaches) {
		this.thresholdBreaches = thresholdBreaches;
	}


/*
	@Override
	public boolean equals(Object o) {
		if(o instanceof ConnectedClient) {
			ConnectedClient other = (ConnectedClient)o;
			return getUsername().equals(other.getUsername());
		}
		return false;
	}
	*/
}


