package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {

    private final Long id;

    @NotBlank(groups = Create.class)
    private final String name;

    @NotBlank(groups = Create.class)
    private final String description;

    @NotNull(groups = Create.class)
    private final Boolean available;

    public ItemDto(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
