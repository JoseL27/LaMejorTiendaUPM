package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.List;

public class Client extends User {

	private final Cashier managedBy;
	private final List<String> ticketIds;

	public Client(String id, String name, String email, Cashier cashier) {
		super(id, name, email);
		this.managedBy = cashier;
		this.ticketIds = new ArrayList<>();
	}

	public void createTicket(String ticketId) {
		ticketIds.add(ticketId);
	}
}
