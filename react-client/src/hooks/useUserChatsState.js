import { useState } from 'react';

class Chat {
    constructor(id, tag, name, messages = []) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.messages = messages;
    }
}

//class 

export default function useUserChatsState() {
    const [userChats, setUserChats] = useState([]);
    return [userChats, setUserChats];
}
