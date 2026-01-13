package es.upm.etsisi.poo;

import es.upm.etsisi.poo.exceptions.*;

import java.io.Serializable;
import java.util.*;

/**
 * UserManager creates and stores a set of Clients and Cashiers (both are Users), and provides methods to perform
 * Add/Remove/Find operations on the Client/Cashier set.
 */
public class UserManager implements Serializable {
	// using hashmap here since it will make my life easier, also there is no explicit limit on the amount of users -jy
	private Map<String, User> users;
    
	public static final int MIN_CASHIER_ID = 0;
	public static final int MAX_CASHIER_ID = 9999999;
	private int nextCashierId = MIN_CASHIER_ID;
    
	// Assuming ticket IDs are global and should be unique across all different Clients/Cashiers
	public static final int MIN_TICKET_ID = 0;
	public static final int MAX_TICKET_ID = 99999;
	private int nextTicketId = MIN_TICKET_ID;
    
	private static UserManager instance;
    
	public static UserManager getInstance() {
		if (instance == null) {
			if (Serialize.dataStore.containsKey("UserManager")) {
				instance = (UserManager) Serialize.dataStore.get("UserManager");
			} else {
				instance = new UserManager();
				Serialize.dataStore.put("UserManager", instance);
			}
		}
		return instance;
	}
    
	private UserManager() {
		this.users = new HashMap<>();
	}
    
	// Client related
    
	/**
	 * Creates a client and adds it to the User set.
	 * @param clientId DNI
	 * @param name Client's name
	 * @param email Client's email, could be any email
	 * @param assignedCashierId Cashier who is linked with the creation of this Client
	 * @return The client that was created
	 */
	public Client addClient(String clientId, String name, String email, String assignedCashierId) throws InvalidDataException, DuplicateItemException, MissingItemException {
		// Make sure assignedCashierId exists within the Cashier set
		Cashier foundCashier = this.findCashier(assignedCashierId);
		Client newClient = new Client(clientId, name, email, foundCashier);
		
		// Attempt to find the client with the same ID in the cashier set
		if (this.users.containsKey(clientId)) throw new DuplicateItemException("Client with id " + clientId + " already exists");
		
		this.users.put(clientId, newClient);
		return newClient;
        
	}
    
	/**
	 * Removes a client from the User set.
	 * @param clientId DNI
	 * @return True if the Client was successfully deleted, false if the Client was not found
	 */
	public void removeClient(String clientId) throws MissingItemException{
		User removedClient = this.users.remove(clientId);
        if (removedClient == null) throw new MissingItemException("Client with id " + clientId + " not found");
	}
    
	/**
	 * Finds the specified Client with the specified ID.
	 * @param clientId DNI
	 * @return Client instance in the set if found, null if not found
	 */
	public Client findClient(String clientId) throws MissingItemException{
		Client result = (Client)this.users.get(clientId);
        if (result == null) throw new MissingItemException("Client with id " + clientId + " not found");
        return result;
	}
    
	/**
	 * Returns an array of Client instances currently stored in the set.
	 * @return Array of Client, array of zero length if there are none
	 */
	public ArrayList<Client> getClients() {
		Collection<User> allUsers = this.users.values();
		ArrayList<Client> result = new ArrayList<>(allUsers.size() / 2);
		
		for (User user : allUsers) {
			if (user instanceof Client){
				result.add((Client)user);
			}
		}
		
		return result;
	}
    
	// Cashier related
    
	/**
	 * Creates a cashier specifying its ID and adds it to the User set.
	 * @param workerId Cashier ID, must start with UW(7 numbers)
	 * @param name Cashier's name
	 * @param email Cashier's corporate email, must end in specific domain (See Cashier class)
	 * @return The cashier that was created
     * @throws DataException if the worker with the same id already exists, or the ID space for cashier is exhausted, or email format is not correct
	 */
	public Cashier addCashier(String workerId, String name, String email) throws DuplicateItemException, InvalidDataException {
		// Attempt to find the cashier with the same ID in the cashier set
		if (this.users.containsKey(workerId)) { 
			throw new DuplicateItemException("Cashier with id " + workerId + " already exists");
		}
		
		int cashierIdValue = Integer.parseInt(workerId.substring(2));
		if (cashierIdValue >= this.nextCashierId)
			this.nextCashierId = cashierIdValue + 1;
		
		// Add the cashier to the set
		Cashier newCashier = new Cashier(workerId, name, email);
		this.users.put(workerId, newCashier);
		return newCashier;
	}
    
	/**
	 * Creates a cashier and adds it to the User set, generating its ID automatically (auto-increment if possible, random if not).
	 * @param name Cashier's name
	 * @param email Cashier's corporate email, must end in specific domain (See Cashier class)
	 * @return The cashier that was created
     * @throws DataException if the worker with the same id already exists, or the ID space for cashier is exhausted
	 */
	public Cashier addCashier(String name, String email) throws DataException {
		String newCashierId = this.generateUniqueCashierId();
		return this.addCashier(newCashierId, name, email);
	}
    
