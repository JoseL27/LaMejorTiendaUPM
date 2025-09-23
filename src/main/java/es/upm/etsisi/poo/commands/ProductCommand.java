package es.upm.etsisi.poo.commands;
import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.Product;

public class ProductCommand extends Command {

	enum SubCommand {
	}

	private SubCommand subCommand;
	
	private int id;
	private String name;
	private Product.Category category;
	private double price;

	public static Command.ParseResult TryParse(String[] tokens, Command outCommand) {
		return null;
	}
	
	public Command.ExecuteResult TryExecute() {
		return null;
	}
}
