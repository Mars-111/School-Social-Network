import React, { useState } from "react";
import "./Message.css";

export default function Message({ message }) {
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [editedText, setEditedText] = useState(message.content);

    const handleEdit = () => {
        setIsEditing(true);
        setIsMenuOpen(false);
    };

    const handleSaveEdit = () => {
        if (editedText.trim() !== "") {
            onEdit(editedText);
        }
        setIsEditing(false);
    };

    return (
        <div className="message-container">
            {/* Содержимое сообщения */}
            <div className="message-content">
                {isEditing ? (
                    <input
                        type="text"
                        value={editedText}
                        onChange={(e) => setEditedText(e.target.value)}
                        onBlur={handleSaveEdit}
                        autoFocus
                        className="edit-input"
                    />
                ) : (
                    <>
                    <strong>{message.sender_id ? message.sender_id : 
                    (message.senderId ? message.senderId : 'Неизвестно')}: </strong>
                    <span>{message.content}</span>
                    </>
                )}
            </div>

            {/* Три точки (меню) */}
            <div className="message-menu-icon" onClick={() => setIsMenuOpen(!isMenuOpen)}>
                &#8226;&#8226;&#8226;
            </div>

            {/* Меню с вариантами */}
            {isMenuOpen && (
                <div className="message-menu">
                    <button onClick={handleEdit}>✏ Изменить</button>
                    <button onClick={onDelete}>❌ Удалить</button>
                </div>
            )}
        </div>
    );
}
