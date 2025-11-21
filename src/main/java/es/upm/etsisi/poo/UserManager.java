package es.upm.etsisi.poo;

import java.util.*;

/**
 * UserManager creates and stores a set of Clients and Cashiers (both are Users), and provides methods to perform
 * Add/Remove/Find operations on the Client/Cashier set.
 */
public class UserManager {
	// using hashmap here since it will make my life easier, also there is no explicit limit on the amount of users -jy
	private Map<String, Cashier> cashiers;
	private Map<String, Client> clients;

	public static final int MIN_CASHIER_ID = 0;
	public static final int MAX_CASHIER_ID = 9999999;
	private int nextCashierId = MIN_CASHIER_ID;

	// Assuming ticket IDs are global and should be unique across all different Clients/Cashiers
	public static final int MIN_TICKET_ID = 0;
	public static final int MAX_TICKET_ID = 99999;
	private int nextTicketId = MIN_TICKET_ID;

	/**
	 * Creates an empty Cashier/Client set
	 */
	public UserManager() {
		this.cashiers = new HashMap<>();
		this.clients = new HashMap<>();
	}

	// Client related

	/**
	 * Creates a client and adds it to the User set.
	 * @param clientId DNI
	 * @param name Client's name
	 * @param email Client's email, could be any email
	 * @param cashierResponsible Cashier who is linked with the creation of this Client
	 * @return True if successful, false if otherwise
	 */
	public Client addClient(String clientId, String name, String email, Cashier cashierResponsible) {
		// Check null in any field
		if (clientId == null || !Client.isValidId(clientId) || name == null || email == null || cashierResponsible == null)
			return null;

		// Make sure cashierResponsible exists within the Cashier set
		Cashier findCashier = this.findCashier(cashierResponsible.getId());
		if (findCashier == null || !findCashier.equals(cashierResponsible)) // Shortcircuit is fun
			return null;

		// Attempt to find the client with the same ID in the cashier set
		if (!this.clients.containsKey(clientId)) {
			Client newClient = new Client(clientId, name, email, cashierResponsible);
			this.clients.put(clientId, newClient);
			return newClient;
		}

		return null;
	}

	/**
	 * Removes a client from the User set.
	 * @param clientId DNI
	 * @return True if the Client was successfully deleted, false if the Client was not found
	 */
	public boolean removeClient(String clientId) {
		Client removedClient = this.clients.remove(clientId);
		return (removedClient != null);
	}

	/**
	 * Finds the specified Client with the specified ID.
	 * @param clientId DNI
	 * @return Client instance in the set if found, null if not found
	 */
	public Client findClient(String clientId) {
		return this.clients.get(clientId);
	}

	/**
	 * Returns an array of Client instances currently stored in the set.
	 * @return Array of Client, array of zero length if there are none
	 */
	public Client[] listClients() {
		if (!this.clients.isEmpty()) {
			Iterator<Client> clientIterator = this.clients.values().iterator();
			Client[] arrayClient = new Client[this.clients.size()];
			int i = 0;
			while (clientIterator.hasNext()) {
				arrayClient[i] = clientIterator.next();
				i++;
			}
			return arrayClient;
		} else {
			return new Client[0];
		}
	}

	/**
	 * @return Client amount in User set
	 */
	public int getClientAmount() {
		return this.clients.size();
	}

	// Cashier related

