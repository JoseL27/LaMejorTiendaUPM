package es.upm.etsisi.poo.commands;
import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.Product;
import es.upm.etsisi.poo.ParseResult;
import es.upm.etsisi.poo.Utils;

/**
 *  ProductCommand class that parses a stream of tokens into a specific ProductCommand,
 *  being one of the following formats:
 *      - prod add <id> "<nombre>" <categoria> <precio> (agrega un producto con nuevo id)
 *      - prod list (lista productos actuales)
 *      - prod update <id> campo valor (campos: nombre|categoria|precio)
 *      - prod remove <id>
 *  
 *  @author Enrique Rocha - 27/09
 *  @see Command
 */
public class ProductCommand extends Command {

	/**
	 * SubCommand enum describing the exiting sub commands to a product command
	 */
	enum SubCommand {
		ADD,	  
		LIST,  
		UPDATE,
		REMOVE;

		/**
		 * Method to get a SubCommand enum from a label. Usefull to parse the meant subcommand from a token.
		 * @param label The label to parse
		 * @return A valid SubCommand or null 
		 */
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

	/**
	 * The correspondant sub command of command.
	 * @see SubCommand
	 */
	private SubCommand subCommand;

	/**
	 * The productId referenced in the command.
	 * Used in every SubCommand except SubCommand.LIST.
	 * @see SubCommand
	 */
	private int productId;

	/**
	 * The productName referenced in the command.
	 * Used in SubCommand.CREATE as the name of the product to add.
	 * Used int SubCommand.EDIT if the field to change is the product name.
	 * @see SubCommand
	 */
	private String productName;

	/**
	 * The productName referenced in the command.
	 * Used in SubCommand.CREATE as the category of the product to add.
	 * Used int SubCommand.EDIT if the field to change is the product category.
	 * @see SubCommand
	 * @see Product.Category
	 */
	private Product.Category productCategory;

	/**
	 * The productName referenced in the command.
	 * Used in SubCommand.CREATE as the price of the product to add.
	 * Used int SubCommand.EDIT if the field to change is the product price.
	 * @see SubCommand
	 */
	private double productPrice;

	/**
	 * The productName referenced in the command.
	 * Used int SubCommand.EDIT to specify the field to change.
	 * @see SubCommand
	 * @see Product.Field
	 */
	private Product.Field productField;


	/**
	 * Basic constructor
	 */
	public ProductCommand(SubCommand subCommand, int productId, String productName, Product.Category productCategory, double productPrice, Product.Field productField) {
		this.subCommand		 = subCommand;
		this.productId		 = productId;
		this.productName	 = productName;
		this.productCategory = productCategory;
		this.productPrice	 = productPrice;
		this.productField    = productField;
	}

	/**
	 * Almost basic constructor without the particular use of Product.Field
	 */
	public ProductCommand(SubCommand subCommand, int productId, String productName, Product.Category productCategory, double productPrice) {
		this(subCommand, productId, productName, productCategory, productPrice, null);
	}

	/**
	 * First entry point to parse 'product' command (assumes the tokens[0] is 'product')
	 * Is responsible for parsing the 'subcommand' and dispatching to the corresponding one.
	 * @param tokens The stream of tokens to parse
	 * @return       The result of the parse. Either a valid ProductCommand instance or a failure code.
	 * @see ParseResult
	 */
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

	/**
	 * Parses the 'add' variation of the 'product' command.
	 * The function parses each field sequentially and short circuits if any fail.
	 * FORMAT: prod add <id> "<nombre>" <categoria> <precio>
	 * @param tokens The stream of tokens to parse
	 * @return       The result of the parse. If every parse succedes a valid ProductCommand Add instance
	 *               specifiying productId, productName, productCategory and productPrice,
	 *               OR a failure code specifying which parse went wrong.
	 * @see ParseResult
	 */
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
	
	/**
	 * Parses the 'list' variation of the 'product' command.
	 * This function does not actual parsing besides checking the number of arguments and
	 * creating a proper ProductCommand List instance.
	 * FORMAT: prod list 
	 * @param tokens The stream of tokens to parse.
	 * @return       The result of the parse. If the amount of tokens is 2 then a valid
	 *               ProductCommand List instance OR a failure code with ParseResult.Code.INSUFICIENT_ARGUMENTS.
	 */
	public static ParseResult tryParseList(String[] tokens) {
		if (tokens.length < 2) return new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);

		if (tokens.length > 2) {
			System.out.println("DEBUG: Excess arguments unimplemented");
		}
		
		return new ParseResult(new ProductCommand(SubCommand.LIST, 0, null, null, 0));
	}   

	/**
	 * Parses the 'update' variation of the 'product' command.
	 * The function parses 'id' and 'field' sequential  and short circuits if any fail. Then, according to
	 * the Product.Field the 'valor' token is parsed accordingly (either String, Product.Category or Integer). 
	 * FORMAT: prod update <id> campo valor (campos: nombre|categoria|precio)
	 * @param tokens The stream of tokens to parse
	 * @return       The result of the parse. If every parse succedes a valid ProductCommand Update instance
	 *               specifiying productId and productField as well as the value to the field specified
	 *               (productName if Field is String; productCategory if Field is Product.Category and productPrice if field is Integer)
	 *               OR a failure code specifying which parse went wrong.
	 * @see ParseResult
	 * @see Product.Field
	 */		
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

	/**
	 * Parses the 'remove' variation of the 'product' command.
	 * The function only parses and 'id'.
	 * FORMAT: prod remove <id>
	 * @param tokens The stream of tokens to parse
	 * @return       The result of the parse. If the amount of tokens is 3 and the 'id' parse succedes then a valid
	 *               ProductCommand Remove instance OR a failure code with the corresponding error code.
	 */		
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
		System.out.println("ProductCommand.TryExecute() UNIMPLEMENTED");
		return null;
	}

	@Override
	public String toString() {
		return String.format("{ subCommand: %s, productId: %d, productName: %s, productCategory: %s, productPrice: %f }",
							 this.subCommand, this.productId, this.productName, this.productCategory, this.productPrice);
	}

	/**
	 * Basic main for tests. Checks each test and prints the result.
	 */
	public static void main(String[] args) {
		String[][] tests = {
			{ "prod" },
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
