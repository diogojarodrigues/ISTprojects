const bgColors = [
  "linear-gradient(to right, #F7931A, #FDE8D0)", // bitcoin
  "linear-gradient(to right, #00b09b, #807CD8)", // solana
  "linear-gradient(to right, #8C8C8C, #393939)", // ethereum
  "linear-gradient(to right, #F26822, #4D4D4D)", // monero
];
const crypto = ["Bitcoin", "Solana", "Ethereum", "Monero"];
let i = 0;
document.getElementById("buy-bitcoin").addEventListener("click", function () {
  let quantity = document.getElementById("quantity-bitcoin").value;
  quantity = quantity === "" ? 0.1 : quantity;
  buy(quantity, "Bitcoin");
});

document.getElementById("buy-solana").addEventListener("click", function () {
  let quantity = document.getElementById("quantity-solana").value;
  quantity = quantity === "" ? 0.1 : quantity;
  buy(quantity, "Solana");
});

document.getElementById("buy-ethereum").addEventListener("click", function () {
    let quantity = document.getElementById("quantity-ethereum").value;
    quantity = quantity === "" ? 0.1 : quantity;
  buy(quantity, "Ethereum");
});

document.getElementById("buy-monero").addEventListener("click", function () {
    let quantity = document.getElementById("quantity-monero").value;
    quantity = quantity === "" ? 0.1 : quantity;
  buy(quantity, "Monero");
});

function buy(quantity, currency) {
  const url = `/api/${currency.toLowerCase()}-service?quantity=${quantity}`;
  console.log(url);
  fetch(url)
    .then((response) => response.json())
    .then((data) => {
      // display toast
      console.log(data);
      Toastify({
        text: `Bought ${quantity} ${currency} for $${data.price}`,
        duration: 3000,
        close: i % 3 ? true : false,
        style: {
          background:
            bgColors[crypto.findIndex((element) => element === currency)],
        },
      }).showToast();
      i++;
    });
}