	/**
	 * Creates a cashier specifying its ID and adds it to the User set.
	 * @param workerId Cashier ID, must start with UW(7 numbers)
	 * @param name Cashier's name
	 * @param email Cashier's corporate email, must end in specific domain (See Cashier class)
	 * @return True if successful, false if the worker with the same id already exists, or the ID space for cashier is exhausted, or email format is not correct
	 */
	public Cashier addCashier(String workerId, String name, String email) {
		// Sanity check for email and workedId
		if (workerId == null || name == null || email == null)
			return null;
		boolean isEmailValid = Cashier.isCompanyEmail(email);
		boolean isWorkerIdValid = Cashier.isValidId(workerId);

		// If ID space is exhausted, do not add any more Cashiers
		int maximumCashierAmountLimitedById = UserManager.MAX_CASHIER_ID - UserManager.MIN_CASHIER_ID + 1;
		if (this.cashiers.size() >= maximumCashierAmountLimitedById)
			return null;

		if (isEmailValid && isWorkerIdValid) {
			// Attempt to find the cashier with the same ID in the cashier set
			if (this.cashiers.containsKey(workerId))
				return null;

			// Update nextCashierId if needed for auto-increment
			String number = Utils.removeLeadingZeros(workerId.substring(2)); // Remove 'UW', remove leading 0
			int cashierIdValue = Integer.parseInt(number);
			if (cashierIdValue >= this.nextCashierId)
				this.nextCashierId = cashierIdValue + 1;

			// Add the cashier to the set
			Cashier newCashier = new Cashier(workerId, name, email);
			this.cashiers.put(workerId, newCashier);
			return newCashier;
		} else {
			return null;
		}
	}

	/**
	 * Creates a cashier and adds it to the User set, generating its ID automatically (auto-increment if possible, random if not).
	 * @param name Cashier's name
	 * @param email Cashier's corporate email, must end in specific domain (See Cashier class)
	 * @return True if successful, false if the worker with the same id already exists, or the ID space for cashier is exhausted
	 */
	public Cashier addCashier(String name, String email) {
		String newCashierId = this.generateUniqueCashierId();
		return this.addCashier(newCashierId, name, email);
	}

	/**
	 * Removes a cashier from the User set.
	 * @param workerId Cashier's ID
	 * @return True if the Cashier was successfully deleted, false if the Cashier was not found
	 */
	public boolean removeCashier(String workerId){
		Cashier removedCashier = this.cashiers.remove(workerId);
		return (removedCashier != null);
	}

	/**
	 * Finds the specified Cashier with the specified ID.
	 * @param workerId Cashier's ID
	 * @return Cashier instance in the set if found, null if not found
	 */
	public Cashier findCashier(String workerId) {
		return this.cashiers.get(workerId);
	}

	/**
	 * Returns an array of Cashier instances currently stored in the set.
	 * @return Array of Cashier, array of zero length if there are none
	 */
	public Cashier[] listCashiers() {
		Iterator<Cashier> cashierIterator = this.cashiers.values().iterator();
		Cashier[] arrayCashier = new Cashier[this.cashiers.size()];
		int i = 0;
		while (cashierIterator.hasNext()) {
			arrayCashier[i] = cashierIterator.next();
			i++;
		}
		return arrayCashier;
	}

	/**
	 * Returns a list of all the tickets created by any cashier existing in the manager
	 * @return ArrayList containing all the tickets in the system, the list will be empty if no tickets exist
	 */
	public List<Ticket> getAllTickets(){
		List<Ticket> tickets = new ArrayList<>();
		for (Cashier cashier: listCashiers()){
			tickets.addAll(Arrays.asList(cashier.getTickets()));
		}
		return tickets;
	}

	/**
	 * Returns a list of Ticket instances created by the specified Cashier ID in String
	 * @param workerId Cashier's ID
	 * @return Array of Ticket instances created by the specified Cashier, if the cashier has no tickets this will
	 * return zero length array, but if the cashier does not exist, this will return null instead
	 */
	public Ticket[] listCashierTicketsArray(String workerId) {
		if (!Cashier.isValidId(workerId))
			return null;

		Cashier cashier = this.cashiers.get(workerId);
		if (cashier != null)
			return cashier.getTickets();
		else
			return null;
	}

	/**
	 * Returns a list of tickets IDs created by the specified Cashier ID in String
	 * @param workerId Cashier's ID
	 * @return String containing the tickets created by the specified Cashier
	 */
	public String listCashierTickets(String workerId) {
		if (!Cashier.isValidId(workerId))
			return null;

		Cashier cashier = this.cashiers.get(workerId);
		if (cashier != null)
			return cashier.getTicketsString();
		else
			return null;
	}

	/**
	 * @return Cashier amount in User set
	 */
	public int getCashierAmount() {
		return this.cashiers.size();
	}

