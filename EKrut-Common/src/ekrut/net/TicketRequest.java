package ekrut.net;

import java.io.Serializable;

public class TicketRequest implements Serializable{
	private static final long serialVersionUID = 7134346281040324590L;
	private TicketRequestType action;
	private int ticketId;
	private String status;
	private String ekrutLocation;
}
