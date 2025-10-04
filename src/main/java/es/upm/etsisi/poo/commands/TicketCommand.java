package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.*;

/**
 * Represents a command that falls under the ticket category, being those:
 *  - ticket new (resetea ticket en curso)
 *  - ticket add <prodId> <cantidad> (agrega al ticket la cantidad de ese producto)
 *  - ticket remove <prodId> (elimina todas las apariciones del producto, revisa si existe el id)
 *  - ticket print (imprime factura)
 */
public class TicketCommand {
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

    public static ParseResult tryParse(Parser parser){
        return null;
    }

    /**
     * Reads the subcommand from this command and calls the corresponding function to execute it
     * @param ticket The ticket on which the changes corresponding to the command will be applied
     * @param dataManager data manager from which necessary products will be taken
     * @return SUCCESS, if the command is executed correctly, and the error code if not
     */
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
}
