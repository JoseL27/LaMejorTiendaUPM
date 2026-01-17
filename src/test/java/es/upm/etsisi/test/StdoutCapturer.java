package es.upm.etsisi.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

public class StdoutCapturer extends BaseTest {
    private ByteArrayOutputStream capturedStdout;
    private PrintStream simOut;
    protected final PrintStream sysOut = System.out;

    protected List<String> getStrippedCapturedStdout() {
        return Arrays.asList(this.capturedStdout.toString().split("\\r?\\n"));
    }

    protected void printCapturedToSystemOut() {
        List<String> output = getStrippedCapturedStdout();
        for (String s : output) {
            sysOut.println(s);
        }
    }

    @BeforeEach
    protected void clearCapturedStdout() {
        this.capturedStdout = new ByteArrayOutputStream();
        this.simOut = new PrintStream(this.capturedStdout);
        System.setOut(this.simOut);
    }

    @AfterEach
    void restoreStdout() {
        System.setOut(this.sysOut);
    }
}
