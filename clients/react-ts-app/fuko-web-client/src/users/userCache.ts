import { type User } from "./models/user";
import UserCacheDatabase from "./internal/db/userCacheDatabase";
import { getUserInfo } from "./internal/api/userApi";

export class UserCache {
    private db: UserCacheDatabase;
    // Список id пользователей, которых мы уже синхронизировали после входа
    private syncUserIdsSet: Set<number>;
    /*
        Откуда мы получили пользователя. Это необходимо что бы контроллировать разрыв в данных между кешем и бекендом.
        Например, если если мы выйдем из чата и позже зайдем обратно, но пользователь обновился - мы это не узнаем.
        Следовательно, если cachedUsersFromMyChatsMap пустой, то мы можем считать, что данные в кеше актуальны.
    */
    private cachedUsersFromMyChatsMap: Map<number, Set<number>>; // key: userId, value: Set<chatId>

    constructor() {
        this.db = new UserCacheDatabase();
        this.syncUserIdsSet = new Set();
        this.cachedUsersFromMyChatsMap = new Map<number, Set<number>>();
    }

    async getUserById(userId: number, fromChatId: number | undefined = undefined): Promise<User | null> {
        try {
            //TODO: реалзовать cachedUsersFromMyChatsMap
            let user: User | undefined = undefined;
            if (this.syncUserIdsSet.has(userId)) {
                user = await this.db.users.get(userId);
                if (user) {
                    console.log(`User with ID ${userId} fetched from cache.`);
                    return user;
                }
            }

            user = await getUserInfo(userId);
            if (!user) {
                console.error(`Failed to fetch user with ID ${userId} from API.`);
                return null;
            }

            this.db.users.put(user);
            console.log(`User with ID ${userId} fetched from API and cached.`);

            return user;
        } catch (error) {
            console.error("Error fetching user by Id: ", error);
            return null;
        }
    }
}