package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.*;
import es.upm.etsisi.poo.exceptions.*;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

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
     * @param params The token stream to parse on each subcommand
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
* ticket new [<id>] <cashId> < userId> -[c|p|s] (default -p option)
     *
     * @param params      The stream of tokens to parse
     * @param userManager Context
     * @param inventory   Context
     */
    private void evalNew(String[] params) throws FailedCommandException {
        // Parse
        if (!App.checkArgsCountWithPrint("ticket new", params.length, 4, 6))
            return;
		
		UserManager userManager = UserManager.getInstance();
		int parseIndex = 2;
		
		String ticketId;
		int ticketIdNum;
		boolean isCustomId = false;
		try {
			ticketIdNum = Integer.parseInt(params[parseIndex]);
			ticketId = params[parseIndex++];
			isCustomId = true;
			
			if (!userManager.isTicketIdUnique(ticketId)) {
				throw new FailedCommandException(String.format("ticket new: error: ticket id '%s' already exists", ticketId));
			}
		} catch (NumberFormatException e) {
			try {
				ticketIdNum = userManager.generateUniqueTicketId();
			} catch (IdSpaceExhaustedException ex) {
				throw new FailedCommandException("ticket new: error: failed to generate an id, out of ids!");
			}
			ticketId = Integer.toString(ticketIdNum);
			isCustomId = false;
		}
		
		String cashierId = params[parseIndex++];
		String clientId = params[parseIndex++];
		
		char ticketType = 'p';
		boolean specifiedTicketType = (parseIndex < params.length);
		if (specifiedTicketType) {
			
			boolean validTicketType = false;
			String ticketTypeStr = params[parseIndex];
			if (ticketTypeStr.length() == 2 && ticketTypeStr.charAt(0) == '-') {
				char typeChar = ticketTypeStr.charAt(1);
				
				if (typeChar == 'c' || typeChar == 'p' || typeChar == 's') {
					ticketType = typeChar;
					validTicketType = true;
				}
			}
			
			if (!validTicketType) {
				throw new FailedCommandException("invalid ticket type, expected 'c', 's' or 'p'");
			}
		}
		
        try {
            Client client = userManager.findClient(clientId);
			Client.IdType idType = client.getIdType();
			
			Ticket created = null;
			if (ticketType == 'p' && idType == Client.IdType.DNI) {
				created = new ProductTicket(ticketIdNum, isCustomId);
				
			} else if (ticketType == 's' && idType == Client.IdType.NIF) {
				created = new ServiceTicket(ticketIdNum, isCustomId);
				
			} else if (ticketType == 'c' && idType == Client.IdType.NIF) {
				created = new CombinedTicket(ticketIdNum, isCustomId);
				
			} else {
				String msg = getTicketNewMismatchErrorMsg(idType, ticketType);
				throw new FailedCommandException(msg);
			}
			
			Cashier cashier = userManager.findCashier(cashierId);
			cashier.addTicket(created);
			
            client.addTicket(ticketIdNum);
            System.out.print(created.summaryString());
            System.out.println("ticket new: ok");
			
        } catch (IllegalArgumentException ex) {
            throw new FailedCommandException("Unable to create ticket: invalid argument");
        } catch (DataException ex) {
            throw new FailedCommandException("Unable to create new ticket: " + ex.getMessage());
        }
    }
	
	private String getTicketNewMismatchErrorMsg(Client.IdType idType, char ticketType) {
		String validTypesForId = switch (idType) {
			case Client.IdType.NIF -> "Combined (-c) or Service (-s)";
			case Client.IdType.DNI -> "Product (-p) (default)";
			default -> "Invalid";
		};
		
		String userTypeName = switch (idType) {
			case Client.IdType.NIF -> "Company";
			case Client.IdType.DNI -> "Regular";
			default -> "Invalid";
		};
		
		String ticketTypeName = switch (ticketType) {
			case 'p' -> "Product (default)";
			case 's' -> "Service";
			case 'c' -> "Combined";
			default -> "Invalid";
		};
		
		return String.format("ticket new: error: %s client's can only create %s tickets. Tried to create %s.", 
							 userTypeName, validTypesForId, ticketTypeName);
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
        if (!App.checkMinArgsCountWithPrint("ticket add", params.length, 5))
            return;
		
        String ticketId = params[2];
        String cashierId = params[3];
        String itemId = params[4];
		
        try {
            int amount = 1; 
			int parseIndex = 5;
			String[] personalizations = new String[0];
			if (params.length > 5) {
				
				try {
					amount = Integer.parseInt(params[parseIndex]);
					parseIndex++;
				} catch (NumberFormatException ex) {
				}
				
                personalizations = parsePersonalizations(parseIndex, params);
			}
			
            InventoryItem itemToAdd = Inventory.getInstance().getItemFromStringId(itemId);
            Cashier cashier = UserManager.getInstance().findCashier(cashierId);
			
            Ticket ticket = cashier.findTicket(ticketId);
            ticket.addItem(itemToAdd, amount, personalizations);
			
            System.out.print(ticket.summaryString());
            System.out.println("ticket add: ok");
			
        } catch (NumberFormatException ex) {
            throw new FailedCommandException("ticket add: error: invalid integer");
        } catch (DataException ex) {
            throw new FailedCommandException("ticket add: error:" + ex.getMessage());
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
    private void evalRemove(String[] params) throws FailedCommandException {
        // Parse
        if (!App.checkArgsCountWithPrint("ticket remove", params.length, 5))
            return;
		
        String cashierId = params[3];
        String ticketId = params[2];
		
        try {
            int itemId = Integer.parseInt(params[4]);
			
            // Execute
            Cashier cashier = UserManager.getInstance().findCashier(cashierId);
            Ticket ticket = cashier.findTicket(ticketId);
			
            ticket.removeItem(itemId);
            System.out.print(ticket.summaryString());
            System.out.println("ticket remove: ok");
			
        } catch (NumberFormatException ex) {
            throw new FailedCommandException("ticket remove: error: invalid integer");
        } catch (MissingItemException ex) {
            throw new FailedCommandException("ticket remove: error: failed to remove product: " + ex.getMessage());
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
    private void evalPrint(String[] params) throws FailedCommandException {
        // Parse
        if (!App.checkArgsCountWithPrint("ticket print", params.length, 4))
            return;
		
        String ticketId = params[2];
        String cashierId = params[3];
		
        if (!Cashier.isValidId(cashierId)) {
            throw new FailedCommandException(String.format("ticket print: error: invalid cashier id '%s' expected 'UW' followed by 7 digits", cashierId));
        }
		
        try {
			
            Cashier cashier = UserManager.getInstance().findCashier(cashierId);
            Ticket ticket = cashier.findTicket(ticketId);
            ticket.close();
            System.out.print(ticket.summaryString());
            System.out.println("ticket print: ok");
        } catch (MissingItemException ex) {
            throw new FailedCommandException("Unable to print ticket, " + ex.getMessage());
        } catch (DateTimeException ex) {
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
        ArrayList<Cashier> cashiers = UserManager.getInstance().getCashiers();
        cashiers.sort((c1, c2) -> c1.getId().compareTo(c2.getId()));
		
        System.out.println("Ticket List:");
        for (Cashier cashier : cashiers) {
			
            ArrayList<Ticket> tickets = new ArrayList(cashier.getTickets());
			
            // NOTE(erb): no sort on purpose (expected output has reverse hashmap order)
            for (int i = tickets.size() - 1; i >= 0; i--) {
                System.out.printf("  %s%n", tickets.get(i).toString());
            }
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
            return new String[0];
        }
        return pers;
    }
}