package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.*;
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
     * First entrypoint to parse 'ticket' command (assumes the params.getCommand(0) is 'ticket').
     * This method is responsible for parsing different subcommands, it also invokes other parsing
     * methods if the subcommand in question needs more arguments.
     * This tryParse ignores extra arguments unless the arguments which are actually used have the wrong type.
     * Prints a warning to STDOUT if excess arguments were found.
     *
     * @param params command
     */

    /**
     * Reads the subcommand from this command and calls the corresponding function to execute it
     *
     * @param ticket    The ticket on which the changes corresponding to the command will be applied
     * @param inventory manager from which necessary products will be taken
     */
    public void eval(String[] params, UserManager userManager, Inventory inventory) {
        // Parse
        if (!Utils.checkMinArgsCountWithPrint("ticket", params.length, 2)) return;

        // Execute
        switch (params[1].toLowerCase()) {
            case "new"    -> evalNew(params, userManager, inventory);
            case "add"    -> evalAdd(params, userManager, inventory);
            case "remove" -> evalRemove(params, userManager, inventory);
            case "print"  -> evalPrint(params, userManager, inventory);
            default       -> System.out.println("ticket: invalid sub command");
        }
    }

    /**
     * Parses the 'new' subcommand of the 'ticket' command.
     * This function does not do any parsing besides checking the number of arguments and
     * creating a proper TicketCommand New instance.
     *
     * @param params The stream of tokens to parse
     * @return The result of the parse. If the amount of tokens is 2 then a valid
     * TicketCommand New instance OR a failure code with ParseResult.Code.INSUFICIENT_ARGUMENTS will be issued.
     */
    /**
     * Converts this instance of Ticket into a new one
     *
     * @param ticket ticket to be reset or created
     * @return SUCCESS always, since no recognisable error can happen
     */
    private void evalNew(String[] params, UserManager userManager, Inventory inventory) {
        // Parse
        if (!Utils.checkArgsCountWithPrint("ticket new", params.length, 4, 5))
            return;

		String cashierId = params[params.length - 2];
		if (!Cashier.isValidId(cashierId)) {
			System.out.printf("ticket new: error: invalid cashier id '%s' expected 'UW' followed by 7 digits\n", cashierId);
			return;
		}
		
		String clientId = params[params.length - 1];
		if (!Client.isValidId(clientId)) {
			System.out.printf("ticket new: error: invalid client id '%s' expected 8 digits followed by a letter\n", clientId);
			return;
		}

		// Execution
		Cashier cashier = userManager.findCashier(cashierId);
		if (cashier == null) {
			System.out.printf("ticket new: error: cashier with id '%s' was not found\n", cashierId);
			return;
		}
		
		Client client = userManager.findClient(clientId);
		if (client == null) {
			System.out.printf("ticket new: error: client with id '%s' was not found\n", clientId);
			return;
		}

		Integer ticketId = null;

		if (params.length == 5) {
			ticketId = Utils.tryParseInt(params[2]);
			if (ticketId == null) {
				Utils.printInvalidDataType("ticket new", "integer", params[2]);
				return;
			}
			
			if (!Ticket.isValidId(ticketId)) {
				System.out.printf("ticket new: error: ticket id '%s' is invalid, expected a 5 digit number\n", ticketId);
				return;
			}
			
			if (!userManager.isTicketIdUnique(ticketId)) {
				System.out.println("ticket new: id allready exists");
				return;
			}
		} else {
			ticketId = userManager.generateUniqueTicketId();
		}

		cashier.createTicket(ticketId);
		client.addTicket(ticketId);
		System.out.println("ticket new: ok");
    }

    /**
     * Parses the 'add' subcommand of the 'ticket' command.
     * This function parses each field sequentially and immediately returns a failed ParseResult if it
     * fails to parse any arguments.
     * FORMAT: ticket add <productId> <cantidad>
     *
     * @param params The stream of tokens to parse
     * @return The result of the parse. If parsing is successful, this will return a valid TicketCommand Add instance
     * specifying productId and quantity. Or a failure code specifying which part of the parsing went wrong.
     */

    /**
     * Adds a product from the storage to the ticket
     *
     * @param ticket    ticket to which the product will be added
     * @param inventory dataManager from which the product will be taken
     * @return SUCCESS, if the product is added correctly, or the corresponding error if not
     */
    private void evalAdd(String[] params, UserManager userManager, Inventory inventory) {
        // Parse
        if (!Utils.checkMinArgsCountWithPrint("ticket add", params.length, 4))
            return;

        Integer ticketId = Utils.tryParseInt(params[2]);
        if (ticketId == null) {
            Utils.printInvalidDataType("ticket add", "integer", params[2]);
            return;
        }

		String cashierId = params[3];
		if (!Cashier.isValidId(cashierId)) {
			System.out.printf("ticket new: error: invalid cashier id '%s' expected 'UW' followed by 7 digits\n", cashierId);
			return;
		}
		
        Integer productId = Utils.tryParseInt(params[4]);
        if (productId == null) {
            Utils.printInvalidDataType("ticket add", "integer", params[4]);
            return;
        }

        Integer amount = Utils.tryParseInt(params[5]);
        if (amount == null) {
            Utils.printInvalidDataType("ticket add", "integer", params[5]);
            return;
        }

		String[] personalizations = parsePersonalizations(6, params);
        if (params.length > 6 && personalizations == null) {
            return;
		}

        // Execute
		if (!Ticket.isValidId(ticketId)) {
			System.out.printf("ticket add: error: ticket id '%d' is invalid, expected a 5 digit number\n", ticketId);
			return;
		}
			
		if (!Inventory.isValidId(productId)) {
			System.out.printf("ticket add: error: expected id greater or equal than zero\n");
			return;
		}
		
		Product productToAdd = inventory.readProduct(productId);
		if (productToAdd == null) {
			System.out.printf("ticket add: error: could not find product with id %s\n", productId);
			return;
		}

		Cashier cashier = userManager.findCashier(cashierId);
		if (cashier == null) {
			System.out.printf("ticket new: error: cashier with id '%s' was not found\n", cashierId);
			return;
		}

		Ticket ticket = cashier.findTicket(ticketId);
		if (ticket == null) {
			System.out.printf("ticket add: error: ticket with id '%d' not found in cashier with id '%s'\n", ticketId, cashierId);
			return;
		}

		if (ticket.addProduct(productToAdd, amount, personalizations)) {
			System.out.println(ticket.summaryString());
			System.out.println("ticket add: ok");
		} else {
			System.out.printf("ticket add: error: failed to add product\n");
		}
    }

    /**
     * Parses the 'remove' subcommand of the 'ticket' command.
     * This function parses each field sequentially and immediately returns a failed ParseResult if it
     * fails to parse any arguments.
     * FORMAT: ticket remove <productId>
     *
     * @param parser The stream of tokens to parse
     * @return The result of the parse. If parsing is successful, this will return a valid TicketCommand Remove instance
     * specifying productId. Or a failure code specifying which part of the parsing went wrong.
     */

    /**
     * Removes all appearances of a product inside the ticket
     *
     * @param ticket ticket from which the product will be removed
     * @return SUCCESS, if the product is removed correctly, or the corresponding error if not
     */
    private void evalRemove(String[] params, UserManager userManager, Inventory inventory) {
        // // Parse
        // if (!Utils.checkArgsCountWithPrint("ticket remove", params.length, 3)) return;
        // Integer productId = Utils.tryParseInt(params[2]);
        // if (productId == null) {
        //     Utils.printInvalidDataType("ticket remove", "integer", params[2]);
        //     return;
        // }

        // // Execute
        // if (!Inventory.isValidId(productId)) {
        //     System.out.printf("ticket add: error: expected id greater or equal than zero\n");
        // } else {
        //     Product removed = userManager.removeProduct(productId);
        //     if (removed != null) {
        //         System.out.println(ticket.summaryString());
        //         System.out.println("ticket remove: ok");
        //     } else {
        //         System.out.printf("ticket remove: error: product with id %d not in ticket\n", productId);
        //     }
        // }
		System.out.println("ProductCommand.evalRemove: NOT IMPLEMENTED");
    }

    /**
     * Parses the 'print' subcommand of the 'ticket' command.
     * This function does not do any parsing besides checking the number of arguments and
     * creating a proper TicketCommand Print instance.
     *
     * @param params The stream of tokens to parse
     * @return The result of the parse. If the amount of tokens is 2 then a valid
     * TicketCommand Print instance OR a failure code with ParseResult.Code.INSUFICIENT_ARGUMENTS will be issued.
     */

    /**
     * Prints in standard output the summary of the ticket
     *
     * @param ticket ticket to be printed
     * @return SUCCESS always, since no recognisable error can happen
     */
    private void evalPrint(String[] params, UserManager userManager, Inventory inventory) {
        // // Parse
        // if (!Utils.checkArgsCountWithPrint("ticket print", params.length, 2)) return;

        // // Execute
        // System.out.println(ticket.summaryString());
        // userManager.resetProductInfos();
        // System.out.println("ticket print: ok");
		System.out.println("ProductCommand.evalPrint: NOT IMPLEMENTED");
    }

    private void evalList(String[] params, UserManager userManager, Inventory inventory) {
        System.out.println("TicketCommand.evalList() NOT IMPLEMENTED");
    }

    private boolean isValidAmount(int amount) {
        return amount > 0 && amount <= Ticket.MAX_PRODUCTS;
    }

	private String[] parsePersonalizations(int beginIndex, String[] params) {
		System.out.println("TicketCommand.parsePersonalizations: UNIMPLEMENTED");
		return null;
	}
}
