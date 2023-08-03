package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.util.Constant.DATE_TIME_PATTERN;

@Data
public class ItemRequestOutputDto {
    private final Long id;

    private final String description;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private final LocalDateTime created;

    private final List<ItemDto> items;
}
