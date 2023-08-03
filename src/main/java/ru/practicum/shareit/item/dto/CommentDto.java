package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank(groups = Create.class)
    private String text;
    private String authorName;
    private LocalDateTime created;
}
