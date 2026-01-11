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
     * ticket new [<id>] <cashId> <userId>
     *
     * @param params      The stream of tokens to parse
     * @param userManager Context
     * @param inventory   Context
     */
    private void evalNew(String[] params) throws FailedCommandException {
        // Parse
        if (!App.checkArgsCountWithPrint("ticket new", params.length, 4, 6))
            return;

        try {
            UserManager userManager = UserManager.getInstance();
            int ticketId = 0;
            String cashierId = "";
            String clientId = "";
            char tickeType = 'p';
            boolean isIdCustom = false;

            if (params.length == 4) {
                cashierId = params[2];
                clientId = params[3];
                ticketId = userManager.generateUniqueTicketId();

            } else if (params.length == 6) {
                ticketId = Integer.parseInt(params[2]);
                cashierId = params[3];
                clientId = params[4];
                tickeType = params[5].charAt(0);
                isIdCustom = true;
            } else {
                char aux = params[4].charAt(0);
                if (params[4].length() == 1 && (aux == 'c' || aux == 'p' || aux == 's')) {
                    ticketId = userManager.generateUniqueTicketId();
                    cashierId = params[2];
                    clientId = params[3];
                    tickeType = aux;
                } else if (params[4].length() != 1) {
                    ticketId = Integer.parseInt(params[2]);
                    isIdCustom = true;
                    cashierId = params[3];
                    clientId = params[4];
                }
            }

            if (!Cashier.isValidId(cashierId)) {
                throw new FailedCommandException(String.format("ticket new: error: invalid cashier id '%s' expected 'UW' followed by 7 digits\n", cashierId));
            }

            String aux = ticketId + "";
            if (!userManager.isTicketIdUnique(aux)) {
                throw new FailedCommandException("ticket new: id already exists");
            }

            Client.IdType idType = Client.getIdType(clientId);
            if (idType == null) {
                throw new FailedCommandException("ticket new: id not valid");
            }
            Cashier cashier = userManager.findCashier(cashierId);
            Client client = userManager.findClient(clientId);
            Ticket created = null;


            if (tickeType == 'p') {
                created = (ProductTicket) cashier.createTicket(ticketId, isIdCustom);

            } else if (tickeType == 's' && idType == Client.IdType.NIF) {
                created = (ServiceTicket) cashier.createTicket(ticketId, isIdCustom);
            } else if (tickeType == 'c' && idType == Client.IdType.NIF) {
                created = (CombinedTicket) cashier.createTicket(ticketId, isIdCustom);
            } else {
                throw new FailedCommandException("ticket new: id and ticket type don't match");
            }

            client.addTicket(ticketId);
            System.out.print(created.summaryString());
            System.out.println("ticket new: ok");

        } catch (DuplicateItemException ex) {
            throw new FailedCommandException("Unable to add ticket to the client, " + ex.getMessage());
        } catch (MissingItemException | IdSpaceExhaustedException ex) {
            throw new FailedCommandException("Unable to create ticket, " + ex.getMessage());
        } catch (NumberFormatException ex) {
            throw new FailedCommandException("Unable to create ticket, " + params[2] + " is not a valid integer");
        } catch (IllegalArgumentException ex) {
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
        if (!App.checkMinArgsCountWithPrint("ticket add", params.length, 6))
            return;

        String ticketId = params[2];
        String cashierId = params[3];
        String itemId = params[4];

        try {
            int amount = Integer.parseInt(params[5]);

            String[] personalizations = new String[0];
            if (params.length > 6) {
                personalizations = parsePersonalizations(6, params);
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
