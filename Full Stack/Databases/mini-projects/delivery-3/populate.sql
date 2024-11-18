INSERT INTO
    customer (cust_no, "name", email, phone, "address")
VALUES
    (
        1,
        'Ethan Reynolds',
        'ethan.reynolds@gmail.com',
        '933444555',
        'Rua dos Clerigos 14 1Esq 4050-205 Vitoria'
    ),
    (
        2,
        'Olivia Foster',
        'olivia.foster@gmail.com',
        '933423662',
        'Rua do Cacto 10 2Dir 1200-234 Alcantara'
    ),
    (
        3,
        'Liam Anderson',
        'liam.anderson@gmail.com',
        '924444333',
        'Rua das Rosas 110 2Esq 1000-500 Alvalade'
    ),
    (
        4,
        'Ava Mitchell',
        'ava.mitchell@gmail.com',
        '933333333',
        'Avenida da Liberdade 130 1Dir 1250-142 Santo Antonio'
    ),
    (
        5,
        'Noah Bennett',
        'noah.bennett@gmail.com',
        '922777777',
        'Rua dos Assaltos 25 3000-500 Odivelas'
    ),
    (
        6,
        'Isabella Sullivan',
        'isabella.sullivan@gmail.com',
        '911222333',
        'Largo D. Afonso Henriques 60 R/C Esq 3004-500 Ramada'
    ),
    (
        7,
        'Lucas Parker',
        'lucas.parker@outlook.com',
        '934567890',
        'Rua D. Sancho I 40 1Dir 2955-500 Almada'
    ),
    (
        8,
        'Sophia Turner',
        'sophia.turner@outlook.com',
        '933322111',
        'Rua Do Grilo 22 3Dir 5000-500 Porto'
    ),
    (
        9,
        'Mason Peterson',
        'mason.peterson@outlook.com',
        '921444222',
        'Rua das Trigosas 13 2Esq 2005-457 Santarem'
    ),
    (
        10,
        'Harper Evans',
        'harper.evans@outlook.com',
        '933352322',
        'Rua da Revolucao 79 2Dir 2330-500 Torres Novas'
    ),
    (
        11,
        'Benjamin Jenkins',
        'benjamin.jenkins@example.com',
        '912345678',
        'Rua dos Cagados 140 2Esq 3052-105 Gloria'
    ),
    (
        12,
        'Emma Roberts',
        'emma.roberts@example.com',
        '925678901',
        'Rua do Relogio 5 3Dir 1234-232 Santa Maria'
    ),
    (
        13,
        'Alexander Hughes',
        'alexander.hughes@example.com',
        '938123456',
        'Rua das Prosas 119 2Esq 1030-500 Luz'
    ),
    (
        14,
        'Charlotte Bryant',
        'charlotte.bryant@example.com',
        '917456789',
        'Avenida da Graca 74 4Dir 1278-122 Pinhal Novo'
    ),
    (
        15,
        'James Russell',
        'james.russell@example.com',
        '939789012',
        'Rua dos Furtos 57 3012-561 Amadora'
    ),
    (
        16,
        'Mia Nelson',
        'mia.nelson@example.com',
        '925012345',
        'Largo D. Albertina 20 R/C Dir 1888-528 Oriente'
    ),
    (
        17,
        'Samuel Mitchell',
        'samuel.mitchell@example.com',
        '918345678',
        'Rua D. Afonso II 45 2Dir 2832-557 Castelo Branco'
    ),
    (
        18,
        'Amelia Price',
        'amelia.price@example.com',
        '932678901',
        'Rua Do Mato 39 3Dir 5101-503 Leiria'
    ),
    (
        19,
        'Henry Foster',
        'henry.foster@example.com',
        '919901234',
        'Rua das Perigosas 89 1Esq 6605-433 Evora'
    ),
    (
        20,
        'Emily Cox',
        'emily.cox@example.com',
        '936234567',
        'Rua do Protesto 65 1Dir 4513-000 Vendas Novas'
    ),
    (
        21,
        'Daniel Hayes',
        'daniel.hayes@example.com',
        '933567890',
        'Rua dos Fortunatos 19 1Esq 2743-282 Setubal'
    ),
    (
        22,
        'Grace Thompson',
        'grace.thompson@example.com',
        '916890123',
        'Avenida dos Aliados 150 2Dir 1837-453 Santa Maria da Feira'
    ),
    (
        23,
        'Jack Parker',
        'jack.parker@example.com',
        '934123456',
        'Rua dos Cravos 75 5Esq 7915-765 Cacem'
    ),
    (
        24,
        'Lily Sullivan',
        'lily.sullivan@example.com',
        '921456789',
        'Avenida das Lendas 120 1Dir 7645-863 Campo Grande'
    ),
    (
        25,
        'Gabriel Johnson',
        'gabriel.johnson@example.com',
        '937789012',
        'Rua dos Lampioes 57 7465-786 Saldanha'
    );

