package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CommentInnerDto {
    private final Long id;
    @NotBlank
    private final String text;
    @NotNull
    private final String authorName;
    private final LocalDateTime created;
}
