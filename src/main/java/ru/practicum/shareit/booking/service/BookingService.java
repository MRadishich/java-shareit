package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.enums.State;

import java.util.List;

public interface BookingService {
    BookingOutputDto createBooking(BookingInputDto bookingDto, Long userId);

    BookingOutputDto getBooking(long bookingId, long userId);

    List<BookingOutputDto> getBookingsByBookerIdAndState(long bookerId, State state, Sort.Direction sort);

    List<BookingOutputDto> getBookingsByOwnerIdAndState(long ownerId, State state, Sort.Direction sort);

    BookingOutputDto changeStatus(long userId, long bookingId, boolean approved);
}
