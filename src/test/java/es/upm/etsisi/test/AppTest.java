package es.upm.etsisi.test;

import es.upm.etsisi.poo.App;
import es.upm.etsisi.poo.UserManager;
import es.upm.etsisi.poo.Inventory;

import java.io.*;
import java.nio.file.*;

import java.util.Scanner;
import java.util.List;
import java.util.Iterator;
import java.lang.StringBuilder;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest extends BaseTest {
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
    
    void fullAppTest(String inPath, String outPath) {
        try {
            Scanner testInScanner = new Scanner(new File(inPath));
            App app = new App();
            app.run(testInScanner, true);
            assertEqualOutputsByLine(Paths.get(outPath));
        } catch (IOException e) {
            fail(e.getMessage());
        }
	}
    
    void runCommand(String inputString, String expectedOutput) {
		App app = new App();
		app.run(new Scanner(inputString));
		assertEqualOutputsByLine(expectedOutput);
	}
    
    void runCommand(String inputString[], String expectedOutput) {
        runCommand(String.join(System.lineSeparator(), inputString), expectedOutput);
	}
    
    void runCommand(String inputString[], String expectedOutput[]) {
        runCommand(inputString, String.join(System.lineSeparator(), expectedOutput));
	}
    
	// ======================================================================
	// FULL APP TESTS
	// ======================================================================
    
	@Test
        @Disabled
        void fullAppE2() throws IOException {
        fullAppTest("test-io/e2/in.txt", "test-io/e2/out.txt");
	}
    
	@Test
        @Disabled
        void fullAppE3() throws IOException {
        fullAppTest("test-io/e3/in.txt", "test-io/e3/out.txt");
	}
    
    // NOTE(erb): test all commands through runCommand()
}
