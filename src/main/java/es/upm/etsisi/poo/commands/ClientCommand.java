package es.upm.etsisi.poo.commands;

import java.util.List;

import es.upm.etsisi.poo.*;
import es.upm.etsisi.poo.exceptions.DataException;
import es.upm.etsisi.poo.exceptions.FailedCommandException;
import es.upm.etsisi.poo.exceptions.MissingItemException;

import javax.xml.crypto.Data;

/**
 * Command to manage clients (add, remove, list)
 */
public class ClientCommand implements Command {
    
	/**
	 * Evaluates client-related commands based on the provided parameters.
	 * Supported subcommands:
	 *
	 *add<: Adds a new client using the specified parameters.</li>
	 * remove: Removes an existing client as specified.</li>
	 * list: Lists all registered clients.</li>
	 *
	 * If the subcommand is not recognized, an error message is printed.
	 *
	 * @param params      The command-line parameters, where the first element is the command and the second is the subcommand.
	 * @param manager     The {@link UserManager} instance for managing users.
	 * @param dataManager The {@link Inventory} instance for managing inventory data.
	 */
    
	@Override
        public void eval(String[] params) throws FailedCommandException{
        // Throw exception (invalid argument amount)
		if(!App.checkArgsCountWithPrint("client", params.length, 2, 6)) { return; }
		final String subcommand = params[1].toLowerCase();
		switch (subcommand) {
			case "add"    -> evalAdd(params);
			case "remove" -> evalRemove(params);
			case "list"   -> evalList();
			default -> System.err.println("Subcommand not recognised");
		}
	}
    
	/**
	 * Evaluates the "add client" command by validating the number of parameters and adding a new client
	 * to the system using the provided user information and the creator cashier.
	 *
	 * Format:
	 * client add "<nombre>" <DNI> <email> <cashId>
	 * 
	 * @param params   An array of command parameters. Expected to contain at least 6 elements:
	 *                 [0] - command name,
	 *                 [1] - subcommand,
	 *                 [2] - client's name,
	 *                 [3] - client's DNI,
	 *                 [4] - client's email,
	 *                 [5] - creator cashier's identifier.
	 * @param manager  The UserManager instance used to find the creator cashier and add the new client.
	 */
	public void evalAdd(String[] params) throws FailedCommandException{
        // Throw exception (invalid argument amount)
		if (!App.checkArgsCountWithPrint("client add", params.length, 6)) {
			return;
		}
		
		String clientName  = params[2];
		String clientId    = params[3];
		String clientEmail = params[4];
        String creatorId   = params[5];
		UserManager userManager = UserManager.getInstance();
        
        try {
            Client addedClient = userManager.addClient(clientId, clientName, clientEmail, creatorId);
            System.out.println(addedClient);
            System.out.println("client add: ok");
        }catch (DataException ex){
            throw new FailedCommandException("Cannot add the client: " + ex.getMessage());
        }
        
	}
    
	public void evalRemove(String[] params) throws FailedCommandException{
        
        // Throw exception (invalid argument amount)
		if (!App.checkArgsCountWithPrint("client remove", params.length, 3)) {
			return;
		}
        try {
            UserManager.getInstance().removeClient(params[2]);
            System.out.println("client remove: ok");
        }catch (MissingItemException ex) {
            throw new FailedCommandException("Cannot remove the client: " + ex.getMessage());
        }
		
	}
    
	public void evalList() {
		List<Client> clients = UserManager.getInstance().getClients();
		clients.sort((c1, c2) -> c1.getName().compareTo(c2.getName()));
		
		System.out.println("Client:");
		for (Client client : clients) {
			System.out.println("  "+client.toString());
		}
		System.out.println("client list: ok");
	}
}