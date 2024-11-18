DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS sales CASCADE;
DROP TABLE IF EXISTS pay CASCADE;
DROP TABLE IF EXISTS employees CASCADE;
DROP TABLE IF EXISTS process CASCADE;
DROP TABLE IF EXISTS departments CASCADE;
DROP TABLE IF EXISTS workplaces CASCADE;
DROP TABLE IF EXISTS works CASCADE;
DROP TABLE IF EXISTS offices CASCADE;
DROP TABLE IF EXISTS warehouses CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS ean_products CASCADE;
DROP TABLE IF EXISTS suppliers CASCADE;
DROP TABLE IF EXISTS contains CASCADE;
DROP TABLE IF EXISTS delivery CASCADE;

CREATE TABLE customers (
    cust_no SERIAL PRIMARY KEY,
    "name" TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL CHECK (email LIKE '%@%.%'),
    phone TEXT NOT NULL,
    "address" TEXT
);

CREATE TABLE orders (
    order_no SERIAL PRIMARY KEY,
    "date" DATE NOT NULL CHECK ("date" <= CURRENT_DATE),
    cust_no INTEGER NOT NULL,
    FOREIGN KEY (cust_no) REFERENCES customers (cust_no)
    -- IC-1: any order_no in orders must exist in contains (implementable with trigger functions)
);

CREATE TABLE sales (
    order_no INTEGER PRIMARY KEY,
    FOREIGN KEY (order_no) REFERENCES orders (order_no) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE pay (
    order_no INTEGER PRIMARY KEY,
    cust_no INTEGER NOT NULL,
    FOREIGN KEY (order_no) REFERENCES sales (order_no),
    FOREIGN KEY (cust_no) REFERENCES customers (cust_no)
    -- IC-2: customers can only pay for the sale of an order they have placed themselves
);

CREATE TABLE employees (
    ssn CHAR(11) PRIMARY KEY,
    tin CHAR(9) UNIQUE NOT NULL,
    bdate DATE NOT NULL CHECK (bdate <= CURRENT_DATE),
    "name" TEXT NOT NULL
    -- IC-3: any ssn in employees must exist in works (implementable with trigger functions)
);

CREATE TABLE process (
    ssn CHAR(11),
    order_no INTEGER,
    PRIMARY KEY (ssn, order_no),
    FOREIGN KEY (ssn) REFERENCES employees (ssn),
    FOREIGN KEY (order_no) REFERENCES orders (order_no)
);

CREATE TABLE departments (
    "name" TEXT PRIMARY KEY
);

CREATE TABLE workplaces (
    "address" TEXT PRIMARY KEY,
    lat NUMERIC(10, 7) NOT NULL CHECK (lat BETWEEN -90 AND 90),
    long NUMERIC(10, 7) NOT NULL CHECK (long BETWEEN -180 AND 180),
    UNIQUE(lat, long)
);

CREATE TABLE works (
    ssn CHAR(11),
    "name" TEXT,
    "address" TEXT,
    PRIMARY KEY (ssn, name, address),
    FOREIGN KEY (ssn) REFERENCES employees (ssn),
    FOREIGN KEY ("name") REFERENCES departments ("name"),
    FOREIGN KEY ("address") REFERENCES workplaces ("address")
);

CREATE TABLE offices (
    "address" TEXT PRIMARY KEY,
    FOREIGN KEY ("address") REFERENCES workplaces ("address") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE warehouses (
    "address" TEXT PRIMARY KEY,
    FOREIGN KEY ("address") REFERENCES workplaces ("address") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE products (
    sku TEXT PRIMARY KEY,
    "name" TEXT NOT NULL,
    "description" TEXT,
    price NUMERIC(10, 2) NOT NULL CHECK (price >= 0)
    -- IC-4: any sku in products must exist in suppliers (implementable with trigger functions)
);

CREATE TABLE ean_products (
    sku TEXT PRIMARY KEY,
    ean TEXT NOT NULL,
    FOREIGN KEY (sku) REFERENCES products (sku) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE suppliers (
    tin CHAR(9) PRIMARY KEY,
    "name" TEXT NOT NULL,
    "address" TEXT NOT NULL,
    sku TEXT NOT NULL,
    "date" DATE NOT NULL,
    FOREIGN KEY (sku) REFERENCES products (sku)
);

CREATE TABLE contains (
    order_no INTEGER,
    sku TEXT,
    qty INTEGER NOT NULL CHECK (qty > 0),
    PRIMARY KEY (order_no, sku),
    FOREIGN KEY (order_no) REFERENCES orders (order_no),
    FOREIGN KEY (sku) REFERENCES products (sku)
);

CREATE TABLE delivery (
    "address" TEXT,
    tin CHAR(9),
    PRIMARY KEY ("address", tin),
    FOREIGN KEY ("address") REFERENCES warehouses ("address"),
    FOREIGN KEY (tin) REFERENCES suppliers (tin)
);
