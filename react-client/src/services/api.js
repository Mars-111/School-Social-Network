const API_BASE_URL = 'https://chats.mars-ssn.ru';

async function handleResponse(response) {
  if (response.status === 401) {
    window.location.href = '/';
    throw new Error('Unauthorized');
  }
  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || 'Ошибка запроса');
  }
  return await response.json();
}

async function getUserChats(token) {

  const response = await fetch(`${API_BASE_URL}/api/users/chats`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return handleResponse(response);
}

async function getChat(chatId, token) {
  const response = await fetch(`${API_BASE_URL}/api/chats/${chatId}`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return handleResponse(response);
}

async function getChatByTag(chatTag, token) {
	const response = await fetch(`${API_BASE_URL}/api/chats/tag/${chatTag}`, {
	  headers: {
		'Authorization': `Bearer ${token}`
	  }
	});
	return handleResponse(response);
  }
  

async function getMessagesByChat(chatId, token) {
  const response = await fetch(`${API_BASE_URL}/api/messages/chat/${chatId}`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return handleResponse(response);
}

async function sendMessage(message, token) {
	
	const response = await fetch(`${API_BASE_URL}/api/messages`, {
		method: 'POST',
		headers: {
		'Authorization': `Bearer ${token}`,
		'Content-Type': 'application/json'
		},
		body: JSON.stringify(message)
	});
	return handleResponse(response);
}

async function joinChat(chatId, token) {
  const response = await fetch(`${API_BASE_URL}/api/users/chats/${chatId}/join`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return handleResponse(response);
}

async function createJoinRequestToChat(chatId, token) {
	await fetch(`${API_BASE_URL}/api/chats/${chatId}/join-request`, {
		method: 'POST',
		headers: {
		  	'Authorization': `Bearer ${token}`
		}
	});
}

export { getChat, getMessagesByChat, sendMessage, joinChat, getUserChats, createJoinRequestToChat, getChatByTag };
