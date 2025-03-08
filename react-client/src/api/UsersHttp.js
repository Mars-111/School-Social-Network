import axios from "axios";

const API_URL = "http://localhost:8082/api/users";

export async function fetchUserChats(token) {
    console.log("Используемый токен:", token); // Проверяем, какой токен уходит

    if (!token) {
        console.error("Ошибка: Токен отсутствует");
        return;
    }

    try {
        const response = await axios.get(`${API_URL}/chats`, {
            headers: {
                'Authorization': `Bearer ${token}`,
            }
        });

        console.log("Ответ сервера:", response);
        return response.data;
    } catch (error) {
        console.error("Ошибка при запросе чатов:", error.response || error);
    }
}
