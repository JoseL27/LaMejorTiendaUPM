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
    public void eval(String[] args, UserManager userManager, Inventory inventory) {
        if (!App.checkArgsCountWithPrint("cash", args.length, 2, 5)) return;
        switch (args[1].toLowerCase()) {
            case "add" -> evalAddCash(args, userManager, inventory);
            case "list" -> evalList(args, userManager, inventory);
            case "remove" -> evalRemove(args, userManager, inventory);
            case "tickets" -> evalTickets(args, userManager, inventory);
            default -> System.out.println("cash: invalid sub command");
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
    private void evalAddCash(String[] params, UserManager userManager, Inventory inventory) {
        // Parse
        if (!App.checkArgsCountWithPrint("cash add", params.length, 4, 5)) return;

        String cashierName = params[params.length-2];
        String cashierEmail = params[params.length-1];

        if (!Cashier.isCompanyEmail(cashierEmail)) {
            System.out.println("cash add: invalid cashier email, not company email");
            return;
        }

		Cashier addedCash = null;
		
        if (params.length == 5) {
			String cashierId = params[2];

			if (!Cashier.isValidId(cashierId)) {
				System.out.printf("cash add: error: invalid cashier id '%s' expected 'UW' followed by 7 digits\n", cashierId);
				return;
			}
			
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
    }

    /**
     * List all the cashiers of the shop
     * @param params
     * @param userManager
     * @param inventory
     */
    private void evalList(String[] params, UserManager userManager, Inventory inventory) {
        // Parse
        if (!App.checkArgsCountWithPrint("cash list", params.length, 2)) return;

        Cashier[] workers = userManager.listCashiers();
		Arrays.sort(workers);
		
        if (workers.length > 0) {
            System.out.println("Cash:");
            for (Cashier cashier : workers) {
                System.out.println("  "+cashier.toString());
            }
            System.out.println("cash list: ok");
        } else {
            System.out.println("cash list: error: no cashiers added yet");
        }

    }

    /**
     * Remove the cashier with de id provided
     * @param params
     * @param userManager
     * @param inventory
     */
    private void evalRemove(String[] params, UserManager userManager, Inventory inventory) {
        if (!App.checkArgsCountWithPrint("cash remove", params.length, 3)) return;
        String cashierId = params[2];
        if (userManager.removeCashier(cashierId)) {
            System.out.println("cash remove: ok");
        } else {
            System.out.println("cash: invalid cashier id '" + cashierId + "'");
        }
    }

    /**
     * List all the tickets of a cashier
     * @param params
     * @param userManager
     * @param inventory
     */
    private void evalTickets(String[] params, UserManager userManager, Inventory inventory) {
        if (!App.checkArgsCountWithPrint("cash tickets", params.length, 3)) return;
        String cashierId = params[2];
		System.out.println("Tickets:");
		String ticketsStr = userManager.findCashier(cashierId).getTicketsString();
		if (ticketsStr != null && ticketsStr.length() > 0) { 
			System.out.print(ticketsStr);
		}
		System.out.println("cash tickets: ok");
    }
}
