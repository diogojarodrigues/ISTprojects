INSERT INTO customers ("name", email, phone, "address")
VALUES
    ('Manuel', 'manuel24@gmail.com', '933444555', 'Rua dos Clerigos 14 1Esq'),
    ('Joao', 'joao-1@gmail.com', '933423662', 'Rua do Cacto 10 2Dir'),
    ('Rui', 'ruiii@gmail.com', '924444333', 'Rua das Rosas 110 2Esq'),
    ('Fabio', 'fmata@gmail.com', '933333333', 'Avenida da Liberdade 130 1Dir'),
    ('Nuno', 'ngfg@gmail.com', '922777777', 'Rua dos Assaltos 25 2Esq'),
    ('Diogo', 'yasuo@gmail.com', '911222333', 'Largo D. Afonso Henriques 60 R/C Esq'),
    ('Pedro', 'piedro@gmail.com', '934567890', 'Rua D. Sancho I 40 1Dir'),
    ('Ana', 'ana@gmail.com', '933322111', 'Rua Do Grilo 22 3Dir'),
    ('Celia', 'celia23@sapo.com', '921444222', 'Rua das Trigosas 13 2Esq'),
    ('Sara', 'sara@outlook.com', '933333322', 'Rua da Revolucao 79 2Dir');

INSERT INTO orders ("date", cust_no)
VALUES
    (DATE '2022-01-05', 1),
    (DATE '2022-01-10', 2),
    (DATE '2022-01-15', 3),
    (DATE '2022-04-05', 4),
    (DATE '2023-01-02', 5),
    (DATE '2023-01-05', 6),
    (DATE '2023-01-10', 7),
    (DATE '2023-01-15', 8),
    (DATE '2023-01-20', 9),
    (DATE '2023-01-25', 10);

INSERT INTO sales (order_no) VALUES (1),(3),(5),(7),(9);

INSERT INTO pay (order_no, cust_no)
VALUES
    (1,1), (3,2), (5,4), (7,5), (9,7);

INSERT INTO employees (ssn, tin, bdate, "name")
VALUES
    ('123-45-6789', '987654321', DATE '1990-01-01', 'John Doe'),
    ('987-65-4321', '123456789', DATE '1992-03-15', 'Jane Smith'),
    ('456-78-9012', '654321987', DATE '1988-06-30', 'Michael Johnson'),
    ('789-01-2345', '789456123', DATE '1995-12-10', 'Emily Davis'),
    ('234-56-7890', '321654987', DATE '1991-09-20', 'Daniel Wilson'),
    ('567-89-0123', '987123654', DATE '1987-04-05', 'Sarah Thompson'),
    ('890-12-3456', '456789123', DATE '1993-07-25', 'Christopher Lee'),
    ('345-67-8901', '789123456', DATE '1989-11-18', 'Olivia Martin'),
    ('678-90-1234', '123987456', DATE '1994-02-14', 'Matthew Taylor'),
    ('901-23-4567', '321789654', DATE '1985-08-08', 'Sophia Anderson'),
    ('111-22-3333', '999888777', DATE '1997-05-03', 'Jessica Adams'),
    ('222-33-4444', '777666555', DATE '1984-12-20', 'Andrew Wilson');



INSERT INTO process (ssn, order_no)
VALUES
    ('123-45-6789', 1),
    ('987-65-4321', 2),
    ('456-78-9012', 3),
    ('789-01-2345', 4),
    ('234-56-7890', 5),
    ('567-89-0123', 6),
    ('890-12-3456', 7),
    ('345-67-8901', 8),
    ('678-90-1234', 9),
    ('901-23-4567', 10);


INSERT INTO departments ("name")
VALUES
    ('Marketing'),
    ('Sales'),
    ('Finance'),
    ('Human Resources'),
    ('Operations'),
    ('Productions');

INSERT INTO workplaces ("address", lat, long)
VALUES
    ('Avenida da Liberdade 10', 40.7128, -70.2345),
    ('Rua dos Clerigos 5', 38.2228, -80.4454),
    ('Alameda D. Afonso Henriques 35', 30.3344, -69.0003),
    ('Rua das Cruzes 7', 39.3333, -85.2222);

