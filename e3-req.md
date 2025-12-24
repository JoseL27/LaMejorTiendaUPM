E3 Version

Ticket, ... , local persistence

The clients now add the enterprise clients type identfied by NIF 

A service is identified with a unique Id generated secuentially starting from 1 but must have an 'S' at the end of the id.
A service price is calculated the exact moment the receipt is made, never when making the receipt.
A service doesn't have a name. nor a price. It will only have a date limiting it's addition to and closure in the tickets.

The tickets now are devided in common tickets and enterprise tickets. The tickets will be typed according to the user, common tickets for common client users and enterprise tickets for enterprise client users.
The common tickets only can have products. The enterprise tickets can have only products, only services, or both.
On the enterprise tickets when printing the tickets:
+ If it is a product only ticket, prints it as a normal ticket.
+ If it is a service only ticket, don't print prices nor discounts as there aren't any
+ If it is a products-services ticket, print a 15% discount per service contracted over the final price and the ticket can't be closed unless there are 1 product and 1 service.

It's crucial to have persistence so the store's state can be restored when turning on the application.


