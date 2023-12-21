INSERT INTO books (id, title, author, isbn, price)
VALUES (1, 'Harry Potter and the Philosopher\'s Stone', 'Rowling, J.K', '9781408855898', 17.83),
       (2, 'And Then There Were None', 'Agatha Christie', '9780008123208', 10.73);

INSERT INTO categories (id, name)
VALUES (1, 'Fantasy'),
       (2, 'Detective');

INSERT INTO books_categories (book_id, category_id)
VALUES (1, 1),
       (2, 2);
