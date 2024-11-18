#!/usr/bin/python3
from logging.config import dictConfig

import psycopg
import json
from flask import flash
from flask import Flask
from flask import jsonify
from flask import redirect
from flask import render_template
from flask import request
from flask import url_for
from psycopg.rows import namedtuple_row
from psycopg_pool import ConnectionPool


# postgres://{user}:{password}@{hostname}:{port}/{database-name}
DATABASE_URL = "postgres://postgres:postgres@postgres/postgres"

pool = ConnectionPool(conninfo=DATABASE_URL)
# the pool starts connecting immediately.

dictConfig(
    {
        "version": 1,
        "formatters": {
            "default": {
                "format": "[%(asctime)s] %(levelname)s in %(module)s:%(lineno)s - %(funcName)20s(): %(message)s",
            }
        },
        "handlers": {
            "wsgi": {
                "class": "logging.StreamHandler",
                "stream": "ext://flask.logging.wsgi_errors_stream",
                "formatter": "default",
            }
        },
        "root": {"level": "INFO", "handlers": ["wsgi"]},
    }
)

app = Flask(__name__)
app.secret_key = "NsJbe2ryUYtopdXWkcx8gyfC8qa5XKW7L5UjmCqJB4hRloL6C8JKnfx0IjbkM5hj"


@app.route("/products", methods=("GET",))
@app.route("/products/<page>", methods=("GET",))
def products_index(page=0):
    """Show all the products, ordered by name."""

    page = int(page)

    # invalid page
    if page < 0:
        return redirect(url_for("products_index"))

    with pool.connection() as conn:
        with conn.cursor(row_factory=namedtuple_row) as cur:
            query = """
                SELECT *
                FROM product
                ORDER BY name ASC
                OFFSET %(page)s
                LIMIT 10;
            """

            products = cur.execute(
                query,
                {"page": page * 10},
            ).fetchall()

            # prevent invalid page access and infinite loop if there's no content to display
            if cur.rowcount == 0 and page != 0:
                return redirect(url_for("products_index"))

            # check if there should be a following page
            cur.execute(
                query,
                {"page": (page + 1) * 10},
            ).fetchall()

            last = True if cur.rowcount == 0 else False

    # API-like response is returned to clients that request JSON explicitly (e.g., fetch)
    if (
        request.accept_mimetypes["application/json"]
        and not request.accept_mimetypes["text/html"]
    ):
        return jsonify(products)

    return render_template(
        "products/index.html", products=products, page=page, last=last
    )


@app.route("/products/create", methods=("GET", "POST"))
def product_create():
    """Create a product."""

    if request.method == "POST":
        sku = request.form["sku"]
        name = request.form["name"]
        description = request.form["description"]
        price = request.form["price"]
        ean = request.form["ean"]

        error = ""

        if not sku:
            error += "SKU is required. "

        if not name:
            error += "Name is required. "

        if not price:
            error += "Price is required. "
        else:
            try:
                float(price)
            except ValueError:
                error += "Price is required to be numeric. "

        if not ean:
            ean = None
        elif not ean.isnumeric():
            error += "EAN is required to be numeric. "

        if error != "":
            flash(error)
        else:
            with pool.connection() as conn:
                with conn.cursor(row_factory=namedtuple_row) as cur:
                    cur.execute(
                        """
                        INSERT INTO product(SKU, name, description, price, ean)
                        VALUES
                            (
                            %(sku)s,
                            %(name)s,
                            %(description)s,
                            %(price)s,
                            %(ean)s
                            );
                        """,
                        {
                            "sku": sku,
                            "name": name,
                            "description": description,
                            "price": price,
                            "ean": ean,
                        },
                    )

            return redirect(url_for("products_index"))

    return render_template("products/create.html")


