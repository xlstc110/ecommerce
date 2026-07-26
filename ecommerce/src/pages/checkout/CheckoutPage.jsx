import "./CheckoutPage.css";
import "./checkout-header.css";
import axios from "axios";
import { Header } from "../../components/Header";
import { useEffect, useState } from "react";
import { OrderSummary } from "./OrderSummary";
import { PaymentSummary } from "./PaymentSummary";

export function CheckoutPage({ cart }) {
  const [deliveryOptions, setDeliveryOtions] = useState([]);
  const [paymentSummary, setPaymentSummary] = useState(null);
  useEffect(() => {
    const getDeliveryData = async () => {
      const response = await axios.get("/api/delivery-options?expand=estimatedDeliveryTime");
      setDeliveryOtions(response.data);
    }
    getDeliveryData();
    const getPaymentData = async () => {
      const response = await axios.get("/api/payment-summary");
      setPaymentSummary(response.data);
    }
    getPaymentData();
  }, []);
  return (
    <>
      <title>Checkout</title>

      <div className="checkout-header">
        <div className="header-content">
          <div className="checkout-header-left-section">
            <a href="/">
              <img className="logo" src="images/logo.png" />
              <img className="mobile-logo" src="images/mobile-logo.png" />
            </a>
          </div>

          <div className="checkout-header-middle-section">
            Checkout (
            <a className="return-to-home-link" href="/">
              3 items
            </a>
            )
          </div>

          <div className="checkout-header-right-section">
            <img src="images/icons/checkout-lock-icon.png" />
          </div>
        </div>
      </div>

      <div className="checkout-page">
        <div className="page-title">Review your order</div>

        <div className="checkout-grid">
            <OrderSummary deliveryOptions={deliveryOptions} cart={cart}/>

          {paymentSummary && (
            <PaymentSummary paymentSummary={paymentSummary}/>
          )}
        </div>
      </div>
    </>
  );
}
