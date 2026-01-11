package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.*;
import es.upm.etsisi.poo.exceptions.*;
import java.util.*;

/**
 * CashCommand class that parses a stream of tokens into a specific CashCommand,
 * being one of the following formats:
 * - cash add [<id>] "<nombre>"<email>
 * - cash list ( Ordenados por nombre y sin mostrar sus tickets)
 * - cash remove <id>
 * - cash tickets <id> (Muestra los tickets del cajero ordenados por el Id del ticket, mostrando solo el ID y el estado)
 *
 * @see Command
 */

public class CashCommand implements Command {
    @Override
		public void eval(String[] args) throws FailedCommandException {
        // Throw exception (invalid argument amount)
        if (!App.checkArgsCountWithPrint("cash", args.length, 2, 5)) return;
        switch (args[1].toLowerCase()) {
            case "add"	   -> evalAddCash(args);
            case "list"	   -> evalList(args);
            case "remove"  -> evalRemove(args);
            case "tickets" -> evalTickets(args);
            default		   -> System.out.println("cash: invalid sub command");
        }
    }
	
    /**
     * Parses the 'add' variation of the 'Cash' command.
     * The function parses each field sequentially and short circuits if any fail.
     * Depending of params.length it calls different constructors
     * FORMAT: cash add [<id>] "<nombre>"<email>
     *
     * @param parser The stream of tokens to parse
     * @return The result of the parse. If every parse succedes a valid CashCommand Add instance
     * specifiying CashId, CashName, CashCategory and CashPrice,
     * or null if it fails
     */
    private void evalAddCash(String[] params) throws FailedCommandException{
        // Parse
        // Throw exception (invalid argument amount)
        if (!App.checkArgsCountWithPrint("cash add", params.length, 4, 5)) return;
		
        String cashierName = params[params.length-2];
        String cashierEmail = params[params.length-1];
		
		Cashier addedCash = null;
		UserManager userManager = UserManager.getInstance();
		
        try {
            if (params.length == 5) {
                String cashierId = params[2];
                addedCash = userManager.addCashier(cashierId, cashierName, cashierEmail);
            } else {
                addedCash = userManager.addCashier(cashierName, cashierEmail);
            }
			
            System.out.println(addedCash);
            System.out.println("cash add: ok");
        }catch (DataException ex){
            throw new FailedCommandException("Cannot add cashier: " + ex.getMessage());
        }
    }
	
    /**
     * List all the cashiers of the shop
     * @param params
     * @param userManager
     * @param inventory
     */
    private void evalList(String[] params) throws FailedCommandException{
        // Parse
        // Throw exception (invalid argument amount)
        if (!App.checkArgsCountWithPrint("cash list", params.length, 2)) return;
		
        ArrayList<Cashier> workers = UserManager.getInstance().getCashiers();
		workers.sort((c1, c2) -> c1.getName().compareTo(c2.getName()));
		
		System.out.println("Cash:");
        for (Cashier cashier : workers) {
            System.out.println("  "+cashier.toString());
        }
        System.out.println("cash list: ok");
    }
	
    /**
     * Remove the cashier with the id provided
     * @param params
     * @param userManager
     * @param inventory
     */
    private void evalRemove(String[] params) throws FailedCommandException{
        // Throw exception (invalid argument amount)
        if (!App.checkArgsCountWithPrint("cash remove", params.length, 3)) return;
		
        String cashierId = params[2];
        try {
            UserManager.getInstance().removeCashier(cashierId);
            System.out.println("cash remove: ok");
        } catch (MissingItemException ex) {
            throw new FailedCommandException("Cannot remove the cashier: " + ex.getMessage());
        }
    }
	
    /**
     * List all the tickets of a cashier
     * @param params
     * @param userManager
     * @param inventory
     */
    private void evalTickets(String[] params) throws FailedCommandException{
        // Throw exception (invalid argument amount)
        if (!App.checkArgsCountWithPrint("cash tickets", params.length, 3)) return;
		
        String cashierId = params[2];
		System.out.println("Tickets: ");
		
        try {
			Cashier cashier = UserManager.getInstance().findCashier(cashierId);
			
			ArrayList<Ticket> tickets = new ArrayList(cashier.getTickets());
			// NOTE(erb): notice comparison is inversed
			tickets.sort((first, second) -> second.getComposedId().compareTo(first.getComposedId()));
			
			for(Ticket ticket : tickets) {
				System.out.printf("  %s%n", ticket.toString());
			}
			
            System.out.println("cash tickets: ok");
			
        }catch (MissingItemException ex){
            throw new FailedCommandException("Cannot list the tickets for cashier " + cashierId +": " + ex.getMessage());
        }
    }
}