@app.route("/products/<sku>/update", methods=("GET", "POST"))
def product_update(sku):
    """Update the product price and description."""

    with pool.connection() as conn:
        with conn.cursor(row_factory=namedtuple_row) as cur:
            product = cur.execute(
                """
                SELECT *
                FROM product
                WHERE SKU = %(sku)s;
                """,
                {"sku": sku},
            ).fetchone()

            # prevent invalid page access
            if product == None:
                return redirect(url_for("products_index"))

    if request.method == "POST":
        price = request.form["price"]
        description = request.form["description"]

        error = ""

        if not price:
            error += "Price is required. "
        else:
            try:
                float(price)
            except ValueError:
                error += "Price is required to be numeric. "

        if error != "":
            flash(error)
        else:
            with pool.connection() as conn:
                with conn.cursor(row_factory=namedtuple_row) as cur:
                    cur.execute(
                        """
                        UPDATE product
                        SET
                            price = %(price)s,
                            description = %(description)s
                        WHERE SKU = %(sku)s;
                        """,
                        {"sku": sku, "price": price, "description": description},
                    )

            return redirect(url_for("products_index"))

    return render_template("products/update.html", product=product)


@app.route("/products/<sku>/delete", methods=("POST",))
def product_delete(sku):
    """Delete the product."""

    with pool.connection() as conn:
        with conn.cursor(row_factory=namedtuple_row) as cur:
            # remove the product from all its supplier contracts
            cur.execute(
                """
                UPDATE supplier
                SET
                    SKU = NULL
                WHERE SKU = %(sku)s;
                """,
                {"sku": sku},
            )

            # grab all orders that only contain the product to delete
            orders = cur.execute(
                """
                SELECT
                    c.order_no
                FROM
                    contains c
                WHERE SKU = %(sku)s
                    AND (SELECT COUNT(*) FROM contains WHERE order_no = c.order_no) = 1;
                """,
                {"sku": sku},
            ).fetchall()

            # remove product from orders that contain this product and others
            cur.execute(
                """
                DELETE FROM contains
                WHERE SKU = %(sku)s
                    AND order_no != ALL(%(orders)s);
                """,
                {"sku": sku, "orders": list(map(lambda x: x[0], orders))},
            )

            # remove all orders (that only have that product) and its references (simulate fk on delete cascade)
            for order in orders:
                order_no = order[0]

                cur.execute(
                    """
                    DELETE FROM pay
                    WHERE order_no = %(order_no)s;
                    """,
                    {"order_no": order_no},
                )
                cur.execute(
                    """
                    DELETE FROM process
                    WHERE order_no = %(order_no)s;
                    """,
                    {"order_no": order_no},
                )
                cur.execute(
                    """
                    DELETE FROM contains
                    WHERE order_no = %(order_no)s;
                    """,
                    {"order_no": order_no},
                )
                cur.execute(
                    """
                    DELETE FROM orders
                    WHERE order_no = %(order_no)s;
                    """,
                    {"order_no": order_no},
                )

            # delete the product itself
            cur.execute(
                """
                DELETE FROM product
                WHERE SKU = %(sku)s;
                """,
                {"sku": sku},
            )

    return redirect(url_for("products_index"))


@app.route("/suppliers", methods=("GET",))
@app.route("/suppliers/<page>", methods=("GET",))
def suppliers_index(page=0):
    """Show all the suppliers, ordered by TIN."""

    page = int(page)

    # invalid page
    if page < 0:
        return redirect(url_for("suppliers_index"))

    with pool.connection() as conn:
        with conn.cursor(row_factory=namedtuple_row) as cur:
            query = """
                SELECT *
                FROM supplier
                ORDER BY TIN ASC
                OFFSET %(page)s
                LIMIT 10;
            """

            suppliers = cur.execute(
                query,
                {"page": page * 10},
            ).fetchall()

            # prevent invalid page access and infinite loop if there's no content to display
            if cur.rowcount == 0 and page != 0:
                return redirect(url_for("suppliers_index"))

            # check if there should be a following page
            cur.execute(
                query,
                {"page": (page + 1) * 10},
            ).fetchall()

            last = True if cur.rowcount == 0 else False

    # API-like response is returned to clients that request JSON explicitly (e.g., fetch)
    if (
        request.accept_mimetypes["application/json"]
        and not request.accept_mimetypes["text/html"]
    ):
        return jsonify(suppliers)

    return render_template(
        "suppliers/index.html", suppliers=suppliers, page=page, last=last
    )


