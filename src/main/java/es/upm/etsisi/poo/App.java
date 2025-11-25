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
	
    public App() {}

    /**
     * This is the main function which runs the program.
     * It has and scanner for read the different commands, then that string is parsed, and checked if it is correct (ParseResult=Success).
     * Then the Command is executed. If any error is detected, a message error is sent indicating the error.
     */
    public void run(Scanner sc) {
        run(sc, false);
    }

    public void run(Scanner sc, boolean echoCmd) {
        Locale.setDefault(new Locale("en", "US"));

        System.out.println("Welcome to the ticket module App.");
        System.out.println("Ticket module. Type 'help' to see commands.");

        String input;
        do {
            System.out.print("tUPM> ");
            input = sc.nextLine();
            if (echoCmd) {
                System.out.println(input);
            }
            if (!input.equals("exit")) {

				try { 
					firstParse(input);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				
            } else {
                System.out.println("Closing application.");
                System.out.println("Goodbye!");
            }
            System.out.println();
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

    private static void echo(String[] params) throws Exception {
        checkArgsCountWithPrint(params.length, 2);
        System.out.printf("\"%s\"\n", params[1]);
    }

    private static void help() {
        System.out.println("Commands:");
        System.out.println("  client add \"<nombre>\" <DNI> <email> <cashId>");
        System.out.println("  client remove <DNI>");
        System.out.println("  client list");

        System.out.println("  cash add [<id>] \"<nombre>\" <email>");
        System.out.println("  cash remove <id>");
        System.out.println("  cash list");
        System.out.println("  cash tickets <id>");

        System.out.println("  ticket new [<id>] <cashId> <userId>");
        System.out.println("  ticket add <ticketId> <cashId> <prodId> <amount> [--p<txt> --p<txt> ...]");
        System.out.println("  ticket remove <ticketId> <cashId> <prodId>");
        System.out.println("  ticket print <ticketId> <cashId>");
        System.out.println("  ticket list");

        System.out.println("  prod add [<id>] \"<name>\" <category> <price> [<maxPers>]");
        System.out.println("  prod update <id> NAME|CATEGORY|PRICE <value>");
        System.out.println("  prod addFood [<id>] \"<name>\" <price> <expiration: yyyy-MM-dd> <max_people>");
        System.out.println("  prod addMeeting [<id>] \"<name>\" <price> <expiration: yyyy-MM-dd> <max_people>");
        System.out.println("  prod list");
        System.out.println("  prod remove <id>");

        System.out.println("  help");
        System.out.println("  echo \"<text>\"");
        System.out.println("  exit");
        System.out.println();

      // Categories are static so could be written
        System.out.println("Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS");
        System.out.print("Discounts if there are ≥2 units in the category: ");

        BaseProduct.Category[] categoryValues = BaseProduct.Category.values();
        for (int i = 0; i < categoryValues.length; i++) {
            System.out.printf("%s %.0f%%", categoryValues[i].name(), categoryValues[i].getDiscountPercent() * 100);
            if (i < categoryValues.length - 1) {
                System.out.print(", ");
            }
        }

        System.out.println(".");
    }

    private void firstParse(String input) throws Exception {
		try {
			String[] params = parser(input);
			checkMinArgsCountWithPrint(params.length, 1);

			Command command = null;
			switch (params[0].toLowerCase()) {
            case "prod"   -> command = new ProductCommand();
            case "ticket" -> command = new TicketCommand();
            case "client" -> command = new ClientCommand();
            case "cash"   -> command = new CashCommand();
            case "echo"   -> echo(params);
            case "help"   -> help();
            default -> throw new Exception("command not recognized. type help to see all commands.");
			}

			if (command != null) {
				command.eval(params);
			}

		} catch (Exception e) {
			throw new Exception("error: "+e.getMessage());
		}
    }

    public static void checkMaxArgsCountWithPrint(int amount, int maxAmount) throws Exception {
        if (amount > maxAmount) {
			throw new Exception(String.format("too many arguments, expected maximum %d and got %d\n", maxAmount, amount));
        }
    }

    public static void checkMinArgsCountWithPrint(int amount, int minAmount) throws Exception {
        if (amount < minAmount) {
			throw new Exception(String.format("too few arguments, expected at least %d arguments and got %d\n", minAmount, amount));
        }
    }

    public static void checkArgsCountWithPrint(int amount, int minAmount, int maxAmount) throws Exception {
        checkMinArgsCountWithPrint(amount, minAmount);
		checkMaxArgsCountWithPrint(amount, maxAmount);
    }

    public static void checkArgsCountWithPrint(int amount, int expectedAmount) throws Exception {
        checkArgsCountWithPrint(amount, expectedAmount, expectedAmount);
    }

	public static int parsePositiveInt(String str) throws Exception {
		int value = Integer.parseInt(str);
		if (value < 0) {
			throw new Exception(String.format("expected positive number, got '%d'", value));
		}
		return value;
	}

	public static double parsePositiveDouble(String str) throws Exception {
		double value = Double.parseDouble(str);
		if (value < 0) {
			throw new Exception(String.format("expected positive number, got '%d'", value));
		}
		return value;
	}
}
