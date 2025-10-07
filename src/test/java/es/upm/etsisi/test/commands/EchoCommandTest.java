package es.upm.etsisi.test.commands;

import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.Parser;
import es.upm.etsisi.poo.commands.EchoCommand;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EchoCommandTest {

	// successes
    @Test
    void tryParseTest() {
		Command result = EchoCommand.tryParse(new Parser("echo test"));
		EchoCommand expected = new EchoCommand("test");
		assertEquals(expected, expected);
    }

	@Test
	void basicTryParseQuotedTest() {
		Command result = EchoCommand.tryParse(new Parser("echo \"test one two three\""));
		EchoCommand expected = new EchoCommand("test one two three");
		assertEquals(expected, result);
	}

	// failures
    @Test
    void insuficientArgumentsTest() {
		assertNull(EchoCommand.tryParse(new Parser("echo")));
		// TODO: Specify with output test
    }

    @Test
    void tooManyArgumentsTest() {
		assertNull(EchoCommand.tryParse(new Parser("echo one two")));
		// TODO: Specify with output test
    }
}
