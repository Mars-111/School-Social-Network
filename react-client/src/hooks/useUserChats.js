import { useState, useEffect, use } from "react";
import { useAuth } from "../components/AuthProvider";
import { fetchUserChats } from "../api/UsersHttp";
import { useSocketSubscriptionChats } from "../socket/socketService";

export default function useUserChats(update = false) {
    const [userChats, setUserChats] = useState([]);
    const tryLoadChats = useRef(false);

    if (update || !tryLoadChats.current.value) {
        tryLoadChats.current.value = true;
        const { token, loading } = useAuth(); // Дожидаемся загрузки

        if (loading) return; // Ждём, пока закончится загрузка
        if (!token) return; // Делаем проверку на наличие токена
    
        const getChats = async () => {
            try {
                const data = await fetchUserChats(token);
                setUserChats(data || []);
            } catch (error) {
                console.error("Ошибка при загрузке чатов:", error);
            }



        };
        
        useSocketSubscriptionChats(token);

        getChats();
    }

    return userChats;
}
