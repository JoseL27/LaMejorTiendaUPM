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
    public void eval(String[] params) throws Exception {
		try { 
			App.checkMinArgsCountWithPrint(params.length, 2);

			switch (params[1].toLowerCase()) {
            case "new"    -> evalNew(params);
            case "add"    -> evalAdd(params);
            case "remove" -> evalRemove(params);
            case "print"  -> evalPrint(params);
            case "list"   -> evalList(params);
            default       -> throw new Exception("invalid sub command");
			}
			
		} catch (Exception e) {
			throw new Exception("ticket "+e.getMessage());
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
    private void evalNew(String[] params) throws Exception {
		try {
			App.checkArgsCountWithPrint(params.length, 4, 5);

			String cashierId = Cashier.validId(params[params.length - 2]);
			String clientId = Client.validId(params[params.length - 1]);

			UserManager userManager = UserManager.getInstance();

			// Execution
			Cashier cashier = userManager.getCashier(cashierId);
			if (cashier == null) {
				System.out.printf("ticket new: error: cashier with id '%s' was not found\n", cashierId);
				return;
			}
		
			Client client = userManager.getClient(clientId);
			if (client == null) {
				System.out.printf("ticket new: error: client with id '%s' was not found\n", clientId);
				return;
			}

			Integer ticketId = null;

			if (params.length == 5) {
				ticketId = Ticket.parseId(params[2]);
			} else {
				ticketId = userManager.generateUniqueTicketId();
			}

			client.addTicket(ticketId);
			Ticket created = cashier.createTicket(ticketId);
			System.out.print(created.summaryString());
			System.out.println("ticket new: ok");

		} catch (Exception e) {
			throw new Exception("new: "+e.getMessage());
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
    private void evalAdd(String[] params) throws Exception {
		try {
			App.checkMinArgsCountWithPrint(params.length, 4);

			int ticketId = Ticket.parseId(params[2]);
			String cashierId = Cashier.validId(params[3]);
			Integer productId = Product.parseId(params[4]);
			Integer amount = Integer.parseInt(params[5]);
			
			String[] personalizations = null;
			if (params.length > 6) {
				personalizations = parsePersonalizations(6, params);
			}
		
			Product productToAdd = Inventory.getInstance().getProduct(productId);

			Cashier cashier = UserManager.getInstance().getCashier(cashierId);
			if (cashier == null) {
				System.out.printf("ticket new: error: cashier with id '%s' was not found\n", cashierId);
				return;
			}

			Ticket ticket = cashier.getTicket(ticketId);

			if (ticket.addProduct(productToAdd, amount, personalizations)) {
				System.out.print(ticket.summaryString());
				System.out.println("ticket add: ok");
			} else {
				System.out.printf("ticket add: error: failed to add product\n");
			}

		} catch (Exception e) {
			throw new Exception("add: "+e.getMessage());
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
    private void evalRemove(String[] params) throws Exception {
		try {
			App.checkArgsCountWithPrint(params.length, 5);

			int ticketId = Ticket.parseId(params[2]);
			String cashierId = Cashier.validId(params[3]);
			int productId = Product.parseId(params[4]);
		
			Cashier cashier = UserManager.getInstance().getCashier(cashierId);
			// if (cashier == null) {
			// 	System.out.printf("ticket remove: error: cashier with id '%s' was not found\n", cashierId);
			// 	return;
			// }

			Ticket ticket = cashier.getTicket(ticketId);

			Product removedProduct = ticket.deleteProduct(productId);
			System.out.print(ticket.summaryString());
			System.out.println("ticket remove: ok");
			
			// if (removedProduct != null) {
			// 	System.out.print(ticket.summaryString());
			// 	System.out.println("ticket remove: ok");
			// } else {
			// 	System.out.printf("ticket remove: error: failed to remove product\n");
			// }

		} catch (Exception e) {
			throw new Exception("remove: "+e.getMessage());
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
    private void evalPrint(String[] params) throws Exception {
		try {
			App.checkArgsCountWithPrint(params.length, 4);

			int ticketId = Ticket.parseId(params[2]);
			String cashierId = Cashier.validId(params[3]);
			Cashier cashier = UserManager.getInstance().getCashier(cashierId);
			Ticket ticket = cashier.getTicket(ticketId);
			
			ticket.close();
			System.out.print(ticket.summaryString());
			System.out.println("ticket print: ok");

		} catch (Exception e) {
			throw new Exception("print: "+e.getMessage());
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
    private void evalList(String[] params) {
		Cashier[] cashiers = UserManager.getInstance().listCashiers();
		Arrays.sort(cashiers, (c1, c2) -> c1.getId().compareTo(c2.getId()));
		System.out.println("Ticket List:");
		for (Cashier cashier : cashiers) {
			System.out.print(cashier.getTicketsString());
		}
		System.out.println("ticket list: ok");
    }

    private boolean isValidAmount(int amount) {
        return amount > 0 && amount <= Ticket.MAX_PRODUCTS;
    }

	private String[] parsePersonalizations(int beginIndex, String[] params) {
		int size = params.length-beginIndex;
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
