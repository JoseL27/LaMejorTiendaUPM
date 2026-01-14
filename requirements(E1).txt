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
