package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.Command;

/**
 * Represents a command that provides help information to the user.
 * This class is intended to be used to display available commands
 * or usage instructions within the application.
 * @author Andresito-Oficial - 04/10
 * @see Command
 */
public class HelpCommand extends Command{

    public HelpCommand() {}

    public Command.ExecuteResult TryExecute() {
        /*System.out.println ( "Commands:" );
        System.out.println ( " prod add <id> \"<name>\" <category> <price>" );
        System.out.println ( " prod list" );
        System.out.println ( " prod update <id> NAME|CATEGORY|PRICE <value>" );
        System.out.println ( " prod remove <id>" );
        System.out.println ( " ticket new" );
        System.out.println ( " ticket add <prodid> <quantity>" );
        System.out.println ( " ticket remove <prodid>" );
        System.out.println ( " ticket print" );
        System.out.println ( " echo \"<text>\"" );
        System.out.println ( " help" );
        System.out.println ( " exit" );
        System.out.println ();
        System.out.print ( " Categories:" );
        for ( Product.Category c : Product.Category.values() )
        {
            System.out.print ( c + ", " );
        }
        System.out.println ();
        System.out.println ( "Discounts if there are ≥2 units in the same category:" );
        for ( Product.Category c : Product.Category.values() )
        {
            final int d = (int) c.getDiscountPercent () * 10;
            System.out.print ("  " + c + " " + d + "%," );
        }
        System.out.println ();*/
        return Command.ExecuteResult.OK;
    }

}
