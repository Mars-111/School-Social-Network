import { useRef, useEffect } from 'react';
import initStompClient from '../services/socket';
import { getChat } from '../services/api';
import keycloak from '../keycloak';
import { useAppContext } from '../AppContext';

function existsChat(chats, chatId) {
    return chats.some(c => c.id === chatId);
}

export default function useInitSocket(chats, setChats, messages, setMessages, setErrors, selectedChat) {
    console.log("chats in useInitSocket: ", chats);
    const stompClientRef = useRef(null);
    
    // Инициализация STOMP-клиента с обработкой личных событий
    useEffect(() => {
        const client = initStompClient(keycloak.token, (message) => {
            try {
                const event = JSON.parse(message.body);
                if (event.type === "join-chat" && event.chat) {
                    setChats(prev => {
                        const exists = existsChat(prev, event.chat.id);
                        const data = JSON.parse(event.data);
                        return exists ? prev : [...prev, data.chat];
                    }); 
                }
            } catch (e) { 
                setErrors(prev => [...prev, `Ошибка обработки личного события сокета: ${e}`]);
            }
        });
        stompClientRef.current = client;
        return () => {
            if (client && client.connected) {
                client.deactivate();
            }
        };
    }, []);

    // Глобальная подписка на чаты
    useEffect(() => {
        if (stompClientRef.current && stompClientRef.current.connected && chats.length > 0) {
            const subscriptions = chats.map(chat => {
                console.log("Подписка на чат: ", chat.id);
                return {
                    id: chat.id,
                    subscription: (
                        stompClientRef.current.subscribe(`/chat/${chat.id}`, (message) => {
                            try {
                                const stompMessage = JSON.parse(message.body);
                                console.log("Получено сообщение из чата: ", stompMessage);

                                if (!existsChat(chats, chat.id)) {
                                    getChat(stompMessage.chat_id, keycloak.token)
                                        .then(chat => setChats(prev => [...prev, chat]))
                                        .catch(err => setErrors(prev => [...prev, `Ошибка получения чата: ${err}`]));
                                }

                                // Если сообщение НЕ из выбранного чата
                                console.log("selectedChat: ", selectedChat);
                                console.log("chat: ", chat);
                                if (selectedChat?.id !== chat.id) {
                                    messages.set(chat.id, [...(messages.get(chat.id) || []), stompMessage]);
                                    return;
                                }

                                setMessages(prev => {
                                    const newMessages = new Map(prev);
                                    const existingMessages = newMessages.get(chat.id) || [];

                                    if (stompMessage.type === "text") {
                                        newMessages.set(chat.id, [...existingMessages, stompMessage]);
                                    } 
                                    else if (stompMessage.type === "update-message") {
                                        const data = JSON.parse(stompMessage.data);
                                        newMessages.set(chat.id, existingMessages.map(m => 
                                            m.id === data.message.id ? data.message : m
                                        ));
                                    } 
                                    else if (stompMessage.type === "delete-message") {
                                        const data = JSON.parse(stompMessage.data);
                                        newMessages.set(chat.id, existingMessages.filter(m => 
                                            m.id !== data.messageId
                                        ));
                                    }

                                    return newMessages;
                                });

                            } catch (e) {
                                setErrors(prev => [...prev, `Ошибка обработки события чата: ${e}`]);
                            }
                        })
                    )
                };
            });

            return () => {
                subscriptions.forEach(sub => sub.subscription.unsubscribe());
            };
        }
    }, [stompClientRef.current?.connected, chats, selectedChat]);

    return stompClientRef;
}
