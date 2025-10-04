package es.upm.etsisi.poo;

/**
 * Manages Create Read Update Delete operations for Products using Array to store the products. Provides input
 * sanity checks for all operations.
 */
public class ArrayDataManager implements DataManager {

    public final int MAX_CAPACITY = 200; // E1: no more than 200 products
    public final int MAX_NAME_LENGTH = 100; // E1: product name contains no more than 100 characters

    private Product[] inventory; // List
    private int productAmount;

    /**
     * Creates a new ArrayDataManager with an empty inventory
     */
    public ArrayDataManager() {
        this.inventory = new Product[this.MAX_CAPACITY];
        this.productAmount = 0;
    }

    /**
     * Checks if given ID is valid (idToCheck >= 0)
     * @param idToCheck numeric id to check
     * @return true if valid, otherwise return false
     */
    public static boolean isValidId(int idToCheck) {
        // Change if 0 is not an allowed ID
        return idToCheck >= 0;
    }

    /**
     * Check if given name is valid (nameToCheck.length < 100)
     * @param nameToCheck Name string to check
     * @return true if valid, otherwise return false
     */
    private boolean isValidName(String nameToCheck) {
        return nameToCheck.length() < this.MAX_NAME_LENGTH;
    }

    /**
     * Check if given price is valid (priceToCheck > 0)
     * @param priceToCheck Price double to check
     * @return true if valid, otherwise return false
     */
    private boolean isValidPrice(double priceToCheck) {
        return priceToCheck > 0;
    }

    /**
     * Attempts to find the product with the same ID, returning its position in the array
     * @param id Product ID
     * @return Product object's index in the array, -1 if not found or ID is invalid (ID < 0)
     */
    public int readProductIndex(int id) {
        if (!isValidId(id)) return -1;
        int result = -1;
        int i = 0;
        while (result == -1 && i < this.productAmount) {
            if (this.inventory[i].getId() == id)
                result = i;
            i++;
        }
        return result;
    }

    /**
     * Attempts to find the product with the same ID
     * @param id Product ID
     * @return Product with the specified id, null if not found or ID is invalid (ID < 0)
     */
    public Product readProduct(int id) {
        if (!isValidId(id)) return null;
        // Linear search for products with the same ID
        Product result = null;
        int i = 0;
        while (result == null && i < this.productAmount) {
            if (this.inventory[i].getId() == id)
                result = this.inventory[i];
            i++;
        }
        return result;
    }

    /**
     * Creates a product and adds it to the array
     * @param id Product ID (must be a positive integer)
     * @param name Product name (length must be less than 100)
     * @param category Product Category
     * @param price Product price (must be greater than 0)
     * @return DataResult enum: SUCCESS, INVALID_ID, INVALID_NAME, INVALID_PRICE, INVALID_CATEGORY, INVENTORY_FULL, PRODUCT_ALREADY_EXISTS
     */
    public DataResult createProduct(int id, String name, Product.Category category, double price) {
        // Sanity checks: ID >= 0, name.length < 100, price > 0
        if (!isValidId(id)) return DataResult.INVALID_ID;
        if (!isValidName(name)) return DataResult.INVALID_NAME;
        if (!isValidPrice(price)) return DataResult.INVALID_PRICE;
        if (category == null) return DataResult.INVALID_CATEGORY;

        // Check inventory full
        if (this.productAmount >= this.MAX_CAPACITY) return DataResult.INVENTORY_FULL;

        Product selectedProduct = this.readProduct(id);

        if (selectedProduct == null) {
            // Create product and add it to the array
            Product prodToAdd = new Product(id, name, category, price);
            this.inventory[this.productAmount] = prodToAdd;
            this.productAmount++;
            return DataResult.SUCCESS;
        } else {
            return DataResult.PRODUCT_ALREADY_EXISTS;
        }
    }

