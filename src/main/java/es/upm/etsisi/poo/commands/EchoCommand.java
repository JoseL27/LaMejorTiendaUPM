
package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.Parser;
import es.upm.etsisi.poo.Utils;
import es.upm.etsisi.poo.Ticket;
import es.upm.etsisi.poo.Inventory;

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

    public static Command tryParse(Parser parser) {
		return Utils.checkArgsCountWithPrint("echo", parser, 2)
			? new EchoCommand(parser.getCommand(1)) : null;
    }

	@Override
	public void tryExecute(Ticket ticket, Inventory data) {
		System.out.printf("echo \"%s\"\n", this.message);
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null || (obj.getClass() != this.getClass())) return false;
		EchoCommand otherEchoCmd = (EchoCommand)obj;
		return Utils.nullOrEquals(this.message, otherEchoCmd.message);
	}

	@Override
	public String toString() {
		return String.format("{class: EchoCommand, message: '%s'}", this.message); 
	}
}
