package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.*;

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
        if (Utils.checkArgsCountWithPrint("cash", args.length, 3, 5)) return;
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
        if (!Utils.checkArgsCountWithPrint("cash add", params.length, 4, 5)) return;

        String cashierId = "";
        String cashierName;
        String cashierEmail;
        if (params.length == 5) {
            cashierId = params[2];
            cashierName = params[3];
            cashierEmail = params[4];
        } else {
            cashierName = params[2];
            cashierEmail = params[3];
        }

        if (!Cashier.isValidId(cashierId)) {
            System.out.printf("cash new: error: invalid cashier id '%s' expected 'UW' followed by 7 digits\n", cashierId);
            return;
        }
        if (!Cashier.isCompanyEmail(cashierEmail)) {
            System.out.println("cash new: invalid cashier email, not company email");
            return;
        }
        if (params.length == 5) userManager.addCashier(cashierId, cashierName, cashierEmail);
        if (params.length == 4) userManager.addCashier(cashierName, cashierEmail);
    }

    /**
     * List all the cashiers of the shop
     * @param params
     * @param userManager
     * @param inventory
     */
    private void evalList(String[] params, UserManager userManager, Inventory inventory) {
        // Parse
        if (!Utils.checkArgsCountWithPrint("cash list", params.length, 2)) return;

        Cashier[] workers = userManager.listCashiers();
        if (workers.length > 0) {
            for (Cashier cashier : workers) {
                System.out.println(" " + cashier.toString());
            }
            System.out.println("cashiers list: ok");
        } else {
            System.out.println("cashiers list: Not cashiers added yet");
        }

    }

    /**
     * Remove the cashier with de id provided
     * @param params
     * @param userManager
     * @param inventory
     */
    private void evalRemove(String[] params, UserManager userManager, Inventory inventory) {
        if (!Utils.checkArgsCountWithPrint("cash remove", params.length, 3)) return;
        String cashierId = params[2];
        if (userManager.removeCashier(cashierId)) {
            System.out.println("cash remove successful");
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
        if (!Utils.checkArgsCountWithPrint("cash tickets", params.length, 2)) return;
        String cashierId = params[2];
        String cashierTickets = userManager.findCashier(cashierId).getTicketsString();
        System.out.println(cashierTickets);
    }
}
}
