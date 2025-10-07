package es.upm.etsisi.poo;

/**
 * Class regarding the result of an command Parse.
 * Consists of a code and a abstract Command object. 
 * @see Command class
 * @see Specific command classes under 'commands' directory
 */
public class ParseResult {

	/**
	 * A code summery of the parse operation.
	 * Either SUCCESS or a specific error value.
	 * @see Specific command classes under 'commands' directory
	 */
	public enum Code { 
		SUCCESS,
		INSUFICIENT_ARGUMENTS,
		TOO_MANY_ARGUMENTS,
		INVALID_COMMAND,
		INVALID_SUB_COMMAND,
		INVALID_NUMBER,
		INVALID_CATEGORY,
		INVALID_PRODUCT_FIELD,		
	}

	/**
	 * Code of the operation.
	 */
	private Code code;

	/**
	 * Abstract Command object if the parse succeded (is NULL if an the code is an error one). 
	 * @see Specific command classes under 'commands' directory
	 */
	private Command command;

	/**
	 * Basic constructor.
	 */
	public ParseResult(Code code, Command command) {
		this.code = code;
		this.command = command;
	}
	
	/**
	 * Constructor with only the code. Usefull when a parse error happens.
	 */
	public ParseResult(Code code) {
		this(code, null);
	}

	/**
	 * Constructor with only the command and a SUCCESS code. Usefull when the parse is a success
	 */
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

	/**
	 * Checks if this is equal to another object, that has to be a ParseResult, based on code and command
	 * @param obj Object to be compared to
	 * @return true if both objects are equal under this criteria, false in other
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || (obj.getClass() != this.getClass())) return false;
		
		ParseResult otherParseResult = (ParseResult)obj;
		return Utils.nullOrEquals(this.code, otherParseResult.code) 
			&& Utils.nullOrEquals(this.command, otherParseResult.command);
	}
}
