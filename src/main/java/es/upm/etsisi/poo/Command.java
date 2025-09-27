package es.upm.etsisi.poo;

import es.upm.etsisi.poo.commands.ProductCommand;

public class Command {
	public enum ExecuteResult {
	}
	
	public static ParseResult TryParse(String[] tokens) {
		ParseResult result = null;

		if (tokens == null){
			System.out.println("Error: Null input, not yet implemented");
		}else if (tokens.length == 0){
			result = new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);
		}else if (tokens.length > 6){
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
