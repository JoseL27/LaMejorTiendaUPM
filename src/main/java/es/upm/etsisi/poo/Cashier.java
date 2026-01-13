package es.upm.etsisi.poo;

import es.upm.etsisi.poo.exceptions.MissingItemException;
import es.upm.etsisi.poo.exceptions.InvalidDataException;

import java.util.*;

public class Cashier extends User {
    public static final String COMPANY_DOMAIN = "upm.es";
    private HashMap<String, Ticket> tickets;
	
    /**
     * Creates new cashier with the id, name and email given in the parameters
     */
    public Cashier(String id, String name, String email) throws InvalidDataException {
        // Should handle: valid id format, valid upm email, none of the arguments is null
        super(id, name, email);
        this.tickets = new HashMap<>();
		
        if (!isValidId(id)) { 
			throw new InvalidDataException("Invalid cashier id: " + id);
		}
        if (!isCompanyEmail(email)) { 
			throw new InvalidDataException("Invalid cashier email: " + email);
		}
    }
	
    /**
     * Creates a new ticket the id given in the parameter.
     */
    public void addTicket(Ticket ticketToAdd) {
        this.tickets.put(ticketToAdd.getComposedId(), ticketToAdd);
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
        boolean result = false;
		
        if (email != null) {
            String[] splitEmail = email.split("@");
			
			if (splitEmail.length == 2) {
				result = splitEmail[0].length() > 0 && 
					splitEmail[1].equals(COMPANY_DOMAIN);
			}
        }
        return result;
    }
	
	public static boolean isValidId(String id) {
		boolean result = (id != null
						  && id.length() == 9
						  && Character.toUpperCase(id.charAt(0)) == 'U'
						  && Character.toUpperCase(id.charAt(1)) == 'W');
		
		if (result) {
			try {
				Integer.parseInt(id.substring(2));
			} catch (NumberFormatException e) {
				result = false;
			}
		}
		
		return result;
		
	}
	
    @Override
		public String toString() {
		return String.format("Cash{identifier='%s', name='%s', email='%s'}",
							 this.getId(), this.getName(), this.getEmail());
	}
	
}
