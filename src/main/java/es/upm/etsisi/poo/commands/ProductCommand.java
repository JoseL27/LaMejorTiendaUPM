package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.*;
import es.upm.etsisi.poo.exceptions.DataException;
import es.upm.etsisi.poo.exceptions.FailedCommandException;
import es.upm.etsisi.poo.exceptions.MissingItemException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
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
	public void eval(String[] args) throws FailedCommandException {
		if (!App.checkArgsCountWithPrint("prod", args.length, 2, 7)) return;

		switch (args[1].toLowerCase()) {
		case "addfood", "addmeeting" -> evalAddTimed(args);
		case "add"	  -> evalAddBase(args);
		case "list"	  -> evalList(args);
		case "update" -> evalUpdate(args);
		case "remove" -> evalRemove(args);
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
	public void evalAddBase(String[] params) throws FailedCommandException{
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
        BaseProduct.Category productCategory;
        try {
            productCategory = BaseProduct.Category.valueOf(productCategoryString.toUpperCase());
        }catch (IllegalArgumentException ex){
            throw new FailedCommandException(String.format("%s: error: invalid %s '%s', expected one of: %s\n","prod add", "category", productCategoryString, Arrays.toString(BaseProduct.Category.values())));
        }

		String productPriceString = params[parseIndex++];
 		Integer productPrice = App.tryParseInt(productPriceString);
		if (productPrice == null){
			App.printInvalidDataType("prod add", "integer", productPriceString);
			return;
		}

		boolean specifiedMaxPers = false;
		Integer productMaxPers = productCategory.getMaxPersonalizations();
		if (parseIndex < params.length) {
			String productMaxPersString = params[parseIndex++];
			productMaxPers = App.tryParseInt(productMaxPersString);
			if (productMaxPers == null) {
				App.printInvalidDataType("prod add", "integer", productMaxPersString);
				return;
			}
			specifiedMaxPers = true;
		}
		
		// Execute
		Inventory inventory = Inventory.getInstance();

		if (productId == null) {
			productId = inventory.generateUniqueProductId();
		}

        try {
            BaseProduct createdProduct = inventory.createBaseProduct(productId, productName, productCategoryString,
                    productPrice, productMaxPers, specifiedMaxPers);
            System.out.println(createdProduct);
            System.out.println("prod add: ok");
        }catch(DataException ex){
            throw new FailedCommandException("Unable to add the product: " + ex.getMessage());
        }
	}

	/**
	 * Checks if the arguments are valid and tries to create a new timed product (food/meeting) from them
	 * @param params The arguments (user input) to consider for the command
	 * @param userManager The manager containing all the info on cliets and cashiers (and by extension on tickets)
	 * @param inventory The manager containing all the info on products, if the command succeeds, the product will be created here
	 */
	public void evalAddTimed(String[] params) throws FailedCommandException {
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

        // Parsing
        Inventory inventory = Inventory.getInstance();

        String typeArgument = params[parseIndex++];
        try {
            type = TimedProduct.TimedType.valueOf(typeArgument.replaceAll("add", "").toUpperCase());
        }catch (IllegalArgumentException ex) {
            throw new FailedCommandException(String.format("%s: error: invalid %s '%s', expected one of: %s\n"
                    ,"prod add", "category", typeArgument, Arrays.toString(TimedProduct.TimedType.values())));
        }

        try {
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
			LocalDateTime prepDoneTime = LocalDateTime.now().plusHours(type.getHoursForPreparing());
			if (prepDoneTime.isAfter(expirationDate)) {
				throw new FailedCommandException(String.format("prod add: error: you need at least %d hours to prepare for this %s\n",
								   type.getHoursForPreparing(), type.toString().toLowerCase()));
			}

			Product addedProduct = inventory.createTimedProduct(id, name, price, maxPeople, typeArgument, expirationDate);
			System.out.println(addedProduct);
			System.out.printf("prod %s: ok\n", typeArgument);
		}catch (NumberFormatException ex) {
		    throw new FailedCommandException("Unable to add product, invalid argument: "  + params[parseIndex] + " for argument number " + parseIndex);
		}catch (DateTimeParseException ex){
            throw new FailedCommandException("Unable to add product, invalid date format.");
        }catch (DataException ex){
            throw new FailedCommandException("Unable to add product: " + ex.getMessage());
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
	public void evalUpdate(String[] params) throws FailedCommandException{
		if (!App.checkArgsCountWithPrint("prod update", params.length, 5)) return;

        int productId;
        try {
            productId = Integer.parseInt(params[2]);
        }catch (NumberFormatException ex){
            throw new FailedCommandException("Unable to update product: " + params[2] + " is not a valid integer");
        }

		Inventory inventory = Inventory.getInstance();
		Product updatedProduct = null;
		String fieldName = params[3].toLowerCase();

        try {
            switch (fieldName) {
                case "name" -> {
                    String productName = params[4];
                    updatedProduct = inventory.updateProductName(productId, productName);
                }
                case "category" -> {
                    String productCategory = params[4];
                    updatedProduct = inventory.updateProductCategory(productId, productCategory);
                }
                case "price" -> {
                    int productPrice;
                    try {
                        productPrice = Integer.parseInt(params[4]);
                    }catch (NumberFormatException ex){
                        throw new FailedCommandException("Unable to update product: " + params[4] + " is not a valid integer");
                    }
                    updatedProduct = inventory.updateProductPrice(productId, productPrice);
                }
                default -> {
                    throw new FailedCommandException("prod update: invalid field");
                }
            }
            System.out.println(updatedProduct);
            System.out.println("prod update: ok");
        }catch (DataException ex){
            throw new FailedCommandException("Unable to update product: " + ex.getMessage());
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
	public void evalRemove(String[] params) throws FailedCommandException{
		 if (!App.checkArgsCountWithPrint("prod remove", params.length, 3)) return;
         int productId;
         try {
             productId = Integer.parseInt(params[2]);
         }catch (NumberFormatException ex){
             throw new FailedCommandException("Unable to remove product, " + params[2] + " is not a valid integer");
         }
		 Inventory inventory = Inventory.getInstance();

		 Product productToRemove = inventory.readProduct(productId);
		 if (productToRemove == null) {
             throw new FailedCommandException("Unable to remove product, product with id " + productId + " not found");
		 }
        // Retrieves the active tickets from the system (Products should not be removed from closed tickets)
        List<Ticket> tickets = UserManager.getInstance().getAllTickets();

        for (Ticket ticket: tickets) {
            if (ticket.isOpen()) {
                ticket.removeProduct(productToRemove.getId());
            }
        }

        try {
            inventory.deleteProduct(productId);
            System.out.println(productToRemove);
            System.out.println("prod remove: ok");
        }catch (MissingItemException ex){
            throw new FailedCommandException("Unable to remove product, " + ex.getMessage());
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
	public void evalList(String[] params) {
		if (!App.checkArgsCountWithPrint("prod list", params.length, 2)) return;

		Product[] products = Inventory.getInstance().listProducts();

		System.out.println("Catalog:");
		if (products != null) {
			for (Product p : products) {
				System.out.println("  "+p.toString());
			}
		}
		System.out.println("prod list: ok");
	}
}
