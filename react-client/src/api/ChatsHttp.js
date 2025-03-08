const API_BASE_URL = "https://your-api.com/api/chats";



// Получить все чаты
async function getAllChats() {
    return fetchWithAuth(`${API_BASE_URL}`);
}

// Получить чат по ID
async function getChatById(id) {
    return fetchWithAuth(`${API_BASE_URL}/${id}`);
}

// Получить чат по тэгу
async function getChatByTag(tag) {
    return fetchWithAuth(`${API_BASE_URL}/tag/${tag}`);
}

// Получить чаты по имени
async function getAllChatsByName(name) {
    return fetchWithAuth(`${API_BASE_URL}/name/${name}`);
}

// Создать чат
async function createChat(chatData) {
    return fetchWithAuth(`${API_BASE_URL}`, {
        method: "POST",
        body: JSON.stringify(chatData)
    });
}

// Изменить чат
async function changeChat(chatId, chatData) {
    return fetchWithAuth(`${API_BASE_URL}/${chatId}`, {
        method: "PUT",
        body: JSON.stringify(chatData)
    });
}

// Удалить чат
async function deleteChat(chatId) {
    return fetchWithAuth(`${API_BASE_URL}/${chatId}`, {
        method: "DELETE"
    });
}

// Получить все запросы на вступление в чат
async function getAllJoinRequests(chatId) {
    return fetchWithAuth(`${API_BASE_URL}/${chatId}/join-request`);
}

// Создать запрос на вступление в чат
async function createJoinRequest(chatId) {
    return fetchWithAuth(`${API_BASE_URL}/${chatId}/join-request`, {
        method: "POST"
    });
}

// Принять запрос на вступление
async function acceptJoinRequest(requestId) {
    return fetchWithAuth(`${API_BASE_URL}/join-request/${requestId}`, {
        method: "POST"
    });
}

// Отклонить запрос на вступление
async function deleteJoinRequest(requestId) {
    return fetchWithAuth(`${API_BASE_URL}/join-request/${requestId}`, {
        method: "DELETE"
    });
}

// Назначить роль пользователю
async function assignRole(chatId, userId, role) {
    return fetchWithAuth(`${API_BASE_URL}/${chatId}/roles/${userId}?role=${role}`, {
        method: "POST"
    });
}

// Получить роли пользователя в чате
async function getUserRoles(chatId, userId) {
    return fetchWithAuth(`${API_BASE_URL}/${chatId}/roles/${userId}`);
}

// Удалить роль у пользователя
async function unassignRole(chatId, userId, role) {
    return fetchWithAuth(`${API_BASE_URL}/${chatId}/roles/${userId}?role=${role}`, {
        method: "DELETE"
    });
}

export {
    getAllChats,
    getChatById,
    getChatByTag,
    getAllChatsByName,
    createChat,
    changeChat,
    deleteChat,
    getAllJoinRequests,
    createJoinRequest,
    acceptJoinRequest,
    deleteJoinRequest,
    assignRole,
    getUserRoles,
    unassignRole
};
