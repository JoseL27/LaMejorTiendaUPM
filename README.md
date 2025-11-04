# La Mejor Tienda UPM 

## Tareas - E2
 - [x] (JULIO)   Crear BaseProduct y TimedProduct como hijos de Product; Crear enum TimedType; Adicionar boolean personalizable.
 - [ ] (ENRIQUE) Adicionar boolean abierto/cerrado a Ticket; ProductInfo ahora representa un producto con personalizables.
 - [x] (ANDRES)  Crear clase Client
   - Jinxian lo revisa
 - [x] (JOSE)    Crear clase Cashier y manejar Ticket's
 - [x] (JOSE)    Cambiar app para que se use la interfaz command en vez de una variable de cada comando
   - [ ] (JINXIAN) Crear UserManager y manejar Clients y Cashiers
   - [ ] (JULIO)Crear CashCommand
   - [ ] (ANDRES)Crear ClientCommand
- (JOSE y ENRIQUE) Debug y que funcione todo
- Escribir tests para lo nuevo de E2

## Como hacer una tarea
1. Programar
2. Documentar codigo
3. Probar funcionalidad
4. Branches y Pull Requests revisados por 2

OBLIGATORIO MAIN SIEMPRE COMPILANDO

# E1 Version
Ticket and Inventory module.

This program should allow the end user to create/update/delete/list products.

Using the products created, the end user should also have the ability to create a new ticket, adding in any product with any amount (E1: less or equal than 100), remove products, or print the ticket (listing all products in ticket, applying discounts, and showing to the end user product information, discounts applied, total price, discount amount, and final price)

# Products
Attributes: ID, Name, Price, Category
- ID must be positive (0 should be invalid)
- Name must be less than 100 characters
- Price must be positive (0 is invalid)
- Category fixed: MERCH(0%), STATIONERY(5%), CLOTHES(7%), BOOK(10%), ELECTRONICS(3%)

Product A and Product B are different if they have different IDs.

**For E1: No more than 200 different products**

## Commands for Products:
	+ prod add <id> "<name>" <category> <price>
	  add new products according to the restrictions above
	
	+ prod list
      list all created products
	
	+ prod update <id> <field> <value>
  	  update specific attributes of a specific product, checking restrictions above

	+ prod remove <id>
      remove products, eliminate appearances in ticket <-- Unspecified but we should assume this behaviour to prevent inconsistencies

# Tickets:
A list containing a list of products and amount.

