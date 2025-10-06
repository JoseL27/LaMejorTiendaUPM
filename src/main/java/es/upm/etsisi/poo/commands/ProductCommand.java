package es.upm.etsisi.poo.commands;
import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.Product;
import es.upm.etsisi.poo.ParseResult;
import es.upm.etsisi.poo.Utils;
import es.upm.etsisi.poo.Parser;
import es.upm.etsisi.poo.DataManager.DataResult;
import es.upm.etsisi.poo.ArrayDataManager;
import es.upm.etsisi.poo.Ticket;

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
	public enum SubCommand {
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
	 * First entry point to parse 'product' command (assumes the parser.getCommand(0) is 'product')
	 * Is responsible for parsing the 'subcommand' and dispatching to the corresponding one.
	 * @param parser The stream of tokens to parse
	 * @return       The result of the parse. Either a valid ProductCommand instance or a failure code.
	 * @see ParseResult
	 */
	public static ParseResult tryParse(Parser parser) {
		if (parser.getLength() < 2)
			return new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);

		SubCommand subCommand = SubCommand.fromLabel(parser.getCommand(1));
		if (subCommand == null) return new ParseResult(ParseResult.Code.INVALID_SUB_COMMAND);

		ParseResult result = switch (subCommand) {
		case ADD	-> tryParseAdd(parser);
		case LIST	-> tryParseList(parser);
		case UPDATE -> tryParseUpdate(parser);
		case REMOVE -> tryParseRemove(parser);
		};
		return result;
	}

	/**
	 * Parses the 'add' variation of the 'product' command.
	 * The function parses each field sequentially and short circuits if any fail.
	 * FORMAT: prod add <id> "<nombre>" <categoria> <precio>
	 * @param parser The stream of tokens to parse
	 * @return       The result of the parse. If every parse succedes a valid ProductCommand Add instance
	 *               specifiying productId, productName, productCategory and productPrice,
	 *               OR a failure code specifying which parse went wrong.
	 * @see ParseResult
	 */
	public static ParseResult tryParseAdd(Parser parser) {
		if (parser.getLength() < 6) return new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);

 		Integer id = Utils.tryParseInt(parser.getCommand(2));
		if (id == null) return new ParseResult(ParseResult.Code.INVALID_NUMBER);

		String name = parser.getCommand(3);

		Product.Category category = Product.Category.fromLabel(parser.getCommand(4));
		if (category == null) return new ParseResult(ParseResult.Code.INVALID_CATEGORY);

 		Integer price = Utils.tryParseInt(parser.getCommand(5));
		if (price == null) return new ParseResult(ParseResult.Code.INVALID_NUMBER);

		if (parser.getLength() > 6) {
			System.out.println("DEBUG: Excess arguments unimplemented");
		}

		return new ParseResult(new ProductCommand(SubCommand.ADD, id, name, category, price));
	}    
	
	/**
	 * Parses the 'list' variation of the 'product' command.
	 * This function does not actual parsing besides checking the number of arguments and
	 * creating a proper ProductCommand List instance.
	 * FORMAT: prod list 
	 * @param parser The stream of tokens to parse.
	 * @return       The result of the parse. If the amount of tokens is 2 then a valid
	 *               ProductCommand List instance OR a failure code with ParseResult.Code.INSUFICIENT_ARGUMENTS.
	 */
	public static ParseResult tryParseList(Parser parser) {
		if (parser.getLength() < 2) return new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);

		if (parser.getLength() > 2) {
			System.out.println("DEBUG: Excess arguments unimplemented");
		}
		
		return new ParseResult(new ProductCommand(SubCommand.LIST, 0, null, null, 0));
	}   

	/**
	 * Parses the 'update' variation of the 'product' command.
	 * The function parses 'id' and 'field' sequential  and short circuits if any fail. Then, according to
	 * the Product.Field the 'valor' token is parsed accordingly (either String, Product.Category or Integer). 
	 * FORMAT: prod update <id> campo valor (campos: nombre|categoria|precio)
	 * @param parser The stream of tokens to parse
	 * @return       The result of the parse. If every parse succedes a valid ProductCommand Update instance
	 *               specifiying productId and productField as well as the value to the field specified
	 *               (productName if Field is String; productCategory if Field is Product.Category and productPrice if field is Integer)
	 *               OR a failure code specifying which parse went wrong.
	 * @see ParseResult
	 * @see Product.Field
	 */		
	public static ParseResult tryParseUpdate(Parser parser) {
		if (parser.getLength() < 5) return new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);
		
 		Integer id = Utils.tryParseInt(parser.getCommand(2));
		if (id == null) return new ParseResult(ParseResult.Code.INVALID_NUMBER);

		Product.Field field = Product.Field.fromLabel(parser.getCommand(3));
		if (field == null) return new ParseResult(ParseResult.Code.INVALID_PRODUCT_FIELD);

		String name = null;
		Product.Category category = null;
		Integer price = 0;

		switch (field) {
		case Product.Field.NAME: {
			name = parser.getCommand(4);
		} break;
		case Product.Field.CATEGORY: {
			category = Product.Category.fromLabel(parser.getCommand(4));
			if (category == null) return new ParseResult(ParseResult.Code.INVALID_CATEGORY);
		} break;
		case Product.Field.PRICE: {
			price = Utils.tryParseInt(parser.getCommand(4));
			if (price == null) return new ParseResult(ParseResult.Code.INVALID_NUMBER);
		} break;
		}
		
		if (parser.getLength() > 5) {
			System.out.println("DEBUG: Excess arguments unimplemented");
		}
		
		return new ParseResult(new ProductCommand(SubCommand.UPDATE, id, name, category, price, field));
	}

	/**
	 * Parses the 'remove' variation of the 'product' command.
	 * The function only parses and 'id'.
	 * FORMAT: prod remove <id>
	 * @param parser The stream of tokens to parse
	 * @return       The result of the parse. If the amount of tokens is 3 and the 'id' parse succedes then a valid
	 *               ProductCommand Remove instance OR a failure code with the corresponding error code.
	 */		
	public static ParseResult tryParseRemove(Parser parser) {
		if (parser.getLength() < 3) return new ParseResult(ParseResult.Code.INSUFICIENT_ARGUMENTS);

		if (parser.getLength() > 3) {
			System.out.println("DEBUG: Excess arguments unimplemented");
		}

 		Integer id = Utils.tryParseInt(parser.getCommand(2));
		if (id == null) return new ParseResult(ParseResult.Code.INVALID_NUMBER);

		return new ParseResult(new ProductCommand(SubCommand.REMOVE, id, null, null, 0));
	} 

	/**
	 * Attempts to execute a product-related command on the given store and ticket.
	 * <p>
	 * Supported subcommands include:
	 * <ul>
	 *   <li>ADD: Adds a new product to the store.</li>
	 *   <li>LIST: Lists all products currently in the store.</li>
	 *   <li>UPDATE: Updates a specific field (name, category, or price) of an existing product.</li>
	 *   <li>REMOVE: Removes a product from the store.</li>
	 * </ul>
	 * The specific action performed depends on the value of {@code this.subCommand}.
	 * 
	 * @param store  the {@link ArrayDataManager} representing the store's data manager
	 * @param ticket the {@link Ticket} associated with the command execution (may be used for logging or tracking)
	 * @return a {@link Command.ExecuteResult} indicating the outcome of the command execution
	 */
	public Command.ExecuteResult TryExecute( ArrayDataManager store, Ticket ticket) {

		//System.out.println("ProductCommand.TryExecute() UNIMPLEMENTED");
		final DataResult result;
		ExecuteResult FinalResult = null;
		switch (this.subCommand) {
		case ADD:
			// Add product to the store
			result = store.createProduct(this.productId, this.productName, this.productCategory, this.productPrice);
			FinalResult = switch (result) {
			case SUCCESS -> Command.ExecuteResult.SUCCESS;
			case INVALID_ID, PRODUCT_ALREADY_EXISTS -> Command.ExecuteResult.INVALID_ID;
			case INVENTORY_FULL -> Command.ExecuteResult.DATA_ERROR;
			case INVALID_NAME, INVALID_CATEGORY, INVALID_PRICE -> Command.ExecuteResult.DATA_ERROR;
			};
		case LIST:
			// List products in the store
			final Product[] products = store.listProducts();
			if (products == null) {
				System.out.println("");
			} else {
				System.out.println("ID\tNOMBRE\tCATEGORIA\tPRECIO");
				for (Product p : products) {
					System.out.println(p.toString());
				}
			}
			FinalResult = Command.ExecuteResult.SUCCESS;
			break;
		case UPDATE:
			// Update product in the store
			result = switch (this.productField) {
			case NAME -> store.updateProductName(this.productId, this.productName);
			case CATEGORY -> store.updateProductCategory(this.productId, this.productCategory);
			case PRICE -> store.updateProductPrice(this.productId, this.productPrice);
			};
			FinalResult = switch (result) {
			case SUCCESS -> Command.ExecuteResult.SUCCESS;
			case INVALID_ID -> Command.ExecuteResult.INVALID_ID;
			case PRODUCT_NOT_FOUND -> Command.ExecuteResult.PRODUCT_NOT_IN_STORAGE;
			case INVALID_NAME, INVALID_CATEGORY, INVALID_PRICE -> Command.ExecuteResult.DATA_ERROR;
			};
			break;
		case REMOVE:
			// Remove product from the store
			ticket.removeProduct(this.productId); // Remove product from the ticket as well
			result = store.deleteProduct(this.productId);
			FinalResult = switch (result) {
			case SUCCESS -> Command.ExecuteResult.SUCCESS;
			case INVALID_ID -> Command.ExecuteResult.INVALID_ID;
			case PRODUCT_NOT_FOUND -> Command.ExecuteResult.PRODUCT_NOT_IN_STORAGE;
			};
			break;
		}
		return FinalResult;
	}

	@Override
	public String toString() {
		return String.format("{ subCommand: %s, productId: %d, productName: %s, productCategory: %s, productPrice: %f }",
							 this.subCommand, this.productId, this.productName, this.productCategory, this.productPrice);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || (obj.getClass() != this.getClass())) return false;
		ProductCommand otherProdCmd = (ProductCommand)obj;
		
		return Utils.nullOrEquals(this.subCommand, otherProdCmd.subCommand) 
			&& Utils.nullOrEquals(this.productName, otherProdCmd.productName)
			&& this.productId == otherProdCmd.productId
			&& this.productCategory == otherProdCmd.productCategory
			&& this.productPrice == otherProdCmd.productPrice;		
	}
}
