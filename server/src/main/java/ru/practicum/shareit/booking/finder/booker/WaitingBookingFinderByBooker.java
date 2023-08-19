package ru.practicum.shareit.booking.finder.booker;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WaitingBookingFinderByBooker implements BookingFinderByBooker {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findBooking(Long bookerId, Pageable pageable) {
        return bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.WAITING, pageable);
    }

    @Override
    public State getState() {
        return State.WAITING;
    }
}
