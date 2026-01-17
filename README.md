# La Mejor Tienda UPM 

## Tareas
 - [x] ⁠(Enrique)  Escribir test con fichero entrada ejemplo (el de moodle)
 - [ ] ⁠(Andres)   Escribir tests generales
 - [ ] ⁠⁠(TODOS)    Probar (y talvez escribir tests) para casos casitos y casetes
 - [x] (⁠TODOS)    Garantizar que todo este funcionando
 - [ ] ⁠⁠(Jin Xian) Simplificar/limpiar codigo 
 - [x] ⁠⁠(Julio)    Cargarse utils
 - [ ] ⁠⁠(Jose)     Hacer UML final
 - [ ] ⁠⁠(TODOS)    Reunion para prepararse a la defensa

## Como hacer una tarea
1. Programar
2. Documentar codigo
3. Probar funcionalidad
4. Branches y Pull Requests revisados por 2

OBLIGATORIO MAIN SIEMPRE COMPILANDO

# E3 Version
Ticket, inventory, local persistence, User/Cashier module.

This program should allow the end user to create/update/delete/list products, and create/remove/list registered
and users. 

Using the products created, the end user should also have the ability to create a new ticket and associate it with a
cashier, adding in any product with any amount (E1: less or equal than 100), remove products, or print the ticket.

Printing the ticket consists of: listing all products in ticket, applying discounts, and showing to the end user product
information, discounts applied,
total price, discount amount, and final price.

The ticket may limit what kind of product is allowed to be added in the ticket.

# Users

Any user is an entity which is modeled after a person. Any person has at least an identifier, name, and an
E-Mail address (contact).

**Note: the term "end user" refers to the user which is using this program, not any entity or User class
in the program design.**

## Cashier

A client is a user which creates the ticket for the client.

A cashier is identified by their Worker ID (Starting with UW and a 7-digit number), name, and its company E-mail address
(Ending in domain `@upm.es`)

- ID (Worker ID), must have format `UW#######`, starting with the `UW` prefix and 7 numbers. ID should be generated
randomly if omitted during `cash add` command
- Name
- Company E-mail address (Must en in domain `@upm.es`)

The cashier holds a list of tickets created by itself. If the cashier is removed, then **its associated tickets will be
also removed.**

## Cashier commands
    + cash add [<id>] "<name>" <email>
      Register a new cashier, email must be a company email address ending in "@upm.es".
      ID is optional, generate automatically if not specified.

    + cash remove <id>
      Remove the cashier with given ID.
      This operation will also erase any associated tickets with the specified cashier.
    
    + cash list
      List all registered cashiers ordered alphabetically by name. Do NOT show tickets created by
      this cashier.

    + cash tickets <workerID>
      Lists ticket IDs and ticket states created by the specified worker.

## Client

A client is a user which is associated to one or more ticket. A cashier must register the client before creating any
ticket.

The client is split into two categories: Natural Person identified by DNI/NIE, and Enterprise identified by CIF.

A client is identified by their national ID number (Spain: DNI/NIE/CIF), name, and (personal or enterprise) E-mail address.
During the registration, the cashier who registered the client should be stored inside the Client as an attribute.

- ID (Spanish National ID)
- Name
- E-Mail address, any E-mail address is possible
- Cashier who registered the client

## Client commands
    + client add "<name>" <id> <email> <cashierID>
      Register a new client with specified CashierID. 
      CashierID should be associated to a valid cashier.
      ID would be the client's national ID.
    
    + client remove <id>
      Removes a client from the registry.
      This would NOT remove any ticket associated to the client.

    + client list
      Lists all registered client's information (id, name, email, cashier) ordered alphabetically by name.

# Products
The products are split into three kinds: Timed Products, Normal Products, and Service Products.

A custom ID can be specified for Normal Products and Timed Products. If omitted, the Product ID should be generated
automatically, following the same behaviour as Cashier.

Product A and Product B are different if they have different IDs.

For E1: No more than 200 different products in inventory

