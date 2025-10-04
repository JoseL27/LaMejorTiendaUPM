package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.*;

/**
 * Represents a command that falls under the ticket category, being those:
 *  - ticket new (resetea ticket en curso)
 *  - ticket add <prodId> <cantidad> (agrega al ticket la cantidad de ese producto)
 *  - ticket remove <prodId> (elimina todas las apariciones del producto, revisa si existe el id)
 *  - ticket print (imprime factura)
 */
public class TicketCommand extends Command{
    /**
     *  Represents a subcommand of a ticket command
     */
    enum SubCommand{
        NEW,
        ADD,
        REMOVE,
        PRINT;

        /**
         * Method to get a SubCommand from a string representation of it.
         * @param label String to parse representing the subcommand
         * @return A SubCommand parsed from the label or null, if the label is not valid
         */
        public static SubCommand fromLabel(String label){
            SubCommand result = null;

            try {
                result = SubCommand.valueOf(label);
            }catch (IllegalArgumentException ex){
                //Show error message or make another class handle it
            }finally{
                return result;
            }
        }
    }

    private SubCommand subCommand;


    /**
     * Used in SubCommand.ADD and SubCommand.REMOVE
     */
    private int prodId;

    /**
     * Used in SubCommand.ADD
     */
    private int amount;

    public TicketCommand(SubCommand subCommand, int prodId, int amount){
        this.subCommand = subCommand;
        this.prodId = prodId;
        this.amount = amount;
    }

    public TicketCommand(SubCommand subCommand, int prodId){
        this.subCommand = subCommand;
        this.prodId = prodId;
    }

    public TicketCommand(SubCommand subCommand){
        this.subCommand = subCommand;
    }

    /**
     * First entrypoint to parse 'ticket' command (assumes the parser.getCommand(0) is 'ticket').
     * This method is responsible for parsing different subcommands, it also invokes other parsing
     * methods if the subcommand in question needs more arguments.
     * This tryParse ignores extra arguments unless the arguments which are actually used have the wrong type.
     * Prints a warning to STDOUT if excess arguments were found.
     * @param parser Tokenized command
     * @return Parse result. Either a valid TicketCommand instance or a failure code
     */
    public static ParseResult tryParse(Parser parser){
        if (parser.getLength() < 2)
            return new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);

        SubCommand subCommand = SubCommand.fromLabel(parser.getCommand(1));

        ParseResult result = switch (subCommand) {
            case NEW -> tryParseNew(parser);
            case ADD -> tryParseAdd(parser);
            case REMOVE -> tryParseRemove(parser);
            case PRINT -> tryParsePrint(parser);
        };

