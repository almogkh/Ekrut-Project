package ekrut.net;

import java.io.Serializable;
import ekrut.entity.Order;

public class OrderRequest implements Serializable{
	private static final long serialVersionUID = -4824573021802436936L;
	private OrderRequestType action;
	private int orderId;
	private Order order;
	
}