@app.route("/suppliers/create", methods=("GET", "POST"))
def supplier_create():
    """Create a supplier."""

    if request.method == "POST":
        tin = request.form["tin"]
        name = request.form["name"]
        address = request.form["address"]
        sku = request.form["sku"]
        date = request.form["date"]

        error = ""

        if not tin:
            error += "TIN is required. "

        if not name:
            name = None

        if not address:
            address = None

        if not sku:
            sku = None

        if not date:
            date = None

        if error != "":
            flash(error)
        else:
            with pool.connection() as conn:
                with conn.cursor(row_factory=namedtuple_row) as cur:
                    cur.execute(
                        """
                        INSERT INTO supplier(TIN, name, address, sku, date)
                        VALUES
                            (
                            %(tin)s,
                            %(name)s,
                            %(address)s,
                            %(sku)s,
                            %(date)s
                            );
                        """,
                        {
                            "tin": tin,
                            "name": name,
                            "sku": sku,
                            "address": address,
                            "date": date,
                        },
                    )

            return redirect(url_for("suppliers_index"))

    return render_template("suppliers/create.html")


@app.route("/suppliers/<tin>/delete", methods=("POST",))
def supplier_delete(tin):
    """Delete the supplier."""

    with pool.connection() as conn:
        with conn.cursor(row_factory=namedtuple_row) as cur:
            # remove all deliveries associated with that supplier (simulate fk on delete cascade)
            cur.execute(
                """
                DELETE FROM delivery
                WHERE TIN = %(tin)s;
                """,
                {"tin": tin},
            )

            # delete the product itself
            cur.execute(
                """
                DELETE FROM supplier
                WHERE TIN = %(tin)s;
                """,
                {"tin": tin},
            )

    return redirect(url_for("suppliers_index"))


@app.route("/", methods=("GET",))
@app.route("/customers", methods=("GET",))
@app.route("/customers/<page>", methods=("GET",))
def customers_index(page=0):
    """Show all the customers, ordered by name."""

    page = int(page)

    # invalid page
    if page < 0:
        return redirect(url_for("customers_index"))

    with pool.connection() as conn:
        with conn.cursor(row_factory=namedtuple_row) as cur:
            query = """
                SELECT *
                FROM customer
                ORDER BY name ASC
                OFFSET %(page)s
                LIMIT 10;
            """

            customers = cur.execute(
                query,
                {"page": page * 10},
            ).fetchall()

            # prevent invalid page access and infinite loop if there's no content to display
            if cur.rowcount == 0 and page != 0:
                return redirect(url_for("customers_index"))

            # check if there should be a following page
            cur.execute(
                query,
                {"page": (page + 1) * 10},
            ).fetchall()

            last = True if cur.rowcount == 0 else False

    # API-like response is returned to clients that request JSON explicitly (e.g., fetch)
    if (
        request.accept_mimetypes["application/json"]
        and not request.accept_mimetypes["text/html"]
    ):
        return jsonify(customers)

    return render_template(
        "customers/index.html", customers=customers, page=page, last=last
    )


@app.route("/customers/<cust_no>/view", methods=("GET",))
@app.route("/customers/<cust_no>/view/<page>", methods=("GET",))
def customer_view(cust_no, page=0):
    """Show the customer."""

    page = int(page)

    # invalid page
    if page < 0:
        return redirect(url_for("customer_view", cust_no=cust_no))

    with pool.connection() as conn:
        with conn.cursor(row_factory=namedtuple_row) as cur:
            customer = cur.execute(
                """
                SELECT
                    *
                FROM customer
                WHERE cust_no = %(cust_no)s;
                """,
                {"cust_no": cust_no},
            ).fetchone()

            # prevent invalid page access
            if customer == None:
                return redirect(url_for("customers_index"))
            
            query = """
                SELECT
                    o.order_no,
                    o.cust_no,
                    o.date,
                    p.cust_no AS paid_by,
                    SUM(qty * price) AS total,
                    SUM(qty) AS product_qty
                FROM orders o
                LEFT JOIN pay p USING (order_no)
                INNER JOIN contains USING (order_no)
                INNER JOIN product USING (sku)
                WHERE o.cust_no = %(cust_no)s
                GROUP BY o.order_no, p.cust_no
                ORDER BY o.date DESC
                OFFSET %(page)s
                LIMIT 10;
            """

            orders = cur.execute(
                query,
                {"cust_no": cust_no, "page": page * 10},
            ).fetchall()

            # prevent invalid page access and infinite loop if there's no content to display
            if cur.rowcount == 0 and page != 0:
                return redirect(url_for("customer_view", cust_no=cust_no))

            # check if there should be a following page
            cur.execute(
                query,
                {"cust_no": cust_no, "page": (page + 1) * 10},
            ).fetchall()

            last = True if cur.rowcount == 0 else False

    # API-like response is returned to clients that request JSON explicitly (e.g., fetch)
    if (
        request.accept_mimetypes["application/json"]
        and not request.accept_mimetypes["text/html"]
    ):
        return jsonify({"customer": customer, "orders": orders})

    return render_template("customers/view.html", customer=customer, orders=orders, page=page, last=last)


