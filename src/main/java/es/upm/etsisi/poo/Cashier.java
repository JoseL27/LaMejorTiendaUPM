package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cashier extends User implements Comparable<Cashier> {
    public static final String COMPANY_DOMAIN = "upm.es";
    private List<Ticket> createdTickets;

    /* Funcionalidades relacionadas que deberian implementar otras clases:
     * -Dar de alta clientes: desde el lugar donde se creen se debe comprobar que existe el cajero y pasar su id al cliente
     * -Operaciones de ticket (add, remove, print y close): El cajero no estara implicado en las operaciones,
     *      unicamente se comprobara si el id que realiza la operacion es el mismo que lo creo
     * -Comprobar si el id existe antes de crear el cajero
     * -Comprobar si el id de ticket existe antes de crear un ticket (desde el comando)
    */


    /**
     * Creates new cashier with the id, name and email given in the parameters
     */
    public Cashier(String id, String name, String email) {
        super(id, name, email);
        createdTickets = new ArrayList<>();
    }

    /**
     * Creates a new ticket the id given in the parameter.
     */
    public boolean createTicket(int id){
        boolean result = false;
        if (Ticket.isValidId(id)){
            createdTickets.add(new Ticket(id));
            result = true;
        }
		return result;
    }

	public Ticket findTicket(int ticketId) {
        Iterator<Ticket> iterator = createdTickets.iterator();
        Ticket result = null;
        Ticket currentTicket;

        while (iterator.hasNext() && result == null){
            currentTicket = iterator.next();
            if (currentTicket.getId() == ticketId){
                result = currentTicket;
            }
        }
		return result;
	}

    /**
     * Returns a string representing the tickets created by this cashier
     * @return A string with the id and state of all the tickets created by this cashier, sorted by id
     */
    public String getTicketsString(){
        StringBuilder result = new StringBuilder();
        createdTickets.sort(null);
        for(Ticket ticket: createdTickets){
            result.append(ticket.getComposedId()).append(" ");
            if (ticket.isEmpty()){
                result.append("Empty");
            }else if (ticket.isOpen()){
                result.append("Open");
            }else{
                result.append("Closed");
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
     * @return true if email contains a single @ and COMPANY_DOMAIN after it
     */
    public static boolean isCompanyEmail(String email){
        boolean result = true;
        String[] splitEmail = email.split("@");

        if (splitEmail.length != 2 || !splitEmail[1].equals(COMPANY_DOMAIN)) {
            result = false;
        }
        return result;
    }

	public static boolean isValidId(String id) {
		return id.length() == 9 
			&& Character.toUpperCase(id.charAt(0)) == 'U'
			&& Character.toUpperCase(id.charAt(1)) == 'W'
			&& (Utils.tryParseInt(id.substring(2)) != null);
	}

	@Override
	public int compareTo(Cashier c) {
		return this.getName().compareTo(c.getName());
	}

    @Override
    public String toString() {
		return "{Cashier:" + this.getId() + ", name:'" + this.getName() + "', email:'" + this.getEmail() + "'}";
	}

}
