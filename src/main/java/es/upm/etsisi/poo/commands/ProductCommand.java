package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.Product;
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
	public static Command tryParse(Parser parser) {
		if (!Command.checkArgsCountWithPrint("prod", parser, 2, 6)) return null;

		SubCommand subCommand = SubCommand.fromLabel(parser.getCommand(1));
		if (subCommand == null) {
			Command.printInvalidEnum("prod", "sub command", parser.getCommand(1), SubCommand.values());
			return null;
		}

		return switch (subCommand) {
		case ADD	-> tryParseAdd(parser);
		case LIST	-> tryParseList(parser);
		case UPDATE -> tryParseUpdate(parser);
		case REMOVE -> tryParseRemove(parser);
		};		
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
	public static Command tryParseAdd(Parser parser) {
		if (!Command.checkArgsCountWithPrint("prod add", parser, 6)) return null;

 		Integer id = Command.tryParseIntWithPrint("prod add", parser.getCommand(2));
		if (id == null) return null;

		String name = parser.getCommand(3);

		Product.Category category = Command.tryParseCategoryWithPrint("prod add", parser.getCommand(4));
		if (category == null) return null;

 		Integer price = Command.tryParseIntWithPrint("prod add", parser.getCommand(5));
		if (price == null) return null;

		return new ProductCommand(SubCommand.ADD, id, name, category, price);
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
	public static Command tryParseList(Parser parser) {
		return Command.checkArgsCountWithPrint("prod list", parser, 2) ? new ProductCommand(SubCommand.LIST, 0, null, null, 0) : null;
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
	public static Command tryParseUpdate(Parser parser) {
		if (!Command.checkArgsCountWithPrint("prod update", parser, 5)) return null;
		
 		Integer id = Command.tryParseIntWithPrint("prod update", parser.getCommand(2));
		if (id == null) return null;

		Product.Field field = Command.tryParseFieldWithPrint("prod update", parser.getCommand(3));
		if (field == null) return null;

		String name = null;
		Product.Category category = null;
		Integer price = 0;

		switch (field) {
		case NAME: {
			name = parser.getCommand(4);
		} break;
		case CATEGORY: {
			category = Command.tryParseCategoryWithPrint("prod update", parser.getCommand(4));
			if (category == null) return null;
		} break;
		case PRICE: {
			price = Command.tryParseIntWithPrint("prod update", parser.getCommand(4));
			if (price == null) return null;
		} break;
		}

		return new ProductCommand(SubCommand.UPDATE, id, name, category, price, field);
	}

	/**
	 * Parses the 'remove' variation of the 'product' command.
	 * The function only parses and 'id'.
	 * FORMAT: prod remove <id>
	 * @param parser The stream of tokens to parse
	 * @return       The result of the parse. If the amount of tokens is 3 and the 'id' parse succedes then a valid
	 *               ProductCommand Remove instance OR a failure code with the corresponding error code.
	 */		
	public static Command tryParseRemove(Parser parser) {
		if (!Command.checkArgsCountWithPrint("prod remove", parser, 3)) return null;

 		Integer id = Command.tryParseIntWithPrint("prod remove", parser.getCommand(2));
		if (id == null) return null;
		
		return new ProductCommand(SubCommand.REMOVE, id, null, null, 0);
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
	 * @param ticket the {@link Ticket} associated with the command execution (may be used for logging or tracking)
	 * @param store  the {@link ArrayDataManager} representing the store's data manager
	 * @return a {@link ExecuteResult} indicating the outcome of the command execution
	 */
	@Override
	public void tryExecute(Ticket ticket, ArrayDataManager store) {

		switch (this.subCommand) {
		case ADD: { 
			// Add product to the store
			DataResult dataResult = store.createProduct(this.productId, this.productName, this.productCategory, this.productPrice);

			switch (dataResult) {
			case SUCCESS: {
				System.out.println(new Product(this.productId, this.productName, this.productCategory, this.productPrice));
                System.out.println("prod add: ok"); 
			} break;
			case PRODUCT_ALREADY_EXISTS: System.out.printf("prod add: error: product with id %d already exists\n", this.productId); break;
			case INVALID_ID:             System.out.printf("prod add: error: expected id greater or equal than zero\n");			break;
			case INVALID_NAME:           System.out.printf("prod add: error: expected name with less than 100 characters\n");		break;
			case INVALID_PRICE:          System.out.printf("prod add: error: expected price greater than zero\n");					break;
			case INVENTORY_FULL:         System.out.printf("prod add: error: inventory full\n");									break;
			default:                     System.out.printf("prod add: error: unexpected issue\n");									break;
			};

		} break;
		case LIST: {
			Product[] products = store.listProducts();
			System.out.println("Catalog:");
			if (products != null) {
				for (Product p : products) {
					System.out.println(" "+p.toString());
				}
			}
			System.out.println("prod list: ok");
		} break;
			
		case UPDATE: { 
			// Update product in the store
			DataResult dataResult = switch (this.productField) {
			case NAME -> store.updateProductName(this.productId, this.productName);
			case CATEGORY -> store.updateProductCategory(this.productId, this.productCategory);
			case PRICE -> store.updateProductPrice(this.productId, this.productPrice);
			};
			
			switch (dataResult) {
			case SUCCESS: {
				System.out.println(store.readProduct(this.productId));
				System.out.printf("prod update: ok\n");	
			} break;
			case INVALID_ID:        System.out.printf("prod update: error: expected id greater or equal than zero\n");		 break;
			case INVALID_NAME:      System.out.printf("prod update: error: expected name with less than 100 characters\n");	 break;
			case INVALID_PRICE:     System.out.printf("prod update: error: expected price greater than zero\n");			 break;
			case PRODUCT_NOT_FOUND: System.out.printf("prod update: error: product with id %d not found\n", this.productId); break;
			default:                System.out.printf("prod update: error: unexpected issue\n");							 break;
			};
			
		} break;
		case REMOVE: {
			
			Product productToRemove = store.readProduct(this.productId);
			if (productToRemove != null) { 
				ticket.removeProduct(this.productId);
				DataResult dataResult = store.deleteProduct(this.productId);
			
				switch (dataResult) {
				case SUCCESS: {
					System.out.println(productToRemove.toString());
					System.out.printf("prod remove: ok\n");
				} break;
				case INVALID_ID: System.out.printf("prod remove: error: expected id greater or equal than zero\n");	break;
				default:         System.out.printf("prod remove: error: unexpected issue\n");						break;
				};

			} else {
				System.out.printf("prod remove: error: product with id %d not found\n", this.productId);
			}
			
		} break;
		}
	}

	@Override
	public String toString() {
		return String.format("{ subCommand: %s, productId: %d, productName: %s, productCategory: %s, productPrice: %f }",
							 this.subCommand, this.productId, this.productName, this.productCategory, this.productPrice);
	}

	/**
	 * Checks if this is the same as other object, that has to be a ProductCommand, based on its subCommand, product name, product id,
	 * product category and product price
	 * @param obj object to be compared to
	 * @return true, if the objects are equal under this criteria, false in other case
	 */
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
