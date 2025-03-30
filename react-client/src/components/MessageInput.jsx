import React, { useRef } from 'react';


function MessageInput({ onSend }) {
    const text = useRef('');

    const handleSubmit = (e) => {
        e.preventDefault();
        onSend(text.current.value);
        text.current.value = '';
    };

    return (
        <form onSubmit={handleSubmit} style={{ display: 'flex' }}>
        <input 
            type="text" 
            ref={text}
            placeholder="Введите сообщение..."
            style={{ flex: 9, padding: '10px' }}
        />
        <button type="submit" style={{ padding: '10px', flex: '1'}}>Отправить</button>
        </form>
    );
}

export default MessageInput;
