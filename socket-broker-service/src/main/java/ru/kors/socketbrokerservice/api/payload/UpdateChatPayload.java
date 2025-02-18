package ru.kors.socketbrokerservice.api.payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateChatPayload {
    private String name;
    private String tag;
    private Boolean privateChat;
}