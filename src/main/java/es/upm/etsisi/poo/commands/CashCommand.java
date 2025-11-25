package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.*;
import java.util.Arrays;

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
    public void eval(String[] args) throws Exception {
		try { 
			App.checkArgsCountWithPrint(args.length, 2, 5);
		
			switch (args[1].toLowerCase()) {
            case "add"     -> evalAddCash(args);
            case "list"    -> evalList(args);
            case "remove"  -> evalRemove(args);
            case "tickets" -> evalTickets(args);
            default -> System.out.println("cash: invalid sub command");
			}
		} catch (Exception e) {
			throw new Exception("cash "+e.getMessage());
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
    private void evalAddCash(String[] params) throws Exception {
		try {
			App.checkArgsCountWithPrint(params.length, 4, 5);

			String cashierName = params[params.length-2];
			String cashierEmail = params[params.length-1];

			if (!Cashier.isCompanyEmail(cashierEmail)) {
				System.out.println("cash add: invalid cashier email, not company email");
				return;
			}

			Cashier addedCash = null;

			UserManager userManager = UserManager.getInstance();

			if (params.length == 5) {
				String cashierId = Cashier.validId(params[2]);
				addedCash = userManager.addCashier(cashierId, cashierName, cashierEmail);
			} else {
				addedCash = userManager.addCashier(cashierName, cashierEmail);
			}

			if (addedCash != null) {
				System.out.println(addedCash);
				System.out.println("cash add: ok");
			} else {
				System.out.println("cash add: error: failed to add cashier");
			}

		} catch (Exception e) {
			throw new Exception("add: "+e.getMessage());
		}
    }

    /**
     * List all the cashiers of the shop
     * @param params
     * @param userManager
     * @param inventory
     */
    private void evalList(String[] params) throws Exception {
		try {
			// Parse
			App.checkArgsCountWithPrint(params.length, 2);
			UserManager userManager = UserManager.getInstance();

			Cashier[] workers = userManager.listCashiers();
			Arrays.sort(workers);
		
			if (workers.length > 0) {
				System.out.println("Cash:");
				for (Cashier cashier : workers) {
					System.out.println("  "+cashier.toString());
				}
				System.out.println("cash list: ok");
			} else {
				throw new Exception("no cashiers added yet");
			}
		} catch (Exception e) {
			throw new Exception("list: "+e.getMessage());
		}
    }

    /**
     * Remove the cashier with de id provided
     * @param params
     * @param userManager
     * @param inventory
     */
    private void evalRemove(String[] params) throws Exception {
		try {
			
			App.checkArgsCountWithPrint(params.length, 3);
			String cashierId = params[2];
			UserManager.getInstance().removeCashier(cashierId);
			System.out.println("cash remove: ok");
			
		} catch (Exception e) {
			throw new Exception("remove: ");
		}
    }

    /**
     * List all the tickets of a cashier
     * @param params
     * @param userManager
     * @param inventory
     */
    private void evalTickets(String[] params) throws Exception {
		try {
			App.checkArgsCountWithPrint(params.length, 3);
			
			String cashierId = params[2];
			System.out.println("Tickets:");
			String ticketsStr = UserManager.getInstance().getCashier(cashierId).getTicketsString();
			if (ticketsStr != null && ticketsStr.length() > 0) { 
				System.out.print(ticketsStr);
			}
			System.out.println("cash tickets: ok");
			
		} catch (Exception e) {
			throw new Exception("tickets: ");
		}
    }
}
