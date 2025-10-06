package es.upm.etsisi.test.commands;

import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.ParseResult;
import es.upm.etsisi.poo.Parser;
import es.upm.etsisi.poo.commands.EchoCommand;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EchoCommandTest {

	// successes
    @Test
    void tryParseTest() {
		ParseResult result = EchoCommand.tryParse(new Parser("echo test"));
		ParseResult expected = new ParseResult(new EchoCommand("test"));
		assertEquals(expected, result);
    }

	@Test
	void basicTryParseQuotedTest() {
		ParseResult result = EchoCommand.tryParse(new Parser("echo \"test one two three\""));
		ParseResult expected = new ParseResult(new EchoCommand("test one two three"));
		assertEquals(expected, result);
	}

	// failures
    @Test
    void insuficientArgumentsTest() {
		ParseResult result = EchoCommand.tryParse(new Parser("echo"));
		assertEquals(result, new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS));
    }

    @Test
    void tooManyArgumentsTest() {
		ParseResult result = EchoCommand.tryParse(new Parser("echo my friend"));
		assertEquals(result, new ParseResult(ParseResult.Code.TOO_MANY_ARGUMENTS));
    }
}
