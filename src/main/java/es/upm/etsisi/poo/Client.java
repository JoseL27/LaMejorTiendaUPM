package es.upm.etsisi.poo;

import es.upm.etsisi.poo.exceptions.DuplicateItemException;

import java.util.ArrayList;
import java.util.List;

public class Client extends User implements Comparable<Client> {

	private final Cashier managedBy;
	private final List<Integer> ticketIds;

	public Client(String id, String name, String email, Cashier cashier) throws IllegalArgumentException {
		super(id, name, email);
		this.managedBy = cashier;
		this.ticketIds = new ArrayList<>();

        if (!isValidDNI(id)) throw new IllegalArgumentException("Invalid client id: " + id + ", please enter a valid NIF/NIE");
        else if (cashier == null) throw new IllegalArgumentException("Client needs an assigned cashier to be created");
	}

	public void addTicket(int ticketId) throws DuplicateItemException{
		if (ticketIds.contains(ticketId)) throw new DuplicateItemException("Client already posseses this ticket");

        ticketIds.add(ticketId);
	}

	public static boolean isValidDNI(String id) {
		// See: https://www.interior.gob.es/opencms/en/servicios-al-ciudadano/tramites-y-gestiones/dni/calculo-del-digito-de-control-del-nif-nie
		// Basic sanity treatment to input
		if (id == null || id.length() != 9)
			return false;

		// Check if all characters are uppercase
		boolean allUppercase = true;
		int charIdx = 0;
		while (charIdx < id.length() && allUppercase) {
			char c = id.charAt(charIdx);
			allUppercase = Character.isUpperCase(c) || Character.isDigit(c);
			charIdx++;
		}
		if (!allUppercase)
			return false;

		// The first character should be either a number, or letters: X, Y, Z
		char firstChar = id.charAt(0);
		boolean isFirstCharADigitOrXYZ =
				Character.isDigit(firstChar) ||
				firstChar == 'X' ||
				firstChar == 'Y' ||
				firstChar == 'Z';
		if (!isFirstCharADigitOrXYZ)
			return false;

		// Extract relevant parts
		String nifNumberString = id.substring(0, 8);
		char nifChecksumLetter = id.charAt(8);

		// If nifNumberString is from a NIE, the first character should be X, Y and Z, each letter has its numeric
		// correspondence: X --> 0, Y --> 1, Z --> 2
        nifNumberString = switch (nifNumberString.charAt(0)) {
            case 'X' -> '0' + nifNumberString.substring(1, 8);
            case 'Y' -> '1' + nifNumberString.substring(1, 8);
            case 'Z' -> '2' + nifNumberString.substring(1, 8);
            default -> nifNumberString;
        };

		// Strip left zeroes
		nifNumberString = nifNumberString.replaceAll("^0+", "");
		if (nifNumberString.isEmpty()) // Everything is 0, keep at least one 0 to make tryParseInt return expected results
			nifNumberString = "0";

		Integer nifNumber = App.tryParseInt(nifNumberString);
		if (nifNumber == null) // If nifNumberString contains any letter this should fail
			return false;

		char[] CHECKSUM_MOD23_TO_LETTER_LUT = {
			'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 'B',
			'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'
		};

		char expectedChecksumLetter = CHECKSUM_MOD23_TO_LETTER_LUT[nifNumber % 23];

		return expectedChecksumLetter == nifChecksumLetter;
	}



        public enum IdType { DNI, NIF }

        // Control DNI/NIE (mod 23)
        private static final String MOD23_LUT = "TRWAGMYFPDXBNJZSQVHLCKE";

        // NIF (persona jurídica / tipo CIF)
        private static final String NIF_CONTROL_LETTERS = "JABCDEFGHI"; // 0->J, 1->A, ... 9->I
        private static final String NIF_NUMERIC_ONLY = "ABEH";
        private static final String NIF_LETTER_ONLY  = "PQSW";
        private static final String NIF_EITHER       = "CDFGJNRUV";


        /** Si es DNI o NIE válido => devuelve DNI. Si no, si es NIF jurídica válido => NIF. Si no => null. */
        public static IdType detectValidType(String input) {
            String s = normalize(input);
            if (s == null) return null;

            if (isValidDniOrNieAsDni(s)) return IdType.DNI;
            if (isValidNifJuridica(s))   return IdType.NIF;

            return null;
        }

        // ------------------ DNI (incluye NIE tratado como DNI) ------------------

        /**
         * Valida:
         * - DNI: 8 dígitos + letra
         * - NIE: X/Y/Z + 7 dígitos + letra (se convierte a número y se aplica el mismo control que DNI)
         * Si cualquiera es válido, se considera "DNI" a efectos de tipo.
         */
        private static boolean isValidDniOrNieAsDni(String s) {
            if (s.length() != 9) return false;

            int number = 0;

            char first = s.charAt(0);

            if (Character.isDigit(first)) {
                // DNI: 8 dígitos
                for (int i = 0; i < 8; i++) {
                    char c = s.charAt(i);
                    if (!Character.isDigit(c)) return false;
                    number = number * 10 + (c - '0');
                }
            } else {
                // NIE: X/Y/Z + 7 dígitos
                int prefixDigit;
                if (first == 'X') prefixDigit = 0;
                else if (first == 'Y') prefixDigit = 1;
                else if (first == 'Z') prefixDigit = 2;
                else return false;

                number = prefixDigit;
                for (int i = 1; i <= 7; i++) {
                    char c = s.charAt(i);
                    if (!Character.isDigit(c)) return false;
                    number = number * 10 + (c - '0');
                }
            }

            char letter = s.charAt(8);
            if (!Character.isLetter(letter)) return false;

            char expected = MOD23_LUT.charAt(number % 23);
            return letter == expected;
        }

        // ------------------ NIF (jurídica / CIF) ------------------

        private static boolean isValidNifJuridica(String s) {
            if (s.length() != 9) return false;

            char type = s.charAt(0);
            if (!Character.isLetter(type)) return false;

            // Excluir K/L/M (suelen ser NIF de persona física especial)
            if (type == 'K' || type == 'L' || type == 'M') return false;

            String digits = s.substring(1, 8);
            if (!allDigits(digits)) return false;

            char control = s.charAt(8);
            if (!Character.isLetterOrDigit(control)) return false;

            int expectedDigit = computeNifControlDigit(digits);
            char expectedLetter = NIF_CONTROL_LETTERS.charAt(expectedDigit);

            return matchesNifControl(type, control, expectedDigit, expectedLetter);
        }

        private static int computeNifControlDigit(String sevenDigits) {
            int sumEven = 0; // posiciones 2,4,6
            int sumOdd  = 0; // posiciones 1,3,5,7 (doble y suma de cifras)

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

        // ------------------ helpers ------------------

        private static boolean allDigits(String str) {
            for (int i = 0; i < str.length(); i++) {
                if (!Character.isDigit(str.charAt(i))) return false;
            }
            return true;
        }

        private static String normalize(String input) {
            if (input == null) return null;
            String s = input.trim();
            if (s.isEmpty()) return null;
            return s.toUpperCase(Locale.ROOT).replace(" ", "").replace("-", "");
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
