package es.upm.etsisi.poo;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Serialize {

    public static void save(File saveLocation) throws IOException {
        Inventory inventory = Inventory.getInstance();
        UserManager userManager = UserManager.getInstance();
        FileOutputStream file = new FileOutputStream(saveLocation);
        ObjectOutputStream out = new ObjectOutputStream(file);
        out.writeObject(inventory);
        out.writeObject(userManager);
        out.close();
        file.close();
    }

    public static void load(File saveLocation) throws IOException, ClassNotFoundException {
        Inventory inventory;
        UserManager userManager;
        FileInputStream file = new FileInputStream(saveLocation);
        ObjectInputStream in = new ObjectInputStream(file);

        // Checks the order in which the inventory and user manager are written,
        // shouldnt be necessary right now but the order can change in the future
        Object firstObject = in.readObject();
        if (firstObject instanceof Inventory){
            inventory = (Inventory) firstObject;
            userManager = (UserManager) in.readObject();
        }else{
            userManager = (UserManager) firstObject;
            inventory = (Inventory) in.readObject();
        }
        in.close();
        file.close();

        Inventory.load(inventory);
        UserManager.load(userManager);
    }
}