@app.route("/customers/create", methods=("GET", "POST"))
def customer_create():
    """Create a customer."""

    if request.method == "POST":
        name = request.form["name"]
        email = request.form["email"]
        phone = request.form["phone"]
        address = request.form["address"]

        error = ""

        if not name:
            error += "Name is required. "

        if not email:
            error += "Email is required. "

        if not phone:
            phone = None

        if not address:
            address = None

        if error != "":
            flash(error)
        else:
            with pool.connection() as conn:
                with conn.cursor(row_factory=namedtuple_row) as cur:
                    cust_no = cur.execute(
                        """
                        SELECT MAX(cust_no) FROM customer;
                        """
                    ).fetchone()

                    # generate next cust_no
                    if cust_no[0]:
                        cust_no = cust_no[0] + 1
                    else:
                        cust_no = 1

                    cur.execute(
                        """
                        INSERT INTO customer(cust_no, name, email, phone, address)
                        VALUES
                            (
                            %(cust_no)s,
                            %(name)s,
                            %(email)s,
                            %(phone)s,
                            %(address)s
                            );
                        """,
                        {
                            "cust_no": cust_no,
                            "name": name,
                            "email": email,
                            "phone": phone,
                            "address": address,
                        },
                    )

            return redirect(url_for("customer_view", cust_no=cust_no))

    with pool.connection() as conn:
        with conn.cursor(row_factory=namedtuple_row) as cur:
            cust_no = cur.execute(
                """
                SELECT MAX(cust_no) FROM customer;
                """
            ).fetchone()

            # generate next cust_no
            if cust_no[0]:
                cust_no = cust_no[0] + 1
            else:
                cust_no = 1

    return render_template("customers/create.html", cust_no=cust_no)


@app.route("/customers/<cust_no>/delete", methods=("POST",))
def customer_delete(cust_no):
    """Delete the customer."""

    with pool.connection() as conn:
        with conn.cursor(row_factory=namedtuple_row) as cur:
            # grab all customer orders
            orders = cur.execute(
                """
                SELECT
                    order_no
                FROM
                    orders
                WHERE cust_no = %(cust_no)s;
                """,
                {"cust_no": cust_no},
            ).fetchall()

            # remove all customer orders and its references (simulate fk on delete cascade)
            for order in orders:
                order_no = order[0]

                cur.execute(
                    """
                    DELETE FROM pay
                    WHERE order_no = %(order_no)s;
                    """,
                    {"order_no": order_no},
                )
                cur.execute(
                    """
                    DELETE FROM process
                    WHERE order_no = %(order_no)s;
                    """,
                    {"order_no": order_no},
                )
                cur.execute(
                    """
                    DELETE FROM contains
                    WHERE order_no = %(order_no)s;
                    """,
                    {"order_no": order_no},
                )
                cur.execute(
                    """
                    DELETE FROM orders
                    WHERE order_no = %(order_no)s;
                    """,
                    {"order_no": order_no},
                )

            # delete the customer itself
            cur.execute(
                """
                DELETE FROM customer
                WHERE cust_no = %(cust_no)s;
                """,
                {"cust_no": cust_no},
            )

    return redirect(url_for("customers_index"))