**For E1: No more than 100 items (sum of all amount of products contained in ticket's product list <= 100)**

## Commands for Ticket:
	+ ticket new
  	  create new ticket, starting with empty ticket
  
	+ ticket add <prodId> <quantity>
  	  add items to ticket, specifying product ID and the amount to add, calls print internally but does not reset the ticket
  
	+ ticket remove <prodID>
  	  remove all appearance of product in ticket, calls print internally but does not reset the ticket
  
	+ ticket print
	  prints current ticket with product information and discount. Calculate total price, discount amount, and discounted total price. Product listing should be in alphabetical order. And starts a new ticket
  
Ticket price calculation: if there are 2 or more products of the same category, the product in question should be discounted at the rate (percertage) mentioned in Product Category.

**For E1: No more than 100 different items (sum of all added product counts) should be allowed inside the ticket.**

I.e. Add `50 Product A` in ticket (50 items), add `50 Product B` (50 items) in ticket. Now that the total items in the ticket is **100 (50 Prod. A + 50 Prod. B)**, **no more items** are allowed to be added.

# Auxiliary commands

	+ echo "Content"
	  Just echoes back whatever user just entered, with the keyword "echo" and its content in quotes

	+ help
	  Displays available commands, product category and discount rates

	+ exit
	  Prints a goodbye message and gracefully closes the program

Examples

```
tUPM> echo "SamekoSaba"
echo "SamekoSaba"

tUPM> help
Commands:
 prod add <id> "<name>" <category> <price>
 prod list
 prod update <id> NAME|CATEGORY|PRICE <value>
 prod remove <id>
 ticket new
 ticket add <prodId> <quantity>
 ticket remove <prodId>
 ticket print
 echo "<texto>"
 help
 exit

Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS
Discounts if there are ≥2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%,
ELECTRONICS 3%.

tUPM> exit
Closing application.
Goodbye!

[Process finished with exit code 0]
```

# Command tests:

The following tests should be executed in order without restarting the application.

Before receiving any command, the CLI interface must prompt `tUPM> `

```
tUPM> echo "Hola mundo"
echo "Hola mundo"

tUPM> help
Commands:
 prod add <id> "<name>" <category> <price>
 prod list
 prod update <id> NAME|CATEGORY|PRICE <value>
 prod remove <id>
 ticket new
 ticket add <prodId> <quantity>
 ticket remove <prodId>
 ticket print
 echo "<texto>"
 help
 exit
 
Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS
Discounts if there are ≥2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%,
ELECTRONICS 3%.

tUPM> 
```

Notice the newline between each command's result/output and the `tUPM> ` prompt.

## Echo command

### `echo "test"`

```
echo "test"
```

## Help command

### `help`

```
Commands:
 prod add <id> "<name>" <category> <price>
 prod list
 prod update <id> NAME|CATEGORY|PRICE <value>
 prod remove <id>
 ticket new
 ticket add <prodId> <quantity>
 ticket remove <prodId>
 ticket print
 echo "<texto>"
 help
 exit

Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS
Discounts if there are ≥2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%,
ELECTRONICS 3%.
```

Beware of the single space at the start of each command's format, and the newline between the command list and category discount information.

## Product commands

### `prod add 1 "Libro POO" BOOK 25`

```
{class:Product, id:1, name:'Libro POO', category:BOOK, price:25.0}
prod add: ok
```

### `prod add 2 "Camiseta talla:M UPM" CLOTHES 15`

```
{class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}
prod add: ok
```

### `prod list`

```
Catalog:
 {class:Product, id:1, name:'Libro POO', category:BOOK, price:25.0}
 {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}
prod list: ok
```

Beware of the single space before each product's information

### `prod update 1 NAME "Libro POO V2"`

```
{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:25.0}
prod update: ok
```

### `prod update 1 PRICE 30`

```
{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0}
prod update: ok
```

Note: `prod update` for **cagetory** does not appear in E1. But it should also follow the same response by printing the updated product information, and displaying `prod update: ok` if the update is successful

### `prod add 3 "Libro POO repetido Error" BOOK 25`

```
{class:Product, id:3, name:'Libro POO repetido Error', category:BOOK, price:25.0}
prod add: ok
```

### `prod remove 3`

```
{class:Product, id:3, name:'Libro POO repetido Error', category:BOOK, price:25.0}
prod remove: ok
```

## Ticket command

### `ticket add 1 2`

```
{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
Total price: 60.0
Total discount: 6.0
Final Price: 54.0
ticket add: ok
```

Note: `ticket add` should invoke `ticket print` to display the current state of the ticket, **without resetting the ticket**

### `ticket print`

```
{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
Total price: 60.0
Total discount: 6.0
Final Price: 54.0
ticket print: ok
```

Note: E1:
> Al imprimir el ticket, se imprime por pantalla y se inicia un nuevo ticket hasta que se cierra la aplicación.

`ticket print` command should reset the ticket afterwards.

### `ticket remove <id>`

> El borrado de un producto borrara todas las apariciones del ticket. Para esta versión se asume que los tickets no tendrán más de 100 productos.

Warning: E1 does not include an example output for `ticket remove`. This command should remove a product entirely from the ticket.

> Al incorporar un producto al ticket, modificarlo o borrarlo, se debe imprimir el importe provisional del ticket, aplicando los descuentos actuales.

**Adding products or removing products should invoke `ticket print` without resetting the ticket.**

### `ticket new`

```
ticket new: ok
```

## Ticket Command (example with various products)

### `ticket add 1 2`

```
{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
Total price: 60.0
Total discount: 6.0
Final Price: 54.0
ticket add: ok
```

### `ticket add 2 1`

```
{class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}
{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
Total price: 75.0
Total discount: 6.0
Final Price: 69.0
ticket print: ok
```

Notice that `ticket add` command displays previously added products, as well as newly added products, and calculates total price, total discount and final price. Please invoke `ticket print` without resetting the ticket.

### `ticket print`

```
{class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}
{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
Total price: 75.0
Total discount: 6.0
Final Price: 69.0
ticket print: ok
```

This should reset the ticket afterwards.

Note: in E1 statement's example output, `ticket print` is always followed by `ticket new`, so `ticket print`'s behaviour is not clear just by looking at the example output.

> Al imprimir el ticket, se imprime por pantalla y se inicia un nuevo ticket hasta que se cierra la aplicación.

The statement clearly indicated that after printing a ticket, the ticket must be reset. Although this behaviour might appear counter-intuitive, it is a requirement nonetheless.

## Exit command

### `exit`

```
Closing application.
Goodbye!
```

# Other requirements

To turn in:
- Source code, JAR package --> Moodle
- UML (PNG, JPG, SVG). **Explain library usages.**

Others:
- Source code must be compilable.
- Input/Output must be matching input/output examples in E1 statement. Specially command formats.
- Creating more commands outside what is defined in E1 statement is prohibited.
- Project exposition/defense: project must be able to execute all commands without any errors and following expected behaviour.

# Other comments

- Command keywords are expected to be lowercase and category keywords are expected to be uppercase (E1 does not specify, but hinted with example outputs)
- The **assumptions** outlined in E1 (200 different products max. and 100 items max. in ticket) are currently treated as hard limitations in our project
