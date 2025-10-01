package es.upm.etsisi.poo;

import es.upm.etsisi.poo.commands.ProductCommand;

public class Command {
	public enum ExecuteResult {
		INVALID_ID,
		INVALID_QUANTITY,
		PRODUCT_NOT_IN_STORAGE,
		PRODUCT_NOT_IN_TICKET
	}

	/**
	 * Tries to parse the command represented by the first element in tokens and calls the corresponding class to 	do the rest of the parsing,
	 * unless there is an impossible amount of arguments or the input is null, in which case it returns an error directly
	 * @param tokens Input representing the command to parse, separated by arguments
	 * @return ParseResult with the command that was parsed, the corresponding error code if the parsing fails, or null if the input is null
	 */
	public static ParseResult tryParse(Parser parser) {
		ParseResult result = null;

		if (parser == null){
			System.out.println("Error: Null input, not yet implemented");
			result = new ParseResult(ParseResult.Code.INVALID_COMMAND);
		} else if (parser.getLength() == 0){
			result = new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);
		} else if (parser.getLength() > 6){ // Currently the highest number of arguments in a command is 6, if this changes this value needs to be updated
			result = new ParseResult(ParseResult.Code.TOO_MANY_ARGUMENTS);
		} else {
			switch (parser.getCommand(0)) {
				case "prod":
					result = ProductCommand.tryParse(parser);
					break;
				case "ticket":
					//result = TicketCommand.tryParse(parser);

					//Provisional for tests
					System.out.println("TicketCommand called");
					result = new ParseResult(ParseResult.Code.SUCCESS);
					break;
				case "help":
					//result = HelpCommand.tryParse(parser);

					//Provisional for tests
					System.out.println("HelpCommand called");
					result = new ParseResult(ParseResult.Code.SUCCESS);
					break;
				case "echo":
					//result = EchoCommand.tryParse(parser);

					//Provisional for tests
					System.out.println("EchoCommand called");
					result = new ParseResult(ParseResult.Code.SUCCESS);
					break;
				case "exit":
					//result = ExitCommand.tryParse(parser);

					//Provisional for tests
					System.out.println("ExitCommand called");
					result = new ParseResult(ParseResult.Code.SUCCESS);
					break;
				default:
					result = new ParseResult(ParseResult.Code.INVALID_COMMAND);
					break;
			}
		}
		return result;
	}
	
	public ExecuteResult TryExecute() {
		assert false;
		return null;
	}

	public static void main(String[] args){
		// String[][] testInputs =
		// 		{
		// 				{ "prod" },
		// 				{ "prod", "add", "INVALID_NUMBER", "Libro POO", "BOOK", "25" },
		// 				{ "prod", "add", "1", "Libro POO", "BOOK", "25" },
		// 				{ "prod", "add", "2", "Camiseta talla:M UPM", "CLOTHES", "15" },
		// 				{ "prod", "list" },
		// 				{ "prod", "update", "1", "NAME", "Libro POO V2" },
		// 				{ "prod", "update", "1", "PRICE", "30" },
		// 				{ "prod", "add", "3", "Libro POO repetido Error", "BOOK", "25" },
		// 				{ "prod", "remove", "3" },
		// 				{ "ticket", "new" },
		// 				{ "ticket", "add", "1", "1" },
		// 				{ "ticket", "remove" },
		// 				{ "ticket", "print" },
		// 				{ "help" },
		// 				{ "echo", "TEST" },
		// 				{ "exit" },
		// 				{ "prud" },
		// 				{ "prod", "add", "1", "Libro POO", "BOOK", "25" , "INVALID", "INVALID"},
		// 				null,
		// 				{},
		// 		};

		// for (int i = 0; i < testInputs.length; i++){
		// 	System.out.println("Test " + (i + 1) + ":");
		// 	if (testInputs[i] != null) {
		// 		System.out.println("Input: " + Utils.arrayToString(testInputs[i]));
		// 	}

		// 	ParseResult testResult = tryParse(testInputs[i]);
		// 	System.out.println("Result: " + testResult.toString());
		// }
	}
}
