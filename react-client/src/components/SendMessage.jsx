import { useRef } from "react";
import { fetchChatMessages, fetchCreateMessage } from "../api/MessagesHttp";
import { useAuth } from "./AuthProvider";


export default function SendMessage({ chatId }) {

    const message = useRef("");
    const { token, loading } = useAuth(); // Дожидаемся загрузки
    
    const sendMessage = () => {
        if (!message) return;
        const messageDTO = {
            chatId,
            content: message.current.value,
            type: "text"
        };
        console.log("Отправляем сообщение: ", messageDTO);
        fetchCreateMessage(messageDTO, token);
        message.current.value = "";
    };

    return (
        <div>
            <input ref={message} type="text" />
            <button onClick={sendMessage}>Send</button>
        </div>
    )
    
}
