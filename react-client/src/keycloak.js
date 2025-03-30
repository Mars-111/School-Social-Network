import Keycloak from "keycloak-js";

// Настройки Keycloak
const keycloak = new Keycloak({
    url: "https://keycloak.mars-ssn.ru", // Адрес сервера Keycloak
    realm: "school-social-network",          // Название твоего реалма
    clientId: "web-client"     // ID клиента (указан в Keycloak)
});

export default keycloak;