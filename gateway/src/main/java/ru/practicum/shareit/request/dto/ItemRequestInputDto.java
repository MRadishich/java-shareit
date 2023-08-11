package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ItemRequestInputDto {
    @JsonCreator
    public ItemRequestInputDto(String description) {
        this.description = description;
    }

    @NotBlank
    private final String description;
}
