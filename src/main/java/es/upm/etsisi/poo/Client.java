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
