package es.upm.etsisi.poo;

/**
 * Static Utilities class.
 */
public class Utils {

    public static void printInvalidDataType(String failedCommand, String expectedDataType, String receivedValue){
        System.out.printf("%s: error: expected %s, got '%s'\n", failedCommand, expectedDataType, receivedValue);
    }

}
