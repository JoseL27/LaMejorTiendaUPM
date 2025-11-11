package es.upm.etsisi.poo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Locale;

import es.upm.etsisi.poo.commands.CashCommand;
import es.upm.etsisi.poo.commands.ClientCommand;
import es.upm.etsisi.poo.commands.ProductCommand;
import es.upm.etsisi.poo.commands.TicketCommand;

public class App {
    private Inventory inventory;
    private UserManager userManager;


    /**
     * Basic constructor
     */
    public App() {
        inventory = new Inventory();
        userManager = new UserManager();
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

        String input;
        do {
            System.out.print("tUPM> ");
            input = sc.nextLine();
            if (!input.equals("exit")) {
				firstParse(input);
			} else {
				System.out.println("Closing application.");
				System.out.println("Goodbye!");
			}
        } while (sc.hasNext() && !input.equals("exit"));
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

            String fileName = args[0];
            try {
                sc = new Scanner(new File(fileName));
                app.run(sc);
            } catch (FileNotFoundException e) {
                System.out.printf("error: file '%s' not found\n", fileName);
            } catch (Exception e) {
                throw e;
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
        System.out.println(" prod add [<id>] \"<name>\" <category> <price> [<maxPers>]");
        System.out.println(" prod list");

        System.out.println(" prod update <id> NAME|CATEGORY|PRICE <value>");

        System.out.println(" prod addFood [<id>] \"<name>\" <price> <expiration: yyyy-MM-dd> <max_people>");
        System.out.println(" prod addMeeting [<id>] \"<name>\" <price> <expiration: yyyy-MM-dd> <max_people>");
        System.out.println(" prod remove <id>");

        System.out.println(" ticket new [<id>] <cashId> <userId>");
        System.out.println(" ticket add <ticketId> <cashId> <prodId> <amount> [--p<txt> --p<txt> ...]");
        System.out.println(" ticket remove <ticketId> <cashId> <prodId>");
        System.out.println(" ticket print <ticketId> <cashId>");
        System.out.println(" ticket list");

        System.out.println(" client add \"<nombre>\" <DNI> <email> <cashId>");
        System.out.println(" client remove <DNI>");
        System.out.println(" client list");
        System.out.println(" cash add [<id>] \"<nombre>\" <email>");
        System.out.println(" cash remove <id>");
        System.out.println(" cash tickets <id>");
        System.out.println(" echo \"<texto>\"");
        System.out.println(" help");
        System.out.println(" exit");
        System.out.println();

        BaseProduct.Category[] categoryValues = BaseProduct.Category.values();
        System.out.printf("Categories: %s\n", Utils.arrayToString(categoryValues, ", "));
        System.out.print("Discounts if there are ≥2 units in the category: ");

        for (int i = 0; i < categoryValues.length; i++) {
            System.out.printf("%s %.0f%%", categoryValues[i].name(), categoryValues[i].getDiscountPercent() * 100);
            if (i < categoryValues.length - 1) {
                System.out.print(", ");
            }
        }

        System.out.println(".");
    }

    private void firstParse(String input) {
        String[] params = parser(input);
        if (!Utils.checkMinArgsCountWithPrint("all", params.length, 1)) return;

        Command command = null;
        switch (params[0].toLowerCase()) {
            case "prod" -> command = new ProductCommand();
            case "ticket" -> command = new TicketCommand();
            case "echo" -> echo(params);
            case "help" -> help();
            case "cash" -> command = new CashCommand();
            case "client" -> command = new ClientCommand();
            default -> System.out.println("all: error: command not recognized. type help to see all commands.");
        }

        if (command != null) {
            command.eval(params, userManager, inventory);
        }
    }
}
