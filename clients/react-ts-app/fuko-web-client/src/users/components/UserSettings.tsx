import { useEffect, useState } from "react";
import { useAuth } from "../../auth/AuthContext";
import { useIdentityStore } from "../../auth/identity";
import { useCache } from "../../general/components/CacheContext";
import { type User } from "../models/user";
import { instantStringToDate } from "../../general/utils/InstantParser";

export function UserSettings() {
    const { identity } = useAuth();
    const userId: number | null = useIdentityStore(state => state.userId);
    const { userCache } = useCache();
    const [userInfo, setUserInfo] = useState<User | null>(null);
    
    if (!userId) {
        console.error("User ID is not available.");
        return <h1>Please log in to view your settings.</h1>;
    }

    if (!userCache) {
        console.error("User cache is not available.");
        return <h1>Error: User cache is not available.</h1>;
    }

    useEffect(() => {
        if (userInfo && userInfo.id === userId) {
            return;
        }
        userCache.getUserById(userId).then(user => {
            setUserInfo(user);
        });
    }, [userId, userCache]);

    console.log("UserInfo:", userInfo);
    return (
        <div>
            <h1>User Settings:</h1>
            <p>User ID: {userId}</p>
            <p>Username: {userInfo ? userInfo.username : "Loading..."}</p>
            <p>Created At: {userInfo ? instantStringToDate(userInfo.createdAt).toLocaleString() : "Unknown"}</p>
            <button onClick={() => identity.logout() }>Logout</button>
        </div>
   );
}