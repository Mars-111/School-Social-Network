//package ru.kors.storemediaservice.services;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.core.io.buffer.DataBufferUtils;
//import org.springframework.http.codec.multipart.FilePart;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import software.amazon.awssdk.core.async.AsyncRequestBody;
//import software.amazon.awssdk.services.s3.S3AsyncClient;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//import software.amazon.awssdk.services.s3.model.PutObjectResponse;
//
//import javax.imageio.ImageIO;
//import javax.imageio.ImageReader;
//import javax.imageio.stream.ImageInputStream;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.ByteBuffer;
//import java.nio.channels.Channels;
//import java.nio.channels.ReadableByteChannel;
//import java.time.Duration;
//import java.util.Iterator;
//import java.util.Set;
//import java.util.UUID;
//import java.util.concurrent.TimeoutException;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicLong;
//import java.util.regex.Pattern;
//
//@Service
//public class S3FileUploadService {
//
//    private final S3AsyncClient s3Client;
//    private final String bucketName;
//
//    // Константы безопасности
//    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB
//    private static final Pattern FILENAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+$");
//
//    public S3FileUploadService(S3AsyncClient s3Client,
//                               @Value("${aws.s3.bucket.name}") String bucketName) {
//        this.s3Client = s3Client;
//        this.bucketName = sanitizeBucketName(bucketName);
//    }
//
//    public Mono<FileUploadResult> uploadFile(FilePart filePart, long contentLength) {
//        // Валидация входных параметров
//        if (filePart == null) {
//            return Mono.error(new IllegalArgumentException("FilePart не может быть null"));
//        }
//
//        if (contentLength <= 0) {
//            return Mono.error(new IllegalArgumentException("Размер файла должен быть положительным"));
//        }
//
//        if (contentLength > MAX_FILE_SIZE) {
//            return Mono.error(new IllegalArgumentException("Размер файла превышает максимально допустимый: " + MAX_FILE_SIZE));
//        }
//
//        String originalFilename = filePart.filename();
//        if (originalFilename == null || originalFilename.trim().isEmpty()) {
//            return Mono.error(new IllegalArgumentException("Имя файла не может быть пустым"));
//        }
//
//        String fileName = generateFileName(originalFilename);
//        String contentType = validateAndSanitizeContentType(filePart.headers().getFirst("Content-Type"));
//
//        // Создаем процессор один раз и переиспользуем
//        DataBufferProcessor processor = new DataBufferProcessor(
//                filePart.content(), contentType, contentLength);
//
//        // Создаем AsyncRequestBody из Publisher
//        AsyncRequestBody requestBody = AsyncRequestBody.fromPublisher(processor.getDataFlux());
//
//        // Настраиваем запрос для S3
//        PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key(fileName)
//                .contentLength(contentLength);
//
//        if (contentType != null) {
//            requestBuilder.contentType(contentType);
//        }
//
//        PutObjectRequest putRequest = requestBuilder.build();
//
//        // Загружаем в S3 и получаем метаданные параллельно
//        Mono<PutObjectResponse> uploadMono = Mono.fromFuture(s3Client.putObject(putRequest, requestBody))
//                .timeout(Duration.ofMinutes(10))
//                .onErrorMap(TimeoutException.class,
//                        ex -> new RuntimeException("Превышено время ожидания загрузки в S3"));
//
//        Mono<ImageMetadata> metadataMono = processor.getImageMetadata();
//
//        return Mono.zip(uploadMono, metadataMono)
//                .map(tuple -> new FileUploadResult(
//                        fileName,
//                        contentLength,
//                        contentType,
//                        tuple.getT2()
//                ))
//                .onErrorMap(this::mapS3Exception)
//                .doFinally(signalType -> {
//                    // Принудительная очистка процессора
//                    processor.cleanup();
//                });
//    }
//
//    private ImageMetadata extractImageMetadata(InputStream inputStream, String contentType) {
//        // Этот метод теперь не используется - логика перенесена в DataBufferProcessor
//        return null;
//    }
//
//    private String validateAndSanitizeContentType(String contentType) {
//        if (contentType == null) {
//            return null;
//        }
//
//        return contentType.split(";")[0].trim().toLowerCase();
//    }
//
//    private String sanitizeBucketName(String bucketName) {
//        if (bucketName == null || bucketName.trim().isEmpty()) {
//            throw new IllegalArgumentException("Имя бакета не может быть пустым");
//        }
//        return bucketName.trim();
//    }
//
//    private Exception mapS3Exception(Throwable throwable) {
//        if (throwable instanceof software.amazon.awssdk.services.s3.model.S3Exception) {
//            software.amazon.awssdk.services.s3.model.S3Exception s3Exception =
//                    (software.amazon.awssdk.services.s3.model.S3Exception) throwable;
//
//            return switch (s3Exception.statusCode()) {
//                case 403 -> new SecurityException("Недостаточно прав для загрузки файла");
//                case 404 -> new IllegalArgumentException("Бакет не найден");
//                case 413 -> new IllegalArgumentException("Файл слишком большой");
//                default -> new RuntimeException("Ошибка загрузки в S3: " + s3Exception.getMessage());
//            };
//        }
//
//        return new RuntimeException("Неожиданная ошибка при загрузке файла", throwable);
//    }
//
//    private String generateFileName(String originalFilename) {
//        if (originalFilename == null) {
//            return UUID.randomUUID().toString();
//        }
//
//        // Санитизация имени файла
//        String sanitized = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
//
//        String extension = "";
//        if (sanitized.contains(".")) {
//            extension = sanitized.substring(sanitized.lastIndexOf("."));
//            if (extension.length() > 10) { // Ограничение длины расширения
//                extension = "";
//            }
//        }
//
//        // Ограничение общей длины имени файла
//        String baseName = UUID.randomUUID().toString();
//        String result = baseName + extension;
//
//        return result.length() > 255 ? baseName : result;
//    }
//
//    private boolean isImageContentType(String contentType) {
//        if (contentType == null) {
//            return false;
//        }
//
//        return contentType.startsWith("image/");
//    }
//
//    // Класс для обработки потока DataBuffer с извлечением метаданных
//    private static class DataBufferProcessor {
//        private final Flux<DataBuffer> originalFlux;
//        private final String contentType;
//        private final long expectedContentLength;
//        private final ByteArrayOutputStream headerBuffer = new ByteArrayOutputStream(8192);
//        private final AtomicBoolean headerProcessed = new AtomicBoolean(false);
//        private final AtomicBoolean cleaned = new AtomicBoolean(false);
//        private volatile ImageMetadata imageMetadata;
//        private final int HEADER_SIZE = 8192;
//        private final AtomicLong processedBytes = new AtomicLong(0);
//
//        public DataBufferProcessor(Flux<DataBuffer> flux, String contentType, long expectedContentLength) {
//            this.originalFlux = flux;
//            this.contentType = contentType;
//            this.expectedContentLength = expectedContentLength;
//        }
//
//        public Flux<ByteBuffer> getDataFlux() {
//            return originalFlux
//                    .doOnNext(this::validateSize)
//                    .doOnNext(this::processHeader)
//                    .map(dataBuffer -> {
//                        try {
//                            ByteBuffer byteBuffer = dataBuffer.asByteBuffer();
//                            return byteBuffer;
//                        } finally {
//                            // КРИТИЧНО: Всегда освобождаем DataBuffer
//                            DataBufferUtils.release(dataBuffer);
//                        }
//                    })
//                    .timeout(Duration.ofMinutes(5))
//                    .onErrorMap(TimeoutException.class,
//                            ex -> new RuntimeException("Превышено время ожидания загрузки файла"))
//                    .doOnError(error -> cleanup())
//                    .doOnComplete(this::cleanup)
//                    .doOnCancel(this::cleanup);
//        }
//
//        public Mono<ImageMetadata> getImageMetadata() {
//            // Исправляем логику: не подписываемся повторно на originalFlux
//            return Mono.fromCallable(() -> imageMetadata)
//                    .delayElement(Duration.ofMillis(100)) // Даем время для обработки заголовка
//                    .timeout(Duration.ofSeconds(30))
//                    .onErrorReturn(null);
//        }
//
//        private void validateSize(DataBuffer dataBuffer) {
//            long currentSize = processedBytes.addAndGet(dataBuffer.readableByteCount());
//
//            // Проверяем превышение размера с небольшим допуском
//            if (currentSize > expectedContentLength * 1.05) { // 5% допуск
//                throw new IllegalArgumentException(
//                        String.format("Размер файла превышает заявленный: %d > %d",
//                                currentSize, expectedContentLength));
//            }
//        }
//
//        private void processHeader(DataBuffer dataBuffer) {
//            // Обрабатываем заголовок только для изображений и только один раз
//            if (headerProcessed.get() || !isImageContentType(contentType)) {
//                return;
//            }
//
//            try {
//                // Читаем данные без изменения позиции оригинального буфера
//                int readable = Math.min(dataBuffer.readableByteCount(),
//                        HEADER_SIZE - headerBuffer.size());
//
//                if (readable <= 0) {
//                    return;
//                }
//
//                // Создаем копию данных для анализа
//                byte[] bytes = new byte[readable];
//                int currentPos = dataBuffer.readPosition();
//                dataBuffer.read(bytes, 0, readable);
//                dataBuffer.readPosition(currentPos); // Восстанавливаем позицию
//
//                synchronized (headerBuffer) {
//                    if (headerBuffer.size() < HEADER_SIZE) {
//                        headerBuffer.write(bytes, 0,
//                                Math.min(bytes.length, HEADER_SIZE - headerBuffer.size()));
//
//                        // Если накопили достаточно данных
//                        if (headerBuffer.size() >= Math.min(HEADER_SIZE, expectedContentLength)) {
//                            extractImageMetadataFromBytes(headerBuffer.toByteArray());
//                            headerProcessed.set(true);
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                headerProcessed.set(true); // Останавливаем обработку при ошибке
//            }
//        }
//
//        private void extractImageMetadataFromBytes(byte[] headerBytes) {
//            ImageInputStream imageStream = null;
//            ImageReader reader = null;
//
//            try (ByteArrayInputStream bis = new ByteArrayInputStream(headerBytes)) {
//                imageStream = ImageIO.createImageInputStream(bis);
//                if (imageStream == null) {
//                    return;
//                }
//
//                Iterator<ImageReader> readers = ImageIO.getImageReaders(imageStream);
//                if (!readers.hasNext()) {
//                    return;
//                }
//
//                reader = readers.next();
//                reader.setInput(imageStream, true, true); // Только метаданные
//
//                int width = reader.getWidth(0);
//                int height = reader.getHeight(0);
//
//                // Валидация размеров изображения
//                if (width <= 0 || height <= 0 || width > 50000 || height > 50000) {
//                    throw new IllegalArgumentException("Недопустимые размеры изображения");
//                }
//
//                String format = reader.getFormatName();
//                imageMetadata = new ImageMetadata(width, height, format);
//
//            } catch (Exception e) {
//                // Логируем ошибку, но не прерываем загрузку
//                System.err.println("Ошибка при извлечении метаданных изображения: " + e.getMessage());
//            } finally {
//                // КРИТИЧНО: Освобождаем ресурсы ImageIO
//                if (reader != null) {
//                    reader.dispose();
//                }
//                if (imageStream != null) {
//                    try {
//                        imageStream.close();
//                    } catch (IOException e) {
//                        // Игнорируем ошибки закрытия
//                    }
//                }
//            }
//        }
//
//        private boolean isImageContentType(String contentType) {
//            return contentType != null && contentType.startsWith("image/");
//        }
//
//        public void cleanup() {
//            if (cleaned.compareAndSet(false, true)) {
//                try {
//                    synchronized (headerBuffer) {
//                        headerBuffer.reset();
//                    }
//                } catch (Exception e) {
//                    // Игнорируем ошибки очистки
//                }
//            }
//        }
//    }
//
//    // Класс для хранения результата загрузки
//    public static class FileUploadResult {
//        private final String fileName;
//        private final long fileSize;
//        private final String contentType;
//        private final ImageMetadata imageMetadata;
//
//        public FileUploadResult(String fileName, long fileSize, String contentType, ImageMetadata imageMetadata) {
//            this.fileName = fileName;
//            this.fileSize = fileSize;
//            this.contentType = contentType;
//            this.imageMetadata = imageMetadata;
//        }
//
//        // Геттеры
//        public String getFileName() { return fileName; }
//        public long getFileSize() { return fileSize; }
//        public String getContentType() { return contentType; }
//        public ImageMetadata getImageMetadata() { return imageMetadata; }
//    }
//
//    // Класс для хранения метаданных изображения
//    public static class ImageMetadata {
//        private final int width;
//        private final int height;
//        private final String format;
//
//        public ImageMetadata(int width, int height, String format) {
//            this.width = width;
//            this.height = height;
//            this.format = format;
//        }
//
//        // Геттеры
//        public int getWidth() { return width; }
//        public int getHeight() { return height; }
//        public String getFormat() { return format; }
//    }
//}