	/**
	 * Loops through all cashier set and accessing their Tickets to see if given ID is already taken.
	 * @param ticketId ticketId to test, must be a 5-digit number
	 * @return True if the given ID is unique, false if is not a 5-digit number or the ID is already taken
	 */
	public boolean isTicketIdUnique(int ticketId) {
		if (ticketId > MAX_TICKET_ID)
			return false;

		boolean isUnique = true;
		Cashier currentCashier;
		Iterator<Cashier> cashierIterator = this.cashiers.values().iterator();
		do {
			currentCashier = cashierIterator.next();
			Ticket[] cashierTicketList = currentCashier.getTickets();
			int i = 0;
			Ticket ticket;
			while (isUnique && i < cashierTicketList.length) {
				ticket = cashierTicketList[i];
				isUnique = (ticket.getId() != ticketId);
				i++;
			}
		} while (isUnique && cashierIterator.hasNext());
		return isUnique;
	}

	/**
	 * Loops through all cashiers and getting the amount of ticket created by them.
	 * @return Amount of tickets created globally
	 */
	private int getGlobalCashierTicketAmount() {
		int amount = 0;
		for (Cashier c : this.cashiers.values()) {
			amount += c.getCreatedTicketAmount();
		}
		return amount;
	}

	/**
	 * Returns either an auto-incremented or any worker ID available (starting from low to high) for tickets.
	 * Auto-increment ID value takes priority, but will return any ID available if a Ticket with the highest possible
	 * ID value is found within all tickets.
	 * @return 5-Digit numeric ID, guaranteed to be unique in the current set, will return null if all
	 * available numbers are exhausted. Returns ID range between '00000' and '99999'.
	 */
	// NOTE(enrique): Implementation Sugestion: loop through all tickets of all cashiers
	// and find the greatest id value and add 1 to it (maybe even keep a 'greatest id value')
	public Integer generateUniqueTicketId() {
		// Check if ID space for ticket is exhausted
		// int maximumTicketAmountLimitedById = UserManager.MAX_TICKET_ID - UserManager.MIN_TICKET_ID + 1;
		// int globalTicketAmount = this.getGlobalCashierTicketAmount();
		// if (globalTicketAmount >= maximumTicketAmountLimitedById)
		//	return -1;

		// Generate
		if (this.nextTicketId <= MAX_TICKET_ID) { // auto-increment possible
			return this.nextTicketId++;
		} else { // A ticket with the highest possible ID was found in set, fallback to generation by iteration
			int i = UserManager.MIN_TICKET_ID;
			int candidate;
			do { // Generate possible ID candidate until a unique one is found
				candidate = i++;
			} while (!isTicketIdUnique(candidate) && i <= UserManager.MAX_TICKET_ID);
			// Check again if the candidate is unique, or the ID space is already exhausted
			if (isTicketIdUnique(candidate))
				return candidate;
			else
				return null;
		}
	}

	/**
	 * Returns either an auto-incremented or any worker ID available (starting from low to high) for cashier.
	 * Auto-increment ID value takes priority, but will return any ID available if a Cashier with the highest possible
	 * ID value is found within the Cashier set.
	 * @return Worker ID for cashier, guaranteed to be unique in the current set, will return null if all
	 * available numbers are exhausted. Returns ID range between 'UW0000000' and 'UW9999999'.
	 */
    public String generateUniqueCashierId(){
		// This is really fucking overengineered. I'm sorry ?_? -jy
		// Check if ID space for cashier is exhausted
		int maximumCashierAmountLimitedById = UserManager.MAX_CASHIER_ID - UserManager.MIN_CASHIER_ID + 1;
		if (this.cashiers.size() >= maximumCashierAmountLimitedById)
			return null;

		// Generate
		if (this.nextCashierId <= MAX_CASHIER_ID) { // auto-increment possible
        	return String.format("UW%07d", this.nextCashierId++);
		} else { // A cashier with the highest possible ID was found in set, fallback to generation by iteration
			int i = 0;
			String candidate;
			do { // Generate possible ID candidate until a unique one is found
				candidate = String.format("UW%07d", i++); // i++;
			} while (this.cashiers.containsKey(candidate) && i <= UserManager.MAX_CASHIER_ID);
			return candidate;
		}
    }
}
	
