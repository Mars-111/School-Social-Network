import React from 'react';
import { createRoot } from 'react-dom/client';
import App from './App';
import keycloak from './keycloak';
import {AppProvider} from './AppContext';

keycloak.init({ onLoad: 'login-required' }).then(authenticated => {
    console.log("Keycloak token: ", keycloak.token);
    if (authenticated) {
        createRoot(document.getElementById('root')).render(
            <AppProvider>
                <App/>
            </AppProvider>
        )
    } else {
        window.location.reload();
    }
}).catch(err => {
    console.error("Keycloak initialization failed", err);
});