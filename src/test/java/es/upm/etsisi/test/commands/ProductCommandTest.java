package es.upm.etsisi.poo.test.commands;

import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.Product;
import es.upm.etsisi.poo.ParseResult;
import es.upm.etsisi.poo.Parser;
import es.upm.etsisi.poo.commands.ProductCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
	
public class ProductCommandTest {
	@Test
	void addBookTest() {
		ParseResult result = ProductCommand.tryParse(new Parser("prod add 1 \"Libro POO\" BOOK 25"));
		ProductCommand expectedCmd = new ProductCommand(ProductCommand.SubCommand.ADD, 1, "Libro POO", Product.Category.BOOK, 25);
		assertEquals(result, new ParseResult(expectedCmd));
	}

	@Test
	void addClothesTest() {
		ParseResult result = ProductCommand.tryParse(new Parser("prod add 2 \"Camiseta talla:M UPM\" CLOTHES 15"));
		ProductCommand expectedCmd = new ProductCommand(ProductCommand.SubCommand.ADD, 2, "Camiseta talla:M UPM", Product.Category.CLOTHES, 15);
		assertEquals(result, new ParseResult(expectedCmd));
	}

	@Test
	void listTest() {
		ParseResult result = ProductCommand.tryParse(new Parser("prod list"));
		ProductCommand expectedCmd = new ProductCommand(ProductCommand.SubCommand.LIST, 0, null, null, 0);
		assertEquals(result, new ParseResult(expectedCmd));
	}

	@Test
	void updateNameTest() {
		ParseResult result = ProductCommand.tryParse(new Parser("prod update 1 NAME \"Libro POO V2\""));
		ProductCommand expectedCmd = new ProductCommand(ProductCommand.SubCommand.UPDATE, 1, "Libro POO V2", null, 0, Product.Field.NAME);
		assertEquals(result, new ParseResult(expectedCmd));
	}

	@Test
	void updatePriceTest() {
		ParseResult result = ProductCommand.tryParse(new Parser("prod update 1 PRICE 30"));
		ProductCommand expectedCmd = new ProductCommand(ProductCommand.SubCommand.UPDATE, 1, null, null, 30, Product.Field.PRICE);
		assertEquals(result, new ParseResult(expectedCmd));
	}

	@Test
	void updateCategoryTest() {
		ParseResult result = ProductCommand.tryParse(new Parser("prod update 1 CATEGORY STATIONERY"));
		ProductCommand expectedCmd = new ProductCommand(ProductCommand.SubCommand.UPDATE, 
														1, null, Product.Category.STATIONERY, 0, Product.Field.CATEGORY);
		assertEquals(result, new ParseResult(expectedCmd));
	}

	@Test
	void removeTest() {
		ParseResult result = ProductCommand.tryParse(new Parser("prod remove 3"));
		ProductCommand expectedCmd = new ProductCommand(ProductCommand.SubCommand.REMOVE, 3, null, null, 0);
		assertEquals(result, new ParseResult(expectedCmd));
	}

	// Failures
	@Test
	void noArgumentsTest() {
		ParseResult result = ProductCommand.tryParse(new Parser("prod"));
		assertEquals(result, new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS));
	}

	@Test
	void invalidSubCommandTest() {
		ParseResult result = ProductCommand.tryParse(new Parser("prod addd 1 \"Libro POO\" BOOK 25"));
		assertEquals(result, new ParseResult(ParseResult.Code.INVALID_SUB_COMMAND));
	}

	// Add Failures
	@Test
	void addInvalidIdTest() {
		ParseResult result = ProductCommand.tryParse(new Parser("prod add INVALID_NUMBER \"Libro POO\" BOOK 25"));
		assertEquals(result, new ParseResult(ParseResult.Code.INVALID_NUMBER));
	}

	@Test
	void addInvalidCategoryTest() {
		ParseResult result = ProductCommand.tryParse(new Parser("prod add 1 \"Libro POO\" INVALID_CATEGORY 25"));
		assertEquals(result, new ParseResult(ParseResult.Code.INVALID_CATEGORY));
	}

	@Test
	void addInvalidPriceTest() {
		ParseResult result = ProductCommand.tryParse(new Parser("prod add 1 \"Libro POO\" BOOK 25.0.0"));
		assertEquals(result, new ParseResult(ParseResult.Code.INVALID_NUMBER));
	}

	// Update Failures
	@Test
	void updateInvalidIdTest() {
		ParseResult result = ProductCommand.tryParse(new Parser("prod update INVALID_NUMBER NAME \"Libro POO V2\""));
		assertEquals(result, new ParseResult(ParseResult.Code.INVALID_NUMBER));
	}

	@Test
	void updateInvalidFieldTest() {
		ParseResult result = ProductCommand.tryParse(new Parser("prod update 1 INVALID_FIELD \"Libro POO V2\""));
		assertEquals(result, new ParseResult(ParseResult.Code.INVALID_PRODUCT_FIELD));
	}

	@Test
	void updateInvalidPriceTest() {
		ParseResult result = ProductCommand.tryParse(new Parser("prod update 1 PRICE 12.03f44"));
		assertEquals(result, new ParseResult(ParseResult.Code.INVALID_NUMBER));
	}
	
	@Test
	void updateInvalidCategoryTest() {
		ParseResult result = ProductCommand.tryParse(new Parser("prod update 1 CATEGORY INVALID_CATEGORY"));
		assertEquals(result, new ParseResult(ParseResult.Code.INVALID_CATEGORY));
	}

	// Remove Failures
	@Test
	void removeInvalidIdTest() {
		ParseResult result = ProductCommand.tryParse(new Parser("prod remove INVALID_NUMBER"));
		assertEquals(result, new ParseResult(ParseResult.Code.INVALID_NUMBER));
	}
}
