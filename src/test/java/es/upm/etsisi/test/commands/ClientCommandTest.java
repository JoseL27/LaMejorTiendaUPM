package es.upm.etsisi.test.commands;

import es.upm.etsisi.poo.*;
import es.upm.etsisi.poo.commands.CashCommand;
import es.upm.etsisi.poo.commands.ClientCommand;
import es.upm.etsisi.poo.exceptions.FailedCommandException;
import es.upm.etsisi.poo.exceptions.MissingItemException;
import es.upm.etsisi.test.StdoutCapturer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClientCommandTest extends StdoutCapturer {
    private static Method appParse;
    private static App app = new App();
    private Command command = new ClientCommand();

    @BeforeAll
    static void setAppParse() {
        try {
            ClientCommandTest.appParse = App.class.getDeclaredMethod("parser", String.class);
            ClientCommandTest.appParse.setAccessible(true);
        } catch (NoSuchMethodException e) {
            fail("Unable to change visibility for App.parse method via reflection");
        }
    }

    private String[] parse(String in) {
        String[] invokeResult = null;
        try {
            invokeResult = (String[])ClientCommandTest.appParse.invoke(ClientCommandTest.app, in);
        } catch (Exception e) {
            fail("Unable to invoke App.parse method via reflection");
        }
        return invokeResult;
    }

    @Test
    void populateRequiredData() {
        CashCommand cashCommand = new CashCommand();
        assertDoesNotThrow(() -> {
            cashCommand.eval(parse("cash add UW1234567 \"pepecurro3\" pepe0@upm.es"));
        });
        clearCapturedStdout();
    }

    @Test
    void NoSubCommand() {
        // The command should fail, it should print an error message
        List<String> captured = getStrippedCapturedStdout();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("client"));
            printCapturedToSystemOut();
        });
        assertFalse(captured.isEmpty());
    }

    @Test
    void InvalidSubCommand() {
        // The command should fail, it should print an error message
        List<String> captured = getStrippedCapturedStdout();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("client ado"));
            printCapturedToSystemOut();
        });
        assertFalse(captured.isEmpty());
    }

    // client add

    @Test
    void AddClient_MissingParameters() {
        List<String> captured = getStrippedCapturedStdout();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("client add X0000000T"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            UserManager.getInstance().findCashier("X0000000T");
        });
        assertFalse(captured.isEmpty());
    }

    @Test
    void AddClient_InvalidDNI() {
        this.populateRequiredData();
        // Command should fail throwing a FailedCommandException
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("client add \"Invalid Client\" X0000000A invalidclient@example.com UW1234567"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            UserManager.getInstance().findClient("X0000000A");
        });
    }

    @Test
    void AddClient_InvalidCIF() {
        this.populateRequiredData();
        // Command should fail throwing a FailedCommandException
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("client add \"Invalid Client\" B1145148B invalidclient@example.com UW1234567"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            UserManager.getInstance().findClient("B1145148B");
        });
    }

    @Test
    void AddClient_InvalidEmail() {
        this.populateRequiredData();
        // Command should fail throwing a FailedCommandException
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("client add \"Invalid Client\" X0000000T a UW1234567"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            UserManager.getInstance().findClient("X0000000T");
        });
    }

    @Test
    void AddClient_InvalidEmail2() {
        this.populateRequiredData();
        // Command should fail throwing a FailedCommandException
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("client add \"Invalid Client\" X0000000T @ UW1234567"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            UserManager.getInstance().findClient("X0000000T");
        });

    }

    @Test
    void AddClient_InvalidCashier() {
        this.populateRequiredData();
        // Command should fail throwing a FailedCommandException
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("client add \"Invalid Client\" X0000000T invalidclient@example.com UW0000000"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            UserManager.getInstance().findClient("X0000000T");
        });
    }

    @Test
    void AddClient_5client() {
        this.populateRequiredData();
        List<String> captured;
        List<String> expected;

        expected = Arrays.asList(
                "USER{identifier='55630667S', name='Pepe3', email='pepe1@upm.es', cash=UW1234567}",
                "client add: ok"
        );
        assertDoesNotThrow(() -> {
            this.command.eval(parse("client add \"Pepe3\" 55630667S pepe1@upm.es UW1234567"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
        clearCapturedStdout();

        expected = Arrays.asList(
                "USER{identifier='98948334B', name='Pepe2', email='pepe2@upm.es', cash=UW1234567}",
                "client add: ok"
        );
        assertDoesNotThrow(() -> {
            this.command.eval(parse("client add \"Pepe2\" 98948334B pepe2@upm.es UW1234567"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
        clearCapturedStdout();

        expected = Arrays.asList(
                "USER{identifier='Y8682724P', name='Pepe1', email='pepe3@upm.es', cash=UW1234567}",
                "client add: ok"
        );
        assertDoesNotThrow(() -> {
            this.command.eval(parse("client add \"Pepe1\" Y8682724P pepe3@upm.es UW1234567"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
        clearCapturedStdout();

        expected = Arrays.asList(
                "COMPANY{identifier='B12345674', name='pepe2', email='pepe5@upm.es', cash=UW1234567}",
                "client add: ok"
        );
        assertDoesNotThrow(() -> {
            this.command.eval(parse("client add \"pepe2\" B12345674 pepe5@upm.es UW1234567"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
        clearCapturedStdout();

        expected = Arrays.asList(
                "COMPANY{identifier='P1145148A', name='La bomba transportes', email='lebomb@c4.com', cash=UW1234567}",
                "client add: ok"
        );
        assertDoesNotThrow(() -> {
            this.command.eval(parse("client add \"La bomba transportes\" P1145148A lebomb@c4.com UW1234567"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
        clearCapturedStdout();

        assertEquals(5, UserManager.getInstance().getClients().size());
    }

    // client list
    void ListClient_empty() {
        List<String> captured = Arrays.asList(
                "Client:",
                "client list: ok"
        );
        List<String> expected;
        assertDoesNotThrow(() -> {
            this.command.eval(parse("client list"));
            printCapturedToSystemOut();
        });
        expected = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
    }

    void ListClient_5Clients() {
        this.AddClient_5client();
        clearCapturedStdout();
        List<String> captured = Arrays.asList(
                "Client:",
                "  COMPANY{identifier='P1145148A', name='La bomba transportes', email='lebomb@c4.com', cash=UW1234567}",
                "  USER{identifier='Y8682724P', name='Pepe1', email='pepe3@upm.es', cash=UW1234567}",
                "  USER{identifier='98948334B', name='Pepe2', email='pepe2@upm.es', cash=UW1234567}",
                "  USER{identifier='55630667S', name='Pepe3', email='pepe1@upm.es', cash=UW1234567}",
                "  COMPANY{identifier='B12345674', name='pepe2', email='pepe5@upm.es', cash=UW1234567}",
                "client list: ok"
        );
        List<String> expected;
        assertDoesNotThrow(() -> {
            this.command.eval(parse("client list"));
            printCapturedToSystemOut();
        });
        expected = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
    }

    // client remove
    @Test
    void RemoveClient_MissingParameters() {
        List<String> captured = getStrippedCapturedStdout();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("client remove"));
            printCapturedToSystemOut();
        });
        assertFalse(captured.isEmpty());
    }

    @Test
    void RemoveClient_InvalidID() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("client remove 123456789"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void RemoveClient_WorkerID() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("client remove UW1234567"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void RemoveClient_InvalidDNI() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("client remove Y8682724A"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void RemoveClient_InvalidCIF() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("client remove B1145148B"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void RemoveClient_NonExistentEnterprise() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("client remove B40020844"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void RemoveClient_NonExistentClient() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("client remove X0000000T"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void RemoveClient_ExistentClient() {
        this.AddClient_5client();
        clearCapturedStdout();
        List<String> captured;
        List<String> expected = Arrays.asList("client remove: ok");
        assertDoesNotThrow(() -> {
            Client c = UserManager.getInstance().findClient("Y8682724P");
            assertEquals("Y8682724P", c.getId());
            assertEquals(c.getIdType(), Client.IdType.DNI);
        });
        assertDoesNotThrow(() -> {
            this.command.eval(parse("client remove Y8682724P"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Client c = UserManager.getInstance().findClient("Y8682724P");
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
    }

    @Test
    void RemoveClient_ExistentEnterprise() {
        this.AddClient_5client();
        clearCapturedStdout();
        List<String> captured;
        List<String> expected = Arrays.asList("client remove: ok");
        assertDoesNotThrow(() -> {
            Client c = UserManager.getInstance().findClient("P1145148A");
            assertEquals("P1145148A", c.getId());
            assertEquals(c.getIdType(), Client.IdType.NIF);
        });
        assertDoesNotThrow(() -> {
            this.command.eval(parse("client remove P1145148A"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Client c = UserManager.getInstance().findClient("P1145148A");
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
    }
}
