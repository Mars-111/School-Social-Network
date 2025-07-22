import type { Axios } from "axios";
import axios from "axios";
import type { GrantType } from "../identity";

const axiosIdentity: Axios = axios.create({
    baseURL: "https://id.mars-ssn.ru",
    withCredentials: true
});

axiosIdentity.interceptors.response.use(
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

interface AuthorizeProps {
    grantType: GrantType, 
    redirectUrl: string,
    codeChallenge?: string,
    client: string,
    username: string,
    password: string
}

export function authorizeByCookie(props: AuthorizeProps): Promise<void> {
    const body = {
        username: props.username,
        password: props.password
    };
    return axiosIdentity.post("/api/authorize", body, {
        params: {
            grantType: props.grantType,
            redirectUrl: props.redirectUrl,
            clientId: props.client,
            codeChallenge: props.codeChallenge
        }
    });
}

export function refreshAccessTokenByCookie(): Promise<string | null> {
    return axiosIdentity.post("/api/refresh").then(resp => {
        return resp.data;
    })
    .catch(error => {
        console.error("Failed to refresh access token:", error)
        return null;
    });
}

export interface RegisterProps {
    username: string;
    password: string;
    email: string;
}

export function registerRequest(props: RegisterProps): Promise<boolean> {
    return axios.post("/api/register", props)
        .then(resp => {
            if (resp.status == 200) 
                return true;
            else
                return false;
        });
}

export function logoutRequest(): Promise<void> {
    return axiosIdentity.post("/api/logout");
}