package es.upm.etsisi.poo.commands;
import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.Product;
import es.upm.etsisi.poo.ParseResult;
import es.upm.etsisi.poo.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
	

/**
 * ProductCommand class tests
 *  @author Enrique Rocha - 27/09
 *  @see ProductCommand
 */
public class ProductCommandTest {

	@Test
	void general() {
		// String[][] tests = {
		// 	{ "prod" },
		// 	{ "prod", "add", "1", "Libro POO", "BOOK", "25" },
		// 	{ "prod", "add", "2", "Camiseta talla:M UPM", "CLOTHES", "15" },
		// 	{ "prod", "list" },
		// 	{ "prod", "update", "1", "NAME", "Libro POO V2" },
		// 	{ "prod", "update", "1", "PRICE", "30" },
		// 	{ "prod", "add", "3", "Libro POO repetido Error", "BOOK", "25" },
		// 	{ "prod", "remove", "3" },
		// };

		// for (int testIndex = 0; testIndex < tests.length; testIndex++) { 
		// 	String[] testTokens = tests[testIndex];
		// 	System.out.printf("TEST #%d\n", testIndex);
		// 	System.out.printf("\tinput: %s\n", Utils.arrayToString(testTokens));

		// 	ParseResult result = ProductCommand.TryParse(testTokens);
		// 	System.out.printf("\tresult: %s\n", result);
		// }
	}
}
