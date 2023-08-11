package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.StartBeforeEndDateValid;

import java.time.LocalDateTime;

import static ru.practicum.shareit.booking.util.Constant.DATE_TIME_PATTERN;

@Data
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEndDateValid
public class BookingInnerDto {
    private Long id;
    private Long bookerId;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime start;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime end;
}
