package ru.kors.socketbrokerservice.controllers.payload;

import lombok.Setter;

public record ChatPersonalMessagePayload(String content, @Setter String sender, String receiver) {
}
