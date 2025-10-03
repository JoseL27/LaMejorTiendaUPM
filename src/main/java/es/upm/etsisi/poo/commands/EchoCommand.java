package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.Command;

public class EchoCommand extends Command{
    private final String message;

    public EchoCommand (String message)
    {
        this.message = message;
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
