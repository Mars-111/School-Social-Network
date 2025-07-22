import Dexie from "dexie";
import type { Table } from "dexie";
import type { User } from "../../models/user";

export default class UserCacheDatabase extends Dexie {
    users!: Table<User, number>;

    constructor() {
        super("UserCache");
        this.version(1).stores({
            users: "id, username"
        });
    }
}