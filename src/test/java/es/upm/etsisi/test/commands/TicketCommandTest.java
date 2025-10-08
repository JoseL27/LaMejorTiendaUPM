package es.upm.etsisi.test.commands;

import java.util.Locale;

import es.upm.etsisi.poo.*;
import es.upm.etsisi.poo.commands.TicketCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

public class TicketCommandTest {

	// DICTATOR LOCALE 
	@BeforeAll
	static void setEnUSLocale() {
		Locale.setDefault(new Locale("en", "US"));
	}
	
	@AfterAll
	static void unsetEnUSLocale() {
		Locale.setDefault(Locale.getDefault());
	}		

	// failures
    @Test
    void insufficientArgumentsTest(){
        assertNull(TicketCommand.tryParse(new Parser("ticket add")));
		// TODO: Specify output test
    }

    @Test
    void invalidSubCommandTest(){
        assertNull(TicketCommand.tryParse(new Parser("ticket INVALID_SUB_COMMAND")));
		// TODO: Specify output test
    }

    @Test
    void invalidNumberTest(){
        assertNull(TicketCommand.tryParse(new Parser("ticket add INVALID_NUMBER")));
		// TODO: Specify output test
    }

    @Test
    void createNewTicketWithExcessArgumentsTest(){
        assertNull(TicketCommand.tryParse(new Parser("ticket new fkahfhdas")));
    }

    // @Test
    // void printEmptyTicketTest(){
    //     ParseResult parseResult = TicketCommand.tryParse(new Parser("ticket print"));
    //     assertEquals(es.upm.etsisi.poo.ParseResult.Code.SUCCESS , parseResult.getCode());
    //     assertEquals(new TicketCommand(TicketCommand.SubCommand.PRINT), parseResult.getCommand());

    //     Ticket testTicket = new Ticket();
    //     assertEquals(Command.ExecuteResult.SUCCESS, parseResult.getCommand().tryExecute(testTicket, new ArrayDataManager()));
    // }

    // @Test
    // void addProductTest(){
    //     Product prod = new Product(1, "Libro POO", Product.Category.BOOK, 25);
    //     ArrayDataManager manager = new ArrayDataManager();
    //     manager.createProduct(1, "Libro POO", Product.Category.BOOK, 25);

    //     Ticket expectedTicket = new Ticket();
    //     expectedTicket.addProduct(prod, 4);
    //     Ticket testTicket = new Ticket();

    //     ParseResult parseResult = TicketCommand.tryParse(new Parser("ticket add 1 4"));
    //     assertEquals(ParseResult.Code.SUCCESS, parseResult.getCode());
    //     assertEquals(new TicketCommand(TicketCommand.SubCommand.ADD, 1, 4), parseResult.getCommand());

    //     Command.ExecuteResult executeResult = parseResult.getCommand().tryExecute(testTicket, manager);
    //     assertEquals(Command.ExecuteResult.SUCCESS, executeResult);
    //     assertEquals(expectedTicket, testTicket);
    // }

    // @Test
    // void addProductWithInvalidIdTest(){
    //     Ticket expectedTicket = new Ticket();
    //     Ticket testTicket = new Ticket();

    //     ParseResult parseResult = TicketCommand.tryParse(new Parser("ticket add -1 4"));
    //     assertEquals(ParseResult.Code.SUCCESS, parseResult.getCode());
    //     assertEquals(new TicketCommand(TicketCommand.SubCommand.ADD, -1, 4), parseResult.getCommand());

    //     Command.ExecuteResult executeResult = parseResult.getCommand().tryExecute(testTicket, new ArrayDataManager());
    //     assertEquals(Command.ExecuteResult.INVALID_ID, executeResult);
    //     assertEquals(expectedTicket, testTicket);
    // }

    // @Test
    // void addProductWithInvalidAmountjTest(){
    //     Ticket expectedTicket = new Ticket();
    //     Ticket testTicket = new Ticket();

    //     ParseResult parseResult = TicketCommand.tryParse(new Parser("ticket add 1 -4"));
    //     assertEquals(ParseResult.Code.SUCCESS, parseResult.getCode());
    //     assertEquals(new TicketCommand(TicketCommand.SubCommand.ADD, 1, -4), parseResult.getCommand());

