package es.upm.etsisi.test;

import es.upm.etsisi.poo.App;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;

import java.util.Scanner;
import java.util.List;
import java.util.Iterator;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach; 
import org.junit.jupiter.api.AfterEach; 
import org.junit.jupiter.api.BeforeAll; 
import org.junit.jupiter.api.AfterAll; 

public class AppTest {
	
	// DICTATOR LOCALE 
	@BeforeAll
	static void setEnUSLocale() {
		Locale.setDefault(new Locale("en", "US"));
	}
	
	@AfterAll
	static void unsetEnUSLocale() {
		Locale.setDefault(Locale.getDefault());
	}
	
	private PrintStream systemOut;
	private ByteArrayOutputStream testOut;
	private App testApp;

	String testOutputString() {
		return testOut.toString();
	}

	Scanner testOutputScanner() {
		return new Scanner(testOut.toString());
	}

	@BeforeEach
	void setupTestOutput() {
		this.systemOut = System.out;
		this.testOut = new ByteArrayOutputStream(); 
		System.setOut(new PrintStream(testOut));
	}

	@AfterEach
	void restoreTestOutput() {
		System.setOut(systemOut);		
	}

	void assertEqualOutputs(String expectedOutput) {
		assertEquals(expectedOutput, testOut.toString());
	}
	
	void assertEqualOutputs(Path expectedOutputPath) throws IOException {
		assertEquals(new String(Files.readAllBytes(expectedOutputPath)), testOut.toString());
	}

	void assertEqualOutputsByLine(String expectedOutput) {
		Scanner expectedOutputScanner = new Scanner(expectedOutput);
		Scanner testOutputScanner = testOutputScanner();
		
		int lineCounter = 0;
		while (expectedOutputScanner.hasNextLine() && testOutputScanner.hasNextLine()) {
			assertEquals(expectedOutputScanner.nextLine(), testOutputScanner.nextLine(), String.format("missmatch line on line %d\n", lineCounter));
			lineCounter++;
		}

		if (expectedOutputScanner.hasNextLine()) {
			fail(String.format("Lines left in expected output: '%s'", expectedOutputScanner.nextLine()));
		}
		if (testOutputScanner.hasNextLine()) {
			fail(String.format("Lines left in test output: '%s'", testOutputScanner.nextLine()));
		}
	}
	
	void assertEqualOutputsByLine(Path expectedOutputPath) throws IOException {
		Scanner testOutputScanner = testOutputScanner();
		List<String> expectedOutputLines = Files.readAllLines(expectedOutputPath);
		Iterator<String> expectedLinesIt = expectedOutputLines.iterator();

		int lineCounter = 0;
		while (expectedLinesIt.hasNext() && testOutputScanner.hasNextLine()) {
			assertEquals(expectedLinesIt.next(), testOutputScanner.nextLine(), String.format("missmatch line on line %d\n", lineCounter));
			lineCounter++;
		}

		if (expectedLinesIt.hasNext()) {
			fail(String.format("Lines left in expected output: '%s'", expectedLinesIt.next()));
		}
		if (testOutputScanner.hasNextLine()) {
			fail(String.format("Lines left in test output: '%s'", testOutputScanner.nextLine()));
		}
	}

	void runAppWithInput(Scanner inputScanner) {
		App app = new App();
		app.run(inputScanner);
	}

	void runAppWithInput(File inputFile) throws FileNotFoundException {
		runAppWithInput(new Scanner(inputFile));
	}
	
	void runAppWithInput(String inputString) {
		runAppWithInput(new Scanner(inputString));
	}

	// ======================================================================
	// TESTS
	// ======================================================================

	@Test
	void fullAppTest() throws IOException {
		Scanner testInScanner = new Scanner(new File("full-app-in.txt"));
		App app = new App();
		app.run(testInScanner);
		assertEqualOutputsByLine(Paths.get("full-app-expected-out.txt"));
	}

	@Test
	void auxCommandsTest() throws IOException {
		String inputString =
			"echo \"SamekoSaba\"\n"
			+"help\n"
			+"exit";
			
		String expectedString =
			"Welcome to the ticket module App.\n"
			+"Ticket module. Type 'help' to see commands.\n"
			+"tUPM> echo \"SamekoSaba\"\n"
			+"echo \"SamekoSaba\"\n"
			+"tUPM> help\n"
			+"Commands:\n"
			+" prod add <id> \"<name>\" <category> <price>\n"
			+" prod list\n"
			+" prod update <id> NAME|CATEGORY|PRICE <value>\n"
			+" prod remove <id>\n"
			+" ticket new\n"
			+" ticket add <prodId> <quantity>\n"
			+" ticket remove <prodId>\n"
			+" ticket print\n"
			+" echo \"<texto>\"\n"
			+" help\n"
			+" exit\n"
			+"\n"
			+"Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS\n"
			+"Discounts if there are ≥2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%, ELECTRONICS 3%.\n"
			+"tUPM> exit\n"
			+"Closing application.\n"
			+"Goodbye!\n";

		App app = new App();
		app.run(new Scanner(inputString));
		assertEqualOutputsByLine(expectedString);
	}


