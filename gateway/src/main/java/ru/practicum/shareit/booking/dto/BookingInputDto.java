package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.StartBeforeEndDateValid;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.shareit.booking.util.Constant.DATE_TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@StartBeforeEndDateValid
public class BookingInputDto {

    @NotNull
    private Long itemId;

    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime start;

    @NotNull
    @Future
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime end;
}