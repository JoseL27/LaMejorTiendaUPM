package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
	public void eval(String[] args, UserManager userManager, Inventory inventory) {
		if (!App.checkArgsCountWithPrint("prod", args.length, 2, 7)) return;

		switch (args[1].toLowerCase()) {
		case "addfood", "addmeeting" -> evalAddTimed(args, userManager, inventory);
		case "add"	  -> evalAddBase(args, userManager, inventory);
		case "list"	  -> evalList(args, userManager, inventory);
		case "update" -> evalUpdate(args, userManager, inventory);
		case "remove" -> evalRemove(args, userManager, inventory);
		default       -> System.out.println("prod: invalid sub command");
		}
	}


	/**
	 * Parses the 'add' variation of the 'product' command.
	 * The function parses each field sequentially and short circuits if any fail.
	 * FORMAT: prod add [<id>] "<name>" <category> <price> [<maxPers>]
	 * @param parser The stream of tokens to parse
	 * @return       The result of the parse. If every parse succedes a valid ProductCommand Add instance
	 *               specifiying productId, productName, productCategory and productPrice,
	 *               or null if it fails
	 */	
	public void evalAddBase(String[] params, UserManager userManager, Inventory inventory) {
		// Parse
		if (!App.checkArgsCountWithPrint("prod add", params.length, 5, 7)) return;
		
		// NOTE(enrique): Index to consume while parsing
		int parseIndex = 2; 

		String productIdOrName = params[parseIndex++];
		String productName = null;
 		Integer productId = App.tryParseInt(productIdOrName);
		
		// NOTE(enrique): Check the 3rd argument:
		//  - If parsing the int succeeds then we assume its the ID and the next token is the name string (advance parsing)
		//  - If it fails then we assume its the name and no ID was supplied (dont advance parsing), next token is the category
		if (productId == null) {
			productName = productIdOrName;
		} else {
			productName = params[parseIndex++];
		}

		String productCategoryString = params[parseIndex++];
		BaseProduct.Category productCategory = BaseProduct.Category.fromLabel(productCategoryString);
		if (productCategory == null) {
            System.out.printf("%s: error: invalid %s '%s', expected one of: %s\n","prod add", "category", productCategoryString, BaseProduct.Category.values());
            return;
		}

		String productPriceString = params[parseIndex++];
 		Integer productPrice = App.tryParseInt(productPriceString);
		if (productPrice == null){
			Utils.printInvalidDataType("prod add", "integer", productPriceString);
			return;
		}

		Integer productMaxPers = productCategory.getMaxPersonalizations();
		if (parseIndex < params.length) {
			String productMaxPersString = params[parseIndex++];
			productMaxPers = App.tryParseInt(productMaxPersString);
			if (productMaxPers == null){
				Utils.printInvalidDataType("prod add", "integer", productMaxPersString);
				return;
			}
		}
		
		// Execute
		if (productMaxPers != null && productMaxPers > productCategory.getMaxPersonalizations()) {
			System.out.printf("prod add: error: category %s only allows a max of %d personalizations, got %d\n",
							  productCategory, productCategory.getMaxPersonalizations(), productMaxPers);
			return;
		}

		if (productId == null) {
			productId = inventory.generateUniqueProductId();
		}
		
		BaseProduct createdProduct = inventory.createBaseProduct(productId, productName, productCategory, productPrice, productMaxPers);
		if (createdProduct != null) {
			System.out.println(createdProduct);
			System.out.println("prod add: ok");
		} else {
			System.out.println("prod add: error: unexpected error");
		}
	}

	/**
	 * Checks if the arguments are valid and tries to create a new timed product (food/meeting) from them
	 * @param params The arguments (user input) to consider for the command
	 * @param userManager The manager containing all the info on cliets and cashiers (and by extension on tickets)
	 * @param inventory The manager containing all the info on products, if the command succeeds, the product will be created here
	 */
	public void evalAddTimed(String[] params, UserManager userManager, Inventory inventory) {
		if (params.length != 6 && params.length != 7){
			System.out.println("prod " + params[1] + ": invalid number of parameters, got " + params.length + ", expected 6 or 7");
			return;
		}

		int id;
		String name;
		double price;
		LocalDateTime expirationDate;
		int maxPeople;
		TimedProduct.TimedType type;


		int parseIndex = 1;
		try {
			// Parsing

			String typeArgument = params[parseIndex++];
			type = TimedProduct.TimedType.fromLabel(typeArgument.replaceAll("add", ""));
			if (params.length == 6){
				name = params[parseIndex++];
				id = inventory.generateUniqueProductId();
			}else{
				id = Integer.parseInt(params[parseIndex++]);
				name = params[parseIndex++];
			}
			price = Double.parseDouble(params[parseIndex++]);
			expirationDate = LocalDate.parse(params[parseIndex++]).atStartOfDay();
			maxPeople = Integer.parseInt(params[parseIndex]);


			// Execution
			if (!Inventory.isValidId(id)){
				System.out.println("prod add: error:" + id + " is not a valid product id");
				return;
			}
			if (!Inventory.isValidName(name)){
				System.out.println("prod add: error:" + name + " is not a valid product name");
				return;
			}
			if(price < 0){
				System.out.println("prod add: error:" + price + " is not a valid product price");
				return;
			}
			if (maxPeople > TimedProduct.TIMED_PRODUCT_MAX_PEOPLE){
				System.out.println("prod add: error: " + maxPeople + " is not a valid maximum of people (max " + TimedProduct.TIMED_PRODUCT_MAX_PEOPLE + ")");
				return;
			}

			Product addedProduct = inventory.createTimedProduct(id, name, price, maxPeople, type, expirationDate);
			if (addedProduct == null){
				System.out.println("prod add: error: Error adding product to the inventory");
				return;
			}
			System.out.println(addedProduct);
			System.out.println("prod " + typeArgument + " : ok");
		}catch (Exception e){
			System.out.println("Invalid argument: "  + params[parseIndex] + " for index " + parseIndex);
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
	public void evalUpdate(String[] params, UserManager userManager, Inventory inventory) {
		if (!App.checkArgsCountWithPrint("prod update", params.length, 5)) return;
		
 		Integer productId = App.tryParseInt(params[2]);
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
			BaseProduct.Category productCategory = BaseProduct.Category.fromLabel(params[4]);
			if (productCategory == null) {
                System.out.printf("%s: error: invalid %s '%s', expected one of: %s\n",
                        "prod update", "category", params[4], BaseProduct.Category.values());
            } else {
				updatedProduct = inventory.updateProductCategory(productId, productCategory);
			}
		}
		case "price" -> {
			Integer productPrice = App.tryParseInt(params[4]);
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

	/**
	 * Parses the 'remove' variation of the 'product' command.
	 * The function only parses and 'id'.
	 * FORMAT: prod remove <id>
	 * @param parser The stream of tokens to parse
	 * @return       The result of the parse. If the amount of tokens is 3 and the 'id' parse succedes then a valid
	 *               ProductCommand Remove instance or null
	 */			
	public void evalRemove(String[] params, UserManager userManager, Inventory inventory) {
		 if (!App.checkArgsCountWithPrint("prod remove", params.length, 3)) return;

 		 Integer productId = App.tryParseInt(params[2]);
		 if (productId == null) {
		 	Utils.printInvalidDataType("prod remove", "integer", params[2]);
		 	return;
		 }

		 Product productToRemove = inventory.readProduct(productId);
		 if (productToRemove != null) {
			 // Retrieves the active ticket from the system (Products should not be removed from closed tickets)
			 List<Ticket> tickets = userManager.getAllTickets();

			 for (Ticket ticket: tickets) {
				 if (ticket.getIsOpen()) {
					 ticket.removeProduct(productToRemove.getId());
				 }
			 }

		 	if (inventory.deleteProduct(productId)) {
		 		System.out.println(productToRemove);
		 		System.out.println("prod remove: ok");
		 	} else {
		 		System.out.println("prod remove: error: unexpected error");
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
	public void evalList(String[] params, UserManager userManager, Inventory inventory) {
		if (!App.checkArgsCountWithPrint("prod list", params.length, 2)) return;

		Product[] products = inventory.listProducts();

		System.out.println("Catalog:");
		if (products != null) {
			for (Product p : products) {
				System.out.println("  "+p.toString());
			}
		}
		System.out.println("prod list: ok");
	}
	
}