    /**
     * Updates a product's name specifying its product ID
     * @param id Product ID (must be a positive integer)
     * @param name Product name (length must be less than 100)
     * @return DataResult enum: SUCCESS, INVALID_ID, INVALID_NAME, PRODUCT_NOT_FOUND
     */
    public DataResult updateProductName(int id, String name) {
        // Sanity checks: ID >= 0, name.length < 100
        if (!isValidId(id)) return DataResult.INVALID_ID;
        if (!isValidName(name)) return DataResult.INVALID_NAME;

        Product selectedProduct = this.readProduct(id);

        if (selectedProduct != null) {
            // Update product's name
            selectedProduct.setName(name);
            return DataResult.SUCCESS;
        } else {
            return DataResult.PRODUCT_NOT_FOUND;
        }
    }

    /**
     * Updates a product's price specifying its product ID
     * @param id Product ID (must be a positive integer)
     * @param price Product price (must be greater than 0)
     * @return DataResult enum: SUCCESS, INVALID_ID, INVALID_PRICE, PRODUCT_NOT_FOUND
     */
    public DataResult updateProductPrice(int id, double price) {
        // Sanity checks: ID >= 0, price > 0
        if (!isValidId(id)) return DataResult.INVALID_ID;
        if (!isValidPrice(price)) return DataResult.INVALID_PRICE;

        Product selectedProduct = this.readProduct(id);

        if (selectedProduct != null) {
            // Update product's price
            selectedProduct.setPrice(price);
            return DataResult.SUCCESS;
        } else {
            return DataResult.PRODUCT_NOT_FOUND;
        }
    }

    /**
     * Updates a product's price specifying its product ID
     * @param id Product ID (must be a positive integer)
     * @param category Product Category
     * @return DataResult enum: SUCCESS, INVALID_ID, INVALID_PRICE, INVALID_CATEGORY
     */
    public DataResult updateProductCategory(int id, Product.Category category) {
        // Sanity checks: ID >= 0, category != null
        if (!isValidId(id)) return DataResult.INVALID_ID;
        if (category == null) return DataResult.INVALID_CATEGORY;

        Product selectedProduct = this.readProduct(id);

        if (selectedProduct != null) {
            // Update product's category
            selectedProduct.setCategory(category);
            return DataResult.SUCCESS;
        } else {
            return DataResult.PRODUCT_NOT_FOUND;
        }
    }

    /**
     * Deletes a product specifying its product ID from the array
     * @param id Product ID (must be a positive integer)
     * @return DataResult enum: SUCCESS, INVALID_ID, PRODUCT_NOT_FOUND
     */
    public DataResult deleteProduct(int id) {
        // Sanity checks: ID >= 0
        if (!isValidId(id)) return DataResult.INVALID_ID;

        int selectedProductIndex = this.readProductIndex(id);

        if (selectedProductIndex != -1) {
            // Remove product from array
            this.productAmount--;
            for (int i = selectedProductIndex; i < this.productAmount; i++) {
                this.inventory[i] = this.inventory[i + 1];
            }
            return DataResult.SUCCESS;
        } else {
            return DataResult.PRODUCT_NOT_FOUND;
        }
    }

    /**
     * Returns an array of all products added. Ordered by first added product to last added product.
     * @return Array of products with length of total product amount in the catalogue. Null if the inventory is empty
     */
    public Product[] listProducts() {
        if (this.productAmount == 0) return null;
        Product[] arrayProducts = new Product[this.productAmount];
        for (int i = 0; i < this.productAmount; i++) {
            arrayProducts[i] = this.inventory[i];
        }
        return arrayProducts;
    }

