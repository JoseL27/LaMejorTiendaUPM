package es.upm.etsisi.poo;

import es.upm.etsisi.poo.commands.EchoCommand;
import es.upm.etsisi.poo.commands.HelpCommand;
import es.upm.etsisi.poo.commands.ProductCommand;
import es.upm.etsisi.poo.commands.TicketCommand;

public class Command {
	/**
	 * Tries to parse the command represented by the first element in tokens and calls the corresponding class to 	do the rest of the parsing,
	 * unless there is an impossible amount of arguments or the input is null, in which case it returns an error directly
	 * @param parser Input representing the command to parse, separated by arguments
	 * @return ParseResult with the command that was parsed, the corresponding error code if the parsing fails, or null if the input is null
	 */
	public static Command tryParse(Parser parser) {
		Command result = null;

		if (Command.checkArgsCountWithPrint("general", parser, 1, 6)) {
			result = switch (parser.getCommand(0)) {
			case "prod"	  -> ProductCommand.tryParse(parser);
			case "ticket" -> TicketCommand.tryParse(parser);
			case "help"	  -> new HelpCommand();
			case "echo"	  -> new EchoCommand(parser.getCommand(1));
			default		  -> null;
			};
		}
		return result;
	}
	
	public void tryExecute(Ticket ticket, ArrayDataManager dataManager) {
		assert false;
	}

	public static boolean checkArgsCountWithPrint(String prefix, Parser parser, int minAmount, int maxAmount) {
		boolean result = false;
		if (parser.getLength() < minAmount) {
			System.out.printf("%s: too few arguments, expected at %d arguments and got %d\n", prefix, minAmount, parser.getLength());
		} else if (parser.getLength() > maxAmount) {
			System.out.printf("%s: too many arguments, expected %d and got %d\n", prefix, maxAmount, parser.getLength());
		} else {
			result = true;
		}
		
		return result;
	}

	public static boolean checkArgsCountWithPrint(String prefix, Parser parser, int expectedAmount) {
		return checkArgsCountWithPrint(prefix, parser, expectedAmount, expectedAmount);
	}

	public static void printInvalidEnum(String prefix, String label, String invalidValue, Enum[] possibleValues) {
		System.out.printf("%s: error: invalid %s '%s', expected one of: %s\n",
						  prefix, label, invalidValue, Utils.arrayToString(possibleValues, "|"));
	}
	
	public static Product.Category tryParseCategoryWithPrint(String prefix, String categoryString) {
		Product.Category category = Product.Category.fromLabel(categoryString);
		if (category == null) {
			printInvalidEnum(prefix, "category", categoryString, Product.Category.values());
		}
		return category;
	}

	public static Product.Field tryParseFieldWithPrint(String prefix, String fieldString) {
		Product.Field field = Product.Field.fromLabel(fieldString);
		if (field == null) {
			printInvalidEnum(prefix, "field", fieldString, Product.Field.values());
		}
		return field;
	}	

	public static Integer tryParseIntWithPrint(String prefix, String intString) {
		Integer number = Utils.tryParseInt(intString);
		if (number == null) {
			System.out.printf("%s: error: expected an integer string, got '%s'\n", prefix, intString);
		}
		return number;
	}

}
