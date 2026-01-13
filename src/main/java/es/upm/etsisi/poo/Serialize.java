package es.upm.etsisi.poo;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Serialize {
    private static Map<String, Serializable> dataStore = new HashMap<>();

	public static void put(String label, Serializable obj) {
		dataStore.put(label, obj);
	}
	
	public static Serializable get(String label) {
		return dataStore.get(label);
	}

    public static void save(File saveLocation) throws IOException {
        FileOutputStream file = new FileOutputStream(saveLocation);
        ObjectOutputStream out = new ObjectOutputStream(file);
        out.writeObject(dataStore);
        out.close();
        file.close();
    }

    public static void load(File saveLocation) throws IOException, ClassNotFoundException {
        FileInputStream file = new FileInputStream(saveLocation);
        ObjectInputStream in = new ObjectInputStream(file);
        dataStore = (Map<String, Serializable>) in.readObject();
        in.close();
        file.close();
    }
}
