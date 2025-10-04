package es.upm.etsisi.poo;

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
    public void run() {
        System.out.println("Tienda UPM");
        Scanner sc = new Scanner(System.in);
        String command;
        do {
            command = sc.nextLine();
            Parser pars = new Parser(command);
            ParseResult parsingInfo=Command.tryParse(pars);
            if(parsingInfo.getCode()== ParseResult.Code.SUCCESS){
                Command.ExecuteResult executingInfo = parsingInfo.getCommand().tryExecute(ticket,dataManager);
                System.out.println(executingInfo);
            }else{
                System.out.println(parsingInfo);
            }

        } while (command.equals("exit"));

        // while (running) {
        // 	// Hacer cosas
        // 	string entrada = CapturarEntrada();

        // 	string Output;
        // 	Error error;
        // 	Command command = TryParseCommand(tokens, output, error);
        // 	if (success) {

        // 		string Output;
        // 		Error error;
        // 		ExecuteResult result = TryExecuteCommand(command, dataManager);
        // 		if (result.success) {
        // 			// log result.output

        // 		} else {
        // 			// error result.error
        // 		}

        // 	} else {

        // 		// error
        // 	}
        // }
    }

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

}
