import "./HomePage.css";
import { useEffect, useState } from "react";
import { Header } from "../../components/Header";
import { ProductGrid } from "./ProductGrid";
import axios from "axios";

export function HomePage({ cart, loadCart, username, onLogout }) {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    const getHomeData = async () => {
      const response = await axios.get("/api/products");
      setProducts(response.data);
    }
    getHomeData();
  }, []);

  return (
    <>
      <title>Ecommerce</title>

      <Header cart={cart} username={username} onLogout={onLogout} />

      <div className="home-page">
        <ProductGrid products={products} loadCart={loadCart}/>
      </div>
    </>
  );
}
