package es.upm.etsisi.poo;


public class Command {

	public enum ParseResult {
		SUCCESS,
		INSUFICIENT_ARGUMENTS,
		INVALID_SUB_COMMAND,
		INVALID_NUMBER,		
	}

	public enum ExecuteResult {
	}

	public static ParseResult TryParse(String[] tokens, Command outCommand) {
		return null;
	}
	
	public ExecuteResult TryExecute() {
		assert false;
		return null;
	}
}
