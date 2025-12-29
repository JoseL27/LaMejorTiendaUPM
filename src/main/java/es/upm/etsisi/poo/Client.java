package es.upm.etsisi.poo;

import es.upm.etsisi.poo.exceptions.DuplicateItemException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Client extends User implements Comparable<Client> {

    public enum IdType {DNI, NIF}

    private final Cashier managedBy;
    private final List<Integer> ticketIds;
    private final IdType idType;

    public Client(String id, String name, String email, Cashier cashier, IdType idType) throws IllegalArgumentException {
        super(id, name, email);
        this.managedBy = cashier;
        this.idType = idType;

        this.ticketIds = new ArrayList<>();

        if (idType == null)
            throw new IllegalArgumentException("Invalid client id: " + id + ", please enter a valid NIF/NIE");
        else if (cashier == null) throw new IllegalArgumentException("Client needs an assigned cashier to be created");
    }

    public void addTicket(int ticketId) throws DuplicateItemException {
        if (ticketIds.contains(ticketId)) throw new DuplicateItemException("Client already posseses this ticket");

        ticketIds.add(ticketId);
    }


    // Control DNI/NIE (mod 23)
    private static final String MOD23_LUT = "TRWAGMYFPDXBNJZSQVHLCKE";

    // NIF (persona jurídica / tipo CIF)
    private static final String NIF_CONTROL_LETTERS = "JABCDEFGHI"; // 0->J, 1->A, ... 9->I
    private static final String NIF_NUMERIC_ONLY = "ABEH";
    private static final String NIF_LETTER_ONLY = "PQSW";
    private static final String NIF_EITHER = "CDFGJNRUV";


    /**
     * Si es DNI o NIE válido => devuelve DNI. Si no, si es NIF jurídica válido => NIF. Si no => null.
     */
    public static IdType isValidId(String input) {
        String s = normalize(input);
        if (s == null) return null;

        if (isValidDni(s)) return IdType.DNI;
        if (isValidNif(s)) return IdType.NIF;

        return null;
    }

    // ------------------ DNI (incluye NIE tratado como DNI) ------------------

    /**
     * Valida:
     * - DNI: 8 dígitos + letra
     * - NIE: X/Y/Z + 7 dígitos + letra (se convierte a número y se aplica el mismo control que DNI)
     * Si cualquiera es válido, se considera "DNI" a efectos de tipo.
     */
    private static boolean isValidDni(String s) {
        if (s.length() != 9) return false;
        int number = 0;
        char first = s.charAt(0);

        if (Character.isLetter(first)) { //NIE
            int prefixDigit;
            switch (first) {
                case 'X' -> prefixDigit = 0;
                case 'Y' -> prefixDigit = 1;
                case 'Z' -> prefixDigit = 2;
                default -> {
                    return false;
                }
            }
            s = prefixDigit + s.substring(1);
        }

        for (int i = 0; i < s.length()-1; i++) {
            char c = s.charAt(i);
            int d = Character.digit(c, 10); // devuelve 0..9 o -1 si no es dígito en base 10
            if (d < 0) return false;
            number = number * 10 + d;
        }

        char letter = s.charAt(8);
        if (!Character.isLetter(letter)) return false;

        char expected = MOD23_LUT.charAt(number % 23);
        return letter == expected;
    }

    // ------------------ NIF (jurídica / CIF) ------------------

    private static boolean isValidNif(String s) {
        if (s.length() != 9) return false;

        char type = s.charAt(0);
        if (!Character.isLetter(type)) return false;

        // Excluir K/L/M (suelen ser NIF de persona física especial)
        if (type == 'K' || type == 'L' || type == 'M') return false;

        String digits = s.substring(1, 8);
        if (App.tryParseInt(digits) == null) return false;

        char control = s.charAt(8);
        if (!Character.isLetterOrDigit(control)) return false;

        int expectedDigit = computeNifControlDigit(digits);
        char expectedLetter = NIF_CONTROL_LETTERS.charAt(expectedDigit);

        return matchesNifControl(type, control, expectedDigit, expectedLetter);
    }

    private static int computeNifControlDigit(String sevenDigits) {
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

    private static boolean matchesNifControl(char type, char control, int expectedDigit, char expectedLetter) {
        boolean isDigit = Character.isDigit(control);
        boolean isLetter = Character.isLetter(control);

        if (NIF_NUMERIC_ONLY.indexOf(type) >= 0) {
            return isDigit && (control - '0') == expectedDigit;
        }
        if (NIF_LETTER_ONLY.indexOf(type) >= 0) {
            return isLetter && control == expectedLetter;
        }
        if (NIF_EITHER.indexOf(type) >= 0) {
            return (isDigit && (control - '0') == expectedDigit) ||
                    (isLetter && control == expectedLetter);
        }
        return false;
    }

    private static String normalize(String input) {
        if (input == null) return null;
        String s = input.trim();
        if (s.isEmpty()) return null;
        return s.toUpperCase(Locale.ROOT).replace(" ", "").replace("-", "");
        //Locale ROOT es para evitar alfabetos extraños
    }


    @Override
    public int compareTo(Client c) {
        return this.getName().compareTo(c.getName());
    }

    @Override
    public String toString() {
        return String.format("Client{identifier='%s', name='%s', email='%s', cash=%s}",
                this.getId(), this.getName(), this.getEmail(), this.managedBy.getId());
    }
}
