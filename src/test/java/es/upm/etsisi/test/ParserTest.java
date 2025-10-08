package es.upm.etsisi.test;

import java.util.Locale;

import es.upm.etsisi.poo.Parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

public class ParserTest {

	// DICTATOR LOCALE 
	@BeforeAll
	static void setEnUSLocale() {
		Locale.setDefault(new Locale("en", "US"));
	}
	
	@AfterAll
	static void unsetEnUSLocale() {
		Locale.setDefault(Locale.getDefault());
	}	
	
	@Test
	void sixTokenTest() { 
		Parser parser = new Parser("zero one two three four five");
		assertEquals(parser.getLength(), 6);
		assertEquals(parser.getCommand(0), "zero");
		assertEquals(parser.getCommand(1), "one");
		assertEquals(parser.getCommand(2), "two");
		assertEquals(parser.getCommand(3), "three");
		assertEquals(parser.getCommand(4), "four");
		assertEquals(parser.getCommand(5), "five");
	}

	@Test
	void sixQuotedTokenTest() { 
		Parser parser = new Parser("zero \"one long token\" two three four five");
		assertEquals(parser.getLength(), 6);
		assertEquals(parser.getCommand(0), "zero");
		assertEquals(parser.getCommand(1), "one long token");
		assertEquals(parser.getCommand(2), "two");
		assertEquals(parser.getCommand(3), "three");
		assertEquals(parser.getCommand(4), "four");
		assertEquals(parser.getCommand(5), "five");
	}

	// @Test
	// void twoQuotedTokenTest() {
	// 	Parser parser = new Parser("\"one\"\"two\"");
	// 	assertEquals(parser.getLength(), 2);
	// 	assertEquals(parser.getCommand(0), "one");
	// 	assertEquals(parser.getCommand(1), "two");
	// }
	
	@Test
	void looseQuoteTest() {
		Parser parser = new Parser("to\"ken");
		assertEquals(parser.getLength(), 1);
		assertEquals(parser.getCommand(0), "token");
	}

	@Test
	void outOfBoundsTest() {
		Parser parser = new Parser("one two");
		assertNull(parser.getCommand(2));
	}
	
	@Test
	void emptyQuoteTest() {
		Parser parser = new Parser("\"\"");
		assertEquals(parser.getLength(), 0);
	}

	@Test
	void emptyStringTest() {
		Parser parser = new Parser("");
		assertEquals(parser.getLength(), 0);
	}

	@Test
	void whiteSpaceStringTest() {
		Parser parser = new Parser(" \t\n");
		assertEquals(parser.getLength(), 0);
	}
}