INSERT INTO
    orders (order_no, "date", cust_no)
SELECT
    generate_series(1, 25),
    (
        TIMESTAMP '2022-01-01' + random() * (TIMESTAMP '2023-12-31' - TIMESTAMP '2022-01-01')
    ) :: DATE,
    generate_series(1, 25);

-- 2.2
INSERT INTO
    orders (order_no, "date", cust_no)
SELECT
    generate_series(26, 390),
    generate_series(
        '2022-01-01' :: TIMESTAMP,
        '2022-12-31' :: TIMESTAMP,
        '1 day'
    ),
    1;

-- 2.3
INSERT INTO
    orders (order_no, "date", cust_no)
SELECT
    generate_series(391, 397),
    generate_series(
        '2022-01-01' :: TIMESTAMP,
        '2022-12-31' :: TIMESTAMP,
        '60 day'
    ),
    1;

-- OLAP
INSERT INTO
    orders (order_no, "date", cust_no)
SELECT
    generate_series(398, 762),
    generate_series(
        '2022-01-01' :: TIMESTAMP,
        '2022-12-31' :: TIMESTAMP,
        '1 day'
    ),
    2;

INSERT INTO
    pay (order_no, cust_no)
VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5),
    (6, 6),
    (7, 7),
    (8, 8),
    (9, 9),
    (10, 10),
    (11, 11),
    (12, 12),
    (13, 13),
    (14, 14),
    (15, 15);

-- 2.2
INSERT INTO
    pay (order_no, cust_no)
SELECT
    generate_series(26, 390),
    1;

-- OLAP
INSERT INTO
    pay (order_no, cust_no)
SELECT
    generate_series(398, 762),
    2;

INSERT INTO
    employee (ssn, tin, bdate, "name")
VALUES
    (
        '123-45-6789',
        '987654321',
        DATE '1990-01-01',
        'John Doe'
    ),
    (
        '987-65-4321',
        '123456789',
        DATE '1992-03-15',
        'Jane Smith'
    ),
    (
        '456-78-9012',
        '654321987',
        DATE '1988-06-30',
        'Michael Johnson'
    ),
    (
        '789-01-2345',
        '789456123',
        DATE '1995-12-10',
        'Emily Davis'
    ),
    (
        '234-56-7890',
        '321654987',
        DATE '1991-09-20',
        'Daniel Wilson'
    ),
    (
        '567-89-0123',
        '987123654',
        DATE '1987-04-05',
        'Sarah Thompson'
    ),
    (
        '890-12-3456',
        '456789123',
        DATE '1993-07-25',
        'Christopher Lee'
    ),
    (
        '345-67-8901',
        '789123456',
        DATE '1989-11-18',
        'Olivia Martin'
    ),
    (
        '678-90-1234',
        '123987456',
        DATE '1994-02-14',
        'Matthew Taylor'
    ),
    (
        '901-23-4567',
        '321789654',
        DATE '1985-08-08',
        'Sophia Anderson'
    ),
    (
        '111-22-3333',
        '999888777',
        DATE '1997-05-03',
        'Jessica Adams'
    ),
    (
        '222-33-4444',
        '777666555',
        DATE '1984-12-20',
        'Andrew Wilson'
    );

INSERT INTO
    process (ssn, order_no)
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
    ('901-23-4567', 10),
    ('123-45-6789', 11),
    ('987-65-4321', 12),
    ('456-78-9012', 13),
    ('789-01-2345', 14),
    ('234-56-7890', 15),
    ('567-89-0123', 16),
    ('890-12-3456', 17),
    ('345-67-8901', 18),
    ('678-90-1234', 19),
    ('901-23-4567', 20),
    ('234-56-7890', 21),
    ('567-89-0123', 22),
    ('890-12-3456', 23),
    ('345-67-8901', 24),
    ('678-90-1234', 25);

-- 2.2
INSERT INTO
    process (ssn, order_no)
SELECT
    '123-45-6789',
    generate_series(26, 390);

-- 2.3
INSERT INTO
    process (ssn, order_no)
SELECT
    '456-78-9012',
    generate_series(391, 397);

