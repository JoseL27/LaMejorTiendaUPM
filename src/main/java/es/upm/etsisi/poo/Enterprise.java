package es.upm.etsisi.poo;

import java.util.Locale;

public class Enterprise extends User {
    public Enterprise(String id, String name) throws IllegalArgumentException {
        super(id, name);
        if (!isValidId(id)) throw new IllegalArgumentException("Invalid cashier id: " + id);
    }

    public static boolean isValidId(String id) {
        boolean result = false;
        return result;
    }


    private static final String CONTROL_LETTERS = "JABCDEFGHI"; // 0->J, 1->A, ... 9->I

    private static final String NUMERIC_ONLY = "ABEH";       // último char: dígito
    private static final String LETTER_ONLY = "PQSW";       // último char: letra
    private static final String EITHER = "CDFGJNRUV";  // último char: letra o dígito

    /**
     * Punto de entrada: valida NIF de persona jurídica (tipo CIF).
     */
    public static boolean isValidNIF(String nif) {
        if (nif == null) return false;

        String s = normalize(nif); //Comprobamos tamaño y normalizamos entrada
        if (s.length() != 9) return false;

        char type = s.charAt(0); //Comprobamos que empiece en mayuscula
        if (!(type >= 'A' && type <= 'Z')) return false;

        // Excluir K/L/M (normalmente usados en NIF de persona física especial)
        if (type == 'K' || type == 'L' || type == 'M') return false;

        String digits = s.substring(1, 8); //Nos quedamos con la parte numerica y comprobamos que sean todos dígitos
        if (App.tryParseInt(digits) == null) return false;

        char control = s.charAt(8);
        if (!Character.isLetterOrDigit(control)) return false;

        int expectedControlDigit = computeControlDigit(digits);
        char expectedControlLetter = CONTROL_LETTERS.charAt(expectedControlDigit);

        return matchesControl(type, control, expectedControlDigit, expectedControlLetter);
    }

    /**
     * Normaliza: quita espacios/guiones y pasa a mayúsculas.
     */
    private static String normalize(String input) {
        String s = input.trim().toUpperCase();
        return s.replace(" ", "").replace("-", "");
    }

    /**
     * Calcula el dígito de control (0..9) con el algoritmo estándar:
     * suma pares + (impares*2 sumando cifras) y ajuste módulo 10.
     */
    private static int computeControlDigit(String sevenDigits) {
        int sumEven = 0; // posiciones 2,4,6
        int sumOdd = 0; // posiciones 1,3,5,7 (doble y suma de cifras)

        for (int i = 0; i < 7; i++) {
            int d = sevenDigits.charAt(i) - '0';
            int pos = i + 1;

            if (pos % 2 == 0) {
                sumEven += d;
            } else {
                int prod = d * 2;
                sumOdd += (prod / 10) + (prod % 10);
            }
        }

        int total = sumEven + sumOdd;
        return (10 - (total % 10)) % 10;
    }

    /**
     * Valida el último carácter (control) según el tipo de entidad.
     */
    private static boolean matchesControl(char type, char control, int expectedDigit, char expectedLetter) {
        boolean isDigit = Character.isDigit(control);
        boolean isLetter = Character.isLetter(control);

        if (NUMERIC_ONLY.indexOf(type) >= 0) {
            return isDigit && (control - '0') == expectedDigit;
        }
        if (LETTER_ONLY.indexOf(type) >= 0) {
            return isLetter && control == expectedLetter;
        }
        if (EITHER.indexOf(type) >= 0) {
            return (isDigit && (control - '0') == expectedDigit) ||
                    (isLetter && control == expectedLetter);
        }
        return false; // tipo desconocido
    }
}
