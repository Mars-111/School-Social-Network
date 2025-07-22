import { createContext, useState, useContext, useEffect, useMemo } from "react";
import type { ReactNode } from "react";
import type { ChildrenType } from "../general/models/ChildrenType";
import Identity from "./identity";

type AuthContextType = {
    identity: Identity,
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
};

export const AuthProvider = ({children}: ChildrenType): ReactNode => {
    const [identity] = useState<Identity>(new Identity({
        client: "web",
        grantType: "cookie"
    }));


    useEffect(() => {
        if (!identity || identity.refreshAccessTokenIntervalIsRunning()) 
            return;
        identity.startRefreshAccessTokenInterval();
        return () => {
            identity.stopRefreshAccessTokenInterval();
        };
    }, [identity]);


    return (
        <AuthContext.Provider value={{ identity }}>
            {children}
        </AuthContext.Provider>
    )
}