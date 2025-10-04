import es.upm.etsisi.poo.Command;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

package es.upm.etsisi.poo.commands;


class EchoCommandTest {

    @Test
    void constructorStoresMessage() {
        EchoCommand cmd = new EchoCommand("hello");
        // Use reflection to access private field for testing
        try {
            var field = EchoCommand.class.getDeclaredField("message");
            field.setAccessible(true);
            assertEquals("hello", field.get(cmd));
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }

    @Test
    void tryParseReturnsInsufficientArgumentsIfNoMessage() {
        Parser parser = mock(Parser.class);
        when(parser.getLength()).thenReturn(1);
        ParseResult result = EchoCommand.tryParse(parser);
        assertEquals(ParseResult.Code.INSUFICIENT_ARGUMENTS, result.getCode());
    }

    @Test
    void tryParseReturnsEchoCommandIfMessagePresent() {
        Parser parser = mock(Parser.class);
        when(parser.getLength()).thenReturn(2);
        when(parser.getToken(1)).thenReturn("test");
        ParseResult result = EchoCommand.tryParse(parser);
        assertNotNull(result.getCommand());
        assertTrue(result.getCommand() instanceof EchoCommand);
        // Check that the message is set correctly
        try {
            var field = EchoCommand.class.getDeclaredField("message");
            field.setAccessible(true);
            assertEquals("test", field.get(result.getCommand()));
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }

    @Test
    void tryExecuteReturnsNull() {
        EchoCommand cmd = new EchoCommand("anything");
        Command.ExecuteResult result = cmd.tryExecute();
        assertNull(result);
    }
}