INSERT INTO works (ssn, "name", "address")
VALUES
    ('123-45-6789', 'Marketing', 'Avenida da Liberdade 10'),
    ('987-65-4321', 'Marketing', 'Avenida da Liberdade 10'),
    ('456-78-9012', 'Sales', 'Alameda D. Afonso Henriques 35'),
    ('789-01-2345', 'Sales', 'Rua dos Clerigos 5'),
    ('234-56-7890', 'Finance', 'Avenida da Liberdade 10'),
    ('567-89-0123', 'Finance', 'Alameda D. Afonso Henriques 35'),
    ('890-12-3456', 'Human Resources', 'Rua das Cruzes 7'),
    ('345-67-8901', 'Human Resources', 'Avenida da Liberdade 10'),
    ('678-90-1234', 'Operations', 'Rua das Cruzes 7'),
    ('901-23-4567', 'Operations', 'Rua dos Clerigos 5'),
    ('111-22-3333', 'Productions', 'Rua das Cruzes 7'),
    ('222-33-4444', 'Productions', 'Rua das Cruzes 7');

INSERT INTO offices ("address")
VALUES
    ('Avenida da Liberdade 10'),
    ('Alameda D. Afonso Henriques 35');
    
INSERT INTO warehouses ("address")
VALUES
    ('Alameda D. Afonso Henriques 35'),
    ('Rua das Cruzes 7');


INSERT INTO products (sku, "name", "description", price)
VALUES 
    ('SKU111', 'Product 1', 'This is the description of product 1.', 9.99),
    ('SKU222', 'Product 2', 'This is the description of product 2.', 24.99),
    ('SKU333', 'Product 3', 'This is the description of product 3.', 49.99),
    ('SKU444', 'Product 4', 'This is the description of product 4.', 64.99),
    ('SKU555', 'Product 5', 'This is the description of product 5.', 89.99);


INSERT INTO ean_products (sku, ean)
VALUES
    ('SKU111', 5901234123457),
    ('SKU222', 8712345678901),
    ('SKU333', 1234567890123);

INSERT INTO suppliers (tin, "name", "address", sku, "date")
VALUES
    ('123456789', 'John Doe', '123 Main Street', 'SKU111', DATE '2023-05-21'),
    ('987654321', 'Jane Smith', '456 Elm Avenue', 'SKU222', DATE '2023-05-21'),
    ('456789123', 'David Johnson', '789 Oak Lane', 'SKU333', DATE '2023-05-21'),
    ('321654987', 'Emily Davis', '987 Maple Road', 'SKU444', DATE '2023-05-21'),
    ('789123456', 'Michael Wilson', '321 Pine Court', 'SKU555', DATE '2023-05-21');
    -- SKU444 e SKU555 acima de 50$
    -- SKU444 mais vendido
    -- SKU555 mais encomendado

INSERT INTO contains (order_no, sku, qty)
VALUES
    (1, 'SKU111', 1),
    (2, 'SKU111', 2),
    (3, 'SKU111', 3),
    (4, 'SKU111', 4),
    -- Mistura
    (5, 'SKU111', 5),
    (5, 'SKU444', 6),
    (6, 'SKU222', 7),
    (6, 'SKU333', 8),
    (6, 'SKU555', 9),
    -- Abaixo
    (7, 'SKU333', 10),
    (7, 'SKU222', 11),
    (8, 'SKU111', 12),
    (8, 'SKU222', 13),
    -- Acima
    (9, 'SKU444', 14),
    (9, 'SKU555', 15),
    (10, 'SKU444', 16),
    (10, 'SKU555', 17);


INSERT INTO delivery ("address", tin)
VALUES
    ('Alameda D. Afonso Henriques 35', '123456789'),
    ('Alameda D. Afonso Henriques 35', '987654321'),
    ('Alameda D. Afonso Henriques 35', '456789123'),
    ('Rua das Cruzes 7', '321654987'),
    ('Rua das Cruzes 7', '789123456');
