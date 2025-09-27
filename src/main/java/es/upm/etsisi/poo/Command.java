package es.upm.etsisi.poo;

import es.upm.etsisi.poo.commands.ProductCommand;

public class Command {
	public enum ExecuteResult {
	}
	
	public static ParseResult TryParse(String[] tokens) {
		ParseResult result = null;

		switch (tokens[0]){
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
				break;
		}
		return result;
	}
	
	public ExecuteResult TryExecute() {
		assert false;
		return null;
	}
}
