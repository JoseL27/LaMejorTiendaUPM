package es.upm.etsisi.poo;
import java.lang.StringBuilder;

/**
 * Static Utilities class.
 */
public class Utils {

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

    public static void printInvalidDataType(String failedCommand, String expectedDataType, String receivedValue){
        System.out.printf("%s: error: expected %s, got '%s'\n", failedCommand, expectedDataType, receivedValue);
    }

}
