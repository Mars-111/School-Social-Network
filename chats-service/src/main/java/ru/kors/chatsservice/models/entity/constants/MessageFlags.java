package ru.kors.chatsservice.models.entity.constants;

public final class MessageFlags {

    private MessageFlags() {} // запрет на создание экземпляров

    // 📌 Базовые флаги состояния и действий
    public static final int IS_EDITED = 1 << 0;
    public static final int IS_DELETED = 1 << 1;
    public static final int IS_PINNED = 1 << 2;
    public static final int IS_FORWARDED = 1 << 3;
    public static final int IS_REPLY = 1 << 4;
    public static final int IS_REACTION_ENABLED = 1 << 5;
    public static final int HAS_REACTIONS = 1 << 6;
    public static final int IS_HIDDEN = 1 << 7;
    public static final int IS_IMPORTANT = 1 << 8;
    public static final int IS_READ = 1 << 9;

    // 📎 Тип содержимого
    public static final int HAS_TEXT = 1 << 10;
    public static final int HAS_MEDIA = 1 << 11;
    public static final int HAS_DOCUMENT = 1 << 12;
    public static final int HAS_AUDIO = 1 << 13;
    public static final int HAS_VIDEO = 1 << 14;
    public static final int HAS_STICKER = 1 << 15;
    public static final int HAS_LINK = 1 << 16;
    public static final int HAS_POLL = 1 << 17;
    public static final int HAS_LOCATION = 1 << 18;
    public static final int HAS_CONTACT = 1 << 19;

    // 🧩 Специальные типы сообщений
    public static final int IS_SYSTEM = 1 << 20;
    public static final int IS_ANNOUNCEMENT = 1 << 21;
    public static final int IS_SCHEDULED = 1 << 22;
    public static final int IS_ENCRYPTED = 1 << 23;
    public static final int IS_SELF_DESTRUCT = 1 << 24;

    // 👤 Отправитель / контекст
    public static final int IS_BOT = 1 << 25;
    public static final int IS_ADMIN = 1 << 26;
    public static final int IS_ANONYMOUS = 1 << 27;
    public static final int IS_GROUP_ONLY = 1 << 28;
    public static final int IS_PRIVATE_ONLY = 1 << 29;
    public static final int IS_MUTED = 1 << 30;

    // ✅ Утилиты

    public static boolean isFlagSet(int flags, int flag) {
        return (flags & flag) != 0;
    }

    public static int setFlag(int flags, int flag) {
        return flags | flag;
    }

    public static int clearFlag(int flags, int flag) {
        return flags & ~flag;
    }

    public static int sortFlagsDefaultUserToCreateMessage(int flags) {
        flags = clearFlag(flags, IS_EDITED);
        flags = clearFlag(flags, IS_DELETED);
        flags = clearFlag(flags, IS_PINNED);
        flags = clearFlag(flags, IS_FORWARDED);
        flags = clearFlag(flags, IS_REPLY);
        flags = clearFlag(flags, HAS_REACTIONS);
        flags = clearFlag(flags, IS_HIDDEN);
        flags = clearFlag(flags, IS_READ);
        flags = clearFlag(flags, IS_SYSTEM);
        flags = clearFlag(flags, IS_BOT);
        flags = clearFlag(flags, IS_ADMIN);
        return flags;
    }
}
