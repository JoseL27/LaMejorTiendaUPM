package es.upm.etsisi.test.commands;

import es.upm.etsisi.poo.*;
import es.upm.etsisi.poo.commands.ProductCommand;

import es.upm.etsisi.poo.exceptions.FailedCommandException;
import es.upm.etsisi.poo.exceptions.MissingItemException;
import es.upm.etsisi.test.StdoutCapturer;
import org.junit.jupiter.api.*;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductCommandTest extends StdoutCapturer {
    private static Method appParse;
    private static App app = new App();
    private Command command = new ProductCommand();

    @BeforeAll
    static void setAppParse() {
        try {
            ProductCommandTest.appParse = App.class.getDeclaredMethod("parser", String.class);
            ProductCommandTest.appParse.setAccessible(true);
        } catch (NoSuchMethodException e) {
            fail("Unable to change visibility for App.parse method via reflection");
        }
    }

    private String[] parse(String in) {
        String[] invokeResult = null;
        try {
            invokeResult = (String[])ProductCommandTest.appParse.invoke(ProductCommandTest.app, in);
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
            this.command.eval(parse("prod"));
            printCapturedToSystemOut();
        });
        assertFalse(captured.isEmpty());
    }

    @Test
    void InvalidSubCommand() {
        // The command should fail, it should print an error message
        List<String> captured = getStrippedCapturedStdout();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod ado"));
            printCapturedToSystemOut();
        });
        assertFalse(captured.isEmpty());
    }

    // prod add

    @Test
    void AddProduct_MissingParameters() {
        List<String> captured = getStrippedCapturedStdout();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod add 1"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("1");
        });
        assertFalse(captured.isEmpty());
    }

    @Test
    void AddProduct_BaseProduct_NegativeID() {
        // Command should fail throwing a FailedCommandException
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod add -1 \"Invalid Product\" CLOTHES 5"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("-1");
        });
    }

    @Test
    void AddProduct_BaseProduct_InvalidName() {
        // Command should fail throwing a FailedCommandException
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod add 1 \"CAMISETA UPMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\" CLOTHES 5"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("1");
        });
    }

    @Test
    void AddProduct_BaseProduct_NoID_InvalidName() {
        // Command should fail throwing a FailedCommandException
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod add \"CAMISETA UPMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\" CLOTHES 5"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("0");
        });
    }

    @Test
    void AddProduct_BaseProduct_InvalidCategory() {
        // Command should fail throwing a FailedCommandException
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod add 1 \"Invalid Product\" INVALID 5"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("1");
        });
    }

    @Test
    void AddProduct_BaseProduct_NoID_InvalidCategory() {
        // Command should fail throwing a FailedCommandException
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod add \"Invalid Product\" INVALID 5"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("0");
        });
    }

    @Test
    void AddProduct_BaseProduct_InvalidPrice() {
        // Command should fail throwing a FailedCommandException
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod add 1 \"Invalid Product\" CLOTHES 0"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("1");
        });
    }

    @Test
    void AddProduct_BaseProduct_NoID_InvalidPrice() {
        // Command should fail throwing a FailedCommandException
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod add \"Invalid Product\" CLOTHES 0"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("0");
        });
    }

    @Test
    void AddProduct_BaseProduct_InvalidMaxPersonalizationsExceed() {
        // Command should fail throwing a FailedCommandException
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod add 1 \"Invalid Product\" CLOTHES 5 6"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("1");
        });
    }

    @Test
    void AddProduct_BaseProduct_NoID_InvalidMaxPersonalizationsExceed() { // Limit established by Product Category
        // Command should fail throwing a FailedCommandException
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod add \"Invalid Product\" CLOTHES 5 999"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("0");
        });
    }

    @Test
    void AddProduct_BaseProduct_InvalidMaxPersonalizationsNegative() {
        // Command should fail throwing a FailedCommandException
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod add 1 \"Invalid Product\" CLOTHES 5 -2"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("1");
        });
    }

    @Test
    void AddProduct_BaseProduct_NoID_InvalidMaxPersonalizationsNegative() {
        // Command should fail throwing a FailedCommandException
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod add \"Invalid Product\" CLOTHES 5 -2"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("0");
        });
    }

    @Test
    void AddProduct_BaseProduct_Book() {
        List<String> expected = Arrays.asList(
                "{class:Product, id:1, name:'Libro POO', category:BOOK, price:25.0}",
                "prod add: ok"
        );
        List<String> captured;
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod add 1 \"Libro POO\" BOOK 25"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
    }

    @Test
    void AddProduct_BaseProduct_RepeatedProductID() {
        AddProduct_BaseProduct_Book();
        clearCapturedStdout();
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("1");
            assertEquals(1, ii.getId());
        });
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod add 1 \"Libro POO\" BOOK 25"));
        });
    }

    @Test
    void AddProduct_BaseProduct_NoID_Book() {
        List<String> expected = Arrays.asList(
                "{class:Product, id:0, name:'Libro POO', category:BOOK, price:25.0}",
                "prod add: ok"
        );
        List<String> captured;
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod add \"Libro POO\" BOOK 25"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
    }

    @Test
    void AddProduct_BaseProduct_4Products() {
        List<String> expected;
        List<String> captured;

        // Libro POO
        expected = Arrays.asList(
                "{class:Product, id:1, name:'Libro POO', category:BOOK, price:25.0}",
                "prod add: ok"
        );
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod add 1 \"Libro POO\" BOOK 25"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
        clearCapturedStdout();

        // Camiseta talla:M UPM
        expected = Arrays.asList(
                "{class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}",
                "prod add: ok"
        );
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod add 2 \"Camiseta talla:M UPM\" CLOTHES 15"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
        clearCapturedStdout();

        //Camiseta talla:M UPM (sin id)
        expected = Arrays.asList(
                "{class:Product, id:0, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}",
                "prod add: ok"
        );
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod add \"Camiseta talla:M UPM\" CLOTHES 15"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
        clearCapturedStdout();

        // Libro POO repetido error
        expected = Arrays.asList(
                "{class:Product, id:3, name:'Libro POO repetido Error', category:BOOK, price:25.0}",
                "prod add: ok"
        );
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod add 3 \"Libro POO repetido Error\" BOOK 25"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
        clearCapturedStdout();
    }

    @Test
    void AddProduct_TimedProduct_MissingParameters() {
        List<String> captured = getStrippedCapturedStdout();

        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod addMeeting 1"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("1");
        });

        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod addFood 1"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("1");
        });

        assertFalse(captured.isEmpty());
    }

    @Test
    void AddProduct_TimedProduct_InvalidID() {
        // Command should fail throwing a FailedCommandException
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addMeeting -1 \"Reunion Rotonda\" 12 2100-12-31 100")); // The date should be calculated dynamically
            // But I'm lazy AF, so hardcoding 2100 should work
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("-1");
        });

        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addFood -1 \"Restaurante Asador\" 50 2100-12-31 40"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("-1");
        });
    }
    
    @Test
    void AddProduct_TimedProduct_InvalidName() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addMeeting 23456 \"Reunion Rotondaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\" 12 2100-12-31 100"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("23456");
        });

        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addFood 23459 \"Restaurante Asadorrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr\" 50 2100-12-31 40"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("23459");
        });
    }
    
    @Test
    void AddProduct_TimedProduct_NoID_InvalidName() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addMeeting \"Reunion Rotondaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\" 12 2100-12-31 100"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("0");
        });

        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addFood \"Restaurante Asadorrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr\" 50 2100-12-31 40"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("0");
        });
    }
    
    @Test
    void AddProduct_TimedProduct_InvalidPrice() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addMeeting 23456 \"Reunion Rotonda\" 0 2100-12-31 100"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("23456");
        });

        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addFood 23459 \"Restaurante Asador\" -1 2100-12-31 40"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("23459");
        });
    }

    @Test
    void AddProduct_TimedProduct_NoID_InvalidPrice() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addMeeting \"Reunion Rotonda\" 0 2100-12-31 100"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("0");
        });

        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addFood \"Restaurante Asador\" -1 2100-12-31 40"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("0");
        });
    }

    @Test
    void AddProduct_TimedProduct_InvalidDateFormat() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addMeeting 23456 \"Reunion Rotonda\" 12 12-31-2100 100"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("23456");
        });

        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addFood 23459 \"Restaurante Asador\" 50 12-31-2100 40"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("23459");
        });
    }

    @Test
    void AddProduct_TimedProduct_NoID_InvalidDateFormat() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addMeeting \"Reunion Rotonda\" 12 12-31-2100 100"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("23456");
        });

        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addFood \"Restaurante Asador\" 50 12-31-2100 40"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("23459");
        });
    }

    @Test
    void AddProduct_TimedProduct_Expired() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addMeeting 23456 \"Reunion Rotonda\" 12 1970-01-01 100"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("23456");
        });

        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addFood 23459 \"Restaurante Asador\" 50 1970-01-01 40"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("23457");
        });
    }

    @Test
    void AddProduct_TimedProduct_NoID_Expired() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addMeeting \"Reunion Rotonda\" 12 1970-01-01 100"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("0");
        });

        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addFood \"Restaurante Asador\" 50 1970-01-01 40"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("0");
        });
    }

    @Test
    void AddProduct_TimedProduct_InPreparation() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String prepDate = today.plusDays(1).format(format);
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval( parse( String.format("prod addFood 23459 \"Restaurante Asador\" 50 %s 40", prepDate) ) );
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("23459");
        });
    }

    @Test
    void AddProduct_TimedProduct_NoID_InPreparation() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String prepDate = today.plusDays(1).format(format);
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval( parse( String.format("prod addFood \"Restaurante Asador\" 50 %s 40", prepDate) ) );
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("0");
        });
    }

    @Test
    void AddProduct_TimedProduct_InvalidMaxPeople() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addMeeting 23456 \"Reunion Rotonda\" 12 2100-12-31 101"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("23456");
        });

        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addFood 23459 \"Restaurante Asador\" 50 2100-12-31 -1"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("23459");
        });
    }

    @Test
    void AddProduct_TimedProduct_NoID_InvalidMaxPeople() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addMeeting \"Reunion Rotonda\" 12 2100-12-31 101"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("0");
        });

        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod addFood \"Restaurante Asador\" 50 2100-12-31 -1"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            Inventory.getInstance().getItemFromStringId("0");
        });
    }

    @Test
    void AddProduct_TimedProduct_3Products() {
        List<String> expected;
        List<String> captured;

        // Reunion rotonda
        expected = Arrays.asList(
                "{class:Meeting, id:23456, name:'Reunion Rotonda', price:12.0, date of Event:2100-12-31, max people allowed:100}",
                "prod addMeeting: ok"
        );
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod addMeeting 23456 \"Reunion Rotonda\" 12 2100-12-31 100"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
        clearCapturedStdout();

        // Graduacion ETSISI
        expected = Arrays.asList(
                "{class:Meeting, id:23457, name:'Graduacion ETSISI', price:40.0, date of Event:2100-12-31, max people allowed:30}",
                "prod addMeeting: ok"
        );
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod addMeeting 23457 \"Graduacion ETSISI\" 40 2100-12-31 30"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
        clearCapturedStdout();

        // Restaurante Asador
        expected = Arrays.asList(
                "{class:Food, id:23459, name:'Restaurante Asador', price:50.0, date of Event:2100-12-31, max people allowed:40}",
                "prod addFood: ok"
        );
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod addFood 23459 \"Restaurante Asador\" 50 2100-12-31 40"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
        clearCapturedStdout();
    }

    @Test
    void AddProduct_PersonalizableProduct_2Products() {
        List<String> expected;
        List<String> captured;

        // Camiseta M
        expected = Arrays.asList(
                "{class:ProductPersonalized, id:5, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0, maxPersonal:3}",
                "prod add: ok"
        );
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod add 5 \"Camiseta talla:M UPM\" CLOTHES 15 3"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
        clearCapturedStdout();

        // Camiseta L
        expected = Arrays.asList(
                "{class:ProductPersonalized, id:6, name:'Camiseta talla:L UPM', category:CLOTHES, price:20.0, maxPersonal:4}",
                "prod add: ok"
        );
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod add 6 \"Camiseta talla:L UPM\" CLOTHES 20 4"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
        clearCapturedStdout();
    }

    @Test
    void AddProduct_Service_InvalidDateFormat() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod add 12-21-2026 INSURANCE"));
        });
        assertThrows(MissingItemException.class, () -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("1S");
        });
    }

    @Test
    void AddProduct_Service_ExpiredDate() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod add 1970-01-01 INSURANCE"));
        });
        assertThrows(MissingItemException.class, () -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("1S");
        });
    }

    @Test
    void AddProduct_Service_InvalidType() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod add 2026-12-21 INVALID"));
        });
        assertThrows(MissingItemException.class, () -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("1S");
        });
    }

    @Test
    void AddProduct_Service_3Services() {
        List<String> expected;
        List<String> captured;

        // INSURANCE
        expected = Arrays.asList(
                "{class:ProductService, id:1, category:INSURANCE, expiration:Mon Dec 21 00:00:00 CET 2026}",
                "prod add: ok"
        );
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod add 2026-12-21 INSURANCE"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
        clearCapturedStdout();

        // TRANSPORT
        expected = Arrays.asList(
                "{class:ProductService, id:2, category:TRANSPORT, expiration:Thu Dec 24 00:00:00 CET 2026}",
                "prod add: ok"
        );
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod add 2026-12-24 TRANSPORT"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
        clearCapturedStdout();

        // SHOW
        expected = Arrays.asList(
                "{class:ProductService, id:3, category:SHOW, expiration:Fri Dec 31 00:00:00 CET 2100}",
                "prod add: ok"
        );
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod add 2100-12-31 SHOW"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
        clearCapturedStdout();
    }

    // prod update
    // I really should use parameterized tests here, but this should work
    // It is 1:46 AM and I don't have the brain power to learn parameterized tests
    @Test
    void UpdateProduct_MissingParameters() {
        AddProduct_BaseProduct_4Products();
        List<String> captured = getStrippedCapturedStdout();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod update 1"));
            printCapturedToSystemOut();
        });
        assertFalse(captured.isEmpty());
    }

    @Test
    void UpdateProduct_InvalidID() {
        assertThrows(MissingItemException.class, () -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("-1");
        });
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod update -1 NAME \"Libro POO V2\""));
            printCapturedToSystemOut();
        });
    }

    @Test
    void UpdateProduct_NonExistentID() {
        assertThrows(MissingItemException.class, () -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("0");
        });
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod update 0 NAME \"Libro POO V2\""));
            printCapturedToSystemOut();
        });
    }

    @Test
    void UpdateProduct_BaseProduct_UpdateName() {
        AddProduct_BaseProduct_4Products();
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("1");
            BaseProduct bp = (BaseProduct)ii;
            assertEquals(1, bp.getId());
            assertEquals("Libro POO", bp.getName());
        });
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod update 1 NAME \"Libro POO V2\""));
            printCapturedToSystemOut();
        });
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("1");
            BaseProduct bp = (BaseProduct)ii;
            assertEquals(1, bp.getId());
            assertEquals("Libro POO V2", bp.getName());
        });
    }

    @Test
    void UpdateProduct_BaseProduct_UpdateName_Invalid() {
        AddProduct_BaseProduct_4Products();
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("1");
            BaseProduct bp = (BaseProduct)ii;
            assertEquals(1, bp.getId());
            assertEquals("Libro POO", bp.getName());
        });
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod update 1 NAME \"Libro POO V22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222\""));
            printCapturedToSystemOut();
        });
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("1");
            BaseProduct bp = (BaseProduct)ii;
            assertEquals(1, bp.getId());
            assertEquals("Libro POO", bp.getName());
        });
    }

    @Test
    void UpdateProduct_BaseProduct_UpdatePrice() {
        AddProduct_BaseProduct_4Products();
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("1");
            BaseProduct bp = (BaseProduct)ii;
            assertEquals(1, bp.getId());
            assertEquals(25.0, bp.getPrice());
        });
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod update 1 PRICE 30"));
            printCapturedToSystemOut();
        });
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("1");
            BaseProduct bp = (BaseProduct)ii;
            assertEquals(1, bp.getId());
            assertEquals(30.0, bp.getPrice());
        });
    }

    @Test
    void UpdateProduct_BaseProduct_UpdatePrice_Invalid() {
        AddProduct_BaseProduct_4Products();
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("1");
            BaseProduct bp = (BaseProduct)ii;
            assertEquals(1, bp.getId());
            assertEquals(25.0, bp.getPrice());
        });
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod update 1 PRICE 0"));
            printCapturedToSystemOut();
        });
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("1");
            BaseProduct bp = (BaseProduct)ii;
            assertEquals(1, bp.getId());
            assertEquals(25.0, bp.getPrice());
        });
    }

    @Test
    void UpdateProduct_BaseProduct_UpdateCategory() {
        AddProduct_BaseProduct_4Products();
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("1");
            BaseProduct bp = (BaseProduct)ii;
            assertEquals(1, bp.getId());
            assertEquals(BaseProduct.Category.BOOK, bp.getCategory());
        });
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod update 1 CATEGORY CLOTHES"));
            printCapturedToSystemOut();
        });
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("1");
            BaseProduct bp = (BaseProduct)ii;
            assertEquals(1, bp.getId());
            assertEquals(BaseProduct.Category.CLOTHES, bp.getCategory());
        });
    }

    @Test
    void UpdateProduct_BaseProduct_UpdateCategory_Invalid() {
        AddProduct_BaseProduct_4Products();
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("1");
            BaseProduct bp = (BaseProduct)ii;
            assertEquals(1, bp.getId());
            assertEquals(BaseProduct.Category.BOOK, bp.getCategory());
        });
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod update 1 CATEGORY INVALID"));
            printCapturedToSystemOut();
        });
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("1");
            BaseProduct bp = (BaseProduct)ii;
            assertEquals(1, bp.getId());
            assertEquals(BaseProduct.Category.BOOK, bp.getCategory());
        });
    }

    @Test
    void UpdateProduct_TimedProduct_UpdateName() {
        AddProduct_TimedProduct_3Products();
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("23459");
            TimedProduct bp = (TimedProduct) ii;
            assertEquals(23459, bp.getId());
            assertEquals("Restaurante Asador", bp.getName());
        });
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod update 23459 NAME \"R\""));
            printCapturedToSystemOut();
        });
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("23459");
            TimedProduct bp = (TimedProduct) ii;
            assertEquals(23459, bp.getId());
            assertEquals("R", bp.getName());
        });
    }

    @Test
    void UpdateProduct_TimedProduct_UpdateName_Invalid() {
        AddProduct_TimedProduct_3Products();
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("23459");
            TimedProduct bp = (TimedProduct) ii;
            assertEquals(23459, bp.getId());
            assertEquals("Restaurante Asador", bp.getName());
        });
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod update 23459 NAME \"RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR\""));
            printCapturedToSystemOut();
        });
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("23459");
            TimedProduct bp = (TimedProduct)ii;
            assertEquals(23459, bp.getId());
            assertEquals("Restaurante Asador", bp.getName());
        });
    }

    @Test
    void UpdateProduct_TimedProduct_UpdateCategory_Invalid() {
        AddProduct_TimedProduct_3Products();
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("23459");
            TimedProduct bp = (TimedProduct) ii;
            assertEquals(23459, bp.getId());
        });
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod update 23459 CATEGORY INVALID"));
            printCapturedToSystemOut();
        });
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("23459");
            TimedProduct bp = (TimedProduct) ii;
            assertEquals(23459, bp.getId());
        });
    }

    @Test
    void UpdateProduct_TimedProduct_UpdatePrice() {
        AddProduct_TimedProduct_3Products();
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("23459");
            TimedProduct bp = (TimedProduct) ii;
            assertEquals(23459, bp.getId());
            assertEquals(50.0, bp.getPrice());
        });
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod update 23459 PRICE 60"));
            printCapturedToSystemOut();
        });
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("23459");
            TimedProduct bp = (TimedProduct) ii;
            assertEquals(23459, bp.getId());
            assertEquals(60.0, bp.getPrice());
        });
    }

    @Test
    void UpdateProduct_TimedProduct_UpdatePrice_Invalid() {
        AddProduct_TimedProduct_3Products();
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("23459");
            TimedProduct bp = (TimedProduct) ii;
            assertEquals(23459, bp.getId());
            assertEquals(50.0, bp.getPrice());
        });
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod update 23459 PRICE 0"));
            printCapturedToSystemOut();
        });
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("23459");
            TimedProduct bp = (TimedProduct) ii;
            assertEquals(23459, bp.getId());
            assertEquals(50.0, bp.getPrice());
        });
    }

    // prod list
    @Test
    void ListProduct_Empty() {
        List<String> expected = Arrays.asList(
                "Catalog:",
                "prod list: ok"
        );
        List<String> captured;
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod list"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
    }

    @Test
    void ListProduct_Full() {
        AddProduct_BaseProduct_4Products();
        AddProduct_TimedProduct_3Products();
        AddProduct_PersonalizableProduct_2Products();
        AddProduct_Service_3Services();
        List<String> expected = Arrays.asList(
                "Catalog:",
                "  {class:Product, id:0, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}",
                "  {class:ProductService, id:1, category:INSURANCE, expiration:Mon Dec 21 00:00:00 CET 2026}",
                "  {class:Product, id:1, name:'Libro POO', category:BOOK, price:25.0}",
                "  {class:ProductService, id:2, category:TRANSPORT, expiration:Thu Dec 24 00:00:00 CET 2026}",
                "  {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}",
                "  {class:ProductService, id:3, category:SHOW, expiration:Fri Dec 31 00:00:00 CET 2100}",
                "  {class:Product, id:3, name:'Libro POO repetido Error', category:BOOK, price:25.0}",
                "  {class:ProductPersonalized, id:5, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0, maxPersonal:3}",
                "  {class:ProductPersonalized, id:6, name:'Camiseta talla:L UPM', category:CLOTHES, price:20.0, maxPersonal:4}",
                "  {class:Meeting, id:23456, name:'Reunion Rotonda', price:12.0, date of Event:2100-12-31, max people allowed:100}",
                "  {class:Meeting, id:23457, name:'Graduacion ETSISI', price:40.0, date of Event:2100-12-31, max people allowed:30}",
                "  {class:Food, id:23459, name:'Restaurante Asador', price:50.0, date of Event:2100-12-31, max people allowed:40}",
                "prod list: ok"
        );
        List<String> captured;
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod list"));
            printCapturedToSystemOut();
        });
        captured = getStrippedCapturedStdout();
        assertLinesMatch(expected, captured);
    }

    // prod remove
    @Test
    void RemoveProduct_MissingParameters() {
        AddProduct_BaseProduct_4Products();
        List<String> captured = getStrippedCapturedStdout();
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod remove 1"));
            printCapturedToSystemOut();
        });
        assertFalse(captured.isEmpty());
    }

    @Test
    void RemoveProduct_InvalidID() {
        assertThrows(MissingItemException.class, () -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("-1");
        });
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod remove -1"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void RemoveProduct_NonExistentID() {
        assertThrows(MissingItemException.class, () -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("0");
        });
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod remove 0"));
            printCapturedToSystemOut();
        });
    }

    @Test
    void RemoveProduct_BaseProduct() {
        AddProduct_BaseProduct_4Products();
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("1");
            assertEquals(1, ii.getId());
        });
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod remove 1"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("1");
        });
    }

    @Test
    void RemoveProduct_PersonalizableProduct() {
        AddProduct_PersonalizableProduct_2Products();
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("5");
            assertEquals(5, ii.getId());
        });
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod remove 5"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("5");
        });
    }

    @Test
    void RemoveProduct_TimedProduct() {
        AddProduct_TimedProduct_3Products();
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("23459");
            assertEquals(23459, ii.getId());
        });
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod remove 23459"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("23459");
        });
    }

    @Test
    void RemoveProduct_Service() {
        AddProduct_Service_3Services();
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("3S");
            assertEquals(3, ii.getId());
        });
        assertDoesNotThrow(() -> {
            this.command.eval(parse("prod remove 3S"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("3S");
        });
    }

    @Test
    void RemoveProduct_Service_NonExistent() {
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod remove 1S"));
            printCapturedToSystemOut();
        });
        assertThrows(MissingItemException.class, () -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("1S");
        });
    }

    // Edge case
    @Test
    void PreventDowngradingMaxPersonalByChangingCategory() {
        AddProduct_PersonalizableProduct_2Products();
        assertThrows(FailedCommandException.class, () -> {
            this.command.eval(parse("prod update 6 CATEGORY BOOK"));
            printCapturedToSystemOut();
        });
        assertDoesNotThrow(() -> {
            InventoryItem ii = Inventory.getInstance().getItemFromStringId("6");
            BaseProduct bp = (BaseProduct)ii;
            assertEquals(BaseProduct.Category.CLOTHES, bp.getCategory());
        });
    }
}
