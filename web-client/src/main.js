import { keycloak } from './KeycloakClass.js';
import {stompClient} from './socket.js'

let currentChatId = null

document.addEventListener('DOMContentLoaded', (event) => {
    let sendButton = document.getElementById('sendButton');
    sendButton.addEventListener('click', sendMessage);

    let addChatButton = document.getElementById('addChatButton');
    addChatButton.addEventListener('click', addChat);

    let form = document.getElementById('inputMessageForm');
    form.addEventListener('submit', onInputMessageFormSubmit);

});

function onInputMessageFormSubmit(event) {
    event.preventDefault(); // Отменяем стандартное поведение отправки формы
    const inputElement = document.getElementById('messageInput');
    const message = inputElement.value.trim();
    sendMessage(message);
    inputElement.value = ''; // Очищаем поле после отправки
}

function sendMessage(messageContent) {
    if (messageContent && stompClient && currentChatId) {
        // Создаем объект заголовков с токеном
        const headers = {
            'token': keycloak.token // Предполагается, что вы используете Keycloak для аутентификации
        };

        // Отправляем сообщение с заголовками 
        //TODO Определять в какой канал отправлять. Пока что all
        stompClient.send("app/send-message" + currentChatId, headers, JSON.stringify({ 
            'chatId': chatId,
            'content': messageContent,
            'type': 'CHAT' // Укажите тип сообщения, если это необходимо
        }));

        // Очищаем поле ввода
        document.getElementById('messageInput').value = '';
    }
}

function showMessage(message) {
    if (message.type == 'CHAT') {
        let messageElement = document.createElement('li');
        messageElement.classList.add('list-group-item');
        messageElement.textContent = message.sender + ": " + message.content;
        document.getElementById('messages').appendChild(messageElement);
    }
    else if (message.type == 'JOIN') { 
        console.log("JOIN" + message.sender);
        let messageElement = document.createElement('li');
        messageElement.classList.add('list-group-item');
        messageElement.textContent = message.sender + " joined the chat";
        document.getElementById('messages').appendChild(messageElement);
    }
    else if (message.type == 'LEAVE') { 
        console.log("LEAVE" + message.sender);
        let messageElement = document.createElement('li');
        messageElement.classList.add('list-group-item');
        messageElement.textContent = message.sender + " left the chat";
        document.getElementById('messages').appendChild(messageElement);
    }
}



function addChat(chatId) {
    let chatId = prompt("Введите id чата:");

    //отпарвим на сокет и проверим есть ли такое вообще

    if (chatId) {
        const headers = {
            'token': keycloak.token
        };

        // Отправляем сообщение на сервер для присоединения к чату
        stompClient.send("/app/join-chat", headers, JSON.stringify({
            'chatId': chatId
        }));
    }
    // Добавляем чат в список на клиенте
    let chatList = document.getElementById('chatList');
    let newChat = document.createElement('li');
    newChat.classList.add('list-group-item');
    newChat.textContent = chatId;
    newChat.addEventListener('click', () => {
        openChat(chatId, chatName);
    });
    chatList.appendChild(newChat);

    stompClient.subscribe('/topic/'+chatId, function (messageOutput) {
        const chatId = JSON.parse(messageOutput.body);
        addChat(chatId);
    });
}


function getPreferredUsernameFromToken() {
    const token = keycloak.token;
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    const tokenPayload = JSON.parse(jsonPayload);
    return tokenPayload.preferred_username;
}

function getSubFromToken() {
    const token = keycloak.token;
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    const tokenPayload = JSON.parse(jsonPayload);
    return tokenPayload.sub;
}

function systemMessageHandler(message) {
    if (message.type === "chat-list") {
        setChatList(message.chatList);
    }
}

function setChatList(list) {
    for (let i = 0; i < list.length; i++) {
        let chatList = document.getElementById('chatList');
        let newChat = document.createElement('li');
        newChat.classList.add('list-group-item');
        newChat.textContent = list[i];
        chatList.appendChild(newChat);
    }
}


export function joinChat(chatId) {
    // Добавляем чат в список на клиенте
    let chatList = document.getElementById('chatList');
    let newChat = document.createElement('li');
    newChat.textContent = chatId;
    newChat.addEventListener('click', () => {
        openChat(chatId);
    });
    chatList.appendChild(newChat);

    stompClient.subscribe('/topic/' + chatId, function (messageOutput) {
        showMessage(JSON.parse(messageOutput.body));
    });

}

function openChat(chatId) {
    
}