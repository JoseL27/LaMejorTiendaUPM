package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.List;

public class Client extends User implements Comparable<Client> {

	private final Cashier managedBy;
	private final List<Integer> ticketIds;

	public Client(String id, String name, String email, Cashier cashier) {
		super(id, name, email);
		this.managedBy = cashier;
		this.ticketIds = new ArrayList<>();
	}

	public void addTicket(int ticketId) {
		ticketIds.add(ticketId);
	}

	public static boolean isValidId(String id) {
		return id.length() == 9 
			&& Character.isLetter(id.charAt(8))
			&& (Utils.tryParseInt(id.substring(0, 7)) != null);
	}

	@Override
	public int compareTo(Client c) {
		return this.getName().compareTo(c.getName());
	}

	@Override
	public String toString() {
		return "{Client:" + this.getId() + ", name:'" + this.getName() + "', email:'" + this.getEmail() + "', created by:" + this.managedBy.getId() + "}";
	}
}
