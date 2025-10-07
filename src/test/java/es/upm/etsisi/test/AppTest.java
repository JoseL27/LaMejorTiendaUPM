package es.upm.etsisi.test;

import es.upm.etsisi.poo.App;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;

import java.util.Scanner;
import java.util.List;
import java.util.Iterator;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

public class AppTest {

	@Test
	void fullAppTest() throws IOException {
		PrintStream systemOut = System.out;
		ByteArrayOutputStream testOut = new ByteArrayOutputStream(); 
		System.setOut(new PrintStream(testOut));

		Scanner testInScanner = new Scanner(new File("full-app-in.txt"));
		App app = new App();
		app.run(testInScanner);

		// assertEquals(new String(Files.readAllBytes(Paths.get("full-app-expected-out.txt"))), testOut.toString());
		
		Scanner testOutScanner = new Scanner(testOut.toString());

		Path expectedOutputPath = Paths.get("full-app-expected-out.txt");
		List<String> expectedOutputLines = Files.readAllLines(expectedOutputPath);
		Iterator<String> expectedLinesIt = expectedOutputLines.iterator();

		int lineCounter = 0;
		
		while (expectedLinesIt.hasNext() && testOutScanner.hasNextLine()) {
			assertEquals(expectedLinesIt.next(), testOutScanner.nextLine(), String.format("%s:%d: difference in line", expectedOutputPath, lineCounter));
			lineCounter++;
		}

		if (expectedLinesIt.hasNext()) {
			fail(String.format("Lines left in expected output: '%s'", expectedLinesIt.next()));
		}
		if (testOutScanner.hasNextLine()) {
			fail(String.format("Lines left in test output: '%s'", testOutScanner.nextLine()));
		}

		System.setOut(systemOut);		
	}
}
