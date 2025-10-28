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
    public enum SubCommand{
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
                result = SubCommand.valueOf(label.toUpperCase());
            }catch (Exception ex){
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
    private int productId;

    /**
     * Used in SubCommand.ADD
     */
    private int amount;

    public TicketCommand(SubCommand subCommand, int productId, int amount){
        this.subCommand = subCommand;
        this.productId = productId;
        this.amount = amount;
    }

    public TicketCommand(SubCommand subCommand, int productId){
        this.subCommand = subCommand;
        this.productId = productId;
    }

    public TicketCommand(SubCommand subCommand){
        this.subCommand = subCommand;
    }

    /**
     * First entrypoint to parse 'ticket' command (assumes the params.getCommand(0) is 'ticket').
     * This method is responsible for parsing different subcommands, it also invokes other parsing
     * methods if the subcommand in question needs more arguments.
     * This tryParse ignores extra arguments unless the arguments which are actually used have the wrong type.
     * Prints a warning to STDOUT if excess arguments were found.
     * @param params Tokenized command
     * @return Parse result. Either a valid TicketCommand instance or a failure code
     */
    public static Command tryParse(String[] params){
		if (!Utils.checkArgsCountWithPrint("ticket", params, 2, 4)) return null;

		SubCommand subCommand = SubCommand.fromLabel(params[1]);
		if (subCommand == null) {
			Utils.printInvalidEnum("ticket", "sub command", params[1], SubCommand.values());
			return null;
		}

		return switch (subCommand) {
		case NEW	-> tryParseNew(params);
		case ADD	-> tryParseAdd(params);
		case REMOVE -> tryParseRemove(params);
		case PRINT	-> tryParsePrint(params);
		};
    }
	
    /**
	 * Parses the 'new' subcommand of the 'ticket' command.
	 * This function does not do any parsing besides checking the number of arguments and
	 * creating a proper TicketCommand New instance.
	 * @param params The stream of tokens to parse
	 * @return The result of the parse. If the amount of tokens is 2 then a valid
	 * TicketCommand New instance OR a failure code with ParseResult.Code.INSUFICIENT_ARGUMENTS will be issued.
	 */
    public static Command tryParseNew(String[] params) {
		return Utils.checkArgsCountWithPrint("ticket new", params, 2) ? new TicketCommand(SubCommand.NEW) : null;
    }
	
    /**
	 * Parses the 'print' subcommand of the 'ticket' command.
	 * This function does not do any parsing besides checking the number of arguments and
	 * creating a proper TicketCommand Print instance.
	 * @param params The stream of tokens to parse
	 * @return The result of the parse. If the amount of tokens is 2 then a valid
	 * TicketCommand Print instance OR a failure code with ParseResult.Code.INSUFICIENT_ARGUMENTS will be issued.
	 */
    public static Command tryParsePrint(String[] params) {
		return Utils.checkArgsCountWithPrint("ticket print", params, 2) ? new TicketCommand(SubCommand.PRINT) : null;
    }
	
    /**
	 * Parses the 'add' subcommand of the 'ticket' command.
	 * This function parses each field sequentially and immediately returns a failed ParseResult if it
	 * fails to parse any arguments.
	 * FORMAT: ticket add <productId> <cantidad>
	 * @param params The stream of tokens to parse
	 * @return The result of the parse. If parsing is successful, this will return a valid TicketCommand Add instance
	 * specifying productId and quantity. Or a failure code specifying which part of the parsing went wrong.
	 */
    public static Command tryParseAdd(String[] params) {
		if (!Utils.checkArgsCountWithPrint("ticket add", params, 4)) return null ;
		
		Integer productId = Utils.tryParseInt(params[2]);
		if (productId == null){
            Utils.printInvalidDataType("ticket add", "integer", params[2]);
            return null;
        }

		Integer quantity = Utils.tryParseInt(params[3]);
        if (quantity == null){
            Utils.printInvalidDataType("ticket add", "integer", params[3]);
            return null;
        }

		return new TicketCommand(SubCommand.ADD, productId, quantity);
    }
	
    /**
	 * Parses the 'remove' subcommand of the 'ticket' command.
	 * This function parses each field sequentially and immediately returns a failed ParseResult if it
	 * fails to parse any arguments.
	 * FORMAT: ticket remove <productId>
	 * @param params The stream of tokens to parse
	 * @return The result of the parse. If parsing is successful, this will return a valid TicketCommand Remove instance
	 * specifying productId. Or a failure code specifying which part of the parsing went wrong.
	 */
    public static Command tryParseRemove(String[] params) {
		if (!Utils.checkArgsCountWithPrint("ticket remove", params, 3)) return null;
		
		Integer productId = Utils.tryParseInt(params[2]);
		if (productId == null){
            Utils.printInvalidDataType("ticket remove", "integer", params[2]);
            return null;
        }
		
		return new TicketCommand(SubCommand.REMOVE, productId);
    }
	
    /**
	 * Reads the subcommand from this command and calls the corresponding function to execute it
	 * @param ticket The ticket on which the changes corresponding to the command will be applied
	 * @param dataManager data manager from which necessary products will be taken
	 * @return SUCCESS, if the command is executed correctly, and the error code if not
	 */
    @Override
    public void tryExecute(Ticket ticket, Inventory dataManager) {
		switch (this.subCommand) {
		case NEW	-> tryExecuteNew(ticket);
		case ADD	-> tryExecuteAdd(ticket, dataManager);
		case REMOVE -> tryExecuteRemove(ticket);
		case PRINT	-> tryExecutePrint(ticket);
		};
    }

    /**
     * Converts this instance of Ticket into a new one
     * @param ticket ticket to be reset or created
     * @return SUCCESS always, since no recognisable error can happen
     */
    private void tryExecuteNew(Ticket ticket){
        ticket.reset();
		System.out.println("ticket new: ok");
    }

    /**
     * Adds a product from the storage to the ticket
     * @param ticket ticket to which the product will be added
     * @param dataManager dataManager from which the product will be taken
     * @return SUCCESS, if the product is added correctly, or the corresponding error if not
     */
    private void tryExecuteAdd(Ticket ticket, Inventory dataManager) {
        if (!Inventory.isValidId(this.productId)) { // Use the one from DataManager when it is public
			System.out.printf("ticket add: error: expected id greater or equal than zero\n");
			
        } else if (!isValidAmount(this.amount)) {
			System.out.printf("ticket add: error: expected amount greater or equal than zero\n");
			
        } else {
            Product productToAdd = dataManager.readProduct(this.productId);
			
            if (productToAdd == null){
				System.out.printf("ticket add: error: product with id %d not found\n", this.productId);
				
            } else if (!ticket.addProduct(productToAdd, this.amount)){
				System.out.printf("ticket add: error: ticket is full (100 items max)\n");
				
            } else {
				System.out.println(ticket.summaryString());
				System.out.println("ticket add: ok");
            }
        }
    }


    /**
     * Removes all appearances of a product inside the ticket
     * @param ticket ticket from which the product will be removed
     * @return SUCCESS, if the product is removed correctly, or the corresponding error if not
     */
    private void tryExecuteRemove(Ticket ticket){
        if (!Inventory.isValidId(this.productId)){
			System.out.printf("ticket add: error: expected id greater or equal than zero\n");
			
        } else {
			Product removed = ticket.removeProduct(this.productId);
			if (removed != null) {
				System.out.println(ticket.summaryString());
				System.out.println("ticket remove: ok");
			
			} else {
				System.out.printf("ticket remove: error: product with id %d not in ticket\n", this.productId);
			}
		}
    }

    /**
     * Prints in standard output the summary of the ticket
     * @param ticket ticket to be printed
     * @return SUCCESS always, since no recognisable error can happen
     */
    private void tryExecutePrint(Ticket ticket){
        System.out.println(ticket.summaryString());
        ticket.reset();
		System.out.println("ticket print: ok");
    }

    private boolean isValidAmount(int quantity){
        return quantity > 0 && quantity <= Ticket.TICKET_MAX_PRODUCTS;
    }

    @Override
    public boolean equals(Object obj){
        boolean result = false;

        if (obj != null && obj.getClass() == this.getClass()){
            TicketCommand otherCommand = (TicketCommand) obj;
            result = otherCommand.subCommand == this.subCommand
                    && otherCommand.productId == this.productId
                    && otherCommand.amount == this.amount;
        }

        return result;
    }
}