-- OLAP
INSERT INTO
    process (ssn, order_no)
SELECT
    '678-90-1234',
    generate_series(398, 762);

INSERT INTO
    department ("name")
VALUES
    ('Marketing'),
    ('Sales'),
    ('Finance'),
    ('Human Resources'),
    ('Operations'),
    ('Productions');

INSERT INTO
    workplace ("address", lat, long)
VALUES
    (
        'Avenida da Liberdade 10 1250-142 Santo Antonio',
        40.7128,
        -70.2345
    ),
    (
        'Rua dos Clerigos 5 4050-205 Vitoria',
        38.2228,
        -80.4454
    ),
    (
        'D. Afonso Henriques 35 1000-123 Alameda',
        30.3344,
        -69.0003
    ),
    (
        'Rua das Cruzes 7 2664-255 Cruzadas',
        39.3333,
        -85.2222
    );

INSERT INTO
    office ("address")
VALUES
    ('Avenida da Liberdade 10 1250-142 Santo Antonio'),
    ('Rua dos Clerigos 5 4050-205 Vitoria');

INSERT INTO
    warehouse ("address")
VALUES
    ('D. Afonso Henriques 35 1000-123 Alameda'),
    ('Rua das Cruzes 7 2664-255 Cruzadas');

INSERT INTO
    works (ssn, "name", "address")
VALUES
    (
        '123-45-6789',
        'Marketing',
        'Avenida da Liberdade 10 1250-142 Santo Antonio'
    ),
    (
        '987-65-4321',
        'Marketing',
        'Avenida da Liberdade 10 1250-142 Santo Antonio'
    ),
    (
        '456-78-9012',
        'Sales',
        'D. Afonso Henriques 35 1000-123 Alameda'
    ),
    (
        '789-01-2345',
        'Sales',
        'Rua dos Clerigos 5 4050-205 Vitoria'
    ),
    (
        '234-56-7890',
        'Finance',
        'Avenida da Liberdade 10 1250-142 Santo Antonio'
    ),
    (
        '567-89-0123',
        'Finance',
        'D. Afonso Henriques 35 1000-123 Alameda'
    ),
    (
        '890-12-3456',
        'Human Resources',
        'Rua das Cruzes 7 2664-255 Cruzadas'
    ),
    (
        '345-67-8901',
        'Human Resources',
        'Avenida da Liberdade 10 1250-142 Santo Antonio'
    ),
    (
        '678-90-1234',
        'Operations',
        'Rua das Cruzes 7 2664-255 Cruzadas'
    ),
    (
        '901-23-4567',
        'Operations',
        'Rua dos Clerigos 5 4050-205 Vitoria'
    ),
    (
        '111-22-3333',
        'Productions',
        'Rua das Cruzes 7 2664-255 Cruzadas'
    ),
    (
        '222-33-4444',
        'Productions',
        'Rua das Cruzes 7 2664-255 Cruzadas'
    );

INSERT INTO
    product (sku, "name", "description", price, ean)
