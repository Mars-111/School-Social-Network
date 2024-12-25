import Keycloak from 'keycloak-js';

// Инициализация Keycloak
const keycloak = new Keycloak({
    url: 'http://localhost:9082',
    realm: 'school-social-network',
    clientId: 'web-client',
    clientSecret: 'ew20Sr4XVPvywR1id81ocA9Y5jJDV3rM'
});

// Настройка Keycloak
keycloak.init({ onLoad: 'login-required' }).then(authenticated => {
    if (authenticated) {
        console.log('Аутентификация успешна');
        if (keycloak.token) {
            console.log('Токен:', keycloak.token);
            connectWebSocket(keycloak.token);
        } else {
            console.warn('Токен отсутствует после аутентификации');
        }
    } else {
        console.warn('Не аутентифицирован');
    }
}).catch(error => {
    console.error('Не удалось инициализировать Keycloak', error);
});

function connectWebSocket(token) {
    const socketUrl = 'http://localhost:8081/ws';
    const socket = new WebSocket(socketUrl, [], {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    socket.onopen = () => {
        console.log('WebSocket соединение установлено');
    };

    socket.onerror = (error) => {
        console.error('Ошибка WebSocket', error);
    };
}