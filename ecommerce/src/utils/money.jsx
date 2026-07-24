export function formatMoney(amountCents) {
    return Number(amountCents / 100).toFixed(2);
}