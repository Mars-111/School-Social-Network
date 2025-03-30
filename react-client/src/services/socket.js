import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

function decodeToken(token) {
  try {
      // JWT состоит из 3 частей, нам нужна 2-я (Payload)
      const base64Url = token.split('.')[1];

      // Base64-URL → Base64
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');

      // Декодируем Payload
      return JSON.parse(atob(base64));
  } catch (e) {
      console.error("Ошибка декодирования токена:", e);
      return null;
  }
}


export default function initStompClient(token, onPrivateMessage) {
  console.log("Инициализация STOMP-клиента...");
  const client = new Client({
    webSocketFactory: () => new SockJS('https://socket.mars-ssn.ru/ws'),
    connectHeaders: {
      'Authorization': `Bearer ${token}`
    },
    debug: function (str) {
      console.log(str);
    },
    reconnectDelay: 5000,
    onConnect: () => {
      console.log("Подключено к STOMP");
      const decoded = decodeToken(token);
      if (decoded && decoded.sub) {
        client.subscribe(`/private/${decoded.sub}`, message => {
            console.log("Your sub: ", decoded.sub);
            console.log("Личное уведомление:", message.body);
            if (onPrivateMessage) {
                onPrivateMessage(message);
            }
        });
      }
    },
    onStompError: (frame) => {
      console.error("Ошибка STOMP: " + frame.headers['message']);
      console.error("Подробности: " + frame.body);
    }
  });

  client.activate();
  return client;
}
