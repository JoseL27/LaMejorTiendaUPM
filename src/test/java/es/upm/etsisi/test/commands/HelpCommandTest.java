package es.upm.etsisi.test.commands;

import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.commands.HelpCommand;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HelpCommandTest {

    @Test
    void testTryExecuteReturnsOK() {
        HelpCommand helpCommand = new HelpCommand();
        Command.ExecuteResult result = helpCommand.TryExecute();
        assertEquals(Command.ExecuteResult.OK, result, "TryExecute should return OK");
    }
}