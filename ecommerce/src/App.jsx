import { Navigate, Route, Routes } from "react-router";
import "./App.css";
import { HomePage } from "./pages/home/HomePage";
import { CheckoutPage } from "./pages/checkout/CheckoutPage";
import { OrdersPage } from "./pages/orders/OrdersPage";
import { TrackingPage } from "./pages/orders/TrackingPage";
import { useEffect, useState } from "react";
import { useAuth } from "react-oidc-context";
import axios from "axios";
import { LoginPage } from "./pages/login/LoginPage";
import { keycloakAuthority } from "./auth/oidcConfig";

function Storefront() {
  const auth = useAuth();
  const [cart, setCart] = useState([]);

  const loadCart = async () => {
    const response = await axios.get("/api/cart-items?expand=product");
    setCart(response.data);
  };

  useEffect(() => {
    loadCart();
  }, []);

  const username = auth.user?.profile.preferred_username
    || auth.user?.profile.name
    || "Customer";
  const logout = () => {
    const logoutParameters = new URLSearchParams({
      client_id: "react-client",
      post_logout_redirect_uri: `${window.location.origin}/login`,
    });
    if (auth.user?.id_token) {
      logoutParameters.set("id_token_hint", auth.user.id_token);
    }

    try {
      void auth.removeUser();
    } finally {
      window.location.href = `${keycloakAuthority}/protocol/openid-connect/logout?${logoutParameters}`;
    }
  };

  return (
    <Routes>
      <Route index element={(
        <HomePage
          cart={cart}
          loadCart={loadCart}
          username={username}
          onLogout={logout}
        />
      )} />
      <Route path="checkout" element={<CheckoutPage cart={cart} loadCart={loadCart}/>} />
      <Route path="orders" element={(
        <OrdersPage cart={cart} username={username} onLogout={logout} />
      )} />
      <Route path="tracking" element={(
        <TrackingPage cart={cart} username={username} onLogout={logout} />
      )} />
    </Routes>
  );
}

function AuthenticatedStorefront() {
  const auth = useAuth();
  const accessToken = auth.user?.access_token;
  const [configuredToken, setConfiguredToken] = useState(null);

  useEffect(() => {
    axios.defaults.headers.common.Authorization = `Bearer ${accessToken}`;
    setConfiguredToken(accessToken);

    return () => {
      delete axios.defaults.headers.common.Authorization;
    };
  }, [accessToken]);

  if (configuredToken !== accessToken) {
    return <div className="auth-status">Preparing your account...</div>;
  }

  return <Storefront />;
}

function App() {
  const auth = useAuth();

  if (auth.isLoading || auth.activeNavigator) {
    return <div className="auth-status">Connecting to Keycloak...</div>;
  }

  return (
    <Routes>
      <Route
        path="/login"
        element={auth.isAuthenticated ? <Navigate to="/" replace /> : <LoginPage />}
      />
      <Route
        path="/*"
        element={auth.isAuthenticated
          ? <AuthenticatedStorefront key={auth.user?.access_token} />
          : <Navigate to="/login" replace />}
      />
    </Routes>
  );
}

export default App;
