package ekrut.net;

import java.io.Serializable;

public class InventoryItemRequest implements Serializable{
	private static final long serialVersionUID = 6395346832081668652L;
	private InventoryItemRequestType action;
	private int itemId;
	private int quantity;
	private String ekrutLocation;
	private int threshold;
	
}
