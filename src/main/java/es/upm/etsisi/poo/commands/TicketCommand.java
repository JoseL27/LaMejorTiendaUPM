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
    private int quantity;

    public static ParseResult tryParse(Parser parser){
        return null;
    }

    /**
     * Reads the subcommand from this command and calls the corresponding function to execute it
     * @param ticket The ticket on which the changes corresponding to the command will be applied
     * @return SUCCESS, if the command is executed correctly, and the error code if not
     */
    public Command.ExecuteResult tryExecute(Ticket ticket){ // may have to change parameters depending on ticket handling implementation
        Command.ExecuteResult result = null;

        if (ticket == null){
            result = Command.ExecuteResult.TICKET_DOES_NOT_EXIST;
        }else {
            switch (this.subCommand) {
                case NEW -> result = tryExecuteNew(ticket);
                case ADD -> result = tryExecuteAdd(ticket);
                case REMOVE -> result = tryExecuteRemove(ticket);
                case PRINT -> result = tryExecutePrint(ticket);
            }
        }

        return result;
    }

    private Command.ExecuteResult tryExecuteNew(Ticket ticket){
        ticket = new Ticket();
        return Command.ExecuteResult.SUCCESS;
    }

    private Command.ExecuteResult tryExecuteAdd(Ticket ticket){
        Command.ExecuteResult result = null;
        if (!isValidId(this.prodId)){ // Use the one from DataManager when it is public
            return Command.ExecuteResult.INVALID_ID;
        }
        if (!isValidQuantity(this.quantity)){
            return Command.ExecuteResult.INVALID_QUANTITY;
        }
        // Get product from dataManager
        // if product == null result = PRODUCT_NOT_IN_STORAGE
        //else if !ticket.addProduct(product, this.quantity) result = DATA_ERROR
        //else result = SUCCESS
        return result;
    }


    private Command.ExecuteResult tryExecuteRemove(Ticket ticket){
        Command.ExecuteResult result = null;

        if (!isValidId(this.prodId)){
            return Command.ExecuteResult.INVALID_ID;
        }

        if (ticket.removeProduct(this.prodId)){
            result = Command.ExecuteResult.SUCCESS;
        }else{
            result = Command.ExecuteResult.PRODUCT_NOT_IN_TICKET;
        }
        return result;
    }

    private Command.ExecuteResult tryExecutePrint(Ticket ticket){
        return null;
    }

    private boolean isValidId(int id){ // Delete this when the one from DataManager is public
        return id >= 0;
    }

    private boolean isValidQuantity(int quantity){
        return quantity >= 0 && quantity <= Ticket.TICKET_MAX_PRODUCTS;
    }
}
