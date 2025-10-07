package es.upm.etsisi.poo;

import java.io.File;
import java.util.Scanner;

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
        System.out.println("Tienda UPM");
        String command;
        do {
            System.out.print("tUPM> ");
            command = sc.nextLine();
            if (!command.equals("exit")) {
                Parser pars = new Parser(command);
                ParseResult parsingInfo = Command.tryParse(pars);
                if (parsingInfo.getCode() == ParseResult.Code.SUCCESS) {
                    Command.ExecuteResult executingInfo = parsingInfo.getCommand().tryExecute(ticket, dataManager);
                    System.out.println(executingInfo);
                } else {
                    System.out.println(parsingInfo);
                }
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

}
