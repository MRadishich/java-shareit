package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

@Data
public class ItemRequestInputDto {
    @JsonCreator
    public ItemRequestInputDto(String description) {
        this.description = description;
    }

    private final String description;
}
