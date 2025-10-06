package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.ParseResult;
import es.upm.etsisi.poo.Parser;

/**
 * Represents a command that echoes to the standard output a given message.
 * @author Andresito-Oficial - 04/10
 * @see Command
 */
public class EchoCommand extends Command{

    private final String message;

    public EchoCommand (String message)
    {
        this.message = message;
    }

    public static ParseResult tryParse (Parser parser)
    {
        if ( parser.getLength() < 2 ) return new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);
        return new ParseResult(new EchoCommand(parser.getCommand(1)));
    }

    public Command.ExecuteResult tryExecute() {
        /*if ( message.isEmpty () )
        {
            System.err.println ( "Error: echo command requires text to echo" );
        }
        else
        {
            //System.out.println ( "echo" + text );
        }*/
        return null;
    }

}
