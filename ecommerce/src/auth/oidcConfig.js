const appOrigin = window.location.origin;
export const keycloakAuthority = "http://localhost:8181/realms/ecommerce-microservices-security-realm";

export const oidcConfig = {
  authority: keycloakAuthority,
  client_id: "react-client",
  redirect_uri: `${appOrigin}/`,
  post_logout_redirect_uri: `${appOrigin}/login`,
  response_type: "code",
  scope: "openid profile email",
  automaticSilentRenew: true,
  loadUserInfo: true,
  onSigninCallback: () => {
    window.history.replaceState({}, document.title, window.location.pathname);
  },
};
