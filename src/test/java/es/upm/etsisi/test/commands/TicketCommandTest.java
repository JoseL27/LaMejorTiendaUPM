package es.upm.etsisi.test.commands;

import es.upm.etsisi.poo.App;
import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.commands.CashCommand;
import es.upm.etsisi.poo.commands.ClientCommand;
import es.upm.etsisi.poo.commands.ProductCommand;
import es.upm.etsisi.poo.commands.TicketCommand;
import es.upm.etsisi.poo.exceptions.FailedCommandException;
import es.upm.etsisi.test.StdoutCapturer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TicketCommandTest extends StdoutCapturer {
    private static Method appParse;
    private static App app = new App();
    private Command command = new TicketCommand();

    @BeforeAll
    static void setAppParse() {
        try {
            TicketCommandTest.appParse = App.class.getDeclaredMethod("parser", String.class);
            TicketCommandTest.appParse.setAccessible(true);
        } catch (NoSuchMethodException e) {
            fail("Unable to change visibility for App.parse method via reflection");
        }
    }

    private String[] parse(String in) {
        String[] invokeResult = null;
        try {
            invokeResult = (String[]) TicketCommandTest.appParse.invoke(TicketCommandTest.app, in);
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
            this.command.eval(parse("ticket"));
            printCapturedToSystemOut();
        });
        assertFalse(captured.isEmpty());
    }

    @Test
    void InvalidSubCommand() {
        // The command should fail, it should print an error message
        List<String> captured = getStrippedCapturedStdout();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket ado"));
            printCapturedToSystemOut();
        });
        assertFalse(captured.isEmpty());
    }

    @Test
    void PopulateRequiredData() {
        CashCommand cash = new CashCommand();
        ClientCommand client = new ClientCommand();
        ProductCommand prod = new ProductCommand();
        assertDoesNotThrow(() -> {
            cash.eval(parse("cash add UW1234567 \"pepecurro3\" pepe0@upm.es"));
            cash.eval(parse("cash add \"pepecurro2\" pepe0@upm.es"));
            cash.eval(parse("cash add UW1234569 \"pepecurro1\" pepe0@upm.es"));
            client.eval(parse("client add \"Pepe3\" 55630667S pepe1@upm.es UW1234567"));
            client.eval(parse("client add \"Pepe2\" 98948334B pepe2@upm.es UW1234567"));
            client.eval(parse("client add \"Pepe1\" Y8682724P pepe3@upm.es UW1234567"));
            client.eval(parse("client add \"pepe2\" B12345674 pepe5@upm.es UW1234567"));
            client.eval(parse("client add \"La bomba transportes\" P1145148A lebomb@c4.com UW1234567"));
            prod.eval(parse("prod add 1 \"Libro POO\" BOOK 25"));
            prod.eval(parse("prod add 2 \"Camiseta talla:M UPM\" CLOTHES 15"));
            prod.eval(parse("prod add \"Camiseta talla:M UPM\" CLOTHES 15"));
            prod.eval(parse("prod add 3 \"Libro POO repetido Error\" BOOK 25"));
            prod.eval(parse("prod addMeeting 23456 \"Reunion Rotonda\" 12 2100-12-31 100"));
            prod.eval(parse("prod addMeeting 23457 \"Graduacion ETSISI\" 40 2100-12-31 30"));
            prod.eval(parse("prod addFood 23459 \"Restaurante Asador\" 50 2100-12-31 40"));
            prod.eval(parse("prod add 5 \"Camiseta talla:M UPM\" CLOTHES 15 3"));
            prod.eval(parse("prod add 6 \"Camiseta talla:L UPM\" CLOTHES 20 4"));
            prod.eval(parse("prod add 2026-12-21 INSURANCE"));
            prod.eval(parse("prod add 2026-12-24 TRANSPORT"));
            prod.eval(parse("prod add 2100-12-31 SHOW"));
        });
        clearCapturedStdout();
    }

    // ticket new

    @Test
    void NewTicket_MissingParameters() {
        List<String> captured = getStrippedCapturedStdout();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket new 1"));
            printCapturedToSystemOut();
        });
        assertFalse(captured.isEmpty());
    }

    @Test
    void NewTicket_NonExistentCashier() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("ticket new 00001 55630667S UW0000000"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void NewTicket_NonExistentCashier_NoID() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("ticket new 55630667S UW0000000"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void NewTicket_InvalidCashierID() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("ticket new 00001 55630667S UP0000000"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void NewTicket_InvalidCashierID_NoID() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("ticket new 55630667S UP0000000"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void NewTicket_NonExistentClient() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("ticket new 00001 X0000000T UW1234567"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void NewTicket_NonExistentClient_NoID() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("ticket new X0000000T UW1234567"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void NewTicket_InvalidClientDNI() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("ticket new 00001 X0000000A UW1234567"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void NewTicket_InvalidClientDNI_NoID() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("ticket new X0000000A UW1234567"));
            printCapturedToSystemOut();
        });
    }

    void NewTicket_Client_Product() {
        this.PopulateRequiredData();
        List<String> expected = Arrays.asList(
                "Ticket : 00001",
                "  Total price: 0.0",
                "  Total discount: 0.0",
                "  Final Price: 0.0",
                "ticket new: ok"
        );
        List<String> captured;
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket new 00001 UW1234567 55630667S"));
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
    }


    @Test
    void NewTicket_Client_Product_NoID() {
        this.PopulateRequiredData();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket new UW1234567 55630667S"));
        });
    }

    void NewTicket_ExistingID() {
        this.PopulateRequiredData();
        List<String> expected = Arrays.asList(
                "Ticket : 00001",
                "  Total price: 0.0",
                "  Total discount: 0.0",
                "  Final Price: 0.0",
                "ticket new: ok"
        );
        List<String> captured;
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket new 00001 UW1234567 55630667S"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("ticket new 00001 UW1234567 55630667S"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void NewTicket_Client_Combined_Fail() {
        this.PopulateRequiredData();
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("ticket new 00001 UW0000000 55630667S -c"));
        });
    }

    @Test
    void NewTicket_Client_Service_Fail() {
        this.PopulateRequiredData();
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("ticket new 00001 UW0000000 55630667S -s"));
        });
    }

    @Test
    void NewTicket_Enterprise_Product_Fail() {
        this.PopulateRequiredData();
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("ticket new 00001 UW0000000 P1145148A -p"));
        });
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("ticket new 00001 UW0000000 P1145148A"));
        });
    }

    @Test
    void NewTicket_Enterprise_Service() {
        List<String> expected = Arrays.asList(
                "Ticket : 00001",
                "ticket new: ok"
        );
        List<String> captured;
        this.PopulateRequiredData();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket new 00001 UW1234567 P1145148A -s"));
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
    }

    @Test
    void NewTicket_Enterprise_Combined() {
        List<String> expected = Arrays.asList(
                "Ticket : 00001",
                "ticket new: ok"
        );
        List<String> captured;
        this.PopulateRequiredData();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket new 00001 UW1234567 P1145148A -c"));
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
    }

    // ticket add

    @Test
    void AddTicket_Product_ProductTicket() {
        NewTicket_Client_Product();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket add 00001 UW1234567 1 20"));
            printCapturedToSystemOut();
        });
    }


    @Test
    void AddTicket_PersonalizableProduct_ProductTicket() {
        NewTicket_Client_Product();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket add 00001 UW1234567 5 2 --pred --pblue"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void AddTicket_TimedProduct_ProductTicket() {
        NewTicket_Client_Product();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket add 00001 UW1234567 23459 10"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void AddTicket_Service_ProductTicket_fail() {
        NewTicket_Client_Product();
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("ticket add 00001 UW1234567 1S"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void AddTicket_Product_ServiceTicket_fail() {
        NewTicket_Enterprise_Service();
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("ticket add 00001 UW1234567 5 1"));
        });
    }

    @Test
    void AddTicket_Product_CombinedTicket() {
        NewTicket_Enterprise_Combined();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket add 00001 UW1234567 1 1"));
        });
    }

    @Test
    void AddTicket_Service_CombinedTicket() {
        NewTicket_Enterprise_Combined();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket add 00001 UW1234567 1S"));
        });
    }

    @Test
    void PrintTicket_Product_ProductTicket() {
        AddTicket_Product_ProductTicket();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket print 00001 UW1234567"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void PrintTicket_CombinedTicket_OnlyProductAdded() {
        NewTicket_Enterprise_Combined();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket add 00001 UW1234567 1 1"));
        });
        assertThrows(FailedCommandException.class, () -> {
           this.command.eval(parse("ticket print 00001 UW1234567"));
        });
    }

    @Test
    void PrintTicket_CombinedTicket_OnlyServicesAdded() {
        NewTicket_Enterprise_Combined();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket add 00001 UW1234567 1S"));
        });
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("ticket print 00001 UW1234567"));
        });
    }

    @Test
    void PrintTicket_CombinedTicket_Both() {
        NewTicket_Enterprise_Combined();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket add 00001 UW1234567 1S"));
        });
        clearCapturedStdout();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket add 00001 UW1234567 1 1"));
        });
        printCapturedToSystemOut();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket print 00001 UW1234567"));
        });
    }

    @Test
    void PrintTicket_CombinedTicket_Both2() {
        NewTicket_Enterprise_Combined();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket add 00001 UW1234567 2S"));
        });
        clearCapturedStdout();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket add 00001 UW1234567 1 1"));
        });
        printCapturedToSystemOut();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("ticket print 00001 UW1234567"));
        });
    }
}
