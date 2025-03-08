import { keycloak } from './KeycloakClass.js';

let stompClient = connectSocket();

function connectSocket(token) {
    console.log('Token: ' + keycloak.token);
    const socket = new SockJS('http://localhost:8081/ws?token=' + keycloak.token);
    stomp = Stomp.over(socket);
    stomp.connect({}, frame => {
        console.log('Connected: ' + frame);
        //loadChatList();
        //TODO: делаем че надо
    });

    return stomp;
}

export { connectSocket, stompClient };