import React, { createContext, useContext, useState } from 'react';

import useInitChats from './hooks/useChats';
import useInitMessagesChats from './hooks/useMessagesChats';
import useInitSocket from './hooks/useSocket';

// Создаём глобальный контекст для всех хуков
const AppContext = createContext();

// Провайдер, который оборачивает всё приложение и предоставляет доступ к состоянию
function AppProvider({ children }) {
    const [errors, setErrors] = useState([]);
    const [chats, setChats, loadingChats] = useInitChats(setErrors);
    const [messages, setMessages] = useInitMessagesChats(chats, loadingChats, setErrors);
    const [selectedChat, setSelectedChat] = useState(null);
    const socketRef = useInitSocket(chats, setChats, messages, setMessages, setErrors, selectedChat);

    const [isAddChatVisible, setIsAddChatVisible] = useState(false);

    return (
        <AppContext.Provider
            value={{
                chats, setChats,
                errors, setErrors,
                messages, setMessages,
                socketRef,
                selectedChat, setSelectedChat,
                isAddChatVisible, setIsAddChatVisible
            }}
        >
            {children}
        </AppContext.Provider>
    );
}


function useAppContext() {
    return useContext(AppContext);
}

export {AppProvider, useAppContext};