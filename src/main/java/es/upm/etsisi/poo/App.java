package es.upm.etsisi.poo;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.Clock;
import java.util.Scanner;
import java.util.Locale;

import es.upm.etsisi.poo.commands.CashCommand;
import es.upm.etsisi.poo.commands.ClientCommand;
import es.upm.etsisi.poo.commands.ProductCommand;
import es.upm.etsisi.poo.commands.TicketCommand;
import es.upm.etsisi.poo.exceptions.FailedCommandException;

public class App {
	
	private static Clock clock = Clock.systemDefaultZone();
	
	public App() {
    }
	
	public static LocalDateTime now() {
		return LocalDateTime.now(clock);
	}
	
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
                firstParse(input);
            } else {
                System.out.println("Closing application.");
                System.out.println("Goodbye!");
            }
            System.out.println();
        } while (!input.equals("exit"));
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
                app.run(sc, true);
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
        if (!checkArgsCountWithPrint("echo", params.length, 2)) return;
        System.out.printf("\"%s\" \n", params[1]);
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
        System.out.println("  ticket add <ticketId> <cashId> <prodId> <amount> [--p<txt> --p<txt>] ");
        System.out.println("  ticket remove <ticketId> <cashId> <prodId> ");
        System.out.println("  ticket print <ticketId> <cashId> ");
        System.out.println("  ticket list");
		
        System.out.println("  prod add [<id>] \"<name>\" <category> <price> [<maxPers>]");
        System.out.println("  prod update <id> NAME|CATEGORY|PRICE <value>");
        System.out.println("  prod addFood [<id>] \"<name>\" <price> <expiration:yyyy-MM-dd> <max_people>");
        System.out.println("  prod addMeeting [<id>] \"<name>\" <price> <expiration:yyyy-MM-dd> <max_people>");
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
	
    private void firstParse(String input) {
        String[] params = parser(input);
        if (!checkMinArgsCountWithPrint("all", params.length, 1)) return;
		
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
            try {
                command.eval(params);
            }catch (FailedCommandException ex){
                System.out.println(ex.getMessage());
            }catch (Exception ex){
                System.out.println("Unexpected error: " + ex.getMessage());
            }
        }
    }
	
    public static void printInvalidDataType(String failedCommand, String expectedDataType, String receivedValue){
        System.out.printf("%s: error: expected %s, got '%s'\n", failedCommand, expectedDataType, receivedValue);
    }
	
    /**
     * Parses an integer from a string, basically supresses the InvalidArgumentException.
     * @return An integer with the underlying int value or NULL if the parse failed.
     */
    public static Integer tryParseInt(String s) {
        Integer value = null;
        try {
            value = Integer.parseInt(s);
        } catch (Exception e) {
        } finally {
            return value;
        }
    }
	
    public static boolean checkMaxArgsCountWithPrint(String prefix, int amount, int maxAmount) {
        if (amount > maxAmount) {
            System.out.printf("%s: too many arguments, expected maximum %d and got %d\n", prefix, maxAmount, amount);
            return false;
        }
        return true;
    }
	
    public static boolean checkMinArgsCountWithPrint(String prefix, int amount, int minAmount) {
        if (amount < minAmount) {
            System.out.printf("%s: too few arguments, expected at least %d arguments and got %d\n", prefix, minAmount, amount);
            return false;
        }
        return true;
    }
	
    public static boolean checkArgsCountWithPrint(String prefix, int amount, int minAmount, int maxAmount) {
        return checkMinArgsCountWithPrint(prefix, amount, minAmount) && checkMaxArgsCountWithPrint(prefix, amount, maxAmount);
    }
	
    public static boolean checkArgsCountWithPrint(String prefix, int amount, int expectedAmount) {
        return checkArgsCountWithPrint(prefix, amount, expectedAmount, expectedAmount);
    }
}
