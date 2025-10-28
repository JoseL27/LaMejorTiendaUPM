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
 *  @see Command
 */
public class ProductCommand implements Command {

	/**
	 * First entry point to parse 'product' command (assumes the parser.getCommand(0) is 'product')
	 * Is responsible for parsing the 'subcommand' and dispatching to the corresponding one.
	 * @param parser The stream of tokens to parse
	 * @return       The result of the parse. Either a valid ProductCommand instance or null
	 */

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
	
	@Override
	public void eval(String[] args, Ticket ticket, ArrayDataManager inventory) {
		if (!Utils.checkArgsCountWithPrint("prod", args.length, 2, 6)) return;

		switch (args[1].toLowerCase()) {
		case "add"	  -> evalAdd(args, ticket, inventory);
		case "list"	  -> evalList(args, ticket, inventory);
		case "update" -> evalUpdate(args, ticket, inventory);
		case "remove" -> evalRemove(args, ticket, inventory);
		default -> System.out.println("prod: invalid sub command");
		}
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
	public void evalAdd(String[] params, Ticket ticket, ArrayDataManager inventory) {
		if (!Utils.checkArgsCountWithPrint("prod add", params.length, 6)) return;

 		Integer productId = Utils.tryParseInt(params[2]);
		if (productId == null) {
			Utils.printInvalidDataType("prod add", "integer", params[2]);
			return;
		}

		String productName = params[3];
		Product.Category productCategory = Product.Category.fromLabel(params[4]);
		if (productCategory == null) {
			Utils.printInvalidEnum("prod add", "category", params[4], Product.Category.values());
			return;
		}

 		Integer productPrice = Utils.tryParseInt(params[5]);
		if (productPrice == null){
			Utils.printInvalidDataType("prod add", "integer", params[5]);
			return;
		}

		DataResult dataResult = inventory.createProduct(productId, productName, productCategory, productPrice);
		switch (dataResult) {
		case SUCCESS -> {
			System.out.println(new Product(productId, productName, productCategory, productPrice));
			System.out.println("prod add: ok");
		}
		case PRODUCT_ALREADY_EXISTS -> System.out.printf("prod add: error: product with id %d already exists\n", productId);
		case INVALID_ID             -> System.out.printf("prod add: error: expected id greater or equal than zero\n");
		case INVALID_NAME           -> System.out.printf("prod add: error: expected name with less than 100 characters\n");
		case INVALID_PRICE          -> System.out.printf("prod add: error: expected price greater than zero\n");
		case INVENTORY_FULL         -> System.out.printf("prod add: error: inventory full\n");
		default                     -> System.out.printf("prod add: error: unexpected issue\n");
		}
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
	public void evalUpdate(String[] params, Ticket ticket, ArrayDataManager inventory) {
		if (!Utils.checkArgsCountWithPrint("prod update", params.length, 5)) return;
		
 		Integer productId = Utils.tryParseInt(params[2]);
		if (productId == null){
			Utils.printInvalidDataType("prod update", "integer", params[2]);
			return;
		}

		String productFieldStr = params[3].toLowerCase();
		DataResult result = null;
		switch (params[3]) {
		case "name" -> {
			String productName = params[4];
			result = inventory.updateProductName(productId, productName);
		}
		case "category" -> {
			Product.Category productCategory = Product.Category.fromLabel(params[4]);
			if (productCategory == null) {
				Utils.printInvalidEnum("prod update", "category", params[4], Product.Category.values());
			} else { 
				result = inventory.updateProductCategory(productId, productCategory);
			}
		}
		case "price" -> {
			Integer productPrice = Utils.tryParseInt(params[4]);
			if (productPrice == null){
				Utils.printInvalidDataType("prod update", "integer", params[4]);
			} else { 
				result = inventory.updateProductPrice(productId, productPrice);
			}
		}
		default -> {
			System.out.println("prod update: invalid field");
		}
		}

		switch (result) {
		case SUCCESS -> {
			System.out.println(inventory.readProduct(productId));
			System.out.printf("prod update: ok\n");
		}
		case INVALID_ID        -> System.out.printf("prod update: error: expected id greater or equal than zero\n");
		case INVALID_NAME      -> System.out.printf("prod update: error: expected name with less than 100 characters\n");
		case INVALID_PRICE     -> System.out.printf("prod update: error: expected price greater than zero\n");
		case PRODUCT_NOT_FOUND -> System.out.printf("prod update: error: product with id %d not found\n", productId);
		default                -> System.out.printf("prod update: error: unexpected issue\n");
		}
	}

	public void evalAddFood(String[] params, Ticket ticket, ArrayDataManager inventory) {
		System.out.println("ProductCommand.evalAddFood: NOT IMPLEMENTED");
	}

	public void evalAddMeeting(String[] params, Ticket ticket, ArrayDataManager inventory) {
		System.out.println("ProductCommand.evalAddMeeting: NOT IMPLEMENTED");
	}

	/**
	 * Parses the 'remove' variation of the 'product' command.
	 * The function only parses and 'id'.
	 * FORMAT: prod remove <id>
	 * @param parser The stream of tokens to parse
	 * @return       The result of the parse. If the amount of tokens is 3 and the 'id' parse succedes then a valid
	 *               ProductCommand Remove instance or null
	 */			
	public void evalRemove(String[] params, Ticket ticket, ArrayDataManager inventory) {
		if (!Utils.checkArgsCountWithPrint("prod remove", params.length, 3)) return;

 		Integer productId = Utils.tryParseInt(params[2]);
		if (productId == null) {
			Utils.printInvalidDataType("prod remove", "integer", params[2]);
			return;
		}

		Product productToRemove = inventory.readProduct(productId);
		if (productToRemove != null) {
			ticket.removeProduct(productId);
			DataResult dataResult = inventory.deleteProduct(productId);

			switch (dataResult) {
			case SUCCESS -> {
				System.out.println(productToRemove.toString());
				System.out.printf("prod remove: ok\n");
			}
			case INVALID_ID -> System.out.printf("prod remove: error: expected id greater or equal than zero\n");
			default         -> System.out.printf("prod remove: error: unexpected issue\n");
			}

		} else {
			System.out.printf("prod remove: error: product with id %d not found\n", productId);
		}		
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
	public void evalList(String[] params, Ticket ticket, ArrayDataManager inventory) {
		if (Utils.checkArgsCountWithPrint("prod list", params.length, 2)) return;

		Product[] products = inventory.listProducts();

		System.out.println("Catalog:");
		if (products != null) {
			for (Product p : products) {
				System.out.println(" "+p.toString());
			}
		}
		System.out.println("prod list: ok");
	}
	
}