    //     Command.ExecuteResult executeResult = parseResult.getCommand().tryExecute(testTicket, new ArrayDataManager());
    //     assertEquals(Command.ExecuteResult.INVALID_AMOUNT, executeResult);
    //     assertEquals(expectedTicket, testTicket);
    // }

    // @Test
    // void addProductNotInStorageTest(){
    //     Product prod = new Product(1, "Libro POO", Product.Category.BOOK, 25);
    //     ArrayDataManager manager = new ArrayDataManager();
    //     manager.createProduct(1, "Libro POO", Product.Category.BOOK, 25);
    //     Ticket expectedTicket = new Ticket();
    //     Ticket testTicket = new Ticket();

    //     ParseResult parseResult = TicketCommand.tryParse(new Parser("ticket add 2 4"));
    //     assertEquals(ParseResult.Code.SUCCESS, parseResult.getCode());
    //     assertEquals(new TicketCommand(TicketCommand.SubCommand.ADD, 2, 4), parseResult.getCommand());

    //     Command.ExecuteResult executeResult = parseResult.getCommand().tryExecute(testTicket, manager);
    //     assertEquals(Command.ExecuteResult.PRODUCT_NOT_IN_STORAGE, executeResult);
    //     assertEquals(expectedTicket, testTicket);
    // }

    // @Test
    // void resetTicketTest(){
    //     Product prod = new Product(1, "Libro POO", Product.Category.BOOK, 25);
    //     ArrayDataManager manager = new ArrayDataManager();
    //     manager.createProduct(1, "Libro POO", Product.Category.BOOK, 25);

    //     Ticket expectedTicket = new Ticket();
    //     Ticket testTicket = new Ticket();

    //     testTicket.addProduct(prod, 4);

    //     Command.ExecuteResult executeResult = new TicketCommand(TicketCommand.SubCommand.NEW).tryExecute(testTicket, new ArrayDataManager());
    //     assertEquals(Command.ExecuteResult.SUCCESS, executeResult);
    //     assertEquals(expectedTicket, testTicket);
    // }

    // @Test
    // void removeProductTest(){
    //     Product prod = new Product(1, "Libro POO", Product.Category.BOOK, 25);
    //     Product prod2 = new Product(2, "Camiseta talla:M UPM", Product.Category.CLOTHES, 15);
    //     Ticket expectedTicket = new Ticket();
    //     Ticket testTicket = new Ticket();

    //     expectedTicket.addProduct(prod, 4);
    //     expectedTicket.addProduct(prod2, 1);
    //     testTicket.addProduct(prod, 4);
    //     testTicket.addProduct(prod2, 1);

    //     ParseResult parseResult = TicketCommand.tryParse(new Parser("ticket remove 1"));
    //     assertEquals(ParseResult.Code.SUCCESS, parseResult.getCode());
    //     assertEquals(new TicketCommand(TicketCommand.SubCommand.REMOVE, 1), parseResult.getCommand());

    //     expectedTicket.removeProduct(1);

    //     Command.ExecuteResult executeResult = parseResult.getCommand().tryExecute(testTicket, new ArrayDataManager());
    //     assertEquals(Command.ExecuteResult.SUCCESS, executeResult);
    //     assertEquals(expectedTicket, testTicket);
    // }

    // @Test
    // void removeProductNotInTicketTest(){
    //     Product prod = new Product(1, "Libro POO", Product.Category.BOOK, 25);
    //     Product prod2 = new Product(2, "Camiseta talla:M UPM", Product.Category.CLOTHES, 15);
    //     Ticket expectedTicket = new Ticket();
    //     Ticket testTicket = new Ticket();

    //     expectedTicket.addProduct(prod, 4);
    //     expectedTicket.addProduct(prod2, 1);
    //     testTicket.addProduct(prod, 4);
    //     testTicket.addProduct(prod2, 1);

    //     ParseResult parseResult = TicketCommand.tryParse(new Parser("ticket remove 3"));
    //     assertEquals(ParseResult.Code.SUCCESS, parseResult.getCode());
    //     assertEquals(new TicketCommand(TicketCommand.SubCommand.REMOVE, 3), parseResult.getCommand());


    //     Command.ExecuteResult executeResult = parseResult.getCommand().tryExecute(testTicket, new ArrayDataManager());
    //     assertEquals(Command.ExecuteResult.PRODUCT_NOT_IN_TICKET, executeResult);
    //     assertEquals(expectedTicket, testTicket);
    // }
}
