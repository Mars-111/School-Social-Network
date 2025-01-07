import { keycloak, authenticationKeycloak } from './KeycloakClass.js';
import { joinChat } from './main.js';

let stompClient;

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
            showMessage(JSON.parse(messageOutput.body));
        });

        stompClient.subscribe('/system/join-chat/' + getSubFromToken(), function (messageOutput) {
            joinChat(JSON.parse(messageOutput.body));
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

export {stompClient};