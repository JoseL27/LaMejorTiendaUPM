package es.upm.etsisi.poo;
import java.lang.StringBuilder;
import java.util.function.Predicate;

public class Utils {
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

	public static Integer tryParseInt(String s) {
		Integer value = null;
		try {
			value = Integer.parseInt(s);
		} catch (Exception e) {
		} finally {
			return value;
		}
	}
}