        return result;
    }

    /**
     * Parses the 'new' subcommand of the 'ticket' command.
     * This function does not do any parsing besides checking the number of arguments and
     * creating a proper TicketCommand New instance.
     * @param parser The stream of tokens to parse
     * @return The result of the parse. If the amount of tokens is 2 then a valid
     * TicketCommand New instance OR a failure code with ParseResult.Code.INSUFICIENT_ARGUMENTS will be issued.
     */
    public static ParseResult tryParseNew(Parser parser) {
        if (parser.getLength() < 2)
            return new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);

        if (parser.getLength() > 2)
            System.out.println("DEBUG: Excess arguments found for subcommand NEW");

        return new ParseResult(new TicketCommand(SubCommand.NEW));
    }

    /**
     * Parses the 'print' subcommand of the 'ticket' command.
     * This function does not do any parsing besides checking the number of arguments and
     * creating a proper TicketCommand Print instance.
     * @param parser The stream of tokens to parse
     * @return The result of the parse. If the amount of tokens is 2 then a valid
     * TicketCommand Print instance OR a failure code with ParseResult.Code.INSUFICIENT_ARGUMENTS will be issued.
     */
    public static ParseResult tryParsePrint(Parser parser) {
        if (parser.getLength() < 2)
            return new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);

        if (parser.getLength() > 2)
            System.out.println("DEBUG: Excess arguments found for subcommand PRINT");

        return new ParseResult(new TicketCommand(SubCommand.PRINT));
    }

    /**
     * Parses the 'add' subcommand of the 'ticket' command.
     * This function parses each field sequentially and immediately returns a failed ParseResult if it
     * fails to parse any arguments.
     * FORMAT: ticket add <prodId> <cantidad>
     * @param parser The stream of tokens to parse
     * @return The result of the parse. If parsing is successful, this will return a valid TicketCommand Add instance
     * specifying productId and quantity. Or a failure code specifying which part of the parsing went wrong.
     */
    public static ParseResult tryParseAdd(Parser parser) {
        if (parser.getLength() < 4)
            return new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);

        Integer prodId = Utils.tryParseInt(parser.getCommand(2));
        if (prodId == null) return new ParseResult(ParseResult.Code.INVALID_NUMBER);

        Integer quantity = Utils.tryParseInt(parser.getCommand(3));
        if (quantity == null) return new ParseResult(ParseResult.Code.INVALID_NUMBER);

        if (parser.getLength() > 4)
            System.out.println("DEBUG: Excess arguments found for subcommand ADD");

        return new ParseResult(new TicketCommand(SubCommand.ADD, prodId, quantity));
    }

    /**
     * Parses the 'remove' subcommand of the 'ticket' command.
     * This function parses each field sequentially and immediately returns a failed ParseResult if it
     * fails to parse any arguments.
     * FORMAT: ticket remove <prodId>
     * @param parser The stream of tokens to parse
     * @return The result of the parse. If parsing is successful, this will return a valid TicketCommand Remove instance
     * specifying productId. Or a failure code specifying which part of the parsing went wrong.
     */
    public static ParseResult tryParseRemove(Parser parser) {
        if (parser.getLength() < 3)
            return new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);

        Integer prodId = Utils.tryParseInt(parser.getCommand(2));
        if (prodId == null) return new ParseResult(ParseResult.Code.INVALID_NUMBER);

        if (parser.getLength() > 3) {
            System.out.println("DEBUG: Excess arguments found for subcommand REMOVE");
        }

        return new ParseResult(new TicketCommand(SubCommand.REMOVE, prodId));
    }

    /**
     * Reads the subcommand from this command and calls the corresponding function to execute it
     * @param ticket The ticket on which the changes corresponding to the command will be applied
     * @param dataManager data manager from which necessary products will be taken
     * @return SUCCESS, if the command is executed correctly, and the error code if not
     */
    @Override
    public Command.ExecuteResult tryExecute(Ticket ticket, ArrayDataManager dataManager){
        Command.ExecuteResult result = null;

        if (ticket == null){
            ticket = new Ticket();
        }

        switch (this.subCommand) {
            case NEW -> result = tryExecuteNew(ticket);
            case ADD -> result = tryExecuteAdd(ticket, dataManager);
            case REMOVE -> result = tryExecuteRemove(ticket);
            case PRINT -> result = tryExecutePrint(ticket);
        }

        return result;
    }

    /**
     * Converts this instance of Ticket into a new one
     * @param ticket ticket to be reset or created
     * @return SUCCESS always, since no recognisable error can happen
     */
    private Command.ExecuteResult tryExecuteNew(Ticket ticket){
        ticket.reset();
        return Command.ExecuteResult.SUCCESS;
    }

    /**
     * Adds a product from the storage to the ticket
     * @param ticket ticket to which the product will be added
     * @param dataManager dataManager from which the product will be taken
     * @return SUCCESS, if the product is added correctly, or the corresponding error if not
     */
    private Command.ExecuteResult tryExecuteAdd(Ticket ticket, ArrayDataManager dataManager) {
        Command.ExecuteResult result;

        if (!ArrayDataManager.isValidId(this.prodId)) { // Use the one from DataManager when it is public
            result = Command.ExecuteResult.INVALID_ID;
        } else if (!isValidAmount(this.amount)) {
            result = Command.ExecuteResult.INVALID_AMOUNT;
        } else{
            Product productToAdd = dataManager.readProduct(this.prodId);
            if (productToAdd == null){
                result = Command.ExecuteResult.PRODUCT_NOT_IN_STORAGE;
            }else if (!ticket.addProduct(productToAdd, this.amount)){
                result = Command.ExecuteResult.DATA_ERROR;
            }else {
                result = Command.ExecuteResult.SUCCESS;
            }
        }
        return result;
    }


    /**
     * Removes all appearances of a product inside the ticket
     * @param ticket ticket from which the product will be removed
     * @return SUCCESS, if the product is removed correctly, or the corresponding error if not
     */
    private Command.ExecuteResult tryExecuteRemove(Ticket ticket){
        Command.ExecuteResult result = null;

        if (!ArrayDataManager.isValidId(this.prodId)){
            return Command.ExecuteResult.INVALID_ID;
        }

        if (ticket.removeProduct(this.prodId)){
            result = Command.ExecuteResult.SUCCESS;
        }else{
            result = Command.ExecuteResult.PRODUCT_NOT_IN_TICKET;
        }
        return result;
    }

    /**
     * Prints in standard output the summary of the ticket
     * @param ticket ticket to be printed
     * @return SUCCESS always, since no recognisable error can happen
     */
    private Command.ExecuteResult tryExecutePrint(Ticket ticket){
        System.out.println(ticket.summaryString());
        return Command.ExecuteResult.SUCCESS;
    }

    private boolean isValidAmount(int quantity){
        return quantity >= 0 && quantity <= Ticket.TICKET_MAX_PRODUCTS;
    }

    /**
     * tests
     */
    public static void main(String[] args) {
        Ticket testTicket = new Ticket();

        ArrayDataManager dataManager = new ArrayDataManager();
        dataManager.createProduct(1, "Libro POO", Product.Category.BOOK, 25);
        dataManager.createProduct(2, "Camiseta talla:M UPM", Product.Category.CLOTHES, 15);
        dataManager.createProduct(3, "Libro POO repetido", Product.Category.BOOK, 25);

        TicketCommand testCommand = null;

        System.out.println("test 1: New ticket");
        testCommand = new TicketCommand(SubCommand.NEW);
        System.out.println(testCommand.tryExecute(testTicket, dataManager));
        testCommand = new TicketCommand(SubCommand.PRINT);
        System.out.println(testCommand.tryExecute(testTicket, dataManager));

        System.out.println("test 2: Add product 1 2 times");
        testCommand = new TicketCommand(SubCommand.ADD, 1, 2);
        System.out.println(testCommand.tryExecute(testTicket, dataManager));
        testCommand = new TicketCommand(SubCommand.PRINT);
        System.out.println(testCommand.tryExecute(testTicket, dataManager));

        System.out.println("test 3: reseteo ticket");
        testCommand = new TicketCommand(SubCommand.NEW);
        System.out.println(testCommand.tryExecute(testTicket, dataManager));
        testCommand = new TicketCommand(SubCommand.PRINT);
        System.out.println(testCommand.tryExecute(testTicket, dataManager));

        System.out.println("test 4: Add product 2 1 time");
        testCommand = new TicketCommand(SubCommand.ADD, 2, 1);
        System.out.println(testCommand.tryExecute(testTicket, dataManager));
        testCommand = new TicketCommand(SubCommand.PRINT);
        System.out.println(testCommand.tryExecute(testTicket, dataManager));

        System.out.println("test 5: Add product with invalid id");
        testCommand = new TicketCommand(SubCommand.ADD, -1, 1);
        System.out.println(testCommand.tryExecute(testTicket, dataManager));
        testCommand = new TicketCommand(SubCommand.PRINT);
        System.out.println(testCommand.tryExecute(testTicket, dataManager));

        System.out.println("test 6: Add product not in storage");
        testCommand = new TicketCommand(SubCommand.ADD, 4, 1);
        System.out.println(testCommand.tryExecute(testTicket, dataManager));
        testCommand = new TicketCommand(SubCommand.PRINT);
        System.out.println(testCommand.tryExecute(testTicket, dataManager));

        System.out.println("test 7: Remove product not in ticket");
        testCommand = new TicketCommand(SubCommand.REMOVE, 3);
        System.out.println(testCommand.tryExecute(testTicket, dataManager));
        testCommand = new TicketCommand(SubCommand.PRINT);
        System.out.println(testCommand.tryExecute(testTicket, dataManager));

        System.out.println("test 8: Remove product 2");
        testCommand = new TicketCommand(SubCommand.REMOVE, 2);
        System.out.println(testCommand.tryExecute(testTicket, dataManager));
        testCommand = new TicketCommand(SubCommand.PRINT);
        System.out.println(testCommand.tryExecute(testTicket, dataManager));
    }
}
