package es.upm.etsisi.poo.commands;

import java.util.Arrays;

import es.upm.etsisi.poo.Cashier;
import es.upm.etsisi.poo.Client;
import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.Inventory;
import es.upm.etsisi.poo.UserManager;
import es.upm.etsisi.poo.Utils;

public class ClientCommand implements Command {


	@Override
	public void eval(String[] params, UserManager manager, Inventory dataManager) {
		if(!Utils.checkArgsCountWithPrint("client", params.length, 2, 6)) { return; }
		final String subcommand = params[1].toLowerCase();
		switch (subcommand) {
			case "add" -> evalAdd(params, manager);
			case "remove" -> evalRemove(params, manager);
			case "list" -> evalList(manager);
			default -> System.err.println("Subcommand not recognised");
		}
	}


	public void evalAdd(String[] params, UserManager manager) {

		if (!Utils.checkArgsCountWithPrint("client add", params.length, 6)) {
			return;
		}
		else {
			final Cashier creator = manager.findCashier(params[5]);
			manager.addClient(params[2], params[3], params[4], creator);
		}

	}


	public void evalRemove(String[] params, UserManager manager) {

		if (!Utils.checkArgsCountWithPrint("client remove", params.length, 3)) {
			return;
		}
		else {
			manager.removeClient(params[2]);
		}

	}

	public void evalList(UserManager manager) { 

		Client[] clients = manager.listClients();
		Arrays.sort(clients);
		for (Client client : clients) {
			System.out.println(client.toString());
		}

	}

}

