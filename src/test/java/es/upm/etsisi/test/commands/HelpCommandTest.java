import es.upm.etsisi.poo.Command;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

package es.upm.etsisi.poo.commands;



class HelpCommandTest {

    @Test
    void testTryExecuteReturnsOK() {
        HelpCommand helpCommand = new HelpCommand();
        Command.ExecuteResult result = helpCommand.TryExecute();
        assertEquals(Command.ExecuteResult.OK, result, "TryExecute should return OK");
    }
}