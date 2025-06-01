// db.js
import { openDB } from 'idb';

const DB_NAME = 'chat-app';
const DB_VERSION = 1;

export const initDB = async () => {
  return openDB(DB_NAME, DB_VERSION, {
    upgrade(db) {
      const store = db.createObjectStore('messages', {
        keyPath: 'messageId',
      });
      store.createIndex('chatId', 'chatId');
    },
  });
};
