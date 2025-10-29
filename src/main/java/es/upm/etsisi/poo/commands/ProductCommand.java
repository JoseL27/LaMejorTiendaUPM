package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.Product;
import es.upm.etsisi.poo.Utils;

import es.upm.etsisi.poo.Inventory;
import es.upm.etsisi.poo.Ticket;

/**
 *  ProductCommand class that parses a stream of tokens into a specific ProductCommand,
 *  being one of the following formats:
 *      - prod add <id> "<nombre>" <categoria> <precio> (agrega un producto con nuevo id)
 *      - prod list (lista productos actuales)
 *      - prod update <id> campo valor (campos: nombre|categoria|precio)
 *      - prod remove <id>
 *  @see Command
 */
public class ProductCommand implements Command {
	@Override
	public void eval(String[] args, Ticket ticket, Inventory inventory) {
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
	public void evalAdd(String[] params, Ticket ticket, Inventory inventory) {
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

		Product createdProduct = inventory.createProduct(productId, productName, productCategory, productPrice);		
		if (createdProduct != null) {
			System.out.println(createdProduct);
			System.out.println("prod add: ok");
		} else {
			System.out.println("prod add: error: unexpected error");
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
	public void evalUpdate(String[] params, Ticket ticket, Inventory inventory) {
		if (!Utils.checkArgsCountWithPrint("prod update", params.length, 5)) return;
		
 		Integer productId = Utils.tryParseInt(params[2]);
		if (productId == null){
			Utils.printInvalidDataType("prod update", "integer", params[2]);
			return;
		}

		String productFieldStr = params[3].toLowerCase();

		Product updatedProduct = null;
		String fieldName = params[3].toLowerCase();
		switch (fieldName) {
		case "name" -> {
			String productName = params[4];
			updatedProduct = inventory.updateProductName(productId, productName);
		}
		case "category" -> {
			Product.Category productCategory = Product.Category.fromLabel(params[4]);
			if (productCategory == null) {
				Utils.printInvalidEnum("prod update", "category", params[4], Product.Category.values());
			} else { 
				updatedProduct = inventory.updateProductCategory(productId, productCategory);
			}
		}
		case "price" -> {
			Integer productPrice = Utils.tryParseInt(params[4]);
			if (productPrice == null){
				Utils.printInvalidDataType("prod update", "integer", params[4]);
			} else { 
				updatedProduct = inventory.updateProductPrice(productId, productPrice);
			}
		}
		default -> {
			System.out.println("prod update: invalid field");
			return;
		}
		}

		if (updatedProduct != null) {
			System.out.println(updatedProduct);
			System.out.println("prod update: ok");
		} else { 
			System.out.printf("prod update: error: unexpected issue\n");
		}
	}

	public void evalAddFood(String[] params, Ticket ticket, Inventory inventory) {
		System.out.println("ProductCommand.evalAddFood: NOT IMPLEMENTED");
	}

	public void evalAddMeeting(String[] params, Ticket ticket, Inventory inventory) {
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
	public void evalRemove(String[] params, Ticket ticket, Inventory inventory) {
		if (!Utils.checkArgsCountWithPrint("prod remove", params.length, 3)) return;

 		Integer productId = Utils.tryParseInt(params[2]);
		if (productId == null) {
			Utils.printInvalidDataType("prod remove", "integer", params[2]);
			return;
		}

		Product productToRemove = inventory.readProduct(productId);
		if (productToRemove != null) {
			ticket.removeProduct(productId);
			
			if (inventory.deleteProduct(productId)) {
				System.out.println(productToRemove);
				System.out.printf("prod remove: ok\n");
			} else {
				System.out.printf("prod remove: error: unexpected error\n");
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
	public void evalList(String[] params, Ticket ticket, Inventory inventory) {
		if (!Utils.checkArgsCountWithPrint("prod list", params.length, 2)) return;

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
