package es.upm.etsisi.poo.commands;

import es.upm.etsisi.poo.*;
import es.upm.etsisi.poo.exceptions.DataException;
import es.upm.etsisi.poo.exceptions.FailedCommandException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * ProductCommand class that parses a stream of tokens into a specific ProductCommand,
 * being one of the following formats:
 * - prod add <id> "<nombre>" <categoria> <precio> (agrega un producto con nuevo id)
 * - prod list (lista productos actuales)
 * - prod update <id> campo valor (campos: nombre|categoria|precio)
 * - prod remove <id>
 *
 * @see Command
 */
public class ProductCommand implements Command {
	@Override
        public void eval(String[] args) throws FailedCommandException {
		if (!App.checkArgsCountWithPrint("prod", args.length, 2, 7)) return;
        
		switch (args[1].toLowerCase()) {
            case "addfood", "addmeeting" -> evalAddTimed(args);
            case "add"	  -> evalAdd(args);
            case "list"	  -> evalList(args);
            case "update" -> evalUpdate(args);
            case "remove" -> evalRemove(args);
            default       -> System.out.println("prod: invalid sub command");
		}
	}
    
    
	/**
	 * Parses the 'add' variation of the 'product' command.
	 * FORMAT: prod add [<id>] "<name>" <category> <price> [<maxPers>]
	 * FORMAT: prod add <expiration: yyyy-MM-dd > <category>
	 */	
    public void evalAdd(String[] params) throws FailedCommandException{
        if (params.length == 4) {
            evalAddService(params);
        } else {
            evalAddBase(params);
        }
    }
    
