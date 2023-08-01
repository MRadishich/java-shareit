package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

import static ru.practicum.shareit.util.Constant.DATE_TIME_PATTERN;

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
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime created;
}
