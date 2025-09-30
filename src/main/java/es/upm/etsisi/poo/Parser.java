package es.upm.etsisi.poo;
//Hablar cual de las dos opciones prefieren implementar para recibir el comando

/**
 * This class function is to parse the command received in different position of the array
 */
public class Parser {
    private String[] commands;
    private String texto;

    /**
     * Esta función elimina todos los espacios repetidos y crea el String array de la longitud máxima
     * de la cadena recibida, para ello va caracter a caracter, si es un espacio aumenta la posición en
     * la que debe guardarse, si hay comillas, no se aumenta este espacio hasta que se cierran
     *
     * @param command
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

    private void auxParser(String command) {
        texto = command.trim().replaceAll(" +", " ");     //Elimina espacios
        int i = 0;
        boolean comillas = false;
        for (int j = 0; j < texto.length(); j++) {
            char ch = texto.charAt(j);
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
     * Adapta el tamaño del array al necesario
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
        String resul = "Error, estas fuera del array";
        if (i > -1 && i < commands.length) {
            resul = commands[i];
        }
        return resul;
    }
    public int getLenght() {
        return commands.length;
    }

    public static void main(String[] args) {
        Parser p = new Parser("Vamos a parsear \"Esta cadena\" y veamos como queda el array");
        p.getCommand(0);
    }
}
