package ru.practicum.shareit.booking.finder.byBooker;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AllBookingFinderByBooker implements BookingFinderByBooker {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findBooking(Long bookerId, Sort sort) {
        return bookingRepository.findByBookerId(bookerId, sort);
    }

    @Override
    public State getState() {
        return State.ALL;
    }
}
