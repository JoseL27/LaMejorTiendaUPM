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

		if (Utils.checkArgsCountWithPrint("general", parser, 1, 6)) {
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

}
