import type { Axios } from "axios";
import axios from "axios";


const axiosUser: Axios = axios.create({
    baseURL: "https://id.mars-ssn.ru",
    withCredentials: true
});

axiosUser.interceptors.response.use(
    (response) => {
        console.log('Успешный запрос');
        return response;
    },
    (error) => {
        console.error('Ошибка при запросе:', error);
        if (error.response) {
            const status = error.response.status;
            console.error(`Ошибка при запросе к ${error.response.url} c кодом ${status}`);
        } else if (error.request) {
            console.error(`Сервер не ответил (возможно CORS или отключение сервера). Url: ${error.request.url}`);
        } else {
            console.error('Произошла ошибка при настройке запроса:', error.message);
        }
        return Promise.reject(error);
    }
);


export function getUserInfo(id: number): Promise<any> {
    return axiosUser.get(`/api/users/${id}`)
        .then(response => response.data)
        .catch(error => {
            console.error("Failed to fetch user info:", error);
            throw error;
        });
}

