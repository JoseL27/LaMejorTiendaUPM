package es.upm.etsisi.poo;

import java.util.Arrays;

/**
 * This class function is to parse the command received in different position of an array.
 */
public class Parser {
    private String[] commands;
    private String text;

    /**
     * Builder that initializes commands, with the length of the imput.
     * It makes some modifications in order to adapt the object
     * @param command Input of the terminal
     */
    public Parser(String command) {
        int n = command.length();
        commands = new String[n];
        for (int k = 0; k < n; k++) {
            commands[k] = "";
        }
        auxParser(command);
        cutter();
    }

    /**
     * This private function is called by the builder, it deletes all the extra spaces in command.
     * Then for each character is added to a position of the array which grows with spaces. If " is found
     * all the following characters will be in the same position no matter if there are spaces until another " is found
     * @param command Input of the terminal
     */
    private void auxParser(String command) {
        text = command.trim().replaceAll(" +", " ");     //Elimina espacios
        int i = 0;
        boolean comillas = false;
        for (int j = 0; j < text.length(); j++) {
            char ch = text.charAt(j);
            if (ch == ' ' && !comillas) {
                i++;
                commands[i] = "";
            } else if (ch == '"') {
                comillas = !comillas;
            } else {
                commands[i] += ch;
            }
        }
    }

    /**
     * This private function is called by the builder, it reduces the array length by detecting which is the highest
     * position that has content, then a new array is initialized with the correct length and the content is copied
     */
    private void cutter() {
        int i = 0;
        while ((i < commands.length) && (commands[i] != "")) {
            i++;
        }
        String[] aux = new String[i];
        for (int j = 0; j < i; j++) {
            aux[j] = commands[j];
        }
        commands = aux;
    }


    /**
     * This getter returns the full array of commands.
     */
    public String[] getCommands() {
        return commands;
    }

    /**
     * This getter returns one command of the array.
     */
    public String getCommand(int i) {
        String resul = null;
        if (i > -1 && i < commands.length) {
            resul = commands[i];
        }
        return resul;
    }

    /**
     * This getter returns the length of the array
     * @return
     */
    public int getLength() {
        return commands.length;
    }
	
	@Override
	public String toString() {
		return String.format("{class: Parser, text: %s, commands: %s}", this.text, Arrays.toString(this.commands));
	}
}
