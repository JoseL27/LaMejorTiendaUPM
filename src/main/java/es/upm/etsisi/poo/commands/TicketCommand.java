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
     * First entrypoint to parse 'ticket' command
     * This method is responsible for parsing different subcommands.
     *
     * @param params       The token stream to parse on each subcommand
	 * @param userManager  The userManager to use for user related operations in subcommands
	 * @param inventory    The product store to use for product related operations in subcommands
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
            case "list"   -> evalList(params, userManager, inventory);
            default       -> System.out.println("ticket: invalid sub command");
        }
    }

    /**
     * Parses the 'new' subcommand of the 'ticket' command and
	 * adds a new creates a new ticket for a client and managed by a cashier.
	 *
	 * Format:
	 * ticket new [<id>] <cashId> <userId>
     *
     * @param params      The stream of tokens to parse
	 * @param userManager Context
	 * @param inventory   Context
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

		if (client.addTicket(ticketId)) {
			cashier.createTicket(ticketId);
			System.out.println("ticket new: ok");
		} else {
			System.out.println("ticket new: failed to add the ticket to the client");
		}
    }

    /**
     * Parses the 'add' subcommand of the 'ticket' command.
     * This function parses each field sequentially and fails to parse any arguments.
	 *	 
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
     * Parses the 'remove' subcommand of the 'ticket' command and
     * removes all appearances of a product inside the ticket.
     * 
     * Format: 
	 * ticket remove <ticketId><cashId> <prodId>	 
     *
	 * @param params      The stream of tokens to parse
	 * @param userManager Context
	 * @param inventory   Context
     */
    private void evalRemove(String[] params, UserManager userManager, Inventory inventory) {
		// Parse
		if (!Utils.checkArgsCountWithPrint("ticket remove", params.length, 5))
            return;

        Integer ticketId = Utils.tryParseInt(params[2]);
        if (ticketId == null) {
            Utils.printInvalidDataType("ticket remove", "integer", params[2]);
            return;
        }

		String cashierId = params[3];
		if (!Cashier.isValidId(cashierId)) {
			System.out.printf("ticket remove: error: invalid cashier id '%s' expected 'UW' followed by 7 digits\n", cashierId);
			return;
		}
		
        Integer productId = Utils.tryParseInt(params[4]);
        if (productId == null) {
            Utils.printInvalidDataType("ticket remove", "integer", params[4]);
            return;
        }

        // Execute
		if (!Ticket.isValidId(ticketId)) {
			System.out.printf("ticket remove: error: ticket id '%d' is invalid, expected a 5 digit number\n", ticketId);
			return;
		}
			
		if (!Inventory.isValidId(productId)) {
			System.out.printf("ticket remove: error: expected id greater or equal than zero\n");
			return;
		}
		
		Cashier cashier = userManager.findCashier(cashierId);
		if (cashier == null) {
			System.out.printf("ticket remove: error: cashier with id '%s' was not found\n", cashierId);
			return;
		}

		Ticket ticket = cashier.findTicket(ticketId);
		if (ticket == null) {
			System.out.printf("ticket remove: error: ticket with id '%d' not found in cashier with id '%s'\n", ticketId, cashierId);
			return;
		}

		Product removedProduct = ticket.removeProduct(productId);
		if (removedProduct != null) {
			System.out.println(ticket.summaryString());
			System.out.println("ticket remove: ok");
		} else {
			System.out.printf("ticket remove: error: failed to remove product\n");
		}
    }

    /**
     * Parses the 'print' subcommand and prints in standard output the summary of the ticket
	 * ALSO closes the ticket!
     *
     * Format:
     * ticket print <ticketId> <cashId>
     * 
	 * @param params      The stream of tokens to parse
	 * @param userManager Context
	 * @param inventory   Context
     */
    private void evalPrint(String[] params, UserManager userManager, Inventory inventory) {
		// Parse
		if (!Utils.checkArgsCountWithPrint("ticket print", params.length, 4))
            return;

        Integer ticketId = Utils.tryParseInt(params[2]);
        if (ticketId == null) {
            Utils.printInvalidDataType("ticket print", "integer", params[2]);
            return;
        }

		String cashierId = params[3];
		if (!Cashier.isValidId(cashierId)) {
			System.out.printf("ticket print: error: invalid cashier id '%s' expected 'UW' followed by 7 digits\n", cashierId);
			return;
		}

        // Execute
		if (!Ticket.isValidId(ticketId)) {
			System.out.printf("ticket print: error: ticket id '%d' is invalid, expected a 5 digit number\n", ticketId);
			return;
		}
		
		Cashier cashier = userManager.findCashier(cashierId);
		if (cashier == null) {
			System.out.printf("ticket print: error: cashier with id '%s' was not found\n", cashierId);
			return;
		}

		Ticket ticket = cashier.findTicket(ticketId);
		if (ticket != null) {
			System.out.println(ticket.summaryString());
			ticket.close();
			System.out.println("ticket print: ok");
		} else {
			System.out.printf("ticket print: error: ticket with id '%d' not found in cashier with id '%s'\n", ticketId, cashierId);
			return;
		}
    }

    /**
     * Parses the 'list' subcommand command and
     * prints the tickets ordered by cashier id
     *
     * Format:
     * ticket list
     * 
	 * @param params      The stream of tokens to parse
	 * @param userManager Context
	 * @param inventory   Context
     */
    private void evalList(String[] params, UserManager userManager, Inventory inventory) {
		Cashier[] cashiers = userManager.listCashiers();
		Arrays.sort(cashiers, (c1, c2) -> c1.getId().compareTo(c2.getId()));
		for (Cashier cashier : cashiers) {
			System.out.println(cashier.getTicketsString());
		}
		System.out.println("ticket list: ok");
    }

	private String[] parsePersonalizations(int beginIndex, String[] params) {
		System.out.println("TicketCommand.parsePersonalizations: UNIMPLEMENTED");
		return null;
	}
}
