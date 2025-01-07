let activeChat = null;
    const chatList = [];
    
    // Функция для добавления нового чата
    function addChat() {
      const chatName = prompt("Введите название чата:");
      if (chatName) {
        const newChat = {
          id: chatList.length + 1,
          name: chatName,
          messages: []
        };
        chatList.push(newChat);
        renderChatList();
      }
    }

    // Функция для рендера списка чатов
    function renderChatList() {
      const chatListContainer = document.getElementById("chatListContainer");
      chatListContainer.innerHTML = '';
      chatList.forEach(chat => {
        const chatItem = document.createElement("div");
        chatItem.classList.add("chat-item");
        chatItem.textContent = chat.name;
        chatItem.onclick = () => openChat(chat.id);
        chatListContainer.appendChild(chatItem);
      });
    }

    // Функция для открытия чата
    function openChat(chatId) {
      activeChat = chatList.find(chat => chat.id === chatId);
      document.getElementById("chatTitle").textContent = activeChat.name;
      renderMessages();
    }

    // Функция для рендера сообщений в чате
    function renderMessages() {
      const messagesContainer = document.getElementById("messagesContainer");
      messagesContainer.innerHTML = '';
      if (activeChat) {
        activeChat.messages.forEach(message => {
          const messageDiv = document.createElement("div");
          messageDiv.classList.add("message");
          messageDiv.innerHTML = `
            <div class="sender">${message.sender}</div>
            <div class="text">${message.text}</div>
          `;
          messagesContainer.appendChild(messageDiv);
        });
      }
    }

    // Функция для отправки сообщения
    function sendMessage() {
      const messageInput = document.getElementById("messageInput");
      const messageText = messageInput.value.trim();
      if (messageText && activeChat) {
        const newMessage = {
          sender: "Вы",
          text: messageText
        };
        activeChat.messages.push(newMessage);
        messageInput.value = '';
        renderMessages();
      }
    }