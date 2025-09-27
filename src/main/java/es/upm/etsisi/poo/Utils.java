package es.upm.etsisi.poo;
import java.lang.StringBuilder;

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
}
