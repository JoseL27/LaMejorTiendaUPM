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

    public static ParseResult tryParse(Parser parser){
        return null;
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
        ticket = new Ticket();
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
