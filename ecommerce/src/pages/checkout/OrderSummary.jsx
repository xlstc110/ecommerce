import dayjs from "dayjs";
import { formatMoney } from "../../utils/money";
import { DeliveryOptions } from "./DeliveryOptions";
import axios from "axios";

export function OrderSummary({deliveryOptions, cart, loadCart}) {
  const deleteCartItem = async (productId) => {
    await axios.delete(`/api/cart-items/${productId}`);
    await loadCart();
  }
  const updateCartItem = async (productId) => {
    await axios.put(`/api/cart-items/${productId}`);
    await loadCart();
  }
  return (
    <>
      <div className="order-summary">
        {deliveryOptions.length > 0 &&
          cart.map((cartItem) => {
            const selectedDeliveryOption = deliveryOptions.find(
              (deliveryOption) => {
                return deliveryOption.id === cartItem.deliveryOptionId;
              },
            );

            return (
              <div key={cartItem.productId} className="cart-item-container">
                <div className="delivery-date">
                  Delivery date:{" "}
                  {dayjs(selectedDeliveryOption.estimatedDeliveryTimeMs).format(
                    "dddd, MMMM D",
                  )}
                </div>

                <div className="cart-item-details-grid">
                  <img className="product-image" src={cartItem.product.image} />

                  <div className="cart-item-details">
                    <div className="product-name">{cartItem.product.name}</div>
                    <div className="product-price">
                      {formatMoney(cartItem.product.priceCents)}
                    </div>
                    <div className="product-quantity">
                      <span>
                        Quantity:{" "}
                        <span className="quantity-label">
                          {cartItem.quantity}
                        </span>
                      </span>
                      <span className="update-quantity-link link-primary"
                        onClick={() => updateCartItem(cartItem.productId)}>
                        Update
                      </span>
                      <span className="delete-quantity-link link-primary"
                        onClick={() => deleteCartItem(cartItem.productId)}>
                        Delete
                      </span>
                    </div>
                  </div>

                  <DeliveryOptions deliveryOptions={deliveryOptions} cartItem={cartItem} loadCart={loadCart}/>
                </div>
              </div>
            );
          })}
      </div>
    </>
  );
}