    public static void main(String[] args) {
        // TODO: Refactor this into JUNIT
        ArrayDataManager adm = new ArrayDataManager();

        System.out.println("Agregar libro");
        int newProdId = 1;
        String newProdName = "Libro POO";
        Product.Category newProdCateg = Product.Category.BOOK;
        double newProdPrice = 25;
        DataResult status = adm.createProduct(newProdId, newProdName, newProdCateg, newProdPrice);
        System.out.println(adm.readProduct(newProdId));
        if (status == DataResult.SUCCESS) System.out.println("prod add: ok");
        else System.out.println("prod add: " + status);

        System.out.println("\n---\n");

        System.out.println("Agregar mismo libro (debe fallar)");
        status = adm.createProduct(newProdId, newProdName, newProdCateg, newProdPrice);
        System.out.println(adm.readProduct(newProdId));
        if (status == DataResult.SUCCESS) System.out.println("prod add: ok");
        else System.out.println("prod add: " + status);

        System.out.println("\n---\n");

        System.out.println("Agregar libro con ID -2 (debe fallar)");
        status = adm.createProduct(-2, newProdName, newProdCateg, newProdPrice);
        System.out.println(adm.readProduct(newProdId));
        if (status == DataResult.SUCCESS) System.out.println("prod add: ok");
        else System.out.println("prod add: " + status);

        System.out.println("\n---\n");

        System.out.println("Agregar mismo libro con nombre super largo (debe fallar)");
        status = adm.createProduct(2, "Libro POOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO", newProdCateg, newProdPrice);
        System.out.println(adm.readProduct(newProdId));
        if (status == DataResult.SUCCESS) System.out.println("prod add: ok");
        else System.out.println("prod add: " + status);

        System.out.println("\n---\n");

        System.out.println("Agregar mismo libro con categoria null (debe fallar)");
        status = adm.createProduct(2, "Libro POOOOOOOOOOOO", null, newProdPrice);
        System.out.println(adm.readProduct(newProdId));
        if (status == DataResult.SUCCESS) System.out.println("prod add: ok");
        else System.out.println("prod add: " + status);

        System.out.println("\n---\n");

        System.out.println("Agregar mismo libro con precio 0 (debe fallar)");
        status = adm.createProduct(2, newProdName, newProdCateg, 0);
        System.out.println(adm.readProduct(newProdId));
        if (status == DataResult.SUCCESS) System.out.println("prod add: ok");
        else System.out.println("prod add: " + status);

        System.out.println("\n---\n");

        System.out.println("Agregar mismo libro con precio -2 (debe fallar)");
        status = adm.createProduct(2, newProdName, newProdCateg, -2.5);
        System.out.println(adm.readProduct(newProdId));
        if (status == DataResult.SUCCESS) System.out.println("prod add: ok");
        else System.out.println("prod add: " + status);

        System.out.println("\n---\n");

        // Agrego camiseta
        System.out.println("Agregar camiseta");
        newProdId = 2;
        newProdName = "Camiseta talla:M UPM";
        newProdCateg = Product.Category.CLOTHES;
        newProdPrice = 15;
        status = adm.createProduct(newProdId, newProdName, newProdCateg, newProdPrice);
        System.out.println(adm.readProduct(newProdId));
        if (status == DataResult.SUCCESS) System.out.println("prod add: ok");
        else System.out.println("prod add: " + status);

        System.out.println("\n---\n");

        // Lista de productos
        System.out.println("Listado de catalogo");
        System.out.println("Catalog:");
        Product[] prodList = adm.listProducts();
        if (prodList != null) {
            for (int i = 0; i < prodList.length; i++) {
                System.out.println(" " + prodList[i]);
            }
            System.out.println("prod list: ok");
        } else {
            System.out.println("prod list: empty");
        }

        System.out.println("\n---\n");

        // Actualizar nombre, precio del libro
        System.out.println("Actualizo nombre del libro");
        status = adm.updateProductName(1, "Libro POO V2");
        System.out.println(adm.readProduct(1));
        if (status == DataResult.SUCCESS) System.out.println("prod add: ok");
        else System.out.println("prod update: " + status);

        System.out.println("\n---\n");

        System.out.println("Actualizo nombre del libro largo (debe fallar)");
        status = adm.updateProductName(1, "Libro POO V2 OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
        System.out.println(adm.readProduct(1));
        if (status == DataResult.SUCCESS) System.out.println("prod add: ok");
        else System.out.println("prod update: " + status);

        System.out.println("\n---\n");

        System.out.println("Actualizo precio del libro");
        status = adm.updateProductPrice(1, 30.0);
        System.out.println(adm.readProduct(1));
        if (status == DataResult.SUCCESS) System.out.println("prod add: ok");
        else System.out.println("prod update: " + status);

        System.out.println("\n---\n");

        System.out.println("Actualizo precio del libro con 0 (debe fallar)");
        status = adm.updateProductPrice(1, 0);
        System.out.println(adm.readProduct(1));
        if (status == DataResult.SUCCESS) System.out.println("prod add: ok");
        else System.out.println("prod update: " + status);

        System.out.println("\n---\n");

        System.out.println("Actualizo precio del libro con -4.32 (debe fallar)");
        status = adm.updateProductPrice(1, -4.32);
        System.out.println(adm.readProduct(1));
        if (status == DataResult.SUCCESS) System.out.println("prod add: ok");
        else System.out.println("prod update: " + status);

        System.out.println("\n---\n");

        System.out.println("Inserto un libro repetido y lo borro");
        newProdId = 3;
        newProdName = "Libro POO repetido Error";
        newProdCateg = Product.Category.BOOK;
        newProdPrice = 25.0;
        status = adm.createProduct(newProdId, newProdName, newProdCateg, newProdPrice);
        System.out.println(adm.readProduct(newProdId));
        if (status == DataResult.SUCCESS) System.out.println("prod add: ok");
        else System.out.println("prod add: " + status);

        System.out.println();
        // Lista de productos
        System.out.println("LIST (BEFORE)");
        System.out.println("Catalog:");
        prodList = adm.listProducts();
        if (prodList != null) {
            for (int i = 0; i < prodList.length; i++) {
                System.out.println(" " + prodList[i]);
            }
            System.out.println("prod list: ok");
        } else {
            System.out.println("prod list: empty");
        }

        System.out.println("\n---\n");

        System.out.println("Lo borro");
        System.out.println(adm.readProduct(3));
        status = adm.deleteProduct(3);
        if (status == DataResult.SUCCESS) System.out.println("prod add: ok");
        else System.out.println("prod add: " + status);

        System.out.println();
        // Lista de productos
        System.out.println("LIST (AFTER)");
        System.out.println("Catalog:");
        prodList = adm.listProducts();
        if (prodList != null) {
            for (int i = 0; i < prodList.length; i++) {
                System.out.println(" " + prodList[i]);
            }
            System.out.println("prod list: ok");
        } else {
            System.out.println("prod list: empty");
        }

        System.out.println("\n---\n");

        System.out.println("Leer un producto con ID 3 (debe retornar null)");
        System.out.println(adm.readProduct(3));

        System.out.println("\n---\n");

        System.out.println("Nuevo ArrayDataManager y lo vamos a llevar");
        adm = new ArrayDataManager();
        for (int i = 1; i <= 200; i++) {
            adm.createProduct(i, "Objeto", Product.Category.BOOK, 32);
        }

        // Lista de productos
        System.out.println("LIST (BEFORE)");
        System.out.println("Catalog:");
        prodList = adm.listProducts();
        if (prodList != null) {
            for (int i = 0; i < prodList.length; i++) {
                System.out.println(" " + prodList[i]);
            }
            System.out.println("prod list: ok");
        } else {
            System.out.println("prod list: empty");
        }

        System.out.println("\n---\n");

        System.out.println("Añado una cosa más (debe fallar)");
        newProdId = 201;
        newProdName = "CosaError";
        newProdCateg = Product.Category.ELECTRONICA;
        newProdPrice = 65.25;
        status = adm.createProduct(newProdId, newProdName, newProdCateg, newProdPrice);
        System.out.println(adm.readProduct(newProdId));
        if (status == DataResult.SUCCESS) System.out.println("prod add: ok");
        else System.out.println("prod add: " + status);

        System.out.println("\n---\n");

        System.out.println("Elimino productos con ID pares o divisibles entre 3");
        for (int i = 1; i <= 200; i++) {
            if (i % 2 == 0 || i % 3 ==0)
                adm.deleteProduct(i);
        }

        // Lista de productos
        System.out.println("LIST (AFTER)");
        System.out.println("Catalog:");
        prodList = adm.listProducts();
        if (prodList != null) {
            for (int i = 0; i < prodList.length; i++) {
                System.out.println(" " + prodList[i]);
            }
            System.out.println("prod list: ok");
        } else {
            System.out.println("prod list: empty");
        }

        System.out.println("Test terminado");
    }
}
