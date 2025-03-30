import React from 'react';
import { joinChat } from '../services/api';
import { useAppContext } from '../AppContext';
import keycloak from '../keycloak';

function ChatList() {
	const {chats} =  useAppContext();
	const {selectedChat, setSelectedChat} = useAppContext();
	const {setErrors} = useAppContext();

	const handleJoinChat = async (chat) => {
		try {
			await joinChat(chat.id, keycloak.token);
		} catch (error) {
			console.error("Ошибка запроса на вступление в чат:", error);
			setErrors("Ошибка запроса на вступление в чат");
		}
	};

	return (
		<div>
		<h2>Чаты</h2>
		<ul style={{ listStyle: 'none', padding: 0 }}>
			{chats.map(chat => (
			<li 
				key={chat.id} 
				onClick={() => setSelectedChat(chat)}
				style={{
				padding: '10px',
				cursor: 'pointer',
				backgroundColor: selectedChat && selectedChat.id === chat.id ? '#eee' : 'transparent',
				borderBottom: '1px solid #ddd',
				position: 'relative'
				}}
			>
				<div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
				<div>
					<div>{chat.name}</div>
					{chat.lastMessage && (
					<div style={{ fontSize: '0.8em', color: '#555' }}>
						{chat.lastMessage.content}
					</div>
					)}
				</div>
				{chat.privateChat && !chat.joined && (
					<button onClick={(e) => { e.stopPropagation(); handleJoinChat(chat); }}>
					Запрос на вступление
					</button>
				)}
				</div>
			</li>
			))}
		</ul>
		</div>
	);
}

export default ChatList;
