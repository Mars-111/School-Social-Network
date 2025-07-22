import { Navigate, useLocation, Outlet } from "react-router-dom";
import { useIdentityStore } from '../identity';
import { type ReactNode } from "react";

export function ProtectedOutlet({ loadComponent }: { loadComponent?: ReactNode }) {
    const location = useLocation();
    const authenticated = useIdentityStore(state => state.authenticated);
    console.log("Rendering ProtectedOutlet, authenticated:", authenticated);
    if (authenticated === "not_authenticated") {
        console.log("Redirecting to login from ProtectedOutlet");
        return <Navigate to={`/login?redirectUrl=${location.pathname}`} replace />;
    }
    if (loadComponent && authenticated === "unknown") {
        return loadComponent;
    }

    return <Outlet />;
}

export function Protected({children, loadComponent}: { children: ReactNode, loadComponent?: ReactNode }) {
    const location = useLocation();
    const authenticated = useIdentityStore(state => state.authenticated);
    console.log("Rendering Protected, authenticated:", authenticated);
    if (authenticated == "not_authenticated") {
        return <Navigate to={`/login?redirectUrl=${location.pathname}`} replace />;
    }
    if (loadComponent && authenticated == "unknown") {
        return loadComponent;
    }
    return children;
}