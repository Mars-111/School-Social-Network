// Подключение к вебсокетам через STOMP
const socket = new WebSocket('ws://localhost:8081/ws');
const client = Stomp.over(socket);

let messagesElement = document.getElementById('messages');

client.connect({}, function(frame) {
    console.log('Connected: ' + frame);

    // Подписываемся на топик "stomp"
    client.subscribe('/topic/all', function(message) {
        showMessage(JSON.parse(message.body));
    });
});

function sendMessage() {
    let messageInput = document.getElementById('messageInput');
    let message = messageInput.value.trim();

    if (message !== '') {
        client.send("/app/all", {}, JSON.stringify({ content: message }));

        // Очищаем поле ввода после отправки сообщения
        messageInput.value = '';
    }
}

function showMessage(message) {
    let messageItem = document.createElement('li');
    messageItem.textContent = message.content;
    messagesElement.appendChild(messageItem);
}