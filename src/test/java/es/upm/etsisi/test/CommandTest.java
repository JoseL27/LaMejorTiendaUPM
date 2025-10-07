package es.upm.etsisi.test;

import es.upm.etsisi.poo.Product;
import es.upm.etsisi.poo.Parser;
import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.commands.ProductCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class CommandTest {

	// Succes
	@Test
	void productAddTest() {
		Command result = Command.tryParse(new Parser("prod add 1 \"Libro POO\" BOOK 25"));
		ProductCommand expectedCmd = new ProductCommand(ProductCommand.SubCommand.ADD, 1, "Libro POO", Product.Category.BOOK, 25);
		assertEquals(result, expectedCmd);
	}

	// @Test
	// void Test() {
	// 	ParseResult result = Command.tryParse(new Parser("ticket new"));
	// 	assertEquals(result, new ParseResult());
	// }

	// @Test
	// void Test() {
	// 	ParseResult result = Command.tryParse(new Parser("help"));
	// 	assertEquals(result, new ParseResult());
	// }

	// @Test
	// void Test() {
	// 	ParseResult result = Command.tryParse(new Parser("echo TEST"));
	// 	assertEquals(result, new ParseResult());
	// }

	// @Test
	// void Test() {
	// 	ParseResult result = Command.tryParse(new Parser("exit"));
	// 	assertEquals(result, new ParseResult());
	// }

	// Failures
	@Test
	void insuficientArgsTest() {
		assertNull(Command.tryParse(new Parser("")));
		// TODO: Specify with output test
	}
	
	@Test
	void tooManyArgsTest() {
		assertNull(Command.tryParse(new Parser("arg1 arg2 arg3 arg4 arg5 arg6 arg7")));
		// TODO: Specify with output test
	}
	
	@Test
	void invalidCmdTest() {
		assertNull(Command.tryParse(new Parser("prodr")));
	}
}
