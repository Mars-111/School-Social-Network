package ru.kors.storemediaservice.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("files")
@Getter
@Setter
@Builder
public class Media {
    @Id
    private Integer id;

    private String key; //Уникальный ключ для доступа к файлу

    @Column(value = "owner_id")
    private Long ownerId;

    private String type;

    @Column(value = "special_id")
    private Long specialId;
}
