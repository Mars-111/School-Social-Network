import { keycloak, authenticationKeycloak } from './KeycloakClass.js';

let stompClient = null;
let currentChatId = null;

authenticationKeycloak().then(authenticated => {
    if (authenticated) {
        connectWebSocket();
    }
});

document.addEventListener('DOMContentLoaded', (event) => {
    let sendButton = document.getElementById('sendButton');
    sendButton.addEventListener('click', sendMessage);
});

function connectWebSocket() {
    const socket = new SockJS('http://localhost:8081/ws?token=' + keycloak.token);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, frame => {
        console.log('Connected: ' + frame);
        loadChatList();
    });
}

const chats = new Map();
let subscribedChats = new Set();

function loadChatList() {
    stompClient.subscribe('/system/get-chats/' + keycloak.subject, message => {
        const chatList = JSON.parse(message.body); // Renamed to avoid conflict
        console.log(chatList);
        const chatListElement = document.getElementById('chat-list');
        chatListElement.innerHTML = '';
        chatList.forEach(chat => {
            const chatElement = document.createElement('div');
            chatElement.textContent = chat.name;
            chatElement.onclick = () => openChat(chat.id);
            chatListElement.appendChild(chatElement);
            
            if (!subscribedChats.has(chat.id)) {
                // Subscribe to chat messages
                stompClient.subscribe('/topic/' + chat.id, message => {
                    const messagePayload = JSON.parse(message.body);
                    if (currentChatId === messagePayload.chatId) { // Corrected variable usage
                        displayMessage(messagePayload);
                    }
                    if (!chats.has(chat.id)) {
                        chats.set(chat.id, []); // Initialize if not present
                    }
                    chats.set(chat.id, chats.get(chat.id).concat(messagePayload));
                });
                subscribedChats.add(chat.id);
            }   

            stompClient.subscribe('/system/get-history/' + keycloak.subject, message => {
                const body = JSON.parse(message.body); // Moved inside callback

                chats.set(body.chatId, body.messages);

                if (currentChatId === body.chatId) {
                    body.messages.forEach(message => {
                        displayMessage(message);
                    });
                }
            });
            stompClient.send('/app/get-history', { token: keycloak.token }, chat.id );
        });
    });
    stompClient.send('/app/get-chats', { token: keycloak.token }, {});
}

function openChat(chatId) {
    currentChatId = chatId;
    const chatMessagesElement = document.getElementById('chat-messages');
    chatMessagesElement.innerHTML = '';

    const messages = chats.get(chatId) || [];
    messages.forEach(message => {
        displayMessage(message);
    });
    console.log(chats.get(chatId));
}

function displayMessage(message) {
    const chatMessagesElement = document.getElementById('chat-messages');
    const messageElement = document.createElement('div');
    console.log("displayMessage: ");
    console.log(message);
    messageElement.textContent = `${message.senderKeycloakId}: ${message.content}`;
    chatMessagesElement.appendChild(messageElement);
}

function sendMessage() {
    console.log('Send button clicked');
    const messageInput = document.getElementById('message-input');
    const messageContent = messageInput.value;
    console.log(messageContent);
    if (messageContent && currentChatId) {
        stompClient.send('/app/send-message', { token: keycloak.token }, JSON.stringify({
            chatId: currentChatId,
            content: messageContent,
            type: 'CHAT'
        }));
        messageInput.value = '';
    }
}