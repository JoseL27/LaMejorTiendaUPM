package es.upm.etsisi.poo;

public class App 
{
	private ArrayDataManager dataManager;
	private boolean running;

	public App() {
		dataManager = new ArrayDataManager();
		running = false;
	}
	
    public void run()
    {
        System.out.println( "Tienda UPM" );
		running = true;

		// while (running) {
		// 	// Hacer cosas
		// 	string entrada = CapturarEntrada();
		// 	string[] tokens = Tokenize(entrada);

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

	public String[] tokenizeInput(String input) {
		return null;
	}

    public static void main(String[] args) {
		App app = new App();
		app.run();
	}
	
}