VALUES
    -- BRAND, COLOR, CODE, SIZE  
    (
        'APL-WH-PR-00',
        'Headphones',
        'Wireless Bluetooth headphones with noise cancellation',
        89.99,
        9781234567896
    ),
    (
        'RLX-GD-B5-00',
        'Watch',
        'Stainless steel wristwatch with leather strap',
        79.99,
        9781234567894
    ),
    (
        'L&V-BL-D4-00',
        'Sunglasses',
        'Aviator-style sunglasses with UV protection',
        24.99,
        NULL
    ),
    (
        'L&V-PP-H4-40',
        'Handbag',
        'Faux leather handbag with adjustable strap',
        34.99,
        9781234567899
    ),
    (
        'L&V-GD-D4-42',
        'Belt',
        'Genuine leather belt with silver buckle',
        19.99,
        9781234567904
    ),
    (
        'L&V-BK-D3-40',
        'Blazer',
        'Classic fitted blazer in navy blue',
        79.99,
        NULL
    ),
    (
        'L&V-WH-D4-42',
        'Satchel',
        'Leather satchel bag with crossbody strap',
        89.99,
        9781234567913
    ),
    (
        'L&V-RD-H5-36',
        'Blouse',
        'Silk blouse with ruffled sleeves',
        49.99,
        9781234567900
    ),
    (
        'NIK-YL-D5-39',
        'Running Shoes',
        'Lightweight running shoes with breathable mesh',
        69.99,
        NULL
    ),
    (
        'NIK-BK-H4-35',
        'Backpack',
        'Durable backpack with multiple compartments',
        54.99,
        9781234567893
    ),
    (
        'NIK-BK-H2-42',
        'Sneakers',
        'Classic white sneakers with rubber sole',
        29.99,
        9781234567892
    ),
    (
        'PRI-WH-B1-38',
        'T-shirt',
        'Cotton crew neck t-shirt in black',
        19.99,
        NULL
    ),
    (
        'PRI-WH-B2-40',
        'Sandals',
        'Leather sandals with ankle strap',
        29.99,
        9781234567902
    ),
    (
        'PRI-GR-T2-38',
        'T-shirt',
        'Green cotton t-shirt with V-neck',
        19.99,
        9781234567930
    ),
    (
        'PRI-BL-B6-36',
        'Socks',
        'Cotton blend socks in assorted colors',
        9.99,
        NULL
    ),
    (
        'PRI-WH-B7-36',
        'Sweatshirt',
        'Cotton blend sweatshirt with hood',
        49.99,
        9781234567909
    ),
    (
        'PRI-YL-B2-38',
        'Hat',
        'Wide-brim straw hat for sun protection',
        14.99,
        9781234567908
    ),
    (
        'PRI-RD-B4-36',
        'Jeans',
        'Slim-fit denim jeans with distressed details',
        39.99,
        NULL
    ),
    (
        'H&M-WH-D4-36',
        'Chinos',
        'Straight-fit chinos in khaki color',
        44.99,
        9781234567901
    ),
    (
        'H&M-BL-B5-39',
        'Jacket',
        'Waterproof windbreaker jacket with hood',
        69.99,
        9781234567903
    ),
    (
        'H&M-GR-B3-36',
        'Sweater',
        'Knit sweater with cable pattern',
        39.99,
        NULL
    ),
    (
        'H&M-DB-B2-38',
        'Shorts',
        'Denim shorts with frayed hem',
        24.99,
        9781234567906
    ),
    (
        'H&M-PK-H4-40',
        'Skirt',
        'Flared midi skirt with floral print',
        39.99,
        9781234567910
    ),
    (
        'H&M-WH-H6-36',
        'Trousers',
        'Tailored trousers in charcoal gray',
        59.99,
        NULL
    ),
    (
        'H&M-BL-J1-42',
        'Jacket',
        'Blue denim jacket with distressed details',
        69.99,
        9781234567931
    );

INSERT INTO
    contains (order_no, sku, qty)
VALUES
    (1, 'APL-WH-PR-00', 1),
    (2, 'RLX-GD-B5-00', 2),
    (3, 'L&V-BL-D4-00', 3),
    (4, 'L&V-PP-H4-40', 1),
    (5, 'L&V-GD-D4-42', 2),
    (6, 'L&V-BK-D3-40', 3),
    (7, 'L&V-WH-D4-42', 1),
    (8, 'L&V-RD-H5-36', 2),
    (9, 'NIK-YL-D5-39', 3),
    (10, 'NIK-BK-H4-35', 1),
    (11, 'NIK-BK-H2-42', 2),
    (12, 'PRI-WH-B1-38', 3),
    (13, 'PRI-WH-B2-40', 1),
    (14, 'PRI-GR-T2-38', 2),
    (15, 'PRI-BL-B6-36', 3),
    (16, 'PRI-WH-B7-36', 1),
    (17, 'PRI-YL-B2-38', 2),
    (18, 'PRI-RD-B4-36', 3),
    (19, 'H&M-WH-D4-36', 1),
    (20, 'H&M-BL-B5-39', 2),
    (21, 'H&M-GR-B3-36', 3),
    (22, 'H&M-DB-B2-38', 1),
    (23, 'H&M-PK-H4-40', 2),
    (24, 'H&M-WH-H6-36', 3),
    (25, 'H&M-BL-J1-42', 1);

INSERT INTO
    contains (order_no, sku, qty)
VALUES
    (3, 'RLX-GD-B5-00', 2),
    (6, 'L&V-GD-D4-42', 2),
    (9, 'L&V-RD-H5-36', 2),
    (12, 'NIK-BK-H2-42', 2),
    (15, 'PRI-GR-T2-38', 2),
    (18, 'PRI-YL-B2-38', 2),
    (21, 'H&M-BL-B5-39', 2),
    (24, 'H&M-PK-H4-40', 2);

INSERT INTO
    contains (order_no, sku, qty)
VALUES
    (5, 'L&V-PP-H4-40', 1),
    (10, 'NIK-YL-D5-39', 3),
    (15, 'NIK-BK-H2-42', 2),
    (20, 'H&M-WH-D4-36', 1),
    (25, 'H&M-WH-H6-36', 3);

