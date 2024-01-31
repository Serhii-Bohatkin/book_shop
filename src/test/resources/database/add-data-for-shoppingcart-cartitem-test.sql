INSERT INTO shopping_carts (id, user_id)
VALUES (1, 1);

INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity)
VALUES (1, 1, 1, 1),
       (2, 1, 2, 1);

INSERT INTO users (id, email, password, first_name, last_name)
VALUES (1, 'user@gmail.com', '12345678', 'John', 'Doe');

INSERT INTO books (id, title, author, isbn, price)
VALUES (1, 'Harry Potter and the Philosopher\'s Stone', 'Rowling, J.K', '9781408855898', 17.83),
       (2, 'And Then There Were None', 'Agatha Christie', '9780008123208', 10.73);
