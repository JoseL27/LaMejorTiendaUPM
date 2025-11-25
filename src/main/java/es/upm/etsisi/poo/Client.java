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

	public void addTicket(int ticketId) throws Exception {
		if (ticketIds.contains(ticketId)) {
			throw new Exception("failed to add ticket to client");
		}
		ticketIds.add(ticketId);
	}

	public static boolean isValidId(String id) {
		return id.length() == 9 
			&& Character.isLetter(id.charAt(8))
			&& (Integer.parseInt(id.substring(0, 7)) >= 0);
	}

	public static String validId(String id) throws Exception {
		if (!isValidId(id)) {
			 throw new Exception(String.format("invalid client id '%s' expected 8 digits followed by a letter\n", id));
		}
		return id;
	}

	@Override
	public int compareTo(Client c) {
		return this.getName().compareTo(c.getName());
	}

	@Override
	public String toString() {
		return String.format("Client{identifier='%s', name='%s', email='%s', cash=%s}",
							 this.getId(), this.getName(), this.getEmail(), this.managedBy.getId());
	}
}
