package es.upm.etsisi.poo;

import es.upm.etsisi.poo.exceptions.MissingItemException;

import java.util.*;

public class Cashier extends User {
    public static final String COMPANY_DOMAIN = "upm.es";
    private HashMap<String, Ticket> tickets;
	
    /**
     * Creates new cashier with the id, name and email given in the parameters
     */
    public Cashier(String id, String name, String email) throws IllegalArgumentException{
        // Should handle: valid id format, valid upm email, none of the arguments is null
        super(id, name, email);
        this.tickets = new HashMap<>();
		
        if (!isValidId(id)) { 
			throw new IllegalArgumentException("Invalid cashier id: " + id);
		}
        if (!isCompanyEmail(email)) { 
			throw new IllegalArgumentException("Invalid cashier email: " + email);
		}
    }
	
    /**
     * Creates a new ticket the id given in the parameter.
     */
    public Ticket createTicket(int id, boolean isCustomId) throws IllegalArgumentException {
        Ticket created = new ProductTicket(id, isCustomId);
        this.tickets.put(created.getComposedId(), created);
		return created;
    }
	
	public Ticket findTicket(String ticketId) throws MissingItemException {
		Ticket result = this.tickets.get(ticketId);
        
        if (result == null) { 
			throw new MissingItemException("The cashier does not own ticket with id " + ticketId);
		}
		
		return result;
	}
	
    /**
     * Returns array of Tickets created by this instance of Cashier
     * @return Array of tickets, zero length array if there are none
     */
    public Collection<Ticket> getTickets() {
		return tickets.values();
    }
	
    /**
     * Checks if email is a company email
     * @return true if email is not null and contains a single @ and COMPANY_DOMAIN after it
     */
    public static boolean isCompanyEmail(String email){
        boolean result = true;
		
        if (email == null){
            result =  false;
        }else {
            String[] splitEmail = email.split("@");
			
            if (splitEmail.length != 2 || !splitEmail[1].equals(COMPANY_DOMAIN)) {
                result = false;
            }
        }
        return result;
    }
	
	public static boolean isValidId(String id) {
		return id != null
            && id.length() == 9
			&& Character.toUpperCase(id.charAt(0)) == 'U'
			&& Character.toUpperCase(id.charAt(1)) == 'W'
			&& (App.tryParseInt(id.substring(2)) != null);
	}
	
    @Override
		public String toString() {
		return String.format("Cash{identifier='%s', name='%s', email='%s'}",
							 this.getId(), this.getName(), this.getEmail());
	}
	
}
