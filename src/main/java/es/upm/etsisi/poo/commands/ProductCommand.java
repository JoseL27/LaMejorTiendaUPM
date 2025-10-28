package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.Product;
import es.upm.etsisi.poo.Utils;
import es.upm.etsisi.poo.Parser;
import es.upm.etsisi.poo.DataManager.DataResult;
import es.upm.etsisi.poo.ArrayDataManager;
import es.upm.etsisi.poo.Ticket;
import jdk.jshell.execution.Util;

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
public class ProductCommand implements Command {
	
	public void eval(String[] args, Ticket ticket, ArrayDataManager dataManager) {
	}
	
	/**
	 * First entry point to parse 'product' command (assumes the parser.getCommand(0) is 'product')
	 * Is responsible for parsing the 'subcommand' and dispatching to the corresponding one.
	 * @param parser The stream of tokens to parse
	 * @return       The result of the parse. Either a valid ProductCommand instance or null
	 */
	public static Command tryParse(Parser parser) {
		// if (!Utils.checkArgsCountWithPrint("prod", parser, 2, 6)) return null;

		// SubCommand subCommand = SubCommand.fromLabel(parser.getCommand(1));
		// if (subCommand == null) {
		// 	Utils.printInvalidEnum("prod", "sub command", parser.getCommand(1), SubCommand.values());
		// 	return null;
		// }

		// return switch (subCommand) {
		// case ADD	-> tryParseAdd(parser);
		// case LIST	-> tryParseList(parser);
		// case UPDATE -> tryParseUpdate(parser);
		// case REMOVE -> tryParseRemove(parser);
		// };

		return null;
	}

	/**
	 * Parses the 'add' variation of the 'product' command.
	 * The function parses each field sequentially and short circuits if any fail.
	 * FORMAT: prod add <id> "<nombre>" <categoria> <precio>
	 * @param parser The stream of tokens to parse
	 * @return       The result of the parse. If every parse succedes a valid ProductCommand Add instance
	 *               specifiying productId, productName, productCategory and productPrice,
	 *               or null if it fails
	 */
	public static Command tryParseAdd(Parser parser) {
		// if (!Utils.checkArgsCountWithPrint("prod add", parser, 6)) return null;

 		// Integer id = Utils.tryParseInt(parser.getCommand(2));
		// if (id == null){
		// 	Utils.printInvalidDataType("prod add", "integer", parser.getCommand(2));
		// 	return null;
		// }

		// String name = parser.getCommand(3);

		// Product.Category category = Product.Category.fromLabel(parser.getCommand(4));
		// if (category == null){
		// 	Utils.printInvalidEnum("prod add", "category", parser.getCommand(4), Product.Category.values());
		// 	return null;
		// }

 		// Integer price = Utils.tryParseInt(parser.getCommand(5));
		// if (price == null){
		// 	Utils.printInvalidDataType("prod add", "integer", parser.getCommand(5));
		// 	return null;
		// }

		// return new ProductCommand(SubCommand.ADD, id, name, category, price);
		return null;
	}    
	
