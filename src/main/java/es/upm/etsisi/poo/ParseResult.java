package es.upm.etsisi.poo;

public class ParseResult {
	public enum Code { 
		SUCCESS,
		INSUFICIENT_ARGUMENTS,
		INVALID_SUB_COMMAND,
		INVALID_NUMBER,
		INVALID_CATEGORY,
		INVALID_PRODUCT_FIELD,		
	}

	private Code code;
	private Command command;

	public ParseResult(Code code, Command command) {
		this.code = code;
		this.command = command;
	}

	public ParseResult(Code code) {
		this(code, null);
	}
	
	public ParseResult(Command command) {
		this(Code.SUCCESS, command);
	}

	public Code getCode() {
		return this.code;
	}
	
	public Command getCommand() {
		return this.command;
	}

	public String toString() {
		return String.format("{ code: %s, command: %s }", code, command);
	}
}
