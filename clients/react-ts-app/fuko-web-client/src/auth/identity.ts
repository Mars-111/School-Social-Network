import InternalLogicError from "../general/errors/classes/InternalLogicError";
import { parseJwt } from "./identity-util";
import { authorizeByCookie, refreshAccessTokenByCookie, registerRequest, logoutRequest } from "./internal/identityApi";
import type { RegisterProps } from "./internal/identityApi";
import { create } from "zustand"

interface IdentityConstructorProps {
 	client: string;
    grantType: GrantType;
    codeChallenge?: string;
}

interface AuthorizeMethodProps {
	username: string;
    password: string;
    redirectUrl: string;
    codeChallenge?: string;
}

export type AuthenticatedType = "authenticated" | "not_authenticated" | "unknown";

export type GrantType = "authorization_code" | "cookie";

type IdentityStoreType = {
    authenticated: AuthenticatedType;
    userId: number | null;
};

export const useIdentityStore = create<IdentityStoreType>()((set) => ({
    authenticated: "unknown",
    userId: null
}));

export default class Identity {
    private accessToken: string | null = null;
    private client: string;
    private grantType: GrantType;

    private intervalRefreshAccessToken: NodeJS.Timeout | null = null;

    constructor(props: IdentityConstructorProps) {
        this.client = props.client;
        this.grantType = props.grantType;
    }

    public getAccessToken(): string | null {
        return this.accessToken;
    }

    public getGrantType(): GrantType {
        return this.grantType;
    }

    public setClient(client: string): void {
        this.client = client;
    }

    public setGrantType(grantType: GrantType): void {
        this.grantType = grantType;
    }

    private setAuthenticated(authenticated: AuthenticatedType): void {
        useIdentityStore.setState(set => ({
            ...set,
            authenticated
        }));
        console.log("Authentication state changed:", authenticated);
    }

    public getAuthenticated(): AuthenticatedType {
        return useIdentityStore.getState().authenticated;
    }

    public authorize(props: AuthorizeMethodProps) : Promise<void> {
        if (props.codeChallenge && this.getGrantType() != "authorization_code") {
            throw new InternalLogicError("Code challenge only for grant type = 'authorization_code'. " +
                `Current grant type = '${this.getGrantType()}', code challenge = '${props.codeChallenge}'`);
        }
        else if (!props.codeChallenge && this.getGrantType() == "authorization_code") {
            throw new InternalLogicError("At grant type = 'authorization code' required code challenge. " +
                `Current grant type = '${this.getGrantType()}', code challenge = '${props.codeChallenge}'`);
        }
        
        return authorizeByCookie({
            grantType: this.getGrantType(),
            codeChallenge: props.codeChallenge,
            client: this.client,
            redirectUrl: props.redirectUrl,
            username: props.username,
            password: props.password
        }).then(() => {
            this.setAuthenticated("authenticated");
        }).catch((error) => {
            console.error("Authorization failed:", error);
            this.setAuthenticated("not_authenticated");
        });
    }

    public async setUserIdByAccessToken(): Promise<void> {
        const accessToken = this.getAccessToken();
        if (!accessToken) {
            console.warn("Cannot set user ID, access token is not available.");
            return;
        }

        const parsed = parseJwt(accessToken);
        const userId = parsed?.userId || null;
        useIdentityStore.setState(set => ({
            ...set,
            userId
        }));
        console.log("User ID set from access token:", userId);
    }

    public async refreshAccessToken(): Promise<void> {
        if (this.getAuthenticated() == "not_authenticated") {
            console.warn("Cannot refresh access token, user is not authenticated.");
            return;
        }
        const refreshedAccessToken: string | null = await refreshAccessTokenByCookie();
        console.log("Access token refreshed:", refreshedAccessToken);
        if (!refreshedAccessToken) {
            console.error("Failed to refresh access token.");
            this.accessToken = null;
            this.setAuthenticated("not_authenticated");
            return;
        }
        this.accessToken = refreshedAccessToken;
        this.setAuthenticated("authenticated");
        this.setUserIdByAccessToken();
    }

    public async startRefreshAccessTokenInterval(): Promise<void> {
        console.log("Starting access token refresh interval...");
        const scheduleNextRefresh = async () => {
            await this.refreshAccessToken();

            const accessToken = this.getAccessToken();

            if (this.getAuthenticated() === "not_authenticated" || !accessToken) {
                console.warn("Access token is not set after refresh. Returning.");
                this.setAuthenticated("not_authenticated");
                this.stopRefreshAccessTokenInterval();
                return;
            }

            const parsed = parseJwt(accessToken);
            const expiresAt: number = parsed?.exp || 0;
            if (expiresAt === 0) {
                console.error("Access token does not contain expiration time.");
                this.stopRefreshAccessTokenInterval();
                return;
            }
            else {
                console.log("Access token expires at:", expiresAt);
            }
            const now = Date.now();
            const expiresAtMs = expiresAt * 1000;
            const msUntilExpiration = expiresAtMs - now;
            console.log("expiresAtMs:", expiresAtMs, "now:", now, "msUntilExpiration:", msUntilExpiration);

            if (msUntilExpiration <= 0) {
                console.warn("Access token already expired or expires too soon.");
                return;
            }

            const refreshIn = msUntilExpiration * 0.9; // 10% до истечения
            console.log("Scheduling next refresh in ", refreshIn, "ms.");

            this.intervalRefreshAccessToken = setTimeout(scheduleNextRefresh, refreshIn);
        };
        this.intervalRefreshAccessToken = setTimeout(scheduleNextRefresh, 0);
    }

    public stopRefreshAccessTokenInterval(): void {
        if (this.intervalRefreshAccessToken) {
            clearInterval(this.intervalRefreshAccessToken);
            this.intervalRefreshAccessToken = null;
        }
    }

    public refreshAccessTokenIntervalIsRunning(): boolean {
        return this.intervalRefreshAccessToken !== null;
    }

    public register(props: RegisterProps): Promise<boolean> {
        return registerRequest(props);
    }

    public logout(): Promise<void> {
        return logoutRequest().then(() => {
            this.accessToken = null;
            this.setAuthenticated("not_authenticated");
        });
    }


}