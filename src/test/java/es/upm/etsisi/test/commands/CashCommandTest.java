package es.upm.etsisi.test.commands;

import es.upm.etsisi.poo.App;
import es.upm.etsisi.poo.Cashier;
import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.UserManager;
import es.upm.etsisi.poo.commands.CashCommand;
import es.upm.etsisi.poo.exceptions.FailedCommandException;
import es.upm.etsisi.poo.exceptions.MissingItemException;
import es.upm.etsisi.test.StdoutCapturer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CashCommandTest extends StdoutCapturer {
    private static Method appParse;
    private static App app = new App();
    private Command command = new CashCommand();

    @BeforeAll
    static void setAppParse() {
        try {
            CashCommandTest.appParse = App.class.getDeclaredMethod("parser", String.class);
            CashCommandTest.appParse.setAccessible(true);
        } catch (NoSuchMethodException e) {
            fail("Unable to change visibility for App.parse method via reflection");
        }
    }

    private String[] parse(String in) {
        String[] invokeResult = null;
        try {
            invokeResult = (String[])CashCommandTest.appParse.invoke(CashCommandTest.app, in);
        } catch (Exception e) {
            fail("Unable to invoke App.parse method via reflection");
        }
        return invokeResult;
    }

    @Test
    void NoSubCommand() {
        // The command should fail, it should print an error message
        List<String> captured = getStrippedCapturedStdout();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("cash"));
            printCapturedToSystemOut();
        });
        assertFalse(captured.isEmpty());
    }

    @Test
    void InvalidSubCommand() {
        // The command should fail, it should print an error message
        List<String> captured = getStrippedCapturedStdout();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("cash ado"));
            printCapturedToSystemOut();
        });
        assertFalse(captured.isEmpty());
    }

    // General guidelines:
    // If Parsing fails like not enough arguments, different argument class (such as Integer gets String)
    // Command should not throw an Exception
    // If during command executions there's a failure, there will be a FailedCommandException
    // For example, cannot find a cashier (either invalid ID, or the ID does not exist)
    // cash add
    @Test
    void AddCashier_MissingParameters() {
        List<String> captured = getStrippedCapturedStdout();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("cash add UW1234567"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            UserManager.getInstance().findCashier("UW1234567");
        });
        assertFalse(captured.isEmpty());
    }

    @Test
    void AddCashier_InvalidID_NoUWPrefix() {
        // Command should fail throwing a FailedCommandException
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("cash add PP1234567 \"Invalid Cashier\" ishouldnotexist@upm.es"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            UserManager.getInstance().findCashier("PP1234567");
        });
    }

    @Test
    void AddCashier_InvalidEmail_NoUPMDomain() {
        // Command should fail throwing a FailedCommandException
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("cash add UW1234567 \"Invalid Cashier\" ishouldnotexist@example.com"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            UserManager.getInstance().findCashier("UW1234567");
        });
    }

    @Test
    void AddCashier_3Cashiers() {
        List<String> captured;
        List<String> expected;

        // UW1234567
        expected = Arrays.asList(
                "Cash{identifier='UW1234567', name='pepecurro3', email='pepe0@upm.es'}",
                "cash add: ok"
        );
        assertDoesNotThrow(() -> {
            this.command.eval(parse("cash add UW1234567 \"pepecurro3\" pepe0@upm.es"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);

        // UW*******
        clearCapturedStdout();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("cash add \"pepecurro2\" pepe0@upm.es"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        String regex1 = "Cash\\{identifier='UW\\d\\d\\d\\d\\d\\d\\d', name='pepecurro2', email='pepe0@upm.es'\\}";
        assertTrue(captured.getFirst().matches(regex1));

        // UW1234569
        clearCapturedStdout();
        expected = Arrays.asList(
                "Cash{identifier='UW1234569', name='pepecurro1', email='pepe0@upm.es'}",
                "cash add: ok"
        );
        assertDoesNotThrow(() -> {
            this.command.eval(parse("cash add UW1234569 \"pepecurro1\" pepe0@upm.es"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
    }

    // cash list
    void ListCashier_empty() {
        List<String> captured = Arrays.asList(
                "Cash:",
                "cash list: ok"
        );
        List<String> expected;
        assertDoesNotThrow(() -> {
            this.command.eval(parse("cash list"));
            printCapturedToSystemOut();
        });
        expected = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
    }

    @Test
    void ListCashier_2cashiers() {
        List<String> captured = Arrays.asList(
                "Cash:",
                "  Cash{identifier='UW1234569', name='pepecurro1', email='pepe0@upm.es'}",
                "  Cash{identifier='UW1234567', name='pepecurro3', email='pepe0@upm.es'}",
                "cash list: ok"
        );

        // Add UW1234567, UW1234569
        List<String> expected;
        assertDoesNotThrow(() -> {
            this.command.eval(parse("cash add UW1234567 \"pepecurro3\" pepe0@upm.es"));
            this.command.eval(parse("cash add UW1234569 \"pepecurro1\" pepe0@upm.es"));

        });
        clearCapturedStdout();

        // cash list
        assertDoesNotThrow(() -> {
            this.command.eval(parse("cash list"));
            printCapturedToSystemOut();
        });
        expected = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
    }

    // cash remove
    @Test
    void RemoveCashier_InvalidID() {
        assertThrows(FailedCommandException.class, () -> {
           this.command.eval(parse("cash remove 123456789"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void RemoveCashier_NonExistentCashier() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("cash remove UW1234567"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void RemoveCashier_ExistingCashier() {
        ListCashier_2cashiers();
        clearCapturedStdout();
        List<String> captured;
        List<String> expected = Arrays.asList("cash remove: ok");
        assertDoesNotThrow(() -> {
            Cashier c = UserManager.getInstance().findCashier("UW1234569");
            assertEquals("UW1234569", c.getId());
        });
        assertDoesNotThrow(() -> {
            this.command.eval(parse("cash remove UW1234569"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Cashier c = UserManager.getInstance().findCashier("UW1234569");
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
    }

    // cash tickets
    @Test
    void TicketsCashier_NotSufficientArguments() {
        List<String> captured = getStrippedCapturedStdout();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("cash tickets"));
            printCapturedToSystemOut();
        });
        assertFalse(captured.isEmpty());
    }

    @Test
    void TicketsCashier_InvalidCashierID() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("cash tickets PP1234567"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void TicketsCashier_NonExistentCashier() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("cash tickets UW1234567"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void TicketsCashier_NoTicketsCreated() {
        ListCashier_2cashiers();
        List<String> captured;
        List<String> expected = Arrays.asList(
                "Tickets:",
                "cash tickets: ok"
        );
        clearCapturedStdout();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("cash tickets UW1234567"));
            printCapturedToSystemOut();
        });
    }
}