**Note: price calculation will be detailed in the [Ticket](#tickets) section**

*Observation: there are two possible interpretation from the original E2 statement:*
> Para los cajeros, será un código compuesto por
> un duo de letras “UW” (UPM Worker) más 7 dígitos aleatorios, este podrá generarse
> internamente si no se pasa como parámetro en su creación
>
> ...
>
> Los productos registrados en la aplicación podrán registrarse con un identificador o se generará de manera
> automática internamente al igual que los cajeros

This can mean:
- Both the cashier and product should **generate its ID automatically if omitted during `prod add` or `cash add`.**
Cashier should generate ID randomly, but since Product ID's automatic generation algorithm is NOT explicitly specified,
the automatic ID generation process is up to us.
- Both the cashier and product should **generate its ID automatically with random numbers** if omitted during `prod add`
or `cash add`, knowing that from the E1 restriction, there will be no more than 200 different products, and the numeric ID
space for Cashier is UW0000000 - UW9999999.

## Normal products

Normal products could refer to any object, such as T-Shirts, books, pens and mugs.

- ID, must be positive (0 is valid)
- Name, must be less than 100 characters (E1 Restriction)
- Price (per piece), must be positive (0 is invalid)
- Category fixed with discount rates: MERCH(0%), STATIONERY(5%), CLOTHES(7%), BOOK(10%), ELECTRONICS(3%)
- Amount of customizations (0 or undefined means non-customizable product), once set then it could not be altered.

Customizations would be a list of strings known when adding the product to a ticket, Customization amount refers to the
maximum size of the Customizations list.

After the product is registered in the inventory, the end user will be unable to alter the number of customization that
any product can bear.

Any normal product has the option to be customized, when adding the product to the inventory, the end user may
specify how many customizations a product can have during `prod add`. And use `ticket add --p` command to specify what
customization will be present in the product at checkout.

Adding a customization to a normal product during `ticket add` will add 10% to the cost of the product (1 customization
= 10% added cost to the price/unit, 2 = 20%, 3 = 30%, until the Customization Amount limit specified during the product
registration in Inventory). Any customizable product (normal product registered in the inventory with customization
amount > 0) can be added to ticket with or without any customizations.

## Timed product

Timed products (or product with time limitation) is usually a kind of service, which could not be added before a certain
time to the ticket.

A timed product is a product with an expiration date and a preparation period. After passing the expiration date, the
product could not be added to any ticket.

The preparation period is a time period placed before the expiration date, during this period, the product could not be
added to any ticket. There are two kinds of product with time limitation, **food** which has a preparation period of 72
hours, and **meetings** which has 12 hours.

For example: a food catering service with pizza and pasta, with specified expiration date of 24th November 2025 @ 00:00.
Such service may be added at any time before 21st November 2025 @ 00:00.
During 21st and 24th, this product is in the preparation period and could not be added to any ticket.

- ID, must be positive (0 is valid)
- Name, must be less than 100 characters (E1 Restriction)
- Price (per person), must be positive (0 is invalid)
- Maximum amount of participants (must be less or equal than 100)
- Expiration date

Preparation period is dependent on the type of the Timed Product (food: 72h or meeting: 12h).

When creating a new Timed Product, its expiration date must be checked before registering at the Inventory, creating a
Timed Product which is already in its preparation period or after being expired is NOT ALLOWED.

## Service Product

A Service is a Product which can only be offered to an Enterprise, and has an expiration date.

After its expiration date, the service could not be added to any Ticket.

The Service Product only has the following attributes:

- ID, auto-incremented (Starting at 1), non-customizable numeric ID with a suffix character 'S' (E.g. 1S, 2S)
- Category (INSURANCE, TRANSPORT, SPECTACLE)
- Expiration date

Services have **no price**, treat the price as 0 in Ticket. Price calculation for services is not calculated in this
program.

When creating a new Service, its expiration date must be checked before registering at the Inventory, creating a
Service after its expiration date should not be allowed.

## Commands for Products:
	+ prod add [<id>] "<name>" <category> <price> [<MaxPersonalizationAmount>]
	  Add a new normal product to the inventory. The product may be personalized specifying the maximum
      amount for the customization string list. Set to 0 if not specified.
      ID is optional. ID will be generated automatically if omitted.

    + prod add <expiration: yyyy-MM-dd> <service category>
      Add a new service to the inventory. ID will be generated automatically (Auto-increment).
      <expiration> should follow strictly by the yyyy-mm-dd format (e.g. 2025-11-25)

    + prod addFood [<id>] "<name>" <price> <expiration: yyyy-MM-dd> <maxPeople>
      Add a new timed product (Food) to the inventory. This kind of product has a preparation period of 72h.
      <expiration> should follow strictly by the yyyy-mm-dd format (e.g. 2025-11-25)
      ID is optional. ID will be generated automatically if omitted.
    
    + prod addMeeting [<id>] "<name>" <price> <expiration: yyyy-MM-dd> <maxPeople>
      Add a new timed product (Meeting) to the inventory. This kind of product has a preparation period of 12h.
      <expiration> should follow strictly by the yyyy-mm-dd format (e.g. 2025-11-25)
      ID is optional. ID will be generated automatically if omitted.

	+ prod list
      List all registered products (normal products, timed product and services), ordered by ID
	
	+ prod update <id> <field> <value>
  	  Update specific attributes of a specific product, checking restrictions on values.
      Possible values for <field>: NAME|CATEGORY|PRICE
      Update category will be an invalid operation on products with time limitations, since these kind of product
      does not have an category field.

	+ prod remove <id>
      Remove products, eliminate appearances in ticket in opened tickets <-- Unspecified but we should assume this behaviour to prevent inconsistencies

# Tickets

A list containing products, product amount (or participants in case of Timed Products), and 
customizations (only applicable to Normal product)

A Ticket will have 3 states:
- EMPTY (The ticket is empty, no products were added, and more products can be added)
- OPEN (The ticket contains at least one product, and more products can be added)
- CLOSE (No more products can be added to this ticket)

A Ticket can limit the product type it can accept by specifying one of the following flags during `ticket new`:
- Product Only (`-p` flag, default): Only accept Timed Product, and Normal Products
- Service Only (`-s` flag): Only accept Services
- Combined (`-p` flag): Accept both Product and Services.

**A combined ticket can only close if it has both Product and Service added.**

A natural person client's ticket can only have Product Only (`-p`) tickets. Enterprises can have Service Only (`-s`),
or Combined Tickets (`-c`).

The Product Only ticket should be the default ticket to create for Natural Person client and Enterprises when the flag
is omitted.

Once a ticket is closed, the end user should be able to print the ticket again specifying the corresponding Cashier ID
and Ticket ID.

A timed product with the same ID could not be added again to the same ticket.

**For E1: No more than 100 different items (sum of all added product counts) should be allowed inside the ticket.**

I.e. Add `50 Product A` in ticket (50 items), add `50 Product B` (50 items) in ticket. Now that the total items in
the ticket is **100 (50 Prod. A + 50 Prod. B)**, **no more items** are allowed to be added.

A timed product with any arbitrary amount of participants in the ticket should be counted as 1 item. For example, a
pizza catering service with 30 participants (50 max.) should be counted as one item in the ticket.

## Price calculations

For any normal product, the price would be simply its price per piece multiplied by the amount in Ticket. If the Product
is customizable and customization is specified during `ticket add`, the price for the customized piece would be 10% more
than its normal price per piece, adding 10% for each customization present. If no customization is specified
during `ticket add`, then sell as normal product (0% added cost).

If there are 2 or more products of the same category, the product in question should be discounted at the rate
(percentage) mentioned in Product Category. The discount is applied on a piece by piece basis.
Adding the same product twice (amount > 1) is also eligible for a category discount.

**Important: customized product should be eligible for category discounts. The price to subtract is the price of the
product after the added cost due to customizations, multiplied by the category discount percentage.**

```
customizationPercent = numCustomizations * 10% (numCustomizations: amount of customization strings specified during ticket add)
productPriceWithCustomization = baseProductPrice + (baseProductPrice * customizationPercent)
                                                    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                                                      Additional cost due to customization
finalProductPrice = productPriceWithCustomization - (productPriceWithCustomization * categoryPercent)
                                                     ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                                                      Cost to discount according to product category
```

## Ticket ID

All tickets are identified with an ID. During ticket creation (`ticket new` command), the ID will be generated
automatically if not specified. The automatically generated ID will have the following format:
`YY-MM-dd-HH:mm-#####` where YY:year (2 digit), MM:month (2 digit), dd:day (2 digit), HH:hour (24-hour format),
mm:minute, #####: random 5-digit number.

When the ticket is closed, the ticket ID will change, appending the date/time when the ticket is closed.

Examples:

Assuming ticket open date will be always 16/11/2025 11:53, and ticket close date will be always 19/11/2025 16:41, random
5-digit number will be always 00721.

When ticket ID is generated automatically:

- Created: `25-11-16-11:53-00721`
- Closed: `25-11-16-11:53-00721-25-11-19-16:41`

When ticket ID is explicitly specified (For example: 157194)

- Created: `157194`
- Closed: `157194-25-11-19-16:41`

Observation: when ticket ID is specified, the opening date and random 5-digit number is replaced entirely with
the ticket ID specified. But closing the ticket will still append the closing date of the ticket.

Observation 2: looking at the E2 specification, it looks like the ID is implemented with a string, it is uncertain
if the ticket ID should allow non-numeric IDs (Such as `21263s`, closing -> `21263s-25-11-19-16:41`)

Observation 3: to print or reference any closed ticket by its ID, it is uncertain if the end user should specify the
full ticket ID with the closing date, or any part matching the ID unambiguously should suffice.
For example:

- Generated ID: `25-11-16-11:53-00721-25-11-19-16:41`, or `25-11-16-11:53-00721`, even `25-11-16-11:53` or `00721`
  if unambiguous
- Custom ID: `157194-25-11-19-16:41`, or `157194`, even `25-11-19-16:41` if unambiguous

## Commands for Ticket
	+ ticket new [<ID>] <CashierID> <ClientID> -[c|p|s]
  	  Create new empty ticket and associate it to a valid cashier and client.
      -[c|p|s] restricts the kind of product which the ticket can accept. If omitted default to -p (Product Only) 
      ID will be generated automatically if omitted.
  
	+ ticket add <ticketID> <cashierID> <productID> <quantity> [--p<customization> --p<customization2> ... --p<customizationN>]
  	  Add items to ticket, specifying product ID and the amount to add, calls print internally but does not close the ticket.
      <cashierID> must be the cashier ID which the ticket was associated during the ticket creation.   
      Customizations are specified via --p argument.
  
	+ ticket remove <productID> <cashierID> <productID>
  	  Remove all appearance of product in ticket, including customized products.
      Calls print internally but does not close the ticket
      <cashierID> must be the cashier ID which the ticket was associated during the ticket creation. 

	+ ticket print <ticketID> <cashierID>
	  Prints current ticket with product information and discount. And closes the ticket.
      Calculate total price, discount amount, and discounted total price.
      Product listing should be in alphabetical order.

    + ticket list
      Lists all tickets IDs created and their states, ordered by Cashier's ID.
      For example: UW0000001 has 222222, UW0000002 has 212121, 222121. This command will list
      ticket IDs created by UW0000001 and then ticket IDs created by UW0000002.
      222222 - EMPTY
      212121 - CLOSED
      222121 - OPEN

# Auxiliary commands

	+ echo "Content"
	  Echoes back whatever user just entered in quotes

	+ help
	  Displays available commands, product category and discount rates

	+ exit
	  Prints a goodbye message and gracefully closes the program

Examples

```
tUPM> echo "saba SABA さば ˢᵃᵇᵃ"
"saba SABA さば ˢᵃᵇᵃ"

tUPM> help
Commands:
  client add "<nombre>" (<DNI>|<NIF>) <email> <cashId>
  client remove <DNI>
  client list
  cash add [<id>] "<nombre>"<email>
  cash remove <id>
  cash list
  cash tickets <id>
  ticket new [<id>] <cashId> <userId> -[c|p|s] (default -p option)
  ticket add <ticketId><cashId> <prodId> <amount> [--p<txt> --p<txt>] 
  ticket remove <ticketId><cashId> <prodId> 
  ticket print <ticketId> <cashId> 
  ticket list
  prod add ([<id>] "<name>" <category> <price> [<maxPers>]) || ("<name>" <category> )
  prod update <id> NAME|CATEGORY|PRICE <value>
  prod addFood [<id>] "<name>" <price> <expiration:yyyy-MM-dd> <max_people>
  prod addMeeting [<id>] "<name>" <price> <expiration:yyyy-MM-dd> <max_people>
  prod list
  prod remove <id>
  help
  echo “<text>” 
  exit

Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS
Discounts if there are ≥2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%, ELECTRONICS 3%.

tUPM> exit
Closing application.
Goodbye!

[Process finished with exit code 0]
```

# Command tests:

The following tests should be executed in order without restarting the application.

Program startup message:
```
Welcome to the ticket module App.
Ticket module. Type 'help' to see commands.
```

Before receiving any command, the CLI interface must prompt `tUPM> `

```
Welcome to the ticket module App.
Ticket module. Type 'help' to see commands.
tUPM> echo "Hola mundo"
"Hola mundo"

tUPM> help
Commands:
  client add "<nombre>" (<DNI>|<NIF>) <email> <cashId>
  client remove <DNI>
  client list
  cash add [<id>] "<nombre>"<email>
  cash remove <id>
  cash list
  cash tickets <id>
  ticket new [<id>] <cashId> <userId> -[c|p|s] (default -p option)
  ticket add <ticketId><cashId> <prodId> <amount> [--p<txt> --p<txt>] 
  ticket remove <ticketId><cashId> <prodId> 
  ticket print <ticketId> <cashId> 
  ticket list
  prod add ([<id>] "<name>" <category> <price> [<maxPers>]) || ("<name>" <category> )
  prod update <id> NAME|CATEGORY|PRICE <value>
  prod addFood [<id>] "<name>" <price> <expiration:yyyy-MM-dd> <max_people>
  prod addMeeting [<id>] "<name>" <price> <expiration:yyyy-MM-dd> <max_people>
  prod list
  prod remove <id>
  help
  echo “<text>” 
  exit

Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS
Discounts if there are ≥2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%, ELECTRONICS 3%.

tUPM> 
```

Notice the newline between each command's result/output and the `tUPM> ` prompt.

## Echo command

### `echo "test"`

```
"test"
```

## Help command

### `help`

```
Commands:
  client add "<nombre>" (<DNI>|<NIF>) <email> <cashId>
  client remove <DNI>
  client list
  cash add [<id>] "<nombre>"<email>
  cash remove <id>
  cash list
  cash tickets <id>
  ticket new [<id>] <cashId> <userId> -[c|p|s] (default -p option)
  ticket add <ticketId><cashId> <prodId> <amount> [--p<txt> --p<txt>] 
  ticket remove <ticketId><cashId> <prodId> 
  ticket print <ticketId> <cashId> 
  ticket list
  prod add ([<id>] "<name>" <category> <price> [<maxPers>]) || ("<name>" <category> )
  prod update <id> NAME|CATEGORY|PRICE <value>
  prod addFood [<id>] "<name>" <price> <expiration:yyyy-MM-dd> <max_people>
  prod addMeeting [<id>] "<name>" <price> <expiration:yyyy-MM-dd> <max_people>
  prod list
  prod remove <id>
  help
  echo “<text>” 
  exit

Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS
Discounts if there are ≥2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%, ELECTRONICS 3%.
```

Beware of the single space at the start of each command's format, and the newline between the command list and category
discount information.

Beware of the full-width quote symbol at `echo “<text>” `.

Keep in mind that for `cash add`, `ticket add`, `ticket remove`, some fields have no space separation in the help text,
this is probably a mistake, the fields in the real command is separated with spaces.

## Cashier commands

### `cash add UW1234567 "pepecurro3" pepe0@upm.es` (Add cashier UW1234567)
```
Cash{identifier='UW1234567', name='pepecurro3', email='pepe0@upm.es'}
cash add: ok
```

### `cash add "pepecurro2" pepe0@upm.es` (Add cashier with automatic ID)
```
Cash{identifier='UW8961116', name='pepecurro2', email='pepe0@upm.es'}
cash add: ok
```

When ID is omitted, ID should be a random number

### `cash add UW1234569 "pepecurro1" pepe0@upm.es` (Add cashier UW1234569)
```
Cash{identifier='UW1234569', name='pepecurro1', email='pepe0@upm.es'}
cash add: ok`
```

### `cash list` (List all registered cashiers)
```
Cash:
  Cash{identifier='UW1234569', name='pepecurro1', email='pepe0@upm.es'}
  Cash{identifier='UW8961116', name='pepecurro2', email='pepe0@upm.es'}
  Cash{identifier='UW1234567', name='pepecurro3', email='pepe0@upm.es'}
cash list: ok
```

### `cash remove UW1234569` (Remove a cashier)
```
cash remove: ok
```

This should also delete any tickets associated to this cashier.

### `cash list` (Verify cashier list if cashier UW1234569 is removed)
```
Cash:
  Cash{identifier='UW8961116', name='pepecurro2', email='pepe0@upm.es'}
  Cash{identifier='UW1234567', name='pepecurro3', email='pepe0@upm.es'}
cash list: ok
```

### `cash tickets UW1234567` (List cashier's tickets, which is currently empty)
```
Tickets: 
cash tickets: ok
```

## Client commands

### `client add "Pepe3" 55630667S pepe1@upm.es UW1234567` (New client)
```
Client{identifier='55630667S', name='Pepe3', email='pepe1@upm.es', cash=UW1234567}
client add: ok
```

### `client add "Pepe2" 98948334B pepe2@upm.es UW1234567` (New client 2)
```
Client{identifier='98948334B', name='Pepe2', email='pepe2@upm.es', cash=UW1234567}
client add: ok
```

### `client add "Pepe1" Y8682724P pepe3@upm.es UW1234567` (New client 3)
```
Client{identifier='Y8682724P', name='Pepe1', email='pepe3@upm.es', cash=UW1234567}
client add: ok
```

### `client list` (List all registered clients)
```
Client:
  Client{identifier='Y8682724P', name='Pepe1', email='pepe3@upm.es', cash=UW1234567}
  Client{identifier='98948334B', name='Pepe2', email='pepe2@upm.es', cash=UW1234567}
  Client{identifier='55630667S', name='Pepe3', email='pepe1@upm.es', cash=UW1234567}
client list: ok
```

Notice the two spaces before any client information

### `client remove Y8682724P` (Remove a client)
```
client remove: ok
```

### `client list` (Verify client list if client Y8682724P is removed)
```
Client:
  Client{identifier='98948334B', name='Pepe2', email='pepe2@upm.es', cash=UW1234567}
  Client{identifier='55630667S', name='Pepe3', email='pepe1@upm.es', cash=UW1234567}
client list: ok
```

## Enterprise Client commands

### `client add "pepe2" B12345674 pepe5@upm.es UW1234567` (Add new enterprise client)
```
COMPANY{identifier='B12345674', name='pepe2', email='pepe5@upm.es', cash=UW1234567}
client add: ok
```

### `client add "La bomba transportes" P1145148A lebomb@c4.com UW1234567` (Add another enterprise client)
```
COMPANY{identifier='P1145148A', name='La bomba transportes', email='lebomb@c4.com', cash=UW1234567}
client add: ok
```

### `client list` (List both client and enterprise)
```
Client:
  COMPANY{identifier='P1145148A', name='La bomba transportes', email='lebomb@c4.com', cash=UW1234567}
  USER{identifier='98948334B', name='Pepe2', email='pepe2@upm.es', cash=UW1234567}
  USER{identifier='55630667S', name='Pepe3', email='pepe1@upm.es', cash=UW1234567}
  COMPANY{identifier='B12345674', name='pepe2', email='pepe5@upm.es', cash=UW1234567}
client list: ok
```

### `client remove P1145148A` (Remove enterprise)
```
client remove: ok
```

### `client list` (Verify removal of P1145148A)
```
Client:
  USER{identifier='98948334B', name='Pepe2', email='pepe2@upm.es', cash=UW1234567}
  USER{identifier='55630667S', name='Pepe3', email='pepe1@upm.es', cash=UW1234567}
  COMPANY{identifier='B12345674', name='pepe2', email='pepe5@upm.es', cash=UW1234567}
client list: ok
```

## Product commands

### `prod add 1 "Libro POO" BOOK 25` (Add normal product: book)
```
{class:Product, id:1, name:'Libro POO', category:BOOK, price:25.0}
prod add: ok
```

### `prod add 2 "Camiseta talla:M UPM" CLOTHES 15` (Add normal product: clothes)
```
{class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}
prod add: ok
```

### `prod add "Camiseta talla:M UPM" CLOTHES 15` (Add normal product without ID)
```
{class:Product, id:0, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}
```

Note: auto-increment will try and find the first (and lowest) product ID available. 
Since during previous `prod add` used ID 1 and 2. This command will take the product ID 0.
Next `prod add` without ID should now take the ID of 3.

### `prod list` (List normal products)
```
Catalog:
  {class:Product, id:0, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}
  {class:Product, id:1, name:'Libro POO', category:BOOK, price:25.0}
  {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}
prod list: ok
```

Beware of the double space before each product's information

### `prod add 3 "Libro POO repetido Error" BOOK 25` (Add a test book for remove test)
```
{class:Product, id:3, name:'Libro POO repetido Error', category:BOOK, price:25.0}
prod add: ok
```

### `prod update 1 NAME "Libro POO V2"` (Update book name)
```
{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:25.0}
prod update: ok
```

### `prod update 1 PRICE 30` (Update book price)
```
{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0}
prod update: ok
```

### `prod update 3 CATEGORY CLOTHES` (Update test book category)
```
{class:Product, id:3, name:'Libro POO repetido Error', category:CLOTHES, price:25.0}
prod update: ok
```

### `prod list` (Check updated product information)
```
Catalog:
  {class:Product, id:0, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0}
  {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}
  {class:Product, id:3, name:'Libro POO repetido Error', category:CLOTHES, price:25.0}
prod list: ok
```

### `prod remove 3` (Remove test book)
```
{class:Product, id:3, name:'Libro POO repetido Error', category:BOOK, price:25.0}
prod remove: ok
```

### `prod list` (Confirm test book was removed)
```
Catalog:
  {class:Product, id:0, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0}
  {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}
prod list: ok
```

### `prod addMeeting 23456 "Reunion Rotonda" 12 2025-11-21 100` (Add a meeting with 100 participants max)
```
{class:Meeting, id:23456, name:'Reunion Rotonda', price:0.0, date of Event:2025-11-21, max people allowed:100}
prod addMeeting: ok
```

### `prod addMeeting 23457 "Graduacion ETSISI" 40 2025-11-21 30` (Add another meeting with 30 participants max)
```
{class:Meeting, id:23457, name:'Graduacion ETSISI', price:0.0, date of Event:2025-11-21, max people allowed:30}
prod addMeeting: ok
```

### `prod addFood 23458 "Cafeteria ETSISI" 5 2025-11-21 300` (Add food exceeding the 100 people max limitation)
```
Error processing ->prod addFood ->Error adding product
```

### `prod addFood 23459 "Restaurante Asador" 50 2025-11-21 40` (Add food)
```
{class:Food, id:23459, name:'Restaurante Asador', price:0.0, date of Event:2025-11-21, max people allowed:40}
prod addFood: ok
```

### `prod list` (List all normal products, food and meetings)
```
Catalog:
  {class:Product, id:0, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0}
  {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}
  {class:Meeting, id:23456, name:'Reunion Rotonda', price:0.0, date of Event:2025-11-21, max people allowed:100}
  {class:Meeting, id:23457, name:'Graduacion ETSISI', price:0.0, date of Event:2025-11-21, max people allowed:30}
  {class:Food, id:23459, name:'Restaurante Asador', price:0.0, date of Event:2025-11-21, max people allowed:40}
prod list: ok
```

### `prod add 5 "Camiseta talla:M UPM" CLOTHES 15 3` (Add personalizable clothes)
```
{class:ProductPersonalized, id:5, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0, maxPersonal:3}
prod add: ok
```

### `prod add 6 "Camiseta talla:L UPM" CLOTHES 20 4` (Add personalizable clothes)
```
{class:ProductPersonalized, id:6, name:'Camiseta talla:L UPM', category:CLOTHES, price:20.0, maxPersonal:4}
prod add: ok
```

### `prod add 2026-12-21 INSURANCE` (Add insurance service)
```
{class:ProductService, id:1, category:INSURANCE, expiration:Mon Dec 21 00:00:00 CET 2026}
prod add: ok
```

### `prod add 2026-12-24 TRANSPORT` (Add transport service)
```
{class:ProductService, id:2, category:TRANSPORT, expiration:Thu Dec 24 00:00:00 CET 2026}
prod add: ok
```

### `prod list` (List all services and products)
```
Catalog:
  {class:Product, id:0, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}
  {class:ProductService, id:1, category:INSURANCE, expiration:Mon Dec 21 00:00:00 CET 2026}
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0}
  {class:ProductService, id:2, category:TRANSPORT, expiration:Thu Dec 24 00:00:00 CET 2026}
  {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}
  {class:ProductPersonalized, id:5, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0, maxPersonal:3}
  {class:ProductPersonalized, id:6, name:'Camiseta talla:L UPM', category:CLOTHES, price:20.0, maxPersonal:4}
  {class:Meeting, id:23456, name:'Reunion Rotonda', price:12.0, date of Event:2026-12-21, max people allowed:100}
  {class:Meeting, id:23457, name:'Graduacion ETSISI', price:40.0, date of Event:2026-12-21, max people allowed:30}
  {class:Food, id:23459, name:'Restaurante Asador', price:50.0, date of Event:2026-12-21, max people allowed:40}
