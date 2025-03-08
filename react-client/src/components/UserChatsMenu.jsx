import React, { useEffect, useState } from "react";
import { useAuth } from "./AuthProvider";
import { fetchUserChats } from "../api/UsersHttp";

export default function UserChatsMenu({ setActiveChat }) {
    const [userChats, setUserChats] = useState([]);
    const { token, loading } = useAuth(); // Дожидаемся загрузки

    useEffect(() => {
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
    
        getChats();
    }, [loading]); // Только по loading
    

    return (
        <div>
            <h2>Chats:</h2>
            <ul>
                {userChats?.map(chat => (
                    <li key={chat.id} onClick={() => setActiveChat(chat.id)}>{chat.name}</li>
                ))}
            </ul>
        </div>
    );
}
