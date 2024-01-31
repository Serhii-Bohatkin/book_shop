INSERT INTO orders (id, user_id, status, total, order_date, shipping_address)
VALUES (1, 1, 'NEW', 18.0, '2023-12-25T10:37:33', 'St. Main-Street 1');

INSERT INTO users (id, email, password, first_name, last_name)
VALUES (1, 'user@gmail.com', '12345678', 'John', 'Doe');

INSERT INTO books (id, title, author, isbn, price)
VALUES (1, 'Harry Potter and the Philosopher\'s Stone', 'Rowling, J.K', '9781408855898', 18.0);

INSERT INTO order_items (id, order_id, book_id, quantity, price)
VALUES (1, 1, 1, 1, 18.0);

INSERT INTO shopping_carts (id, user_id)
VALUES (1, 1);

INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity)
VALUES (1, 1, 1, 1);
