import { useAuth } from "react-oidc-context";
import "./LoginPage.css";

export function LoginPage() {
  const auth = useAuth();

  const login = () => auth.signinRedirect();

  return (
    <main className="login-page">
      <section className="login-card">
        <img className="login-logo" src="images/logo-white.png" alt="Ecommerce" />
        <p className="login-eyebrow">Welcome back</p>
        <h1>Sign in to your account</h1>
        <p className="login-description">
          Continue securely through Keycloak to shop, manage your cart, and view orders.
        </p>

        {auth.error && (
          <div className="login-error" role="alert">
            Login failed: {auth.error.message}
          </div>
        )}

        <button className="login-button" type="button" onClick={login}>
          Sign in with Keycloak
        </button>

        <p className="login-security-note">
          Your password is entered only on the Keycloak login page.
        </p>
      </section>
    </main>
  );
}
