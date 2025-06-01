// components/Media.jsx
import React from "react";
import "./Media.css";

const IMAGE_EXTENSIONS = ["jpg", "jpeg", "png", "webp", "gif"];

export default function Media({ media }) {
    const {
        extension,
        size,
        filename,
        localUrl,
        loaded,
        file_metadata
    } = media;

    const isImage = extension && IMAGE_EXTENSIONS.includes(extension.toLowerCase());
    const width = file_metadata?.width;
    const height = file_metadata?.height;
    const aspectRatio = width && height ? `${width} / ${height}` : "1 / 1";
    const readableSize = size ? (size / 1024).toFixed(1) + " KB" : "";


    async function handleFileClick(media, messageId) {
        const fileId = media.media_id;
        const existing = saveFilesMap.current.get(fileId) || localStorage.getItem("fileId:" + fileId);

        if (existing && existing !== "downloaded") {
            // Уже загружено как Blob URL
            const a = document.createElement("a");
            a.href = existing;
            a.download = `file_${fileId}`;
            a.click();
            return;
        }

        // Загружаем и сохраняем
        let accessMediaToken;
        try {
            accessMediaToken = await getAccessTokenForMessageMedia(messageId);
        } catch (e) {
            console.error("Не удалось получить accessMediaToken:", e);
            return;
        }

        try {
            const fileBlob = await getFile(accessMediaToken, fileId);
            const blobUrl = URL.createObjectURL(fileBlob);
            const a = document.createElement("a");
            a.href = blobUrl;
            a.download = `file_${fileId}`;
            a.click();

            // сохраняем в кэш
            saveFile(fileId, blobUrl);
            media.path = blobUrl; // сохраняем путь в метаданные
        } catch (e) {
            console.error("Ошибка при загрузке файла по клику:", e);
        }
    }


    if (isImage) {
        return (
            <div className="media-wrapper" style={{ aspectRatio }}>
                {loaded && localUrl ? (
                    <img
                        src={localUrl}
                        alt={filename || "image"}
                        className="media-img"
                    />
                ) : (
                    <div className="media-placeholder">
                        Загрузка изображения…
                    </div>
                )}
            </div>
        );
    }

    // Другие типы файлов
    return (
        <div className="media-item file-placeholder">
            <div><strong>{filename || "Файл"}</strong></div>
            {extension && <div>Формат: {extension.toUpperCase()}</div>}
            {readableSize && <div>Размер: {readableSize}</div>}
        </div>
    );
}
