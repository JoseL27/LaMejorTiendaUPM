package es.upm.etsisi.poo;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Serialize {
    public static Map<String, Serializable> dataStore = new HashMap<>();

    public static void save(File saveLocation) throws IOException {
        //System.out.println("Lemon Melon Cookie; Lemon Melon Cookie; Lemon Melon; Lemon Melon Cookie; Lemon Melon Cookie; Cookie " + saveLocation.toString());
        FileOutputStream file = new FileOutputStream(saveLocation);
        ObjectOutputStream out = new ObjectOutputStream(file);
        out.writeObject(dataStore);
        out.close();
        file.close();
    }

    public static void load(File saveLocation) throws IOException, ClassNotFoundException {
        //System.out.println("mochimochi, mochimochi, mochimochi omochi; kuchibiru ga masshiro ni: " + saveLocation.toString());
        FileInputStream file = new FileInputStream(saveLocation);
        ObjectInputStream in = new ObjectInputStream(file);
        dataStore = (Map<String, Serializable>) in.readObject();
        in.close();
        file.close();
    }
}