	/**
	 * Parses the 'list' variation of the 'product' command.
	 * This function does not actual parsing besides checking the number of arguments and
	 * creating a proper ProductCommand List instance.
	 * FORMAT: prod list 
	 * @param parser The stream of tokens to parse.
	 * @return       The result of the parse. If the amount of tokens is 2 then a valid
	 *               ProductCommand List instance or null
	 */
	public static Command tryParseList(Parser parser) {
		// return Utils.checkArgsCountWithPrint("prod list", parser, 2) ? new ProductCommand(SubCommand.LIST, 0, null, null, 0) : null;
		return null;
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
	 *               or null
	 * @see Product.Field
	 */		
	public static Command tryParseUpdate(Parser parser) {
// 		if (!Utils.checkArgsCountWithPrint("prod update", parser, 5)) return null;
		
//  		Integer id = Utils.tryParseInt(parser.getCommand(2));
// 		if (id == null){
// 			Utils.printInvalidDataType("prod update", "integer", parser.getCommand(2));
// 			return null;
// 		}

// 		Product.Field field = Product.Field.fromLabel(parser.getCommand(3));
// ;
// 		if (field == null) {
// 				Utils.printInvalidEnum("prod update", "field", parser.getCommand(3), Product.Field.values());
// 			return null;
// 		}

// 		String name = null;
// 		Product.Category category = null;
// 		Integer price = 0;

// 		switch (field) {
// 		case NAME: {
// 			name = parser.getCommand(4);
// 		} break;
// 		case CATEGORY: {
// 			category = Product.Category.fromLabel(parser.getCommand(4));
// 			if (category == null){
// 				Utils.printInvalidEnum("prod update", "category", parser.getCommand(4), Product.Category.values());
// 				return null;
// 			}
// 		} break;
// 		case PRICE: {
// 			price = Utils.tryParseInt(parser.getCommand(4));
// 			if (price == null){
// 				Utils.printInvalidDataType("prod update", "integer", parser.getCommand(4));
// 				return null;
// 			}
// 		} break;
// 		}

// 		return new ProductCommand(SubCommand.UPDATE, id, name, category, price, field);
		return null;
	}

	/**
	 * Parses the 'remove' variation of the 'product' command.
	 * The function only parses and 'id'.
	 * FORMAT: prod remove <id>
	 * @param parser The stream of tokens to parse
	 * @return       The result of the parse. If the amount of tokens is 3 and the 'id' parse succedes then a valid
	 *               ProductCommand Remove instance or null
	 */		
	public static Command tryParseRemove(Parser parser) {
		// if (!Utils.checkArgsCountWithPrint("prod remove", parser, 3)) return null;

 		// Integer id = Utils.tryParseInt(parser.getCommand(2));
		// if (id == null) {
		// 	Utils.printInvalidDataType("prod remove", "integer", parser.getCommand(2));
		// 	return null;
		// }

		// return new ProductCommand(SubCommand.REMOVE, id, null, null, 0);
		return null;
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
	 */
	// @Override
	public void tryExecute(Ticket ticket, ArrayDataManager store) {

		// switch (this.subCommand) {
		// 	case ADD -> {
		// 		// Add product to the store
		// 		DataResult dataResult = store.createProduct(this.productId, this.productName, this.productCategory, this.productPrice);

		// 		switch (dataResult) {
		// 			case SUCCESS -> {
		// 				System.out.println(new Product(this.productId, this.productName, this.productCategory, this.productPrice));
		// 				System.out.println("prod add: ok");
		// 			}
		// 			case PRODUCT_ALREADY_EXISTS -> System.out.printf("prod add: error: product with id %d already exists\n", this.productId);
		// 			case INVALID_ID             -> System.out.printf("prod add: error: expected id greater or equal than zero\n");
		// 			case INVALID_NAME           -> System.out.printf("prod add: error: expected name with less than 100 characters\n");
		// 			case INVALID_PRICE          -> System.out.printf("prod add: error: expected price greater than zero\n");
		// 			case INVENTORY_FULL         -> System.out.printf("prod add: error: inventory full\n");
		// 			default                     -> System.out.printf("prod add: error: unexpected issue\n");
		// 		}

		// 	}

		// 	case LIST -> {
		// 		Product[] products = store.listProducts();
		// 		System.out.println("Catalog:");
		// 		if (products != null) {
		// 			for (Product p : products) {
		// 				System.out.println(" "+p.toString());
		// 			}
		// 		}
		// 		System.out.println("prod list: ok");
		// 	}

		// 	case UPDATE -> {
		// 		// Update product in the store
		// 		DataResult dataResult = switch (this.productField) {
		// 			case NAME -> store.updateProductName(this.productId, this.productName);
		// 			case CATEGORY -> store.updateProductCategory(this.productId, this.productCategory);
		// 			case PRICE -> store.updateProductPrice(this.productId, this.productPrice);
		// 		};

		// 		switch (dataResult) {
		// 			case SUCCESS -> {
		// 				System.out.println(store.readProduct(this.productId));
		// 				System.out.printf("prod update: ok\n");
		// 			}
		// 			case INVALID_ID        -> System.out.printf("prod update: error: expected id greater or equal than zero\n");
		// 			case INVALID_NAME      -> System.out.printf("prod update: error: expected name with less than 100 characters\n");
		// 			case INVALID_PRICE     -> System.out.printf("prod update: error: expected price greater than zero\n");
		// 			case PRODUCT_NOT_FOUND -> System.out.printf("prod update: error: product with id %d not found\n", this.productId);
		// 			default                -> System.out.printf("prod update: error: unexpected issue\n");
		// 		}

		// 	}
		// 	case REMOVE -> {

		// 		Product productToRemove = store.readProduct(this.productId);
		// 		if (productToRemove != null) {
		// 			ticket.removeProduct(this.productId);
		// 			DataResult dataResult = store.deleteProduct(this.productId);

		// 			switch (dataResult) {
		// 				case SUCCESS -> {
		// 					System.out.println(productToRemove.toString());
		// 					System.out.printf("prod remove: ok\n");
		// 				}
		// 				case INVALID_ID -> System.out.printf("prod remove: error: expected id greater or equal than zero\n");
		// 				default         -> System.out.printf("prod remove: error: unexpected issue\n");
		// 			}

		// 		} else {
		// 			System.out.printf("prod remove: error: product with id %d not found\n", this.productId);
		// 		}

		// 	}
		// }
	}

	@Override
	public String toString() {
		// return String.format("{ subCommand: %s, productId: %d, productName: %s, productCategory: %s, productPrice: %f }",
		// 					 this.subCommand, this.productId, this.productName, this.productCategory, this.productPrice);
		return null;
	}

	/**
	 * Checks if this is the same as other object, that has to be a ProductCommand, based on its subCommand, product name, product id,
	 * product category and product price
	 * @param obj object to be compared to
	 * @return true, if the objects are equal under this criteria, false in other case
	 */
	@Override
	public boolean equals(Object obj) {
		// if (obj == null || (obj.getClass() != this.getClass())) return false;
		// ProductCommand otherProdCmd = (ProductCommand)obj;
		
		// return Utils.nullOrEquals(this.subCommand, otherProdCmd.subCommand) 
		// 	&& Utils.nullOrEquals(this.productName, otherProdCmd.productName)
		// 	&& this.productId == otherProdCmd.productId
		// 	&& this.productCategory == otherProdCmd.productCategory
		// 	&& this.productPrice == otherProdCmd.productPrice;
		return false;
	}
}
