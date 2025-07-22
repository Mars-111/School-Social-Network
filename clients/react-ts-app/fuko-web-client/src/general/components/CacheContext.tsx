import { createContext, useContext, type ReactNode } from "react";
import { UserCache } from "../../users/userCache";
import type { ChildrenType } from "../models/ChildrenType";
type CacheContextType = {
    userCache: UserCache;
};


const CacheContext = createContext<CacheContextType | undefined>(undefined);

export const useCache = () => {
    const context = useContext(CacheContext);
    if (context === undefined) {
        throw new Error("useCache must be used within a CacheProvider");
    }
    return context;
};

type CacheProviderValueType = {
    userCache: UserCache;
};

export const CacheProvider = ({ children }: ChildrenType): ReactNode => {
    const userCache = new UserCache();

    return (
        <CacheContext.Provider value={{ userCache } as CacheProviderValueType}>
            {children}
        </CacheContext.Provider>
    );
}
