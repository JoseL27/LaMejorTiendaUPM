package es.upm.etsisi.poo;

public class UserManager {
	
	public UserManager() {
	}
	
	public Client findClient(String clientId) {
		System.out.println("UserManager.findClient: NOT IMPLEMENTED");
		return null;
	}

	public Cashier findCashier(String cashierId) {
		System.out.println("UserManager.findCashier: NOT IMPLEMENTED");
		return null;
	}

	public boolean isTicketIdUnique(int ticketId) {
		System.out.println("UserManager.isTicketIdUnique: NOT IMPLEMENTED");
		return false;
	}

	// NOTE(enrique): Implementation Sugestion: loop through all tickets of all cashiers
	// and find the greatest id value and add 1 to it (maybe even keep a 'greatest id value')
	public int generateUniqueTicketId() {
		System.out.println("UserManager.generateUniqueTicketId: NOT IMPLEMENTED");
		return -1;
	}
}
	
