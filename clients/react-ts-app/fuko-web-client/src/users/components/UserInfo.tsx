import { useEffect, useState } from "react";
import { useCache } from "../../general/components/CacheContext";
import { type User } from "../models/user";
import { instantStringToDate } from "../../general/utils/InstantParser";

export function UserInfo({ userId }: { userId: number }) {
    const { userCache } = useCache();
    const [userInfo, setUserInfo] = useState<User | null>(null);

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
        </div>
   );
}