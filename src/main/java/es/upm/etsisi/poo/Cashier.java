package es.upm.etsisi.poo;

import es.upm.etsisi.poo.exceptions.MissingItemException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cashier extends User implements Comparable<Cashier> {
    public static final String COMPANY_DOMAIN = "upm.es";
    private List<Ticket> createdTickets;

    /**
     * Creates new cashier with the id, name and email given in the parameters
     */
    public Cashier(String id, String name, String email) throws IllegalArgumentException{
        // Should handle: valid id format, valid upm email, none of the arguments is null
        super(id, name, email);
        createdTickets = new ArrayList<>();

        if (!isValidId(id)) throw new IllegalArgumentException("Invalid cashier id: " + id);
        else if (!isCompanyEmail(email)) throw new IllegalArgumentException("Invalid cashier email: " + email);
    }

    /**
     * Creates a new ticket the id given in the parameter.
     */
    public Ticket createTicket(int id) throws IllegalArgumentException{
        Ticket created = null;
        created = new Ticket(id);
        createdTickets.add(created);
		return created;
    }

	public Ticket findTicket(int ticketId) throws MissingItemException {
        Iterator<Ticket> iterator = createdTickets.iterator();
        Ticket result = null;
        Ticket currentTicket;

        while (iterator.hasNext() && result == null){
            currentTicket = iterator.next();
            if (currentTicket.getId() == ticketId){
                result = currentTicket;
            }
        }
        if (result == null) throw new MissingItemException("The cashier does not own ticket with id " + ticketId);
		return result;
	}

    /**
     * Returns a string representing the tickets created by this cashier
     * @return A string with the id and state of all the tickets created by this cashier, sorted by id
     */
    public String getTicketsString(){
        StringBuilder result = new StringBuilder();
        createdTickets.sort(null);
        for(Ticket ticket : createdTickets) {
			
			result
				.append("  ")
				.append(ticket.getComposedId())
				.append(" - ");
			
            if (ticket.isEmpty()){
                result.append("EMPTY");
            }else if (ticket.isOpen()){
                result.append("OPEN");
            }else{
                result.append("CLOSE");
            }
            result.append("\n");
        }
        return result.toString();
    }

    /**
     * Returns array of Tickets created by this instance of Cashier
     * @return Array of tickets, zero length array if there are none
     */
    public Ticket[] getTickets() {
        if (!this.createdTickets.isEmpty()) {
            Ticket[] tickets = new Ticket[this.createdTickets.size()];
            for (int i = 0; i < this.createdTickets.size(); i++) {
                tickets[i] = this.createdTickets.get(i);
            }
            return tickets;
        } else {
            return new Ticket[0];
        }
    }

    public int getCreatedTicketAmount() {
        return this.createdTickets.size();
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
	public int compareTo(Cashier c) {
		return this.getName().compareTo(c.getName());
	}

    @Override
    public String toString() {
		return String.format("Cash{identifier='%s', name='%s', email='%s'}",
							 this.getId(), this.getName(), this.getEmail());
	}

}
