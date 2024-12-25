import { keycloak, authenticationKeycloak } from './KeycloakClass.js';

let stompClient;


document.addEventListener('DOMContentLoaded', (event) => {
    let sendButton = document.getElementById('sendButton');
    sendButton.addEventListener('click', sendMessage);

    connect();
});


authenticationKeycloak().then(() => {
    console.log('Token: ' + keycloak.token);
    if (keycloak.token) {
        connectSocket();
    } else {
        console.log('No token available');
    }
}).catch(() => {
    console.log('Failed to authenticate');
});

function connectSocket() {
    let socket = new SockJS(`http://localhost:8081/ws?token=${keycloak.token}`);
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/all', function (messageOutput) {
            showMessage(JSON.parse(messageOutput.body));//.content);
        });
    }, function (error) {
        console.log('Connection error: ' + error);
    });

    keycloak.onTokenExpired = () => {
        keycloak.updateToken(30).then(() => {
            stompClient.disconnect(() => {
                connectSocket();
            });
        }).catch(() => {
            console.log('Failed to refresh token');
        });
    };
}

function sendMessage() {
    let messageContent = document.getElementById('messageInput').value.trim();
    if (messageContent && stompClient) {
        // Создаем объект заголовков с токеном
        const headers = {
            'token': keycloak.token // Предполагается, что вы используете Keycloak для аутентификации
        };

        // Отправляем сообщение с заголовками
        stompClient.send("/app/all", headers, JSON.stringify({ 
            'content': messageContent,
            'type': 'CHAT' // Укажите тип сообщения, если это необходимо
        }));

        // Очищаем поле ввода
        document.getElementById('messageInput').value = '';
    }
}


function showMessage(message) {
    let messageElement = document.createElement('li');
    messageElement.classList.add('list-group-item');
    messageElement.textContent = message.sender + ": " + message.content;
    document.getElementById('messages').appendChild(messageElement);
}
