package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.*;
import es.upm.etsisi.poo.exceptions.*;


import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Represents a command that falls under the ticket category, being those:
 * - ticket new (resetea ticket en curso)
 * - ticket add <prodId> <cantidad> (agrega al ticket la cantidad de ese producto)
 * - ticket remove <prodId> (elimina todas las apariciones del producto, revisa si existe el id)
 * - ticket print (imprime factura)
 */
public class TicketCommand implements Command {
    
    /**
     * First entrypoint to parse 'ticket' command
     * This method is responsible for parsing different subcommands.
     *
     * @param params      The token stream to parse on each subcommand
     */
    public void eval(String[] params) throws FailedCommandException, DataException {
        // Parse
        if (!App.checkMinArgsCountWithPrint("ticket", params.length, 2)) return;
        
        // Execute
        switch (params[1].toLowerCase()) {
            case "new" -> evalNew(params);
            case "add" -> evalAdd(params);
            case "remove" -> evalRemove(params);
            case "print" -> evalPrint(params);
            case "list" -> evalList(params);
            default -> System.out.println("ticket: invalid sub command");
        }
    }
    
    /**
     * Parses the 'new' subcommand of the 'ticket' command and
     * adds a new creates a new ticket for a client and managed by a cashier.
     * <p>
     * Format:
     * ticket new [<id>] <cashId> <userId>
     *
     * @param params      The stream of tokens to parse
     * @param userManager Context
     * @param inventory   Context
     */
    private void evalNew(String[] params) throws FailedCommandException{
        // Parse
        if (!App.checkArgsCountWithPrint("ticket new", params.length, 4, 5))
            return;
        
		String cashierId = params[params.length - 2];
		if (!Cashier.isValidId(cashierId)) {
			throw new FailedCommandException(String.format("ticket new: error: invalid cashier id '%s' expected 'UW' followed by 7 digits\n", cashierId));
		}
		
		String clientId = params[params.length - 1];
		UserManager userManager = UserManager.getInstance();
        Cashier cashier;
        Client client;
        int ticketId;
        
		// Execution
        try{
            cashier = userManager.findCashier(cashierId);
            client = userManager.findClient(clientId);
            
            if (params.length == 5) {
                ticketId = Integer.parseInt(params[2]);
                
                if (!userManager.isTicketIdUnique(ticketId)) {
                    throw new FailedCommandException("ticket new: id already exists");
                }
            } else {
                ticketId = userManager.generateUniqueTicketId();
            }
            
            Ticket created = cashier.createTicket(ticketId);
            client.addTicket(ticketId);
            System.out.print(created.summaryString());
            System.out.println("ticket new: ok");
            
        }catch (DuplicateItemException ex){
            throw new FailedCommandException("Unable to add ticket to the client, " + ex.getMessage());
        }catch (MissingItemException | IdSpaceExhaustedException ex) {
            throw new FailedCommandException("Unable to create ticket, " + ex.getMessage());
        } catch (NumberFormatException ex) {
            throw new FailedCommandException("Unable to create ticket, " + params[2] + " is not a valid integer");
        }catch (IllegalArgumentException ex){
            throw new FailedCommandException("Unable to add ticket to the cashier, " + ex.getMessage());
        }
    }
    
    /**
     * Parses the 'add' subcommand of the 'ticket' command.
     * This function parses each field sequentially and fails to parse any arguments.
     * <p>
     * Format:
     * ticket add <ticketId><cashId> <prodId> <amount> [--p<txt> --p<txt>]
     * --p for every personalization (optional).
     * - Amount is the number of products to add to regular products and the number of people to rooms and foods.
     * - You can't add a new meeting or food that is allready added
     *
     * @param params      The stream of tokens to parse
     * @param userManager Context
     * @param inventory   Context
     */
    private void evalAdd(String[] params) throws FailedCommandException, DataException {
        // Parse
        if (!App.checkMinArgsCountWithPrint("ticket add", params.length, 4))
            return;
        
        int ticketId;
        String cashierId = params[3];
        int productId;
        int amount;
        String[] personalizations = null;
        
        try {
            ticketId = Integer.parseInt(params[2]);
        }catch (NumberFormatException ex){
            throw new FailedCommandException("Unable to add product to ticket, " + params[2] + " is not a valid integer");
        }
        
		if (!Cashier.isValidId(cashierId)) {
			throw new FailedCommandException(String.format("ticket add: error: invalid cashier id '%s' expected 'UW' followed by 7 digits\n", cashierId));
		}
        
        try {
            productId = Integer.parseInt(params[4]);
        }catch(NumberFormatException ex){
            throw new FailedCommandException("Unable to add product to ticket, " + params[4] + " is not a valid integer");
        }
        
        try {
            amount = Integer.parseInt(params[5]);
        }catch(NumberFormatException ex){
            throw new FailedCommandException("Unable to add product to ticket, " + params[5] + " is not a valid integer");
        }
        
        if (params.length > 6) {
            personalizations = parsePersonalizations(6, params);
        }
        
        // Execute
        Product productToAdd = Inventory.getInstance().getProduct(productId);
		if (ticketId < 0) {
			throw new FailedCommandException(String.format("ticket add: error: ticket id '%d' is invalid, expected a positive number\n", ticketId));
		}
		if (productToAdd == null) {
			throw new FailedCommandException(String.format("ticket add: error: could not find product with id %s\n", productId));
		}
        
        if (productToAdd instanceof TimedProduct timedProduct) {
            //Cast before checking
            if (App.now().isAfter(timedProduct.getExpirationDate())) {
                throw new FailedCommandException(String.format("ticket add: error: not enough time to prepare, you need more than %s hours\n",
                                                               timedProduct.getType().getHoursForPreparing()));
            }
        }
        
        try {
            Cashier cashier = UserManager.getInstance().findCashier(cashierId);
            Ticket ticket = cashier.findTicket(ticketId);
            ticket.addProduct(productToAdd, amount, personalizations);
            System.out.print(ticket.summaryString());
            System.out.println("ticket add: ok");
        }catch (DataException ex){
            throw new FailedCommandException("Unable to add product to ticket, " + ex.getMessage());
        }
    }
    
