package es.upm.etsisi.poo.commands;
import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.Product;
import es.upm.etsisi.poo.ParseResult;
import es.upm.etsisi.poo.Utils;

/*
  prod add <id> "<nombre>" <categoria> <precio> (agrega un producto con nuevo id)
  prod list (lista productos actuales)
  prod update <id> campo valor (campos: nombre|categoria|precio)
  prod remove <id>
*/

public class ProductCommand extends Command {

	enum SubCommand {
		ADD,	  
		LIST,  
		UPDATE,
		REMOVE;

		public static SubCommand fromLabel(String label) {
			SubCommand subCmd = null;
			try {
				subCmd = SubCommand.valueOf(label.toUpperCase());				
			} catch (Exception e) {
			} finally {
				return subCmd;
			}
		}
	}

	private SubCommand subCommand;
	private int productId;
	private String productName;
	private Product.Category productCategory;
	private double productPrice;
	private Product.Field productField;
	
	public ProductCommand(SubCommand subCommand, int productId, String productName, Product.Category productCategory, double productPrice) {
		this.subCommand		 = subCommand;
		this.productId		 = productId;
		this.productName	 = productName;
		this.productCategory = productCategory;
		this.productPrice	 = productPrice;
	}
	
	public ProductCommand(SubCommand subCommand, int productId, String productName, Product.Category productCategory, double productPrice, Product.Field productField) {
		this(subCommand, productId, productName, productCategory, productPrice);
		this.productField = productField;
	}

	public static ParseResult TryParse(String[] tokens) {
		if (tokens.length < 2)
			return new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);

		SubCommand subCommand = SubCommand.fromLabel(tokens[1]);
		if (subCommand == null) return new ParseResult(ParseResult.Code.INVALID_SUB_COMMAND);

		ParseResult result = switch (subCommand) {
		case ADD	-> tryParseAdd(tokens);
		case LIST	-> tryParseList(tokens);
		case UPDATE -> tryParseUpdate(tokens);
		case REMOVE -> tryParseRemove(tokens);
		};
		return result;
	}

	//   prod add <id> "<nombre>" <categoria> <precio> (agrega un producto con nuevo id)
	public static ParseResult tryParseAdd(String[] tokens) {
		if (tokens.length < 6) return new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);

 		Integer id = Utils.tryParseInt(tokens[2]);
		if (id == null) return new ParseResult(ParseResult.Code.INVALID_NUMBER);

		String name = tokens[3];

		Product.Category category = Product.Category.fromLabel(tokens[4]);
		if (category == null) return new ParseResult(ParseResult.Code.INVALID_CATEGORY);

 		Integer price = Utils.tryParseInt(tokens[5]);
		if (price == null) return new ParseResult(ParseResult.Code.INVALID_NUMBER);

		if (tokens.length > 6) {
			System.out.println("DEBUG: Excess arguments unimplemented");
		}

		return new ParseResult(new ProductCommand(SubCommand.ADD, id, name, category, price));
	}    

	// prod list (lista productos actuales)
	public static ParseResult tryParseList(String[] tokens) {
		if (tokens.length < 2) return new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);

		if (tokens.length > 2) {
			System.out.println("DEBUG: Excess arguments unimplemented");
		}
		
		return new ParseResult(new ProductCommand(SubCommand.LIST, 0, null, null, 0));
	}   

	// prod update <id> campo valor (campos: nombre|categoria|precio)
	public static ParseResult tryParseUpdate(String[] tokens) {
		if (tokens.length < 5) return new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);
		
 		Integer id = Utils.tryParseInt(tokens[2]);
		if (id == null) return new ParseResult(ParseResult.Code.INVALID_NUMBER);

		Product.Field field = Product.Field.fromLabel(tokens[3]);
		if (field == null) return new ParseResult(ParseResult.Code.INVALID_PRODUCT_FIELD);

		String name = null;
		Product.Category category = null;
		Integer price = 0;

		switch (field) {
		case Product.Field.NAME: {
			name = tokens[4];
		} break;
		case Product.Field.CATEGORY: {
			category = Product.Category.fromLabel(tokens[4]);
			if (category == null) return new ParseResult(ParseResult.Code.INVALID_CATEGORY);
		} break;
		case Product.Field.PRICE: {
			price = Utils.tryParseInt(tokens[4]);
			if (price == null) return new ParseResult(ParseResult.Code.INVALID_NUMBER);
		} break;
		}
		
		if (tokens.length > 5) {
			System.out.println("DEBUG: Excess arguments unimplemented");
		}
		
		return new ParseResult(new ProductCommand(SubCommand.UPDATE, id, name, category, price, field));
	}

	// prod remove <id>
	public static ParseResult tryParseRemove(String[] tokens) {
		if (tokens.length < 3) return new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);

		if (tokens.length > 3) {
			System.out.println("DEBUG: Excess arguments unimplemented");
		}

 		Integer id = Utils.tryParseInt(tokens[2]);
		if (id == null) return new ParseResult(ParseResult.Code.INVALID_NUMBER);

		return new ParseResult(new ProductCommand(SubCommand.REMOVE, id, null, null, 0));
	} 
	
	public Command.ExecuteResult TryExecute() {
		return null;
	}

	@Override
	public String toString() {
		return String.format("{ subCommand: %s, productId: %d, productName: %s, productCategory: %s, productPrice: %f }",
							 this.subCommand, this.productId, this.productName, this.productCategory, this.productPrice);
	}

	public static void main(String[] args) {
		String[][] tests = {
			{ "prod" },
			{ "prod", "add", "INVALID_NUMBER", "Libro POO", "BOOK", "25" },
			{ "prod", "add", "1", "Libro POO", "BOOK", "25" },
			{ "prod", "add", "2", "Camiseta talla:M UPM", "CLOTHES", "15" },
			{ "prod", "list" },
			{ "prod", "update", "1", "NAME", "Libro POO V2" },
			{ "prod", "update", "1", "PRICE", "30" },
			{ "prod", "add", "3", "Libro POO repetido Error", "BOOK", "25" },
			{ "prod", "remove", "3" },
		};

		for (int testIndex = 0; testIndex < tests.length; testIndex++) { 
			String[] testTokens = tests[testIndex];
			System.out.printf("TEST #%d\n", testIndex);
			System.out.printf("\tinput: %s\n", Utils.arrayToString(testTokens));

			ParseResult result = TryParse(testTokens);
			System.out.printf("\tresult: %s\n", result);
		}
	}
}