	@Test
	void cliInterfaceTest() throws IOException {
		String inputString =
			"echo \"Hola mundo\"\n"
			+"help\n"
			+"exit";
		
		String expectedString =
			"Welcome to the ticket module App.\n"
			+"Ticket module. Type 'help' to see commands.\n"
			+"tUPM> echo \"Hola mundo\"\n"
			+"echo \"Hola mundo\"\n"
			+"tUPM> help\n"
			+"Commands:\n"
			+" prod add <id> \"<name>\" <category> <price>\n"
			+" prod list\n"
			+" prod update <id> NAME|CATEGORY|PRICE <value>\n"
			+" prod remove <id>\n"
			+" ticket new\n"
			+" ticket add <prodId> <quantity>\n"
			+" ticket remove <prodId>\n"
			+" ticket print\n"
			+" echo \"<texto>\"\n"
			+" help\n"
			+" exit\n"
			+"\n"
			+"Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS\n"
			+"Discounts if there are ≥2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%, ELECTRONICS 3%.\n"
			+"tUPM> exit\n"
			+"Closing application.\n"
			+"Goodbye!\n";

		App app = new App();
		app.run(new Scanner(inputString));
		assertEqualOutputsByLine(expectedString);
	}

	@Test
	void echoCommandTest() {
		String inputString = "echo \"test\"\n"+"exit\n";
		String expectedOutput =
			"Welcome to the ticket module App.\n"
			+"Ticket module. Type 'help' to see commands.\n"
			+"tUPM> echo \"test\"\n"
			+"echo \"test\"\n"
			+"tUPM> exit\n"
			+"Closing application.\n"
			+"Goodbye!\n";
		
		App app = new App();
		app.run(new Scanner(inputString));
		assertEqualOutputsByLine(expectedOutput);
	}

	@Test
	void helpCommandTest() {
		String inputString = "help\n"+"exit\n";
		String expectedOutput =
			"Welcome to the ticket module App.\n"
			+"Ticket module. Type 'help' to see commands.\n"
			+"tUPM> help\n"
			+"Commands:\n"
			+" prod add <id> \"<name>\" <category> <price>\n"
			+" prod list\n"
			+" prod update <id> NAME|CATEGORY|PRICE <value>\n"
			+" prod remove <id>\n"
			+" ticket new\n"
			+" ticket add <prodId> <quantity>\n"
			+" ticket remove <prodId>\n"
			+" ticket print\n"
			+" echo \"<texto>\"\n"
			+" help\n"
			+" exit\n"
			+"\n"
			+"Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS\n"
			+"Discounts if there are ≥2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%, ELECTRONICS 3%.\n"			
			+"tUPM> exit\n"
			+"Closing application.\n"
			+"Goodbye!\n";
		
		App app = new App();
		app.run(new Scanner(inputString));
		assertEqualOutputsByLine(expectedOutput);
	}

	@Test
	void prodAddTest() {
		String inputString = "prod add 1 \"Libro POO\" BOOK 25\n"
			+"exit\n";
		
		String expectedOutput =
			"Welcome to the ticket module App.\n"
			+"Ticket module. Type 'help' to see commands.\n"
			+"tUPM> prod add 1 \"Libro POO\" BOOK 25\n"
			+"{class:Product, id:1, name:'Libro POO', category:BOOK, price:25.0}\n"
			+"prod add: ok\n"
			+"tUPM> exit\n"
			+"Closing application.\n"
			+"Goodbye!\n";

		App app = new App();
		app.run(new Scanner(inputString));
		assertEqualOutputsByLine(expectedOutput);
	}
	
	@Test
	void prodAddTest2() {
		String inputString = 		
			"prod add 2 \"Camiseta talla:M UPM\" CLOTHES 15\n"
			+"exit\n";
		
		String expectedOutput =
			"Welcome to the ticket module App.\n"
			+"Ticket module. Type 'help' to see commands.\n"
			+"tUPM> prod add 2 \"Camiseta talla:M UPM\" CLOTHES 15\n"
			+"{class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}\n"
			+"prod add: ok\n"
			+"tUPM> exit\n"
			+"Closing application.\n"
			+"Goodbye!\n";

		App app = new App();
		app.run(new Scanner(inputString));
		assertEqualOutputsByLine(expectedOutput);
	}