    // FORMAT: prod add <expiration: yyyy-MM-dd > <category>
    public void evalAddService(String[] params) throws FailedCommandException{
        if (!App.checkArgsCountWithPrint("prod add", params.length, 4)) return;
        
        String dateStr = params[2];
        String categoryStr = params[3];
		
        try {
			LocalDateTime expirationDate = LocalDate.parse(dateStr).atStartOfDay();
            ServiceProduct service = Inventory.getInstance().createServiceProduct(categoryStr, expirationDate);
			
			System.out.println(service);
			System.out.println("prod add: ok");
			
        } catch (DateTimeParseException e) {
            throw new FailedCommandException("Failed to parse date from text "+e.getParsedString());
        } catch (DataException e) {
            throw new FailedCommandException("Failed to service: " + e.getMessage());
        }
    }
    
    
    // FORMAT: prod add [<id>] "<name>" <category> <price> [<maxPers>]
	public void evalAddBase(String[] params) throws FailedCommandException{
		// Parse
		if (!App.checkArgsCountWithPrint("prod add", params.length, 5, 7)) return;
		
		// NOTE(enrique): Index to consume while parsing
		int parseIndex = 2; 
        
        try {
			// NOTE(enrique): Check the 3rd argument:
			//  - If parsing the int succeeds then we assume its the ID and the next token is the name string (advance parsing)
			//  - If it fails then we assume its the name and no ID was supplied (dont advance parsing), next token is the category
			String productIdOrName = params[parseIndex++];
			String productName = null;
			int productId;
			try {
				productId = Integer.parseInt(productIdOrName);
				productName = params[parseIndex++];
			}catch(NumberFormatException ex) {
				productName = productIdOrName;
				productId = Inventory.getInstance().generateUniqueProductId();
			}
			
			String productCategoryString = params[parseIndex++];
			String productPriceString = params[parseIndex++];
			
			int productPrice = Integer.parseInt(productPriceString);
			BaseProduct.Category productCategory = BaseProduct.Category.valueOf(productCategoryString.toUpperCase());
            
            boolean specifiedMaxPers = false;
            int productMaxPers = productCategory.maxPersonalizations;;
            if (parseIndex < params.length) {
                String productMaxPersString = params[parseIndex++];
                
                productMaxPers = Integer.parseInt(productMaxPersString);
                specifiedMaxPers = true;
            }
            
            // Execute
            Inventory inventory = Inventory.getInstance();
            
            BaseProduct createdProduct = inventory.createBaseProduct(productId, productName, productCategoryString,
                                                                     productPrice, productMaxPers, specifiedMaxPers);
            System.out.println(createdProduct);
            System.out.println("prod add: ok");
        }catch (IllegalArgumentException ex) {
            throw new FailedCommandException("Unable to add product, invalid integer value in arguments");
        }catch(DataException ex) {
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
			throw new FailedCommandException("prod " + params[1] + ": invalid number of parameters, got " + params.length + ", expected 6 or 7");
		}
        
        // Parsing
        int parseIndex = 1;
        Inventory inventory = Inventory.getInstance();

		String typeParam = params[parseIndex++];
        String typeString = typeParam.replaceAll("add", "").toUpperCase();
        
        try {
			
			int id;
			String name;
			
            if (params.length == 6){
				name = params[parseIndex++];
				id = inventory.generateUniqueProductId();
			}else{
				id = Integer.parseInt(params[parseIndex++]);
				name = params[parseIndex++];
			}
			
			
			double price = Double.parseDouble(params[parseIndex++]);
			LocalDateTime expirationDate = LocalDate.parse(params[parseIndex++]).atStartOfDay();
			int maxPeople = Integer.parseInt(params[parseIndex]);
			
			TimedProduct addedProduct = inventory.createTimedProduct(id, name, price, maxPeople, typeString, expirationDate);
			
			// Execution
			TimedProduct.TimedType type = addedProduct.getType();
			LocalDateTime prepDoneTime = App.now().plusHours(type.hoursForPreparing);
			if (prepDoneTime.isAfter(expirationDate)) {
				throw new FailedCommandException(String.format("prod add: error: you need at least %d hours to prepare for this %s\n",
                                                               type.hoursForPreparing, type.toString().toLowerCase()));
			}
            
			System.out.println(addedProduct);
			System.out.printf("prod %s: ok\n", typeParam);
		}catch (NumberFormatException ex) {
		    throw new FailedCommandException("Unable to add product, invalid argument: "  + params[parseIndex] + " for argument number " + parseIndex);
		}catch (DateTimeParseException ex){
            throw new FailedCommandException("Unable to add product, invalid date format.");
        }catch (DataException | IllegalArgumentException ex){
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
		Inventory inventory = Inventory.getInstance();
		Product updatedProduct = null;
		String fieldName = params[3].toLowerCase();
		
		try {
			productId = Integer.parseInt(params[2]);
			
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
					productPrice = Integer.parseInt(params[4]);
					updatedProduct = inventory.updateProductPrice(productId, productPrice);
				}
				default -> {
					throw new FailedCommandException("prod update: invalid field");
				}
			}
			System.out.println(updatedProduct);
			System.out.println("prod update: ok");
			System.out.println(); // NOTE(erb): this extra line is on purpose since it appears ONLY ON THIS COMMAND in the e3 expected output
		}catch (DataException ex){
			throw new FailedCommandException("Unable to update product: " + ex.getMessage());
		}catch (NumberFormatException ex){
			throw new FailedCommandException("Unable to update product: invalid integer value in arguments");
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
		InventoryItem item = null; 
		
		try {
			item = inventory.deleteItem(productId);
			
			
			// NOTE(erb): this only removes 'Products' from tickets and NOT service products
			// TODO(erb): update this to remove service products
			if (item instanceof Product) {
				Product productToRemove = (Product)item;
				
				// Retrieves the active tickets from the system (Products should not be removed from closed tickets)
				List<Ticket> tickets = UserManager.getInstance().getAllTickets();
				
				for (Ticket ticket: tickets) {
					if (ticket.isOpen()) {
						ticket.removeItem(productToRemove.getId());
					}
				}
			}
			
			System.out.println(item);
			System.out.println("prod remove: ok");
		} catch (DataException e) {
			throw new FailedCommandException("Unable to remove product " + e.getMessage());
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
		
		ArrayList<InventoryItem> items = new ArrayList(Inventory.getInstance().getItems());
		items.sort((i1, i2) -> i1.getId() - i2.getId());
		
		System.out.println("Catalog:");
		for (InventoryItem i : items) {
			System.out.println("  "+i.toString());
		}
		System.out.println("prod list: ok");
	}
}