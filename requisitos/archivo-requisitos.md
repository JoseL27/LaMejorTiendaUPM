E1 Version




E2 Version

Ticket, inventory, Client/Cashier module.

This program should allow the end user to create/update/delete/list products. Create/remove/list registered cashiers and clients.

Using the products created, the end user should also have the ability to create a new ticket, adding in any product with any amount (E1: less or equal than 100), remove products, or print the ticket (listing all products in ticket, applying discounts, and showing to the end user product information, discounts applied, total price, discount amount, and final price).
Users

Any user is an entity which is modeled after a person. Any person has at least an identifier, name, and an E-Mail address (contact).

Note: the term "end user" refers to the user which is using this program, not any entity or User class in the program design.
Cashier

A client is another kind of user which creates the ticket for the client.

A cashier is identified by their Worker ID (Starting with UW and a 7-digit number), name, and its company E-mail address (Ending in domain @upm.es)

    ID (Worker ID), must have format UW#######, starting with the UW prefix and 7 numbers
    Name
    Company E-mail address (Must en in domain @upm.es)

The cashier holds a list of tickets created by itself. If the cashier is removed, then its associated tickets will be also removed.
Cashier commands

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

Client

A client is a kind of user, which a cashier must register before creating any ticket.

A client is identified by their national ID number (Spain: DNI), name, and (personal) E-mail address. During the registration, the cashier who registered the client should be stored inside the Client as an attribute.

    ID (National ID) (Must be non-null, should have at least one character)
    Name
    E-Mail address, any E-mail address is possible
    Cashier which registered the client, cashierID must be valid

Client commands

+ client add "<name>" <id> <email> <cashierID>
  Register a new client with specified CashierID. 
  CashierID should be associated to a valid cashier.
  ID would be the client's national ID.

+ client remove <id>
  Removes a client from the registry.
  Note: this would NOT remove any ticket associated to the client.

+ client list
  Lists all registered client's information (id, name, email, cashier) ordered alphabetically by name.

Products

The products are split into two kinds, products with expiry time, and normal products.

Note: price calculation will be detailed in the Ticket section
Normal products

Normal products could refer to any object, such as T-Shirts, books, pens and mugs.

Attributes: ID, Name, Price, Category, Customization Amount (Customizations would be a list of strings, Customization amount refers to the size of the list).

    ID must be positive (0 should be invalid)
    Name must be less than 100 characters (E1 Restriction)
    Price (per piece) must be positive (0 is invalid)
    Category fixed with discount rates: MERCH(0%), STATIONERY(5%), CLOTHES(7%), BOOK(10%), ELECTRONICS(3%)
    Amount of customizations (0 or undefined means non-customizable product), once set then it could not be altered. Product A and Product B are different if they have different IDs.

For E1: No more than 200 different products

Any normal product now has the option to be customized, when adding the product to the inventory, the end user may specify how many customizations a product can have. And using ticket add --p command to specify what customization will be present in the product.

Adding a customization to a normal product during ticket add will add 10% to the cost of the product (1 customization = 10% added cost, 2 = 20%, 3 = 30%, until the Customization Amount limit specified during the product registration in Inventory). Any customizable product (normal product registered in the inventory with customization amount > 0) can be added to ticket with or without any customizations.

After the product is registered in the inventory, the end user will be unable to alter the number of customization that any product can bear.
Timed product

Timed products (or product with time limitation) is usually a kind of service, which could not be added before a certain time to the ticket.

A timed product is a product in inventory which has an expiration date and a preparation period. After passing the expiration date, the product could not be added to any ticket. The preparation period is a time period placed before the expiration date, during this period, the product could not be added to any ticket. There are two kinds of product with time limitation, food which has a preparation period of 72 hours, and meetings which has 12 hours.

For example: a food catering service with pizza and pasta, with specified expiration date of 24th November 2025 @ 00:00. Such service may be added at any time before 21st November 2025 @ 00:00. During 21st and 24th, this product is in the preparation period and could not be added to any ticket.

    ID must be positive (0 should be invalid)
    Name must be less than 100 characters (E1 Restriction)
    Price (per person) must be positive (0 is invalid)
    Maximum amount of participants (must be less or equal than 100)
    Expiration date
    Product A and Product B are different if they have different IDs.

Preparation period is dependent on the kind of product (food: 72h or meeting: 12h).
Commands for Products:

+ prod add [<id>] "<name>" <category> <price> [<MaxPersonalizationAmount>]
  Add a new normal product to the inventory. The product may be personalized specifying the maximum
  amount for the customization string list. Set to 0 if not specified.
  ID is now optional. ID will be generated automatically if omitted.

+ prod addFood [<id>] "<name>" <price> <expiration: yyyy-MM-dd> <maxPeople>
  Add a new timed product (Food) to the inventory. This kind of product has a preparation period of 72h.
  <expiration> should follow strictly by the yyyy-mm-dd format (e.g. 2025-11-25)
  ID is now optional. ID will be generated automatically if omitted.

+ prod addMeeting [<id>] "<name>" <price> <expiration: yyyy-MM-dd> <maxPeople>
  Add a new timed product (Meeting) to the inventory. This kind of product has a preparation period of 12h.
  <expiration> should follow strictly by the yyyy-mm-dd format (e.g. 2025-11-25)
  ID is now optional. ID will be generated automatically if omitted.

+ prod list
  List all registered products (normal products and products with time limitations)

