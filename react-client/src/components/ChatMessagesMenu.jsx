import { useEffect, useState } from "react";
import { fetchChatMessages } from "../api/MessagesHttp";
import { useAuth } from "./AuthProvider";

// async function getMessages(token, chatId, beginIndex, endIndex) {
//     const data = await fetchChatPartMessages(chatId, token); // Загружаем сообщения

// } TODO

export default function ChatMessagesMenu({ chatId }) {
    const [messages, setMessages] = useState({}); // Делаем объект для хранения сообщений по chatId
    const { token, loading } = useAuth(); // Дожидаемся загрузки

    useEffect(() => {
        if (!chatId) return; // Предотвращаем вызов при отсутствии chatId
        if (loading) return; // Ждём, пока закончится загрузка

        if (!messages[chatId]) {
        const loadMessages = async () => {
            try {
                const data = await fetchChatMessages(chatId, token); // Загружаем сообщения
                setMessages((prev) => ({
                    ...prev, // Сохраняем предыдущие чаты
                    [chatId]: data || [], // Обновляем сообщения только для текущего чата
                }));
                } catch (error) {
                    console.error("Ошибка загрузки сообщений:", error);
                }
            };
            loadMessages();
        }
    }, [chatId, loading]); // Вызываем эффект только при изменении chatId

    return (
        <div>
            <h2>Chat messages:</h2>
            <ul>
                {messages[chatId]?.map((message) => ( // Безопасный доступ к сообщениям
                    <li key={message.id}>
                        {message.sender_id}: {message.content}
                    </li>
                )) || <li>Нет сообщений</li>}
            </ul>
        </div>
    );
}
