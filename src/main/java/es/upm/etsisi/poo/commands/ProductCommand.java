package es.upm.etsisi.poo.commands;
import es.upm.etsisi.poo.Command;
import es.upm.etsisi.poo.Product;
import es.upm.etsisi.poo.Utils;

public class ProductCommand extends Command {

	enum SubCommand {
		ADD,
		LIST,
		UPDATE,
		REMOVE;

		public static SubCommand TryParseName(String name) {
			return switch (name) {
			case "add"	  -> SubCommand.ADD;
			case "list"   -> SubCommand.LIST;
			case "update" -> SubCommand.UPDATE;
			case "remove" -> SubCommand.REMOVE;
			default       -> null;
			};
		}
	}

	private SubCommand subCommand;
	
	private int id;
	private String name;
	private Product.Category category;
	private double price;

	public static Command.ParseResult TryParse(String[] tokens, Command outCommand) {
		outCommand = null;
		
		if (tokens.length < 2)
			return Command.ParseResult.INSUFICIENT_ARGUMENTS;

		SubCommand subCommand = SubCommand.TryParseName(tokens[1]);
		if (subCommand == null) return Command.ParseResult.INVALID_SUB_COMMAND;

		return switch (subCommand) {
		case ADD	-> TryParseAdd(tokens);
		case LIST	-> TryParseList(tokens);
		case UPDATE -> TryParseUpdate(tokens);
		case REMOVE -> TryParseRemove(tokens);
		};
	}

	public static Command.ParseResult TryParseAdd(String[] tokens) {
		if (tokens.length < 6) return Command.ParseResult.INSUFICIENT_ARGUMENTS;

		// { "prod", "add", "1", "Libro POO", "BOOK", "25" },
		Integer amount = Integer.parseInt(tokens[2]);
		if (amount == null) return Command.ParseResult.INVALID_NUMBER;
		

		if (tokens.length > 6) {
			System.out.println("Excess arguments unimplemented");
			return null;
		}
		return null;
	}    

	public static Command.ParseResult TryParseList(String[] tokens) {
		if (tokens.length < 2) return Command.ParseResult.INSUFICIENT_ARGUMENTS;

		if (tokens.length > 2) {
			System.out.println("Excess arguments unimplemented");
			return null;
		}
		return null;
	}   

	public static Command.ParseResult TryParseUpdate(String[] tokens) {
		if (tokens.length < 5) return Command.ParseResult.INSUFICIENT_ARGUMENTS;

		if (tokens.length > 5) {
			System.out.println("Excess arguments unimplemented");
			return null;
		}
		return null;
	} 

	public static Command.ParseResult TryParseRemove(String[] tokens) {
		if (tokens.length < 3) return Command.ParseResult.INSUFICIENT_ARGUMENTS;

		if (tokens.length > 3) {
			System.out.println("Excess arguments unimplemented");
			return null;
		}
		return null;
	} 
	
	public Command.ExecuteResult TryExecute() {
		return null;
	}

	public static void main(String[] args) {
		String[][] tests = {
			{ "prod" },
			{ "prod", "add", "hey", "Libro POO", "BOOK", "25" },
			{ "prod", "add", "1", "Libro POO", "BOOK", "25" },
			{ "prod", "add", "2", "Camiseta talla:M UPM", "CLOTHES", "15" },
			{ "prod", "list" },
			{ "prod", "update", "1", "NAME", "Libro POO V2" },
			{ "prod", "update", "1", "PRICE", "30" },
			{ "prod", "add", "3", "Libro POO repetido Error", "BOOK", "25" },
			{ "prod", "remove", "3" },
		};

		for (int testIndex = 0; testIndex < tests.length; testIndex++) { 
			String[] testTokens = tests[testIndex];
			System.out.printf("TEST #%d\n", testIndex);
			System.out.printf("\tinput: %s\n", Utils.arrayToString(testTokens));
			
			Command outCommand = null;
			Command.ParseResult result = TryParse(testTokens, outCommand);
			System.out.printf("\tresult: %s\n", result);
		}
	}
}
