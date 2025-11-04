package es.upm.etsisi.poo;

import java.io.File;
import java.util.Scanner;
import java.util.Locale;
import es.upm.etsisi.poo.commands.ProductCommand;
import es.upm.etsisi.poo.commands.TicketCommand;

public class App {
    private Inventory inventory;
    private Ticket ticket;
    private UserManager userManager;

	private ProductCommand productCommand;
	private TicketCommand ticketCommand;

    /**
     * Basic constructor
     */
    public App() {
        inventory = new Inventory();
		userManager = new UserManager();
		productCommand = new ProductCommand();
		ticketCommand = new TicketCommand();
    }

    /**
     * This is the main function which runs the program.
     * It has and scanner for read the different commands, then that string is parsed, and checked if it is correct (ParseResult=Success).
     * Then the Command is executed. If any error is detected, a message error is sent indicating the error.
     */
    public void run(Scanner sc) {
		Locale.setDefault(new Locale("en", "US"));
		
		 System.out.println("Welcome to the ticket module App.");
		 System.out.println("Ticket module. Type 'help' to see commands.");

        String command;
        do {
            System.out.print("tUPM> ");
            command = sc.nextLine();
            if (!command.equals("exit")) {
                firstParse(command);
            } else {
				System.out.println("Closing application.");
				System.out.println("Goodbye!");
			}
        } while (!command.equals("exit"));
    }

    /**
     * Main class, the Scanner is initialized in different ways attending to the args length
     * if it is 0, the scanner will read from the console, in the rest of the cases the scanner will
     * try to read from a document
     *
     * @param args
     */
    public static void main(String[] args) {
        App app = new App();
        Scanner sc;
        if (args.length == 0) {
            sc = new Scanner(System.in);
            app.run(sc);
        } else {
            try {
                sc = new Scanner(new File(args[0]));
                app.run(sc);
            } catch (Exception e) {
                System.out.println("Error al leer");
            }
        }
    }

    private static String[] parser(String text) {
        int n = text.length();
        String[] commands = new String[n];
        for (int k = 0; k < n; k++) {
            commands[k] = "";
        }
        auxParser(text, commands);
        return cutter(commands);
    }

    private static void auxParser(String input, String[] commands) {
        final String text = input.trim().replaceAll(" +", " ");     //Elimina espacios
        int i = 0;
        boolean brackets = false;
        for (int j = 0; j < text.length(); j++) {
            char ch = text.charAt(j);
            if (ch == ' ' && !brackets) {
                i++;
                commands[i] = "";
            } else if (ch == '"') {
                brackets = !brackets;
            } else {
                commands[i] += ch;
            }
        }

    }

    private static String[] cutter(String[] commands) {
        int i = 0;
        while ((i < commands.length) && (commands[i] != "")) {
            i++;
        }
        String[] aux = new String[i];
        for (int j = 0; j < i; j++) {
            aux[j] = commands[j];
        }
        return aux;
    }

    private static void echo(String[] params) {
		if (!Utils.checkArgsCountWithPrint("echo", params.length, 2)) return;
		System.out.printf("echo \"%s\"\n", params[1]);
    }

    private static void help() {
        System.out.println("Commands:");
        System.out.println(" prod add <id> \"<name>\" <category> <price>");
        System.out.println(" prod list" );
		
        System.out.printf(" prod update <id> NAME|CATEGORY|PRICE <value>\n");
		
        System.out.println(" prod remove <id>");
        System.out.println(" ticket new");
        System.out.println(" ticket add <prodId> <quantity>");
        System.out.println(" ticket remove <prodId>");
        System.out.println(" ticket print" );
        System.out.println(" echo \"<texto>\"");
        System.out.println(" help");
        System.out.println(" exit");
		System.out.println();

		BaseProduct.Category[] categoryValues = BaseProduct.Category.values();
        System.out.printf("Categories: %s\n", Utils.arrayToString(categoryValues, ", "));
        System.out.print("Discounts if there are ≥2 units in the category: ");

		for (int i = 0; i < categoryValues.length; i++) { 
			System.out.printf("%s %.0f%%", categoryValues[i].name(), categoryValues[i].getDiscountPercent()*100);
			if (i  < categoryValues.length - 1) {
				System.out.print(", ");
			}
        }
		
        System.out.println(".");
    }

    private void firstParse(String input)
    {
        String[] params = parser(input);
		if (!Utils.checkMinArgsCountWithPrint("all", params.length, 1)) return;
		
		switch (params[0].toLowerCase()) {
		case "prod"   -> productCommand.eval(params, userManager, inventory);
		case "ticket" -> ticketCommand.eval(params, userManager, inventory);
		case "echo"   -> echo(params);
		case "help"   -> help();
		default       -> System.out.println("all: error: command not recognized. type help to see all commands.");
		}
    }

}
