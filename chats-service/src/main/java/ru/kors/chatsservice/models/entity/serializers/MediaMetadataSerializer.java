package ru.kors.chatsservice.models.entity.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ru.kors.chatsservice.models.entity.MediaMetadata;

import java.io.IOException;

public class MediaMetadataSerializer extends JsonSerializer<MediaMetadata> {

    @Override
    public void serialize(MediaMetadata mediaMetadata, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("media_id", mediaMetadata.getMediaId());
        gen.writeNumberField("message_id", mediaMetadata.getMessage().getId());
        gen.writeNumberField("size", mediaMetadata.getSize());
        gen.writeStringField("extension", mediaMetadata.getExtension());
        gen.writeStringField("filename", mediaMetadata.getFilename());

        if (mediaMetadata.getHeight() != null) {
            gen.writeNumberField("height", mediaMetadata.getHeight());
        }
        if (mediaMetadata.getWidth() != null) {
            gen.writeNumberField("width", mediaMetadata.getWidth());
        }
        gen.writeEndObject();
    }
}
