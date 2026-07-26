import "./HomePage.css";
import { useEffect, useState } from "react";
import { Header } from "../../components/Header";
import { ProductGrid } from "./ProductGrid";
import axios from "axios";

export function HomePage({ cart }) {
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

      <Header cart={cart} />

      <div className="home-page">
        <ProductGrid products={products}/>
      </div>
    </>
  );
}
