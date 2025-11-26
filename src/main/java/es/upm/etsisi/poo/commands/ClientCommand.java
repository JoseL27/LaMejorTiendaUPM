package es.upm.etsisi.poo.commands;

import java.util.Arrays;

import es.upm.etsisi.poo.*;

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
	public void eval(String[] params, UserManager manager, Inventory dataManager) {
		if(!App.checkArgsCountWithPrint("client", params.length, 2, 6)) { return; }
		final String subcommand = params[1].toLowerCase();
		switch (subcommand) {
			case "add" -> evalAdd(params, manager);
			case "remove" -> evalRemove(params, manager);
			case "list" -> evalList(manager);
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
	public void evalAdd(String[] params, UserManager manager) {
		if (!App.checkArgsCountWithPrint("client add", params.length, 6)) {
			return;
		}
		
		String clientName  = params[2];
		String clientId    = params[3];
		String clientEmail = params[4];
		
		if (!Client.isValidId(clientId))  {
			System.out.printf("ticket new: error: invalid client id '%s' expected 8 digits followed by a letter\n", clientId);
			return;
		}

		Cashier creator = manager.findCashier(params[5]);
		
		Client addedClient = manager.addClient(clientId, clientName, clientEmail, creator);
		if (addedClient != null) {
			System.out.println(addedClient);
			System.out.println("client add: ok");
		} else {
			System.out.printf("client add: error: client with id %s could not be added\n", params[2]);
		}

	}

	public void evalRemove(String[] params, UserManager manager) {

		if (!App.checkArgsCountWithPrint("client remove", params.length, 3)) {
			return;
		}
		if (manager.removeClient(params[2])) {
			System.out.println("client remove: ok");
		} else {
			System.out.printf("client remove: client with id '%s' not found\n", params[2]);
		}
		
	}

	public void evalList(UserManager manager) {

		Client[] clients = manager.listClients();
		Arrays.sort(clients);
		if (clients.length == 0) {
			System.out.println("client list: no clients.");

		} else {
			System.out.println("Client:");
			for (Client client : clients) {
				System.out.println("  "+client.toString());
			}
			System.out.println("client list: ok");
		}
	}

}

