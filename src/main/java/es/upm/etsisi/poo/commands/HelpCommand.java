package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.Ticket;
import es.upm.etsisi.poo.ArrayDataManager;
import es.upm.etsisi.poo.Product;
import es.upm.etsisi.poo.Utils;

/**
 * Represents a command that provides help information to the user.
 * This class is intended to be used to display available commands
 * or usage instructions within the application.
 * @author Andresito-Oficial - 04/10
 * @see Command
 */
public class HelpCommand extends Command{

    public HelpCommand() {}

	public void tryExecute(Ticket ticket, ArrayDataManager dataManager) {
        System.out.println("Commands:");
        System.out.println(" prod add <id> \"<name>\" <category> <price>");
        System.out.println(" prod list" );
		
        System.out.printf(" prod update <id> %s <value>\n", Utils.arrayToString(Product.Field.values(), "|"));
		
        System.out.println(" prod remove <id>");
        System.out.println(" ticket new");
        System.out.println(" ticket add <prodId> <quantity>");
        System.out.println(" ticket remove <prodId>");
        System.out.println(" ticket print" );
        System.out.println(" echo \"<texto>\"");
        System.out.println(" help");
        System.out.println(" exit");
		System.out.println();

		Product.Category[] categoryValues = Product.Category.values();
        System.out.printf("Categories: %s\n", Utils.arrayToString(categoryValues, ", "));
        System.out.print("Discounts if there are ≥2 units in the category: ");

		for (int i = 0; i < categoryValues.length; i++) { 
			System.out.printf("%s %.0f%%", categoryValues[i].name(), categoryValues[i].getDiscountPercent()*100);
			if (i  < categoryValues.length - 1) {
				System.out.print(", ");
			}
        }
		
        System.out.println(".");
    }

}