    /**
     * Parses the 'remove' subcommand of the 'ticket' command and
     * removes all appearances of a product inside the ticket.
     * <p>
     * Format:
     * ticket remove <ticketId><cashId> <prodId>
     *
     * @param params      The stream of tokens to parse
     * @param userManager Context
     * @param inventory   Context
     */
    private void evalRemove(String[] params) throws FailedCommandException{
        // Parse
        if (!App.checkArgsCountWithPrint("ticket remove", params.length, 5))
            return;
        
        int ticketId;
        String cashierId = params[3];
        
        try {
            ticketId = Integer.parseInt(params[2]);
        }catch(NumberFormatException ex) {
            throw new FailedCommandException("Unable to remove product from ticket, " + params[2] + " is not a valid integer");
        }
        
        if (!Cashier.isValidId(cashierId)) {
            throw new FailedCommandException(String.format("ticket remove: error: invalid cashier id '%s' expected 'UW' followed by 7 digits\n", cashierId));
        }
        
        int productId;
        try {
            productId = Integer.parseInt(params[4]);
        }catch(NumberFormatException ex) {
            throw new FailedCommandException("Unable to remove product from ticket, " + params[4] + " is not a valid integer");
        }
        
        // Execute
        if (ticketId < 0) {
            throw new FailedCommandException(String.format("ticket remove: error: ticket id '%d' is invalid, expected a positive number", ticketId));
        }
        if (!Product.isValidId(productId)) {
            throw new FailedCommandException(("ticket remove: error: expected product id greater or equal than zero"));
        }
        
        Cashier cashier;
        Ticket ticket;
        try {
            cashier = UserManager.getInstance().findCashier(cashierId);
            ticket = cashier.findTicket(ticketId);
        }catch(MissingItemException ex){
            throw new FailedCommandException("Unable to remove product from ticket, " + ex.getMessage());
        }
        
        if (ticket.removeProduct(productId)) {
            System.out.print(ticket.summaryString());
            System.out.println("ticket remove: ok");
        } else {
            throw new FailedCommandException(("ticket remove: error: failed to remove product"));
        }
    }
    
    /**
     * Parses the 'print' subcommand and prints in standard output the summary of the ticket
     * ALSO closes the ticket!
     * <p>
     * Format:
     * ticket print <ticketId> <cashId>
     *
     * @param params      The stream of tokens to parse
     * @param userManager Context
     * @param inventory   Context
     */
    private void evalPrint(String[] params) throws FailedCommandException{
		// Parse
		if (!App.checkArgsCountWithPrint("ticket print", params.length, 4))
            return;
        
        int ticketId;
        try {
            ticketId = Integer.parseInt(params[2]);
        }catch(NumberFormatException ex) {
            throw new FailedCommandException("Unable to add ticket, " + params[2] + " is not a valid integer");
        }
        
		String cashierId = params[3];
		if (!Cashier.isValidId(cashierId)) {
			throw new FailedCommandException(String.format("ticket print: error: invalid cashier id '%s' expected 'UW' followed by 7 digits", cashierId));
		}
        
        // Execute
		if (ticketId < 0) {
			throw new FailedCommandException(String.format("ticket print: error: ticket id '%d' is invalid, expected a positive number", ticketId));
		}
        
        try {
            Cashier cashier = UserManager.getInstance().findCashier(cashierId);
            Ticket ticket = cashier.findTicket(ticketId);
            ticket.close();
            System.out.print(ticket.summaryString());
            System.out.println("ticket print: ok");
        }catch (MissingItemException ex){
            throw new FailedCommandException("Unable to print ticket, " + ex.getMessage());
        }catch (DateTimeException ex){
            throw new FailedCommandException("Unable to prit ticket, " + ex.getMessage());
        }
    }
    
    /**
     * Parses the 'list' subcommand command and
     * prints the tickets ordered by cashier id
     * <p>
     * Format:
     * ticket list
     *
     * @param params      The stream of tokens to parse
     * @param userManager Context
     * @param inventory   Context
     */
    private void evalList(String[] params) {
        Cashier[] cashiers = UserManager.getInstance().listCashiers();
        Arrays.sort(cashiers, (c1, c2) -> c1.getId().compareTo(c2.getId()));
        System.out.println("Ticket List:");
        for (Cashier cashier : cashiers) {
            System.out.print(cashier.getTicketsString());
        }
        System.out.println("ticket list: ok");
    }
    
    private String[] parsePersonalizations(int beginIndex, String[] params) {
        int size = params.length - beginIndex;
        String[] pers = new String[size];
        
        boolean result = true;
        int i = 0;
        while (i < size && result) {
            String str = params[i + beginIndex];
            result = str.length() >= 3 && str.substring(0, 3).equals("--p");
            if (result) {
                pers[i] = str.substring(3);
            }
            i++;
        }
        
        if (!result) {
            return null;
        }
        return pers;
    }
}
