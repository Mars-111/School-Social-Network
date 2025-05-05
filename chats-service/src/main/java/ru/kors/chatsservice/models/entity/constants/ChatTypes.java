package ru.kors.chatsservice.models.entity.constants;

public class ChatTypes {

    private ChatTypes() {} //запрет на создание экземпляров класса

    public static final String PUBLIC_GROUP = "PUBLIC_GROUP";
    public static final String PRIVATE_GROUP = "PRIVATE_GROUP";
    public static final String PRIVATE = "PRIVATE";
    public static final String CHANNEL = "CHANNEL";
    public static final String BOT = "BOT";

    public static boolean thisStringIsChatType(String type) {
        return type.equals(PUBLIC_GROUP) || type.equals(PRIVATE_GROUP) || type.equals(PRIVATE) || type.equals(CHANNEL) || type.equals(BOT);
    }
}
