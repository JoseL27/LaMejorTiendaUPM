package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.*;

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
    public void eval(String[] params) {
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
    private void evalNew(String[] params) {
        // Parse
        if (!App.checkArgsCountWithPrint("ticket new", params.length, 4, 5))
            return;

        String cashierId = params[params.length - 2];
        if (!Cashier.isValidId(cashierId)) {
            System.out.printf("ticket new: error: invalid cashier id '%s' expected 'UW' followed by 7 digits\n", cashierId);
            return;
        }

        String clientId = params[params.length - 1];
        if (!Client.isValidId(clientId)) {
            System.out.printf("ticket new: error: invalid client id '%s', please enter a valid NIF/NIE\n", clientId);
            return;
        }

        UserManager userManager = UserManager.getInstance();

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
            ticketId = App.tryParseInt(params[2]);
            if (ticketId == null) {
                App.printInvalidDataType("ticket new", "integer", params[2]);
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
            Ticket created = cashier.createTicket(ticketId);

            if (created != null) {
                System.out.print(created.summaryString());
                System.out.println("ticket new: ok");
            } else {
                System.out.println("ticket new: error: failed to add ticket to cashier.");
            }
        } else {
            System.out.println("ticket new: error: failed to add the ticket to the client");
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
    private void evalAdd(String[] params) {
        // Parse
        if (!App.checkMinArgsCountWithPrint("ticket add", params.length, 4))
            return;

        Integer ticketId = App.tryParseInt(params[2]);
        if (ticketId == null) {
            App.printInvalidDataType("ticket add", "integer", params[2]);
            return;
        }

        String cashierId = params[3];
        if (!Cashier.isValidId(cashierId)) {
            System.out.printf("ticket new: error: invalid cashier id '%s' expected 'UW' followed by 7 digits\n", cashierId);
            return;
        }

        Integer productId = App.tryParseInt(params[4]);
        if (productId == null) {
            App.printInvalidDataType("ticket add", "integer", params[4]);
            return;
        }

        Integer amount = App.tryParseInt(params[5]);
        if (amount == null) {
            App.printInvalidDataType("ticket add", "integer", params[5]);
            return;
        }

        String[] personalizations = null;
        if (params.length > 6) {
            personalizations = parsePersonalizations(6, params);
        }

        // Execute
        if (ticketId < 0) {
            System.out.printf("ticket add: error: ticket id '%d' is invalid, expected a positive number\n", ticketId);
            return;
        }

        if (!Inventory.isValidId(productId)) {
            System.out.printf("ticket add: error: expected id greater or equal than zero\n");
            return;
        }

        Product productToAdd = Inventory.getInstance().readProduct(productId);
        if (productToAdd == null) {
            System.out.printf("ticket add: error: could not find product with id %s\n", productId);
            return;
        }

        if (Inventory.getInstance().isTimedProduct(productToAdd)) {
            TimedProduct timedProduct = (TimedProduct) productToAdd; //Cast before checking
            if (LocalDateTime.now().isAfter(timedProduct.getExpirationDate())) {
                System.out.printf("ticket add: error: not enough time to prepare, you need more than %s hours\n",
                        timedProduct.getType().getHoursForPreparing());
                return;
            }
        }

        Cashier cashier = UserManager.getInstance().findCashier(cashierId);
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
            System.out.print(ticket.summaryString());
            System.out.println("ticket add: ok");
        } else {
            System.out.printf("ticket add: error: failed to add product\n");
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
    private void evalRemove(String[] params) {
        // Parse
        if (!App.checkArgsCountWithPrint("ticket remove", params.length, 5))
            return;

        Integer ticketId = App.tryParseInt(params[2]);
        if (ticketId == null) {
            App.printInvalidDataType("ticket remove", "integer", params[2]);
            return;
        }

        String cashierId = params[3];
        if (!Cashier.isValidId(cashierId)) {
            System.out.printf("ticket remove: error: invalid cashier id '%s' expected 'UW' followed by 7 digits\n", cashierId);
            return;
        }

        Integer productId = App.tryParseInt(params[4]);
        if (productId == null) {
            App.printInvalidDataType("ticket remove", "integer", params[4]);
            return;
        }

        // Execute
        if (ticketId < 0) {
            System.out.printf("ticket remove: error: ticket id '%d' is invalid, expected a positive number\n", ticketId);
            return;
        }

        if (!Inventory.isValidId(productId)) {
            System.out.printf("ticket remove: error: expected id greater or equal than zero\n");
            return;
        }

        Cashier cashier = UserManager.getInstance().findCashier(cashierId);
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
            System.out.print(ticket.summaryString());
            System.out.println("ticket remove: ok");
        } else {
            System.out.printf("ticket remove: error: failed to remove product\n");
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
    private void evalPrint(String[] params) {
        // Parse
        if (!App.checkArgsCountWithPrint("ticket print", params.length, 4))
            return;

        Integer ticketId = App.tryParseInt(params[2]);
        if (ticketId == null) {
            App.printInvalidDataType("ticket print", "integer", params[2]);
            return;
        }

        String cashierId = params[3];
        if (!Cashier.isValidId(cashierId)) {
            System.out.printf("ticket print: error: invalid cashier id '%s' expected 'UW' followed by 7 digits\n", cashierId);
            return;
        }

        // Execute
        if (ticketId < 0) {
            System.out.printf("ticket print: error: ticket id '%d' is invalid, expected a positive number\n", ticketId);
            return;
        }

        Cashier cashier = UserManager.getInstance().findCashier(cashierId);
        if (cashier == null) {
            System.out.printf("ticket print: error: cashier with id '%s' was not found\n", cashierId);
            return;
        }

        Ticket ticket = cashier.findTicket(ticketId);
        if (ticket != null) {
            if(ticket.tryClose()){
                System.out.print(ticket.summaryString());
                System.out.println("ticket print: ok");
            }else{
                System.out.printf("ticket print: error: ticket with id '%d' has TimedProducts out of date\n", ticketId);
            }
        } else {
            System.out.printf("ticket print: error: ticket with id '%d' not found in cashier with id '%s'\n", ticketId, cashierId);
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
