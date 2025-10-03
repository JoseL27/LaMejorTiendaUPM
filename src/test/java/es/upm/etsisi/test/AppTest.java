package es.upm.etsisi.test;

import es.upm.etsisi.poo.App;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;


import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class AppTest {

	// @Test
	// void fullAppTest() throws IOException {
	// 	byte[] inputBytes = Files.readAllBytes(Paths.get("full-app-in.txt"));
	// 	final InputStream systemIn = System.in;
	// 	ByteArrayInputStream testIn = new ByteArrayInputStream(inputBytes);
	// 	System.setIn(testIn);

	// 	final PrintStream systemOut = System.out;
	// 	ByteArrayOutputStream testOut = new ByteArrayOutputStream();
	// 	System.setOut(new PrintStream(testOut));

	// 	String expectedOutput = new String(Files.readAllBytes(Paths.get("full-app-expected-out.txt")));
	// 	assertEquals(expectedOutput, testOut.toString());
		
	// 	// Cleanup
	// 	System.setIn(systemIn);
	// 	System.setOut(systemOut);		
	// }
}