@app.route("/customers/<cust_no>/orders/<order_no>/view", methods=("GET",))
@app.route("/customers/<cust_no>/orders/<order_no>/view/<page>", methods=("GET",))
def order_view(cust_no, order_no, page=0):
    """Show the order."""

    page = int(page)

    # invalid page
    if page < 0:
        return redirect(url_for("order_view", cust_no=cust_no, order_no=order_no))

    with pool.connection() as conn:
        with conn.cursor(row_factory=namedtuple_row) as cur:
            order = cur.execute(
                """
                SELECT
                    o.order_no,
                    o.cust_no,
                    o.date,
                    p.cust_no AS paid_by,
                    SUM(qty * price) AS total,
                    SUM(qty) AS product_qty
                FROM orders o
                LEFT JOIN pay p USING (order_no)
                INNER JOIN contains USING (order_no)
                INNER JOIN product USING (sku)
                WHERE o.order_no = %(order_no)s
                GROUP BY o.order_no, p.cust_no
                ORDER BY o.date DESC;
                """,
                {"order_no": order_no},
            ).fetchone()

            # prevent invalid page access
            if order == None:
                return redirect(url_for("customer_view", cust_no=cust_no))

            query = """
                SELECT *
                FROM contains
                WHERE order_no = %(order_no)s
                OFFSET %(page)s
                LIMIT 10;
            """

            products = cur.execute(
                query,
                {"order_no": order_no, "page": page * 10},
            ).fetchall()

            # prevent invalid page access and infinite loop if there's no content to display
            if cur.rowcount == 0 and page != 0:
                return redirect(url_for("order_view", cust_no=cust_no, order_no=order_no))

            # check if there should be a following page
            cur.execute(
                query,
                {"order_no": order_no, "page": (page + 1) * 10},
            ).fetchall()

            last = True if cur.rowcount == 0 else False

    # API-like response is returned to clients that request JSON explicitly (e.g., fetch)
    if (
        request.accept_mimetypes["application/json"]
        and not request.accept_mimetypes["text/html"]
    ):
        return jsonify({"order": order, "products": products})

    return render_template("orders/view.html", order=order, products=products, page=page, last=last)


@app.route(
    "/customers/<cust_no>/orders/<order_no>/pay",
    methods=(
        "POST",
    ),
)
def order_pay(cust_no, order_no):
    """Pay the order."""

    with pool.connection() as conn:
        with conn.cursor(row_factory=namedtuple_row) as cur:
            cur.execute(
                """
                INSERT INTO pay(order_no, cust_no)
                VALUES
                    (
                    %(order_no)s,
                    (SELECT cust_no FROM orders WHERE order_no = %(order_no)s)
                    );
                """,
                {"order_no": order_no},
            )

    return redirect(url_for("order_view", cust_no=cust_no, order_no=order_no))

@app.route("/customers/<cust_no>/orders/create", methods=("GET", "POST"))
def order_create(cust_no):
    """Create a order."""

    if request.method == "POST":
        date = request.form["date"]

        # products data was created with JS as an array and sent as JSON
        products = json.loads(request.form["products"])

        error = ""

        if not date:
            error += "Date is required. "

        if not products or len(products) == 0:
            error += "Products are required. "
        else:
            for product in products:
                if not product["sku"] or not product["qty"]:
                    error += "Products are required. "
                    break

        if error != "":
            flash(error)
        else:
            with pool.connection() as conn:
                with conn.cursor(row_factory=namedtuple_row) as cur:
                    order_no = cur.execute(
                        """
                        SELECT MAX(order_no) FROM orders;
                        """
                    ).fetchone()

                    # generate next order_no
                    if order_no[0]:
                        order_no = order_no[0] + 1
                    else:
                        order_no = 1

                    cur.execute(
                        """
                        INSERT INTO orders(order_no, cust_no, date)
                        VALUES
                            (
                            %(order_no)s,
                            %(cust_no)s,
                            %(date)s
                            );
                        """,
                        {"order_no": order_no, "cust_no": cust_no, "date": date},
                    )

                    for product in products:
                        cur.execute(
                            """
                            INSERT INTO contains(order_no, SKU, qty)
                            VALUES
                                (
                                %(order_no)s,
                                %(SKU)s,
                                %(qty)s
                                );
                            """,
                            {
                                "order_no": order_no,
                                "SKU": product["sku"],
                                "qty": product["qty"],
                            },
                        )

            return redirect(url_for("order_view", cust_no=cust_no, order_no=order_no))

    with pool.connection() as conn:
        with conn.cursor(row_factory=namedtuple_row) as cur:
            order_no = cur.execute(
                """
                SELECT MAX(order_no) FROM orders;
                """
            ).fetchone()

            # generate next order_no
            if order_no[0]:
                order_no = order_no[0] + 1
            else:
                order_no = 1

    return render_template("orders/create.html", cust_no=cust_no, order_no=order_no)


@app.route("/ping", methods=("GET",))
def ping():
    return jsonify({"message": "pong!", "status": "success"})


if __name__ == "__main__":
    app.run()
