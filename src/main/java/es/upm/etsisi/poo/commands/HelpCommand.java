package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.Ticket;
import es.upm.etsisi.poo.ArrayDataManager;
import es.upm.etsisi.poo.Product;

/**
 * Represents a command that provides help information to the user.
 * This class is intended to be used to display available commands
 * or usage instructions within the application.
 * @author Andresito-Oficial - 04/10
 * @see Command
 */
public class HelpCommand extends Command{

    public HelpCommand() {}

	public Command.ExecuteResult tryExecute(Ticket ticket, ArrayDataManager dataManager) {
        System.out.println("Commands:");
        System.out.println(" prod add <id> \"<name>\" <category> <price>");
        System.out.println(" prod list" );
		
        System.out.println(" prod update <id> ");
		Product.Field[] fieldValues = Product.Field.values();
		for (int i = 0; i < fieldValues.length; i++) { 
            System.out.print(fieldValues[i]);
			if (i  < fieldValues.length - 1) {
				System.out.print("|");
			}
        }
		System.out.println(" <value>");
		
        System.out.println(" prod remove <id>");
        System.out.println(" ticket new");
        System.out.println(" ticket add <prodid> <quantity>");
        System.out.println(" ticket remove <prodid>");
        System.out.println(" ticket print" );
        System.out.println(" echo \"<text>\"");
        System.out.println(" help");
        System.out.println(" exit");
        System.out.println();
		
        System.out.print("Categories: ");
		Product.Category[] categoryValues = Product.Category.values();
		for (int i = 0; i < categoryValues.length; i++) { 
            System.out.print(categoryValues[i]);
			if (i  < categoryValues.length - 1) {
				System.out.print(", ");
			}
        }
        System.out.println();
		
        System.out.println("Discounts if there are ≥2 units in the same category:");
		for (int i = 0; i < categoryValues.length; i++) { 
			System.out.printf("%s %d%%", categoryValues[i].name(), (int)categoryValues[i].getDiscountPercent()*100);
			if (i  < categoryValues.length - 1) {
				System.out.print(", ");
			}
        }
		
        System.out.println(".");
        return Command.ExecuteResult.SUCCESS;
    }

}
