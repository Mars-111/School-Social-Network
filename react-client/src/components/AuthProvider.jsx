import { createContext, useContext, useEffect, useState } from "react";
import keycloak from "../keycloak";

const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [token, setToken] = useState(null);
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        let tokenRefreshInterval;

        keycloak
            .init({ onLoad: "login-required", checkLoginIframe: false })
            .then((authenticated) => {
                console.log("Auth status:", authenticated);
                setIsAuthenticated(authenticated);

                if (authenticated) {
                    setToken(keycloak.token);
                    setUser(keycloak.tokenParsed);
                    setLoading(false);

                    // Устанавливаем интервал обновления токена (раз в 60 сек)
                    tokenRefreshInterval = setInterval(() => {
                        refreshToken();
                    }, 60000);
                } else {
                    setLoading(false);
                }
            })
            .catch((error) => {
                console.error("Ошибка авторизации:", error);
                setLoading(false);
            });

        // Очищаем таймер при размонтировании
        return () => {
            if (tokenRefreshInterval) {
                clearInterval(tokenRefreshInterval);
            }
        };
    }, []);

    const refreshToken = async () => {
        try {
            if (!keycloak) return;
            const refreshed = await keycloak.updateToken(30);
            if (refreshed) {
                console.log("Токен обновлен:", keycloak.token);
                setToken(keycloak.token);
                setUser(keycloak.tokenParsed);
            }
        } catch (error) {
            console.error("Ошибка обновления токена", error);
        }
    };

    const logout = () => {
        keycloak.logout();
        setIsAuthenticated(false);
        setToken(null);
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, token, user, logout, loading }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}