+ prod update <id> <field> <value>
  Update specific attributes of a specific product, checking restrictions on values.
  Possible values for <field>: NAME|CATEGORY|PRICE
  Update category will be an invalid operation on products with time limitations, since these kind of product
  does not have an category field.

+ prod remove <id>
  Remove products, eliminate appearances in ticket <-- Unspecified but we should assume this behaviour to prevent inconsistencies

Tickets

A list containing products, product amount, and if applicable, customizations (Normal product), participants (Timed Product only).

The ticket will be associated to one of 3 states:

    EMPTY (The ticket is empty, no products were added, and more products can be added)
    OPEN (The ticket contains at least one product, and more products can be added)
    CLOSE (No more products can be added to this ticket)

Once a ticket is closed, the end user should be able to print the ticket again specifying the corresponding Cashier ID and Ticket ID.

For E1: No more than 100 items (sum of all amount of products contained in ticket's product list <= 100)

A timed product with any arbitrary amount of participants in the ticket should be counted as 1 item. For example, a pizza catering service with 30 participants (50 max.) should be counted as one item in the ticket.

A timed product with the same ID could not be added again to the same ticket.

Ticket price calculation: if there are 2 or more products of the same category, the product in question should be discounted at the rate (percentage) mentioned in Product Category.

If a product is customizable but during ticket add no customization is added, sell as a normal product without any additional costs (0 customization = 0% added cost)

Important: customized product should be eligible for category discounts. The price to subtract is the price of the product after the added cost due to customizations, multiplied by the category discount percentage

customizationPercent = numCustomizations * 10% (numCustomizations: amount of customization strings specified during ticket add)
productPriceWithCustomization = baseProductPrice + (baseProductPrice * customizationPercent)
                                                    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                                                      Additional cost due to customization
finalProductPrice = productPriceWithCustomization - (productPriceWithCustomization * categoryPercent)
                                                     ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                                                      Cost to discount according to product category

For E1: No more than 100 different items (sum of all added product counts) should be allowed inside the ticket.

I.e. Add 50 Product A in ticket (50 items), add 50 Product B (50 items) in ticket. Now that the total items in the ticket is 100 (50 Prod. A + 50 Prod. B), no more items are allowed to be added.
Ticket ID

All tickets are now identified with an ID. During ticket creation (ticket new command), the ID will be generated automatically if not specified. The automatically generated ID will have the following format: YY-MM-dd-HH:mm-##### where YY:year (2 digit), MM:month (2 digit), dd:day (2 digit), HH:hour (24-hour format), mm:minute, #####: random 5-digit number.

When the ticket is closed, the ticket ID will change, appending the date/time when the ticket is closed.

Examples:

Assuming ticket open date will be always 16/11/2025 11:53, and ticket close date will be always 19/11/2025 16:41, random 5-digit number will be always 00721.

When ticket ID is generated automatically:

    Created: 25-11-16-11:53-00721
    Closed: 25-11-16-11:53-00721-25-11-19-16:41

When ticket ID is explicitly specified (For example: 157194)

    Created: 157194
    Closed: 157194-25-11-19-16:41

Observation: when ticket ID is specified, the opening date and random 5-digit number is replaced entirely with the ticket ID specified. But closing the ticket will still append the closing date of the ticket.

Observation 2: looking at the E2 specification, it looks like the ID is implemented with a string, it is uncertain if the ticket ID should allow non-numeric IDs (Such as 21263s, closing -> 21263s-25-11-19-16:41)

Observation 3: to print any closed ticket, it is uncertain if the end user should specify the full ticket ID with the closing date, or any part matching the ID unambiguously should suffice. For example:

    Generated ID: 25-11-16-11:53-00721-25-11-19-16:41, or 25-11-16-11:53-00721, even 25-11-16-11:53 or 00721 if unambiguous
    Custom ID: 157194-25-11-19-16:41, or 157194, even 25-11-19-16:41 if unambiguous

Commands for Ticket

+ ticket new [<ID>] <CashierID> <ClientID>
  Create new empty ticke and associate it to a valid cashier and client.
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
  Prints current ticket with product information and discount.
  Calculate total price, discount amount, and discounted total price.
  Product listing should be in alphabetical order. And closes the ticket.

+ ticket list
  Lists all tickets IDs created and their states, ordered by Cashier's ID.
  For example: UW0000001 has 222222, UW0000002 has 212121, 222121. This command will list
  ticket IDs created by UW0000001 and then ticket IDs created by UW0000002.
  222222 - EMPTY
  212121 - CLOSED
  222121 - OPEN

Auxiliary commands

+ echo "Content"
  Echoes back whatever user just entered in quotes

+ help
  Displays available commands, product category and discount rates

+ exit
  Prints a goodbye message and gracefully closes the program

Examples

tUPM> echo "saba SABA さば ˢᵃᵇᵃ"
"saba SABA さば ˢᵃᵇᵃ"

tUPM> help
Commands:
  client add "<nombre>" <DNI> <email> <cashId>
  client remove <DNI>
  client list
  cash add [<id>] "<nombre>"<email>
  cash remove <id>
  cash list
  cash tickets <id>
  ticket new [<id>] <cashId> <userId>
  ticket add <ticketId><cashId> <prodId> <amount> [--p<txt> --p<txt>] 
  ticket remove <ticketId><cashId> <prodId> 
  ticket print <ticketId> <cashId> 
  ticket list
  prod add <id> "<name>" <category> <price>
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

