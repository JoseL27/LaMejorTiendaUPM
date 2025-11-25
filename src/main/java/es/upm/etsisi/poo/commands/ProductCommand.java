package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
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
	public void eval(String[] args) throws Exception {
		try {
			App.checkArgsCountWithPrint(args.length, 2, 7);

			switch (args[1].toLowerCase()) {
			case "addfood", "addmeeting" -> evalAddTimed(args);
			case "add"	  -> evalAddBase(args);
			case "list"	  -> evalList(args);
			case "update" -> evalUpdate(args);
			case "remove" -> evalRemove(args);
			default       -> throw new Exception("invalid sub command");
			}
		} catch (Exception e) {
			throw new Exception("prod "+e.getMessage());
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
	public void evalAddBase(String[] params) throws Exception {
		try {
			App.checkArgsCountWithPrint(params.length, 5, 7);
		
			int parseIndex = 2; 

			String productIdOrName = params[parseIndex++];
			String productName = null;
			Integer productId = null;
			try { 
				productId = Integer.parseInt(productIdOrName);
			} catch (Exception e) {}
		
			// NOTE(enrique): Check the 3rd argument:
			//  - If parsing the int succeeds then we assume its the ID and the next token is the name string (advance parsing)
			//  - If it fails then we assume its the name and no ID was supplied (dont advance parsing), next token is the category

			Inventory inventory = Inventory.getInstance();
			
			if (productId == null) {
				productName = productIdOrName;
				productId = inventory.generateUniqueProductId();
			} else {
				productName = params[parseIndex++];
			}

			String productCategoryString = params[parseIndex++];
			BaseProduct.Category productCategory = BaseProduct.Category.valueOf(productCategoryString.toUpperCase());

			String productPriceString = params[parseIndex++];
			Integer productPrice = Integer.parseInt(productPriceString);

			boolean specifiedMaxPers = false;
			int productMaxPers = productCategory.getMaxPersonalizations();
			if (parseIndex < params.length) {
				String productMaxPersString = params[parseIndex++];
				productMaxPers = Integer.parseInt(productMaxPersString);
				specifiedMaxPers = true;
			}
		
			if (productMaxPers > productCategory.getMaxPersonalizations()) {
				throw new Exception(String.format("category %s only allows a max of %d personalizations, got %d\n",
												  productCategory, productCategory.getMaxPersonalizations(), productMaxPers));
			}

			BaseProduct createdProduct = inventory.createBaseProduct(productId, productName, productCategory, 
																	 productPrice, productMaxPers, specifiedMaxPers);
			System.out.println(createdProduct);
			System.out.println("prod add: ok");
			
		} catch (Exception e) {
			throw new Exception("add: "+e.getMessage());
		}
	}

	/**
	 * Checks if the arguments are valid and tries to create a new timed product (food/meeting) from them
	 * @param params The arguments (user input) to consider for the command
	 * @param userManager The manager containing all the info on cliets and cashiers (and by extension on tickets)
	 * @param inventory The manager containing all the info on products, if the command succeeds, the product will be created here
	 */
	public void evalAddTimed(String[] params) throws Exception {
		try {
			App.checkArgsCountWithPrint(params.length, 6, 7);

			int parseIndex = 1;
			String typeArgument = params[parseIndex++];

			Inventory inventory = Inventory.getInstance();
			TimedProduct.TimedType type = TimedProduct.TimedType.valueOf(typeArgument.replaceAll("add", "").toUpperCase());

			String name;
			int id;
			if (params.length == 6) {
				name = Product.validName(params[parseIndex++]);
				id = inventory.generateUniqueProductId();
			} else {
				id = Product.parseId(params[parseIndex++]);
				name = Product.validName(params[parseIndex++]);
			}
		
			double price = App.parsePositiveDouble(params[parseIndex++]);
			LocalDateTime expirationDate = LocalDate.parse(params[parseIndex++]).atStartOfDay();
			int maxPeople = App.parsePositiveInt(params[parseIndex]);
			
			Duration hourDiff = Duration.between(LocalDateTime.now(), expirationDate);
			if (hourDiff.toHours() < type.getHoursForPreparing()) {
				throw new Exception(String.format("you need at least %d hours to prepare for %s, is in %d hours",
												  type.getHoursForPreparing(), type.toString().toLowerCase(), hourDiff.toHours()));
			}
		
			if (maxPeople > TimedProduct.TIMED_PRODUCT_MAX_PEOPLE) {
				throw new Exception(String.format("%d is not a valid maximum of people (max %d)", 
												  maxPeople, TimedProduct.TIMED_PRODUCT_MAX_PEOPLE));
			}

			Product addedProduct = inventory.createTimedProduct(id, name, price, maxPeople, type, expirationDate);
			System.out.println(addedProduct);
			System.out.printf("prod %s: ok\n", params[1]);
			
		} catch (Exception e) {
			throw new Exception(String.format("%s: %s", params[1], e.getMessage()));
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
	public void evalUpdate(String[] params) throws Exception {
		try {
			App.checkArgsCountWithPrint(params.length, 5);
		
			Integer productId = Integer.parseInt(params[2]);
			String productFieldStr = params[3].toLowerCase();
			Inventory inventory = Inventory.getInstance();

			Product updatedProduct = null;
			String fieldName = params[3].toLowerCase();
			switch (fieldName) {
			case "name" -> {
				String productName = params[4];
				updatedProduct = inventory.updateProductName(productId, productName);
			}
			case "category" -> {
				BaseProduct.Category productCategory = BaseProduct.Category.valueOf(params[4].toUpperCase());
				updatedProduct = inventory.updateProductCategory(productId, productCategory);
			}
			case "price" -> {
				Integer productPrice = Integer.parseInt(params[4]);
				updatedProduct = inventory.updateProductPrice(productId, productPrice);
			}
			default -> {
				throw new Exception("invalid field");
			}
			}

			System.out.println(updatedProduct);
			System.out.println("prod update: ok");

		} catch (Exception e) {
			throw new Exception("update: "+e.getMessage());
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
	public void evalRemove(String[] params) throws Exception {
		try {
			App.checkArgsCountWithPrint(params.length, 3);

			Integer productId = Integer.parseInt(params[2]);
			Inventory inventory = Inventory.getInstance();

			Product productToRemove = inventory.getProduct(productId);
			List<Ticket> tickets = UserManager.getInstance().getAllTickets();

			for (Ticket ticket: tickets) {
				if (ticket.getIsOpen()) {
					ticket.deleteProduct(productToRemove.getId());
				}
			}

			inventory.deleteProduct(productId);
			System.out.println(productToRemove);
			System.out.println("prod remove: ok");
			
		} catch (Exception e) {
			throw new Exception("remove: "+e.getMessage());
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
	public void evalList(String[] params) throws Exception {
		try {
			App.checkArgsCountWithPrint(params.length, 2);
			
			Product[] products = Inventory.getInstance().listProducts();

			System.out.println("Catalog:");
			if (products != null) {
				for (Product p : products) {
					System.out.println("  "+p.toString());
				}
			}
			System.out.println("prod list: ok");

		} catch (Exception e) {
			throw new Exception("list: "+e.getMessage());
		}
	}
	
}
