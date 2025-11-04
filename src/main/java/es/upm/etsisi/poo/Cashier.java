package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.List;

public class Cashier extends User {
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
     * @throws IllegalArgumentException when there is already a cashier with this id or email is not a company email
     * This should receive an already valid id, name and email, so no exception throwing should be needed, remove once the command is implemented
     */
    public Cashier(String id, String name, String email) throws IllegalArgumentException{

        super(id, name, email);
        if (!isCompanyEmail(email)){
            throw new IllegalArgumentException("Cant create cashier, email " + email + " is not a valid company email");
        }

        createdTickets = new ArrayList<>();
    }

    /**
     * Creates new cashier with the name and email given in the parameters and a randomly generated id
     */
    public Cashier(String name, String email){
        this(generateId(), name, email);
    }

    /**
     * Creates a new ticket the id given in the parameter
     */
    public boolean createTicket(String id){
        boolean result = true;

        createdTickets.add(new Ticket());
        // TODO: create ticket with generated id once ticket id is implemented
        //createdTickets.add(new Ticket(id));
        return result;
    }

    /**
     * Returns a string representing the tickets created by this cashier
     * @return A string with the id and state of all the tickets created by this cashier, sorted by id
     */
    public String getTicketsString(){
        StringBuilder result = new StringBuilder();
        //sort array
        for(Ticket ticket: createdTickets){
            //append id and state
        }
        return result.toString();
    }

    /**
     * Checks if email is a company email
     * @return true if email contains a single @ and COMPANY_DOMAIN after it
     */
    public boolean isCompanyEmail(String email){
        boolean result = true;
        String[] splitEmail = email.split("@");

        if (splitEmail.length != 2 || !splitEmail[1].equals(COMPANY_DOMAIN)) {
            result = false;
        }
        return result;
    }

    /**
     * Generates a random cashier id
     * @return String with format UWnnnnnnn, being n integer digits
     */
    private static String generateId(){
        return "UW" + (int)(Math.random() * 10000000);
    }
}
