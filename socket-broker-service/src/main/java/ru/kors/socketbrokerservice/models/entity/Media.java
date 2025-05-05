package ru.kors.socketbrokerservice.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Media {
    private Long id;
    private String type;
    private String url;
}
