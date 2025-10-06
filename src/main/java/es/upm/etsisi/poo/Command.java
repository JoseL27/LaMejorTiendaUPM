package es.upm.etsisi.poo;

import es.upm.etsisi.poo.commands.EchoCommand;
import es.upm.etsisi.poo.commands.HelpCommand;
import es.upm.etsisi.poo.commands.ProductCommand;

public class Command {
	public enum ExecuteResult {
		SUCCESS,
		INVALID_ID,
		INVALID_AMOUNT,
		PRODUCT_NOT_IN_STORAGE,
		PRODUCT_NOT_IN_TICKET,
		DATA_ERROR
	}

	/**
	 * Tries to parse the command represented by the first element in tokens and calls the corresponding class to do the rest of the parsing,
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
					result = new ParseResult(new HelpCommand());
					//Provisional for tests
					System.out.println("HelpCommand called");
					break;
				case "echo":
					result = new ParseResult(new EchoCommand(parser.getCommand(1)));

					//Provisional for tests
					System.out.println("EchoCommand called");
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
}
