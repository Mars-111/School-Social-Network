import axios from 'axios';



const API_URL = 'http://localhost:8082/api/messages';

export const fetchChatMessages = async (chatId, token) => {
    const response = await axios.get(`${API_URL}/chat/${chatId}`, {
        headers: {
            'Authorization': `Bearer ${token}`,
        }
    });
    return response.data;
};

// export const fetchChatPartMessages = async (chatId, token, beginIndex, endIndex) => {
//     const response = await axios.get(`${API_URL}/chat/${chatId}`, {
//         headers: {
//             'Authorization': `Bearer ${token}`,
//         }
//     });
//     return response.data;
// }; TODO

export const fetchCreateMessage = async (message, token) => {
    const response = await axios.post(API_URL, message, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    return response.data;
};
