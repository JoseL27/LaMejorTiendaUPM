package es.upm.etsisi.poo;
import java.lang.StringBuilder;

/**
 * Static Utilities class.
 */
public class Utils {

	/** true if both objects are equal under this criteria, false in other
	 * Creats a string of an array. Calls .toString on every element
	 * with a format '<elem0><delim><elem1><delim><elem2>...'
	 */
	public static <T> String arrayToString(T[] array, String delim) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i].toString());
			if (i != array.length - 1)
				sb.append(delim);
		}
		return sb.toString();
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

    public static void printInvalidEnum(String failedCommand, String enumName, String receivedValue, Enum[] possibleValues) {
        System.out.printf("%s: error: invalid %s '%s', expected one of: %s\n",
                          failedCommand, enumName, receivedValue, arrayToString(possibleValues, "|"));
    }

    public static void printInvalidDataType(String failedCommand, String expectedDataType, String receivedValue){
        System.out.printf("%s: error: expected %s, got '%s'\n", failedCommand, expectedDataType, receivedValue);
    }

    /**
     * public static Product.Category tryParseCategoryWithPrint(String prefix, String categoryString) {
     *         Product.Category category = Product.Category.fromLabel(categoryString);
     *         if (category == null) {
     *             printInvalidEnum(prefix, "category", categoryString, Product.Category.values());
     *         }
     *         return category;
     *     }
     */


    public static Integer tryParseIntWithPrint(String prefix, String intString) {
        Integer number = tryParseInt(intString);
        if (number == null) {
            System.out.printf("%s: error: expected an integer string, got '%s'\n", prefix, intString);
        }
        return number;
    }

	public static String removeLeadingZeros(String str) {
		int pos = 0;
		// Find the index of the first non-zero character
		while (pos < str.length() && str.charAt(pos) == '0') {
			pos++;
		}

		// Directly store the result
		String result = str.substring(pos);
		if (result.isEmpty())
			return "0";
		else
			return result;
	}
}
