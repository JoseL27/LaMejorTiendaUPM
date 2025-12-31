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
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.Instant;
import java.time.Duration;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest extends BaseTest {
	
	public static final LocalDateTime E2_TEST_DATE = LocalDateTime.of(25, 11, 14, 18, 21);
    
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
	
	static void setAppTime(LocalDateTime time) {
		try {
			Clock fixedClock = Clock.fixed(time.toInstant(ZoneOffset.UTC), ZoneId.ofOffset("", ZoneOffset.UTC));
			Clock clock = Clock.tick(fixedClock, Duration.ZERO);
			
			Field f = App.class.getDeclaredField("clock");
			f.setAccessible(true);
			f.set(null, clock);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	void fullAppTest(LocalDateTime time, String inPath, String outPath) {
        try {
            Scanner testInScanner = new Scanner(new File(inPath));
            App app = new App();
			setAppTime(time);
            app.run(testInScanner, true);
            assertEqualOutputsByLine(Paths.get(outPath));
        } catch (IOException e) {
            fail(e.getMessage());
        }
	}
    
    void runCommand(LocalDateTime time, String inputString, String expectedOutput) {
		App app = new App();
		setAppTime(time);
		app.run(new Scanner(inputString));
		assertEqualOutputsByLine(expectedOutput);
	}
    
    void runCommand(LocalDateTime time, String inputString[], String expectedOutput) {
        runCommand(time, String.join(System.lineSeparator(), inputString), expectedOutput);
	}
    
    void runCommand(LocalDateTime time, String inputString[], String expectedOutput[]) {
        runCommand(time, inputString, String.join(System.lineSeparator(), expectedOutput));
	}
    
	// ======================================================================
	// FULL APP TESTS
	// ======================================================================
    
	@Test
        @Disabled
        void fullAppE2() throws IOException {
        fullAppTest(E2_TEST_DATE, "test-io/e2/in.txt", "test-io/e2/out.txt");
	}
    
	@Test
        @Disabled
        void fullAppE3() throws IOException {
        fullAppTest(E2_TEST_DATE, "test-io/e3/in.txt", "test-io/e3/out.txt");
	}
    
    // NOTE(erb): test all commands through runCommand()
	
	
	// ======================================================================
	// UTILS
	// ======================================================================
	
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
    
}