prod list: ok
```

## Ticket command

**Observation: the local system time is probably set to 2025/11/24 18:21**. TImedProduct expiry date
are inconsistent. The output should not be taken as the correct output. But only as a template example.

After ANY ticket command, the program should print the ticket in its entirety (calling ticket print, but
without closing the ticket).

### `ticket new UW1234567 55630667S` (New ticket with automatic ID)
```
Ticket : 25-11-14-18:21-92581
  Total price: 0.0
  Total discount: 0.0
  Final Price: 0.0
ticket new: ok
```

Note: `ticket add` should invoke `ticket print` to display the current state of the ticket, **without closing the ticket**

### `cash tickets UW1234567` (List tickets created by UW1234567)
```
Tickets: 
  25-11-14-18:21-92581->EMPTY
cash tickets: ok
```

### `ticket new 212121 UW1234567 55630667S` (New ticket with specified ID)
```
Ticket : 212121
  Total price: 0.0
  Total discount: 0.0
  Final Price: 0.0
ticket new: ok
```

### `ticket add 212121 UW1234567 1 20` (Add normal products to ticket 212121)
```
Ticket : 212121
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  Total price: 600.0
  Total discount: 60.0
  Final Price: 540.0
ticket add: ok
```

### `ticket list` (List all tickets)
```
Ticket List:
  25-11-14-18:21-92581 - EMPTY
  212121 - OPEN
