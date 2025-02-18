import { keycloak, authenticationKeycloak } from './KeycloakClass.js';

let stompClient = null;
let currentChatId = null;

authenticationKeycloak().then(authenticated => {
    console.log('Token: ' + keycloak.token);
    if (authenticated) {
        connectWebSocket();
    }
});

document.addEventListener('DOMContentLoaded', (event) => {
    let sendButton = document.getElementById('sendButton');
    sendButton.addEventListener('click', sendMessage);
    let createChatButton = document.getElementById('createChatButton');
    createChatButton.addEventListener('click', createChat);
});

function connectWebSocket() {
    console.log('Token: ' + keycloak.token);
    const socket = new SockJS('http://localhost:8081/ws?token=' + keycloak.token);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, frame => {
        console.log('Connected: ' + frame);
        loadChatList();
    });
}

let chats = new Set();
let chatsMessages = new Map();

function loadChatList() {
    fetch('http://localhost:8082/api/users/chats', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + keycloak.token
        }
    })
    .then(response => response.json())
    .then(data => {
        chats = data;
        const chatListElement = document.getElementById('chat-list');
        chatListElement.innerHTML = '';
        chats.forEach(chat => {
            const chatElement = document.createElement('div');
            chatElement.textContent = chat.name;
            chatElement.onclick = () => openChat(chat.id);
            chatListElement.appendChild(chatElement);
        });
    })
    .catch(error => {
        console.error('Error geting chats:', error);
    });
}

function createChat() {
    const chatName = prompt("Enter chat name:");
    const chatTag = prompt("Enter chat tag:");
    const isPrivate = confirm("Is this a private chat?");
    
    if (chatName && chatTag) {
        fetch('http://localhost:8082/api/chats', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + keycloak.token
            },
            body: JSON.stringify({
                tag: chatTag,
                name: chatName,
                privateChat: isPrivate
            })
        })
        .then(response => response.json())
        .then(data => {
            console.log('Chat created:', data);
            loadChatList(); // Refresh the chat list
        })
        .catch(error => {
            console.error('Error creating chat:', error);
        });
    }
}

function openChat(chatId) {
    currentChatId = chatId;
    const chatMessagesElement = document.getElementById('chat-messages');
    chatMessagesElement.innerHTML = '';

    const messages = chatsMessages.get(chatId) || [];
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
    if (messageContent && currentChatId) {
        fetch('http://localhost:8082/api/messages', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + keycloak.token
            },
            body: JSON.stringify({
                chatId: currentChatId,
                content: messageContent,
                type: 'CHAT'
            })
        })
        .then(response => response.json())
        .then(data => {
            console.log('Message sent:', data);
        })
        .catch(error => {
            console.error('Error sending message:', error);
        });
        messageInput.value = '';
        console.log(messageContent);
    }
}