package ekrut.net;

import java.io.Serializable;
import java.util.ArrayList;

import ekrut.entity.Ticket;

/**
 * A class that represents the response to a ticket request.
 *
 * @author Noy Malka
 */

public class TicketResponse implements Serializable{
	private static final long serialVersionUID = 5458389358466759549L;
	private ResultType resultType;
	private ArrayList<Ticket> ticketsList;
	
	
	public TicketResponse(ResultType resultType) {
		this.resultType=resultType;
	}

	public TicketResponse(ResultType resultType, ArrayList<Ticket> ticketsList) {
		this.resultType = resultType;
		this.ticketsList = ticketsList;
	}


	public ResultType getResultType() {
		return resultType;
	}


	public ArrayList<Ticket> getTicketsList() {
		return ticketsList;
	}


	public void setTicketsList(ArrayList<Ticket> ticketsList) {
		this.ticketsList = ticketsList;
	}
	
}
