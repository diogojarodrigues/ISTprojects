const { Pool } = require("pg");

const port = process.env.DB_PORT;
const host = process.env.DB_HOST;
const user = process.env.DB_USER;
const password = process.env.DB_PASSWORD;
const database = process.env.DB_DATABASE;

// Set up the connection pool
const pool = new Pool({
  user: user,
  host: host,
  database: database,
  password: password,
  port: port,
});

// Function to write to Postgres database
async function writeToPostgres(price) {
  const logText = `A volume of Bitcoin was requested. The price is ${price}$. Expensive!`;

  const query = {
    text: "INSERT INTO transactions (log) VALUES ($1)",
    values: [logText],
  };

  try {
    await pool.query(query);
    console.log("Log entry saved to Postgres!:", logText);
  } catch (error) {
    console.error("Error writing log entry to Postgres:", error);
  }
}

// Express server setup
var express = require("express");
var app = express();

app.use(function (req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  next();
});

app.get("/", async (req, res) => {
  const quantity = parseFloat(req.query["quantity"]);

  const currentBitcoinPrice =
    Math.round(Math.random() * 10000.75 + 50000 * 100) / 100;
  const price = quantity * currentBitcoinPrice;

  // Log the transaction in Postgres
  await writeToPostgres(price);

  res.send({ price });
});

app.get("/metrics", async (req, res) => {
  const query = {
    text: "SELECT COUNT(*) FROM transactions WHERE log LIKE 'A volume of Bitcoin was requested%'",
  };

  try {
    const result = await pool.query(query);
    const count = parseInt(result.rows[0].count);
    let metrics = '';
    metrics += `bitcoin_service_transaction_count ${count}\n`;
    res.send(metrics);
  } catch (error) {
    console.error("Error reading log entries from Postgres:", error);
    res.send('');
  }
}
);
 

app.listen(80, () => {
  console.log("bitcoinService listening at port 80");
});