-- 2.2
INSERT INTO
    contains (order_no, sku, qty)
SELECT
    generate_series(26, 390),
    'PRI-WH-B1-38',
    1;

-- 2.3
INSERT INTO
    contains (order_no, sku, qty)
SELECT
    generate_series(391, 397),
    'PRI-WH-B1-38',
    1;

-- OLAP
INSERT INTO
    contains (order_no, sku, qty)
SELECT
    generate_series(398, 762),
    'PRI-RD-B4-36',
    2;

INSERT INTO
    supplier (tin, "name", "address", sku, "date")
VALUES
    (
        '123456789',
        'Stellar Supplies',
        'Rua do Cacto 12 1200-234 Alcantara',
        'PRI-WH-B1-38',
        DATE '2023-09-15'
    ),
    (
        '987654321',
        'Horizon Trading Co.',
        'Rua Do Grilo 22 5000-500 Porto',
        'PRI-RD-B4-36',
        DATE '2024-03-27'
    ),
    (
        '456789123',
        'Atlas Enterprises',
        'Rua D. Sancho I 65 2955-500 Almada',
        'NIK-BK-H2-42',
        DATE '2023-12-08'
    ),
    (
        '321654987',
        'Silverline Distributors',
        'Largo D. Afonso Henriques 25 3004-500 Ramada',
        'NIK-BK-H4-35',
        DATE '2024-07-23'
    ),
    (
        '789123456',
        'Sunburst Imports',
        'Rua das Rosas 123 1000-500 Alvalade',
        'RLX-GD-B5-00',
        DATE '2024-08-05'
    ),
    (
        '234567890',
        'Emerald Global Services',
        'Avenida da Liberdade 50 1500-234 Lisboa',
        'L&V-PP-H4-40',
        DATE '2023-07-10'
    ),
    (
        '876543210',
        'Vanguard Merchants',
        'Rua dos Lírios 78 2500-500 Coimbra',
        'NIK-BK-H4-35',
        DATE '2024-10-19'
    ),
    (
        '567890123',
        'Crestline Traders',
        'Praceta das Oliveiras 10 3500-500 Braga',
        'H&M-PK-H4-40',
        DATE '2023-11-30'
    ),
    (
        '654321987',
        'Golden Harvest Suppliers',
        'Avenida das Flores 27 4000-500 Faro',
        'PRI-WH-B2-40',
        DATE '2024-09-02'
    ),
    (
        '901234567',
        'Pacific Trading Company',
        'Rua do Sol 99 2000-500 Sintra',
        'PRI-GR-T2-38',
        DATE '2023-08-12'
    ),
    (
        '345678912',
        'Evergreen Sourcing Solutions',
        'Largo das Figueiras 14 6000-500 Setúbal',
        'H&M-WH-D4-36',
        DATE '2023-10-07'
    ),
    (
        '789012345',
        'Azure Logistics',
        'Avenida do Mar 33 7000-500 Évora',
        'PRI-RD-B4-36',
        DATE '2024-06-16'
    ),
    (
        '098765432',
        'Sapphire Enterprises',
        'Rua do Bosque 87 8000-500 Albufeira',
        'H&M-BL-J1-42',
        DATE '2023-07-25'
    ),
    (
        '543210987',
        'Royal Crest Imports',
        'Praceta dos Pinheiros 19 9000-500 Viseu',
        'L&V-BK-D3-40',
        DATE '2024-11-03'
    ),
    (
        '876543219',
        'Moonlight Distributors',
        'Avenida dos Carvalhos 75 1000-500 Leiria',
        'APL-WH-PR-00',
        DATE '2024-12-29'
    );

-- NIK-BK-H4-35 e RLX-GD-B5-00 acima de 50$
-- NIK-BK-H4-35 mais vendido
-- RLX-GD-B5-00 mais encomendado
INSERT INTO
    delivery ("address", tin)
VALUES
    (
        'D. Afonso Henriques 35 1000-123 Alameda',
        '123456789'
    ),
    (
        'D. Afonso Henriques 35 1000-123 Alameda',
        '987654321'
    ),
    (
        'D. Afonso Henriques 35 1000-123 Alameda',
        '456789123'
    ),
    (
        'Rua das Cruzes 7 2664-255 Cruzadas',
        '321654987'
    ),
    (
        'Rua das Cruzes 7 2664-255 Cruzadas',
        '789123456'
    );