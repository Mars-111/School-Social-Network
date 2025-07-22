import { useSearchParams, Navigate } from 'react-router-dom';
import { useAuth } from "../AuthContext";
import { isValidGrantType } from "../identity-util";
import { useState } from "react";
import { useIdentityStore } from '../identity';

export function Login() {
    const [searchParams] = useSearchParams();

    const { identity } = useAuth();
    const authenticated = useIdentityStore(state => state.authenticated);
    const [awaitAuthorization, setAwaitAuthorization] = useState(false);

    const clientIdParam = searchParams.get('clientId');
    const grantTypeParam = searchParams.get('grantType');
    
    if (clientIdParam) {
        identity.setClient(clientIdParam);
    }

    if (grantTypeParam) {
        if (!isValidGrantType(grantTypeParam)) {
            throw new Error(`Invalid grant type: ${grantTypeParam}`);
        }
        identity.setGrantType(grantTypeParam);
    }

    if (authenticated === "authenticated") {
        const redirectUrl = searchParams.get('redirectUrl') || '/';
        return <Navigate to={redirectUrl} replace />;
    }

    const handleLogin = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        const formData = new FormData(event.currentTarget);
        const username = formData.get('username') as string;
        const password = formData.get('password') as string;
        const redirectUrl = searchParams.get('redirectUrl') || '/';

        identity.authorize({
            username,
            password,
            redirectUrl
        }).then(() => {
            setAwaitAuthorization(false);
        }).catch((error) => {
            console.error("Authorization failed:", error);
            setAwaitAuthorization(false);
            return <Navigate to="/errors/authorization-failed" />;
        });

        setAwaitAuthorization(true);
    };

    return (
        <div>
            <h2>Login</h2>
            <form onSubmit={handleLogin}>
                <div>
                    <label htmlFor="username">Username or email:</label>
                    <input type="text" id="username" name="username" required />
                </div>
                <div>
                    <label htmlFor="password">Password:</label>
                    <input type="password" id="password" name="password" required />
                </div>
                {awaitAuthorization && <p>Awaiting authorization...</p>}
                {!awaitAuthorization && <button type="submit">Login</button>}
            </form>
        </div>
    );
}