	@Test
	void prodAddList() {
		String inputString =
			"prod add 1 \"Libro POO\" BOOK 25\n"			
			+"prod add 2 \"Camiseta talla:M UPM\" CLOTHES 15\n"
			+"prod list\n"
			+"exit\n";
		
		String expectedOutput =
			"Welcome to the ticket module App.\n"
			+"Ticket module. Type 'help' to see commands.\n"
			+"tUPM> prod add 1 \"Libro POO\" BOOK 25\n"
			+"{class:Product, id:1, name:'Libro POO', category:BOOK, price:25.0}\n"
			+"prod add: ok\n"
			
			+"tUPM> prod add 2 \"Camiseta talla:M UPM\" CLOTHES 15\n"
			+"{class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}\n"
			+"prod add: ok\n"

			+"tUPM> prod list\n"
			+"Catalog:\n"
			+" {class:Product, id:1, name:'Libro POO', category:BOOK, price:25.0}\n"
			+" {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}\n"
			+"prod list: ok\n"
			
			+"tUPM> exit\n"
			+"Closing application.\n"
			+"Goodbye!\n";

		App app = new App();
		app.run(new Scanner(inputString));
		assertEqualOutputsByLine(expectedOutput);
	}


	@Test
	void prodUpdateNameTest() {
		String inputString = "prod add 1 \"Libro POO\" BOOK 25\n"
			+"prod update 1 NAME \"Libro POO V2\"\n"
			+"exit\n";
		
		String expectedOutput =
			"Welcome to the ticket module App.\n"
			+"Ticket module. Type 'help' to see commands.\n"
			
			+"tUPM> prod add 1 \"Libro POO\" BOOK 25\n"
			+"{class:Product, id:1, name:'Libro POO', category:BOOK, price:25.0}\n"
			+"prod add: ok\n"

			+"tUPM> prod update 1 NAME \"Libro POO V2\"\n"
			+"{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:25.0}\n"
			+"prod update: ok\n"
			
			+"tUPM> exit\n"
			+"Closing application.\n"
			+"Goodbye!\n";

		App app = new App();
		app.run(new Scanner(inputString));
		assertEqualOutputsByLine(expectedOutput);
	}

	@Test
	void prodUpdatePriceTest() {
		String inputString = "prod add 1 \"Libro POO V2\" BOOK 25\n"
			+"prod update 1 PRICE 30\n"
			+"exit\n";
		
		String expectedOutput =
			"Welcome to the ticket module App.\n"
			+"Ticket module. Type 'help' to see commands.\n"
			
			+"tUPM> prod add 1 \"Libro POO V2\" BOOK 25\n"
			+"{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:25.0}\n"
			+"prod add: ok\n"

			+"tUPM> prod update 1 PRICE 30\n"
			+"{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0}\n"
			+"prod update: ok\n"
			
			+"tUPM> exit\n"
			+"Closing application.\n"
			+"Goodbye!\n";

		App app = new App();
		app.run(new Scanner(inputString));
		assertEqualOutputsByLine(expectedOutput);
	}

	@Test
	void prodUpdateCategoryTest() {
		String inputString = "prod add 1 \"Libro POO\" BOOK 25\n"
			+"prod update 1 CATEGORY MERCH\n"
			+"exit\n";
		
		String expectedOutput =
			"Welcome to the ticket module App.\n"
			+"Ticket module. Type 'help' to see commands.\n"
			
			+"tUPM> prod add 1 \"Libro POO\" BOOK 25\n"
			+"{class:Product, id:1, name:'Libro POO', category:BOOK, price:25.0}\n"
			+"prod add: ok\n"

			+"tUPM> prod update 1 CATEGORY MERCH\n"
			+"{class:Product, id:1, name:'Libro POO', category:MERCH, price:25.0}\n"
			+"prod update: ok\n"
			
			+"tUPM> exit\n"
			+"Closing application.\n"
			+"Goodbye!\n";

		App app = new App();
		app.run(new Scanner(inputString));
		assertEqualOutputsByLine(expectedOutput);
	}

	@Test
	void prodRemoveTest() {
		
		String inputString = 
			"prod add 3 \"Libro POO repetido Error\" BOOK 25\n"			
			+"prod remove 3\n"
			+"exit\n";
		
		String expectedOutput =
			"Welcome to the ticket module App.\n"
			+"Ticket module. Type 'help' to see commands.\n"
			
			+"tUPM> prod add 3 \"Libro POO repetido Error\" BOOK 25\n"
			+"{class:Product, id:3, name:'Libro POO repetido Error', category:BOOK, price:25.0}\n"
+"prod add: ok\n"

			+"tUPM> prod remove 3\n"
			+"{class:Product, id:3, name:'Libro POO repetido Error', category:BOOK, price:25.0}\n"
+"prod remove: ok\n"
			
			+"tUPM> exit\n"
			+"Closing application.\n"
			+"Goodbye!\n";

		App app = new App();
		app.run(new Scanner(inputString));
		assertEqualOutputsByLine(expectedOutput);
	}	
}
