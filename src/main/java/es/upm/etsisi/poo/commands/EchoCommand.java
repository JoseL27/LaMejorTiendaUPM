
package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.ParseResult;
import es.upm.etsisi.poo.Parser;
import es.upm.etsisi.poo.Utils;
import es.upm.etsisi.poo.Ticket;
import es.upm.etsisi.poo.ArrayDataManager;

/**
 * Represents a command that echoes to the standard output a given message.
 * @author Andresito-Oficial - 04/10
 * @see Command
 */
public class EchoCommand extends Command {

    private String message;

    public EchoCommand (String message) {
        this.message = message;
    }

	public String getMessage() {
		return this.message;
	}

    public static ParseResult tryParse (Parser parser) {
        if (parser.getLength() < 2) return new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);
		if (parser.getLength() > 2) return new ParseResult(ParseResult.Code.TOO_MANY_ARGUMENTS);
        return new ParseResult(new EchoCommand(parser.getCommand(1)));
    }

	@Override
	public Command.ExecuteResult tryExecute(Ticket ticket, ArrayDataManager data) { 
		System.out.printf("echo \"%s\"", this.message);
        return Command.ExecuteResult.SUCCESS;
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null || (obj.getClass() != this.getClass())) return false;
		EchoCommand otherEchoCmd = (EchoCommand)obj;
		return Utils.nullOrEquals(this.message, otherEchoCmd.message);
	}
}
