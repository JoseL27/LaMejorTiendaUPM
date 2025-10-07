package es.upm.etsisi.poo;
import java.lang.StringBuilder;
import java.util.function.Predicate;

/**
 * Static Utilities class.
 */
public class Utils {

	/** true if both objects are equal under this criteria, false in other
	 * Creats a string of an array. Calls .toString on every element
	 * with a format '[elem0, elem1, ...]'
	 */
	public static <T> String arrayToString(T[] array) {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i].toString());
			if (i != array.length - 1)
				sb.append(", ");
		}

		sb.append(" ]");
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

	public static boolean nullOrEquals(Object a, Object b) {
		return (a == b) || (a != null && a.equals(b));
	}
}
