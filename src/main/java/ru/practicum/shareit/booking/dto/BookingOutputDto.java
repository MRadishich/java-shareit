package ru.practicum.shareit.booking.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static ru.practicum.shareit.util.Constant.DATE_TIME_PATTERN;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingOutputDto {
    private Long id;
    private ItemDto item;
    private UserDto booker;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime start;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime end;
    private BookingStatus status;
}
