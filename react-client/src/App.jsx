import React, { useEffect } from "react";
import { useAuth } from "./components/AuthProvider";
import UserChatsMenu from "./components/UserChatsMenu";
import { useState } from "react";
import ChatMessagesMenu from "./components/ChatMessagesMenu";
import { useWebSocket } from "./socket/socketService";
import SendMessage from "./components/sendMessage";


export default function App() {


  const [activeChat, setActiveChat] = useState(null);
  const { token, loading } = useAuth(); // Дожидаемся загрузки

  useWebSocket(); // Подключаемся к WebSocket

  return (
    <>
      <GetToken />

      <UserChatsMenu setActiveChat={setActiveChat} />
      <ChatMessagesMenu chatId={activeChat} />
      <SendMessage chatId={activeChat} />
    </>
  );
}









function GetToken() {
  const { isAuthenticated, token, loading } = useAuth();

  if (loading) {
    return <p>Загрузка...</p>;
  }

  return (
    <div>
      <p>Keycloak token: {isAuthenticated ? token : "Не авторизован"}</p>
    </div>
  );
}
