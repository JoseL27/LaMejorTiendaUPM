package es.upm.etsisi.poo;

import java.io.File;
import java.util.Scanner;
import java.util.Locale;

public class App {
    private ArrayDataManager dataManager;
    private Ticket ticket;

    /**
     * Builder of the app
     */
    public App() {
        dataManager = new ArrayDataManager();
        ticket = new Ticket();
    }

    /**
     * This is the main function which runs the program.
     * It has and scanner for read the different commands, then that string is parsed, and checked if it is correct (ParseResult=Success).
     * Then the Command is executed. If any error is detected, a message error is sent indicating the error.
     */
    public void run(Scanner sc) {
		System.out.println("Welcome to the ticket module App.");
		System.out.println("Ticket module. Type 'help' to see commands.");
		
        String command;
        do {
            System.out.print("tUPM> ");
            command = sc.nextLine();
			System.out.println(command);
            if (!command.equals("exit")) {
                Parser pars = new Parser(command);
                Command parsedCommand = Command.tryParse(pars);
				if (parsedCommand != null) {
                    parsedCommand.tryExecute(ticket, dataManager);
				}
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
		Locale.setDefault(new Locale("en", "US"));
		
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

}
