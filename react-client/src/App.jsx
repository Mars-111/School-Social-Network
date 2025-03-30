import React, { useState, useEffect } from 'react';
import ChatList from './components/ChatList';
import ChatWindow from './components/ChatWindow';
import ErrorNotification from './components/ErrorNotification';
import './App.css';
import keycloak from './keycloak';
import {useAppContext} from './AppContext';
import AddChatButton from './components/AddChatButton';

function App() {
  const {chats, setChats} = useAppContext();
  const {socketRef} = useAppContext();
  const {errors, setErrors} = useAppContext();
  const {messages, setMessages} = useAppContext();
  const {selectedChat, setSelectedChat} = useAppContext();

  const {isAddChatVisible} = useAppContext();

  return (
    <div className="app-container">
      <div className="sidebar">
        <AddChatButton/>
        <ChatList/>
      </div>
      <div className="chat-container">
        {selectedChat ? (
          <ChatWindow/>
        ) : (
          <div className="no-chat-selected">
            Выберите чат для начала общения
          </div>
        )}
      </div>
      {errors.length > 0 && <ErrorNotification onClose={() => setErrors([])} />}
    </div>
  );
}

export default App;
