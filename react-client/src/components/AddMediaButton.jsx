import React, { useState } from "react";
import keycloak from "../keycloak";

export default function AddMediaButton() {
    const [files, setFiles] = useState([]);
    const [uploadStatus, setUploadStatus] = useState(null);

    const handleFileChange = (event) => {
        setFiles(event.target.files);
    };

    const handleUpload = async () => {
        if (files.length === 0) {
            alert("Выберите файлы для загрузки.");
            return;
        }

        const formData = new FormData();
        Array.from(files).forEach((file) => {
            formData.append("files", file);
        });

        // Пример длины файла (в байтах)
        const totalLength = Array.from(files).reduce((sum, file) => sum + file.size, 0);

        try {
            const response = await fetch("https://localhost:8083/api/files/upload?length=" + totalLength, {
                method: "POST",
                headers: {
                    'Authorization': `Bearer ${keycloak.token}`,
                },
                body: formData,
            });

            const result = await response.json();
            setUploadStatus(result);
            console.log("Результат загрузки:", result);
        } catch (error) {
            console.error("Ошибка загрузки:", error);
            setUploadStatus({ status: "error", message: error.message });
        }
    };

    return (
        <div>
            <input
                type="file"
                multiple
                onChange={handleFileChange}
                style={{ marginBottom: "10px" }}
            />
            <button onClick={handleUpload}>Загрузить файлы</button>
            {uploadStatus && (
                <div style={{ marginTop: "10px" }}>
                    <h4>Статус загрузки:</h4>
                    <pre>{JSON.stringify(uploadStatus, null, 2)}</pre>
                </div>
            )}
        </div>
    );
}