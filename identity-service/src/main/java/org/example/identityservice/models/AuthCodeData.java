package org.example.identityservice.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthCodeData {
    private Long userId;
    private String codeChallenge;
    private String clientId;

    public String toJson() {
        return '{' +
                "\"userId\":\"" + userId + "\"," +
                "\"codeChallenge\":\"" + codeChallenge + "\"," +
                "\"clientId\":\"" + clientId + '\"' +
                '}';
    }

    public AuthCodeData(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            AuthCodeData temp = mapper.readValue(json, AuthCodeData.class);
            this.userId = temp.userId;
            this.codeChallenge = temp.codeChallenge;
            this.clientId = temp.clientId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AuthCodeData from JSON", e);
        }
    }
}
