import { useAuth } from "../components/AuthProvider";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client"; 
import { useEffect, useState, useRef } from "react";
import useUserChats from "../hooks/useUserChats";

export function useWebSocket() {
    const { token, loading } = useAuth(); // Получаем актуальный токен
    const [client, setClient] = useState(null);

    console.log("Токен в WebSocket:", token);

    useEffect(() => {
        if (loading) return; // Ждем, пока не загрузится токен
        if (!token) return; // Ждем, пока токен не будет получен

        const socket = new SockJS('http://localhost:8081/ws'); // Создаем SockJS-соединение
        const wsClient = new Client({
            webSocketFactory: () => socket, // Указываем SockJS как WebSocket
            connectHeaders: {
                Authorization: `Bearer ${token}`,
            },
            onConnect: (frame) => {
                console.log("Connected to WebSocket");
                console.log("Connected headers:", frame.headers);

                useSocketSubscriptionChats(token, wsClient);
            },
            onStompError: (frame) => {
                console.error("Broker reported error: ", frame.headers["message"]);
                console.error("Additional details: ", frame.body);
            },
            onDisconnect: (frame) => {
                console.log("Disconnected from WebSocket");
            }
        });

        wsClient.activate();
        setClient(wsClient);

        return () => {
            wsClient.deactivate();
        };
    }, [loading, token]); // Подключаемся один раз при монтировании компонента

    
    return client;
}



export function useSocketSubscriptionChats(token) {
    const socket = useWebSocket();
    const chats = useUserChats(); // Получаем актуальный список чатов
    const subscribedChatsRef = useRef(new Set()); // Храним подписки

    useEffect(() => {
        if (!socket) return;

        const actualChats = new Set(chats.map(chat => chat.id));
        const subscribedChats = subscribedChatsRef.current;

        // Отписываемся от чатов, которых больше нет
        subscribedChats.forEach(chatId => {
            if (!actualChats.has(chatId)) {
                socket.unsubscribe(`/chat/${chatId}`);
                subscribedChats.delete(chatId);
            }
        });

        // Подписываемся на новые чаты
        actualChats.forEach(chatId => {
            if (!subscribedChats.has(chatId)) {
                socket.subscribe(`/chat/${chatId}`, message => {
                    console.log("New message in chat:", chatId);
                    console.log("Message:", message.body);
                    
                });
                subscribedChats.add(chatId);
            }
        });

    }, [socket, chats]); // useEffect сработает только при изменении чатов или сокета
}
