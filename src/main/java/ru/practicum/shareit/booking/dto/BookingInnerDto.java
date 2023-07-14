package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class BookingInnerDto {
    private final Long id;
    private final Long bookerId;
    private final LocalDateTime start;
    private final LocalDateTime end;
}
