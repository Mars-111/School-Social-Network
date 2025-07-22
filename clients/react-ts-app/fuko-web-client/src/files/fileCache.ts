import { FileCacheDatabase } from "./internal/db/fileCacheDatabase";
import InvalidFileError from "../general/errors/classes/InvalidFileError";
import { uploadFileRequest } from "./internal/api/fileApi";
import type { FileType } from "./models/fileType";
import type { AccessPrivateFileToken } from "./models/accessPrivateFileToken";
import InternalLogicError from "../general/errors/classes/InternalLogicError";

export class FileCache {
    private db: FileCacheDatabase;

    private accessPrivateFileTokenList: AccessPrivateFileToken[];

    constructor() {
        this.db = new FileCacheDatabase();
        this.accessPrivateFileTokenList = [];
    }

    async uploadFile(file: File, isPrivate: boolean): Promise<string> {
        if (file.size <= 0) {
            console.error("file size <= 0!");
            throw new InvalidFileError("file size <= 0");
        }
        return uploadFileRequest(file, isPrivate);
    }

    async getFileById(id: number, isPrivate: boolean, getAccessTokenFunc: (() => Promise<string | null>) | undefined): Promise<FileType | null> {
        try {
            if (isPrivate && !getAccessTokenFunc)
                throw new InternalLogicError("getAccessTokenFunc is undefined, but isPrivate = true");
            else if (!isPrivate && getAccessTokenFunc) 
                throw new InternalLogicError("getAccessTokenFunc is not undefined, but isPrivate = false");

            let file: FileType | undefined = undefined;
            file = await this.db.files.get(id);
            if (file) 
                return file;

            const accessToken: string | null = isPrivate ? await getAccessTokenFunc!() : null;
            if (!accessToken)
                return null;

            //TODO

        } catch (error) {
            console.error("Error fetching file bu Id: " + error);
            return null;
        }
    }
}