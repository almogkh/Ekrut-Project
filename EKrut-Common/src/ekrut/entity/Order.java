package ekrut.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Order implements Serializable{
	private static final long serialVersionUID = -1777296007338157917L;
	private int orderId;
	private LocalDateTime date;
	private int sumAmount;
	private String status;
	private LocalDateTime dueDate;
	private String clientAddress;
	private String ekrutLocation;
}
