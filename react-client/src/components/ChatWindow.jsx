import MessageInput from './MessageInput';
import { sendMessage } from '../services/api';
import { useAppContext } from '../AppContext';
import keycloak from '../keycloak';
import Message from './Message';

function ChatWindow() {
    const {messages, chats, selectedChat} = useAppContext();
    
    const chat = chats.find((c) => c.id === selectedChat.id);
    const chatMessages = messages.get(selectedChat.id) || [];

    function handleSendMessage(text) {
        console.log("handleSendMessage: ", text);
        const message = {
            content: text,
            type: 'text',
            chatId: selectedChat.id
        }
        sendMessage(message, keycloak.token);
    }

    return (
        <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
        <div style={{ borderBottom: '1px solid #ddd', padding: '10px' }}>
            <h3>{chat.name}</h3>
            <div style={{ fontSize: '0.9em', color: '#888' }}>
            Скоро появится дополнительная информация (профиль, настройки и т.д.).
            </div>
        </div>
        <div style={{ flex: 1, padding: '10px', overflowY: 'auto' }}>
            {chatMessages.map((msg) => (
            <div key={msg.id} style={{ marginBottom: '10px' }}>
                <Message message={msg}/>
            </div>
            ))}
        </div>

        <div style={{ borderTop: '1px solid #ddd', padding: '10px' }}>
            <MessageInput onSend={handleSendMessage} />
        </div>
        </div>
    );
}

export default ChatWindow;