	/**
	 * Removes a cashier from the User set.
	 * @param workerId Cashier's ID
	 * @throws MissingItemException if the cashier is not found
	 */
	public void removeCashier(String workerId) throws MissingItemException{
		User removedCashier = this.users.remove(workerId);
        if (removedCashier == null) throw new MissingItemException("Cashier with id " + workerId + " not found");
	}
    
	/**
	 * Finds the specified Cashier with the specified ID.
	 * @param workerId Cashier's ID
	 * @return Cashier instance in the set if found
     * @throws MissingItemException if the cashier is not found
	 */
	public Cashier findCashier(String workerId) throws MissingItemException{
        Cashier result = (Cashier)this.users.get(workerId);
        if (result == null) throw new MissingItemException("Cashier with id " + workerId + " not found");
		return result;
	}
    
	/**
	 * Returns an array of Cashier instances currently stored in the set.
	 * @return Array of Cashier, array of zero length if there are none
	 */
	public ArrayList<Cashier> getCashiers() {
		Collection<User> allUsers = this.users.values();
		ArrayList<Cashier> result = new ArrayList<>(allUsers.size() / 2);
		
		for (User user : allUsers) {
			if (user instanceof Cashier){
				result.add((Cashier) user);
			}
		}
		
		return result;
	}
    
	/**
	 * Returns a list of all the tickets created by any cashier existing in the manager
	 * @return ArrayList containing all the tickets in the system, the list will be empty if no tickets exist
	 */
	public ArrayList<Ticket> getAllTickets(){
		ArrayList<Ticket> result = new ArrayList<>();
		
		Collection<User> allUsers = this.users.values();
		for (User user : allUsers) {
			if (user instanceof Cashier cash){
				result.addAll(cash.getTickets());
			}
		}
		
		return result;
	}
    
	/**
	 * Loops through all cashier set and accessing their Tickets to see if given ID is already taken.
	 * @param ticketId ticketId to test, must be a 5-digit number
	 * @return True if the given ID is unique, false if is not a 5-digit number or the ID is already taken
	 */
	public boolean isTicketIdUnique(String ticketId) {
		boolean isUnique = true;
		List<Cashier> cashiers = this.getCashiers();
		
		for (Cashier cash : cashiers) {
			try {
				cash.findTicket(ticketId);
				isUnique = false;
			} catch (MissingItemException e) {
				isUnique = true;
			}
		}
		
		return isUnique;
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
	public int generateUniqueTicketId() throws IdSpaceExhaustedException{
		
		if (this.nextTicketId <= MAX_TICKET_ID) {
			return this.nextTicketId++;
			
		} else { 
			// A ticket with the highest possible ID was found in set, fallback to generation by iteration
			int candidate = UserManager.MIN_TICKET_ID;
			while (!isTicketIdUnique(Integer.toString(candidate)) && candidate <= UserManager.MAX_TICKET_ID) {
				candidate++;
			}
			
			if (candidate >= UserManager.MAX_TICKET_ID) {
                throw new IdSpaceExhaustedException("All of the ids for tickets have already been used");
			}
			
            return candidate;
		}
	}
    
	/**
	 * Returns either an auto-incremented or any worker ID available (starting from low to high) for cashier.
	 * Auto-increment ID value takes priority, but will return any ID available if a Cashier with the highest possible
	 * ID value is found within the Cashier set.
	 * @return Worker ID for cashier, guaranteed to be unique in the current set, will return null if all
	 * available numbers are exhausted. Returns ID range between 'UW0000000' and 'UW9999999'.
	 */
    public String generateUniqueCashierId() throws IdSpaceExhaustedException{
		// This is really fucking overengineered. I'm sorry ?_? -jy
		// Check if ID space for cashier is exhausted
		int maximumCashierAmountLimitedById = UserManager.MAX_CASHIER_ID - UserManager.MIN_CASHIER_ID + 1;
		if (this.getCashiers().size() >= maximumCashierAmountLimitedById)
            throw new IdSpaceExhaustedException("All of the ids for cashiers have already been used");
        
		// Generate
		if (this.nextCashierId <= MAX_CASHIER_ID) { // auto-increment possible
        	return String.format("UW%07d", this.nextCashierId++);
		} else { // A cashier with the highest possible ID was found in set, fallback to generation by iteration
			int i = 0;
			String candidate;
			do { // Generate possible ID candidate until a unique one is found
				candidate = String.format("UW%07d", i++); // i++;
			} while (this.users.containsKey(candidate) && i <= UserManager.MAX_CASHIER_ID);
			return candidate;
		}
    }
}
