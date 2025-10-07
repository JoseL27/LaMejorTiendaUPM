package es.upm.etsisi.test.commands;

import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.Product;
import es.upm.etsisi.poo.Parser;
import es.upm.etsisi.poo.commands.ProductCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
	
public class ProductCommandTest {
	@Test
	void addBookTest() {
		Command result = ProductCommand.tryParse(new Parser("prod add 1 \"Libro POO\" BOOK 25"));
		ProductCommand expected = new ProductCommand(ProductCommand.SubCommand.ADD, 1, "Libro POO", Product.Category.BOOK, 25);
		assertEquals(result, expected);
	}

	@Test
	void addClothesTest() {
		Command result = ProductCommand.tryParse(new Parser("prod add 2 \"Camiseta talla:M UPM\" CLOTHES 15"));
		ProductCommand expected = new ProductCommand(ProductCommand.SubCommand.ADD, 2, "Camiseta talla:M UPM", Product.Category.CLOTHES, 15);
		assertEquals(result, expected);
	}

	@Test
	void listTest() {
		Command result = ProductCommand.tryParse(new Parser("prod list"));
		ProductCommand expected = new ProductCommand(ProductCommand.SubCommand.LIST, 0, null, null, 0);
		assertEquals(result, expected);
	}

	@Test
	void updateNameTest() {
		Command result = ProductCommand.tryParse(new Parser("prod update 1 NAME \"Libro POO V2\""));
		ProductCommand expected = new ProductCommand(ProductCommand.SubCommand.UPDATE, 1, "Libro POO V2", null, 0, Product.Field.NAME);
		assertEquals(result, expected);
	}

	@Test
	void updatePriceTest() {
		Command result = ProductCommand.tryParse(new Parser("prod update 1 PRICE 30"));
		ProductCommand expected = new ProductCommand(ProductCommand.SubCommand.UPDATE, 1, null, null, 30, Product.Field.PRICE);
		assertEquals(result, expected);
	}

	@Test
	void updateCategoryTest() {
		Command result = ProductCommand.tryParse(new Parser("prod update 1 CATEGORY STATIONERY"));
		ProductCommand expected = new ProductCommand(ProductCommand.SubCommand.UPDATE, 
														1, null, Product.Category.STATIONERY, 0, Product.Field.CATEGORY);
		assertEquals(result, expected);
	}

	@Test
	void removeTest() {
		Command result = ProductCommand.tryParse(new Parser("prod remove 3"));
		ProductCommand expected = new ProductCommand(ProductCommand.SubCommand.REMOVE, 3, null, null, 0);
		assertEquals(result, expected);
	}

	// Failures
	@Test
	void noArgumentsTest() {
		assertNull(ProductCommand.tryParse(new Parser("prod")));
		// TODO: Specify with output test
	}

	@Test
	void invalidSubCommandTest() {
		assertNull(ProductCommand.tryParse(new Parser("prod addd 1 \"Libro POO\" BOOK 25")));
		// TODO: Specify with output test
	}

	// Add Failures
	@Test
	void addInvalidIdTest() {
		assertNull(ProductCommand.tryParse(new Parser("prod add INVALID_NUMBER \"Libro POO\" BOOK 25")));
		// TODO: Specify with output test
	}

	@Test
	void addInvalidCategoryTest() {
		assertNull(ProductCommand.tryParse(new Parser("prod add 1 \"Libro POO\" INVALID_CATEGORY 25")));
		// TODO: Specify with output test
	}

	@Test
	void addInvalidPriceTest() {
		assertNull(ProductCommand.tryParse(new Parser("prod add 1 \"Libro POO\" BOOK 25.0.0")));
		// TODO: Specity with output test
	}

	// Update Failures
	@Test
	void updateInvalidIdTest() {
		assertNull(ProductCommand.tryParse(new Parser("prod update INVALID_NUMBER NAME \"Libro POO V2\"")));
		// TODO: Specity with output test
	}

	@Test
	void updateInvalidFieldTest() {
		assertNull(ProductCommand.tryParse(new Parser("prod update 1 INVALID_FIELD \"Libro POO V2\"")));
		// TODO: Specity with output test
	}

	@Test
	void updateInvalidPriceTest() {
		assertNull(ProductCommand.tryParse(new Parser("prod update 1 PRICE 12.03f44")));
		// TODO: Specity with output test
	}
	
	@Test
	void updateInvalidCategoryTest() {
		assertNull(ProductCommand.tryParse(new Parser("prod update 1 CATEGORY INVALID_CATEGORY")));
		// TODO: Specity with output test
	}

	// Remove Failures
	@Test
	void removeInvalidIdTest() {
		assertNull(ProductCommand.tryParse(new Parser("prod remove INVALID_NUMBER")));
		// TODO: Specity with output test
	}
}
