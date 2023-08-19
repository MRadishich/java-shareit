package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.enums.State;

import java.util.List;

public interface BookingService {
    BookingOutputDto createBooking(BookingInputDto bookingDto, Long userId);

    BookingOutputDto getBooking(Long bookingId, Long userId);

    List<BookingOutputDto> getBookingsByBookerIdAndState(Long bookerId, State state, Pageable pageable);

    List<BookingOutputDto> getBookingsByOwnerIdAndState(Long ownerId, State state, Pageable pageable);

    BookingOutputDto changeStatus(Long userId, Long bookingId, Boolean approved);
}
