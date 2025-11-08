package es.upm.etsisi.poo;

import es.upm.etsisi.poo.commands.ProductCommand;
import es.upm.etsisi.poo.commands.TicketCommand;

/**
 * Tries to parse the command represented by the first element in tokens and calls the corresponding class to 	do the rest of the parsing,
 * unless there is an impossible amount of arguments or the input is null, in which case it returns an error directly
 * @param parser Input representing the command to parse, separated by arguments
 * @return ParseResult with the command that was parsed, the corresponding error code if the parsing fails, or null if the input is null
 */
		
public interface Command {
	public void eval(String[] args, UserManager userManager, Inventory dataManager);
}
