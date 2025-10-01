package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.ParseResult;

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

    public static ParseResult tryParse(String[] tokens){
        return null;
    }

    public Command.ExecuteResult tryExecute(){
        return null;
    }
}