ticket list: ok
```

### `ticket print 212121 UW1234567` (Print and close ticket 212121)
```
Ticket : 212121-25-11-14-18:21
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  Total price: 600.0
  Total discount: 60.0
  Final Price: 540.0
ticket print: ok
```

Note: ticket 212121 ID has changed.

### `ticket list` (Verify if ticket 212121 state is closed)
```
Ticket List:
  25-11-14-18:21-92581 - EMPTY
  212121-25-11-14-18:21 - CLOSE
ticket list: ok
```

## Ticket Command (example with various products)

### `ticket new 212123 UW1234567 98948334B` (Create new ticket with ID 212123)
```
Ticket : 212123
  Total price: 0.0
  Total discount: 0.0
  Final Price: 0.0
ticket new: ok
```

### `ticket add 212123 UW1234567 1 20` (Add 20 books to ticket 212123)
```
Ticket : 212123
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  Total price: 600.0
  Total discount: 60.0
  Final Price: 540.0
ticket add: ok
```

### `ticket add 212123 UW1234567 2 1` (Add a shirt to ticket 212123)
```
Ticket : 212123
  {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  Total price: 615.0
  Total discount: 60.0
  Final Price: 555.0
ticket add: ok
```
### `ticket remove 212123 UW1234567 2` (Remove all clothes from ticket 212123)
```
Ticket : 212123
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  Total price: 600.0
  Total discount: 60.0
  Final Price: 540.0
ticket remove: ok
```

This would also remove personalized products.

### `ticket add 212123 UW1234567 2 3` (Add 3 clothes to ticket 212123)
```
Ticket : 212123
  {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0} **discount -1.05
  {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0} **discount -1.05
  {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0} **discount -1.05
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  Total price: 645.0
  Total discount: 63.15
  Final Price: 581.85
ticket add: ok
```

### `ticket list` (Verify ticket 212123 is open)
```
Ticket List:
  25-11-14-18:21-92581 - EMPTY
  212121-25-11-14-18:21 - CLOSE
  212123 - OPEN
ticket list: ok
```

### `ticket print 212123 UW1234567` (Print and close ticket 212123)
```
Ticket : 212123-25-11-14-18:21
  {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0} **discount -1.05
  {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0} **discount -1.05
  {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0} **discount -1.05
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
  Total price: 645.0
  Total discount: 63.15
  Final Price: 581.85
ticket print: ok
```

### `ticket list` (Verify ticket 212123 is closed)
```
Ticket List:
  25-11-14-18:21-92581 - EMPTY
  212121-25-11-14-18:21 - CLOSE
  212123-25-11-14-18:21 - CLOSE
ticket list: ok
```

## Ticket Command (example with food/meeting)

### `ticket new 212127 UW1234567 98948334B` (New ticket with ID 212127)
```
Ticket : 212127
  Total price: 0.0
  Total discount: 0.0
  Final Price: 0.0
ticket new: ok
```

### `ticket add 212127 UW1234567 23456 20` (Add meeting to ticket 212127)
```
Ticket : 212127
  {class:Meeting, id:23456, name:'Reunion Rotonda', price:240.0, date of Event:2025-11-21, max people allowed:100, actual people in event:20}
  Total price: 240.0
  Total discount: 0.0
  Final Price: 240.0
ticket add: ok
```

### `ticket add 212127 UW1234567 2 3` (Add clothes to ticket 212127)
```
Ticket : 212127
  {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0} **discount -1.05
  {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0} **discount -1.05
  {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0} **discount -1.05
  {class:Meeting, id:23456, name:'Reunion Rotonda', price:240.0, date of Event:2025-11-21, max people allowed:100, actual people in event:20}
  Total price: 285.0
  Total discount: 3.1499999
  Final Price: 281.85
ticket add: ok
```

Note: rounding error present in the output

### `ticket print 212127 UW1234567` (Print and close ticket 212127)
```
Ticket : 212127-25-11-14-18:21
  {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0} **discount -1.05
  {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0} **discount -1.05
  {class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0} **discount -1.05
  {class:Meeting, id:23456, name:'Reunion Rotonda', price:240.0, date of Event:2025-11-21, max people allowed:100, actual people in event:20}
  Total price: 285.0
  Total discount: 3.1499999
  Final Price: 281.85
ticket print: ok
```

### `ticket list` (Verify ticket 212127 is closed)
```
Ticket List:
  25-11-14-18:21-92581 - EMPTY
  212121-25-11-14-18:21 - CLOSE
  212123-25-11-14-18:21 - CLOSE
  212127-25-11-14-18:21 - CLOSE
ticket list: ok
```

## Ticket Commands (example with personalized products)

### `ticket new 212128 UW1234567 98948334B` (New ticket with ID 212128)
```
Ticket : 212128
  Total price: 0.0
  Total discount: 0.0
  Final Price: 0.0
ticket new: ok
```

### `ticket add 212128 UW1234567 5 1` (Add personalizable clothes WITHOUT personalization to ticket 212128)
```
Ticket : 212128
  {class:ProductPersonalized, id:5, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0, maxPersonal:3}
  Total price: 15.0
  Total discount: 0.0
  Final Price: 15.0
ticket add: ok
```

### `ticket add 212128 UW1234567 5 3 --pred --pblue --pgreen` (Add various personalized clothes to ticket 212128)
```
Ticket : 212128
  {class:ProductPersonalized, id:5, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0, maxPersonal:3} **discount -1.05
  {class:ProductPersonalized, id:5, name:'Camiseta talla:M UPM', category:CLOTHES, price:19.5, maxPersonal:3, personalizationList:[red, blue, green]} **discount -1.365
  {class:ProductPersonalized, id:5, name:'Camiseta talla:M UPM', category:CLOTHES, price:19.5, maxPersonal:3, personalizationList:[red, blue, green]} **discount -1.365
  {class:ProductPersonalized, id:5, name:'Camiseta talla:M UPM', category:CLOTHES, price:19.5, maxPersonal:3, personalizationList:[red, blue, green]} **discount -1.365
  Total price: 73.5
  Total discount: 5.145
  Final Price: 68.355
ticket add: ok
```

### `ticket add 212128 UW1234567 6 3 --pred --pblue` (Add other personalized clothes to ticket 212128)
```
Ticket : 212128
  {class:ProductPersonalized, id:6, name:'Camiseta talla:L UPM', category:CLOTHES, price:24.0, maxPersonal:4, personalizationList:[red, blue]} **discount -1.68
  {class:ProductPersonalized, id:6, name:'Camiseta talla:L UPM', category:CLOTHES, price:24.0, maxPersonal:4, personalizationList:[red, blue]} **discount -1.68
  {class:ProductPersonalized, id:6, name:'Camiseta talla:L UPM', category:CLOTHES, price:24.0, maxPersonal:4, personalizationList:[red, blue]} **discount -1.68
  {class:ProductPersonalized, id:5, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0, maxPersonal:3} **discount -1.05
  {class:ProductPersonalized, id:5, name:'Camiseta talla:M UPM', category:CLOTHES, price:19.5, maxPersonal:3, personalizationList:[red, blue, green]} **discount -1.365
  {class:ProductPersonalized, id:5, name:'Camiseta talla:M UPM', category:CLOTHES, price:19.5, maxPersonal:3, personalizationList:[red, blue, green]} **discount -1.365
  {class:ProductPersonalized, id:5, name:'Camiseta talla:M UPM', category:CLOTHES, price:19.5, maxPersonal:3, personalizationList:[red, blue, green]} **discount -1.365
  Total price: 145.5
  Total discount: 10.184999
  Final Price: 135.315
ticket add: ok
```

### `ticket print 212128 UW1234567` (Print and close ticket 212128)
```
Ticket : 212128-25-11-14-18:21
  {class:ProductPersonalized, id:6, name:'Camiseta talla:L UPM', category:CLOTHES, price:24.0, maxPersonal:4, personalizationList:[red, blue]} **discount -1.68
  {class:ProductPersonalized, id:6, name:'Camiseta talla:L UPM', category:CLOTHES, price:24.0, maxPersonal:4, personalizationList:[red, blue]} **discount -1.68
  {class:ProductPersonalized, id:6, name:'Camiseta talla:L UPM', category:CLOTHES, price:24.0, maxPersonal:4, personalizationList:[red, blue]} **discount -1.68
  {class:ProductPersonalized, id:5, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0, maxPersonal:3} **discount -1.05
  {class:ProductPersonalized, id:5, name:'Camiseta talla:M UPM', category:CLOTHES, price:19.5, maxPersonal:3, personalizationList:[red, blue, green]} **discount -1.365
  {class:ProductPersonalized, id:5, name:'Camiseta talla:M UPM', category:CLOTHES, price:19.5, maxPersonal:3, personalizationList:[red, blue, green]} **discount -1.365
  {class:ProductPersonalized, id:5, name:'Camiseta talla:M UPM', category:CLOTHES, price:19.5, maxPersonal:3, personalizationList:[red, blue, green]} **discount -1.365
  Total price: 145.5
  Total discount: 10.184999
  Final Price: 135.315
ticket print: ok
```

### `ticket new 212129 UW1234567 B12345674 -s` (Create a Service only Ticket)
```
Ticket : 212129
ticket new: ok
```

### `ticket add 212129 UW1234567 1S` (Add insurance service to ticket)
```
Ticket : 212129
Services Included: 
  {class:ProductService, id:1, category:INSURANCE, expiration:Mon Dec 21 00:00:00 CET 2026}
ticket add: ok
```

### `ticket add 212129 UW1234567 2S` (Add transport service to ticket)
```
Ticket : 212129
Services Included: 
  {class:ProductService, id:1, category:INSURANCE, expiration:Mon Dec 21 00:00:00 CET 2026}
  {class:ProductService, id:2, category:TRANSPORT, expiration:Thu Dec 24 00:00:00 CET 2026}
ticket add: ok
```

### `ticket print 212129 UW1234567` (Print service only ticket)
```
Ticket : 212129-25-11-14-18:21
Services Included: 
  {class:ProductService, id:1, category:INSURANCE, expiration:Mon Dec 21 00:00:00 CET 2026}
  {class:ProductService, id:2, category:TRANSPORT, expiration:Thu Dec 24 00:00:00 CET 2026}
ticket add: ok
```

### `ticket new 212130 UW1234567 B12345674 -c` (Create a combined ticket)
```
Ticket : 212130
ticket new: ok
```

### `ticket add 212130 UW1234567 1S` (Add insurance service to ticket)
```
Ticket : 212130
Services Included:
  {class:ProductService, id:1, category:INSURANCE, expiration:Mon Dec 21 00:00:00 CET 2026} 
ticket add: ok
```

### `ticket add 212130 UW1234567 2S` (Add transport service to ticket)
```
Ticket : 212130
Services Included:
  {class:ProductService, id:1, category:INSURANCE, expiration:Mon Dec 21 00:00:00 CET 2026}
  {class:ProductService, id:2, category:TRANSPORT, expiration:Thu Dec 24 00:00:00 CET 2026} 
ticket add: ok
```

### `ticket add 212130 UW1234567 23456 20` (Add meeting to ticket)
```
Ticket : 212130
Services Included:
  {class:ProductService, id:1, category:INSURANCE, expiration:Mon Dec 21 00:00:00 CET 2026}
  {class:ProductService, id:2, category:TRANSPORT, expiration:Thu Dec 24 00:00:00 CET 2026}
Product Included
  {class:Meeting, id:23456, name:'Reunion Rotonda', price:240.0, date of Event:2025-12-21, max people allowed:100, actual people in event:20}
  Total price: 240.0
  Extra Discount from services:72.0 **discount -72.0
  Total discount: 72.0
  Final Price: 168.0
ticket add: ok
```
### `ticket print 212130 UW1234567` (Print and close combined ticket)
```
Ticket : 212130-25-11-14-18:21
Services Included:
  {class:ProductService, id:1, category:INSURANCE, expiration:Mon Dec 21 00:00:00 CET 2026}
  {class:ProductService, id:2, category:TRANSPORT, expiration:Thu Dec 24 00:00:00 CET 2026}
Product Included
  {class:Meeting, id:23456, name:'Reunion Rotonda', price:240.0, date of Event:2025-12-21, max people allowed:100, actual people in event:20}
  Total price: 240.0
  Extra Discount from services:72.0 **discount -72.0
  Total discount: 72.0
  Final Price: 168.0
ticket add: ok
```

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
- Input/Output should be mostly matching input/output examples in E2 statement. Specially command formats.
- Creating more commands outside what is defined in E1/E2 statement is prohibited.
- Project exposition/defense: project must be able to execute all commands without any errors and following expected
behaviour.

# Other comments

- Command keywords are expected to be lowercase and category keywords are expected to be uppercase (E1 does not specify,
but hinted with example outputs)
- The **assumptions** outlined in E1 (200 different products max. and 100 items max. in ticket) are currently treated
as hard limitations in our project
- E1 restriction has remained in E2