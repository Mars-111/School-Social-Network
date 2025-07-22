import type { Axios } from "axios";
import axios from "axios";


const axiosFile: Axios = axios.create({
    baseURL: "https://file.mars-ssn.ru",
    withCredentials: true
});


axiosFile.interceptors.response.use(
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


/**
 * Загружает файл на сервер
 * @param file - файл для загрузки
 * @param size - исходный размер файла (можно получить через file.size)
 * @param isPrivate - приватный ли файл
 * @returns токен доступа к файлу от сервера
 */
export function uploadFileRequest(file: File, isPrivate: boolean): Promise<string> {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("size", file.size.toString());
    formData.append("private", isPrivate.toString());

    return axiosFile.post("/upload", formData, {
        headers: {
            "Content-Type": "multipart/form-data"
        }
    }).then(response => response.data);
}

export function downloadFileRequest(id: number, )