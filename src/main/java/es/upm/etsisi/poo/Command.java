package es.upm.etsisi.poo;

import es.upm.etsisi.poo.commands.ProductCommand;

public class Command {
	public enum ExecuteResult {
	}

	/**
	 * Tries to parse the command represented by the first element in tokens and calls the corresponding class to do the rest of the parsing,
	 * unless there is an impossible amount of arguments or the input is null, in which case it returns an error directly
	 * @param tokens Input representing the command to parse, separated by arguments
	 * @return ParseResult with the command that was parsed, the corresponding error code if the parsing fails, or null if the input is null
	 */
	public static ParseResult TryParse(String[] tokens) {
		ParseResult result = null;

		if (tokens == null){
			System.out.println("Error: Null input, not yet implemented");
		}else if (tokens.length == 0){
			result = new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);
		}else if (tokens.length > 6){ // Currently the highest number of arguments in a command is 6, if this changes this value needs to be updated
			result = new ParseResult(ParseResult.Code.TOO_MANY_ARGUMENTS);
		}else {
			switch (tokens[0]) {
				case "prod":
					result = ProductCommand.TryParse(tokens);
					break;
				case "ticket":
					//result = TicketCommand.TryParse(tokens);
					break;
				case "help":
					//result = HelpCommand.TryParse(tokens);
					break;
				case "echo":
					//result = EchoCommand.TryParse(tokens);
					break;
				case "exit":
					//result = ExitCommand.TryParse(tokens);
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
