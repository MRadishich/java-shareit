package ru.practicum.shareit.booking.finder.byBooker;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApprovedBookingFinderByBooker implements BookingFinderByBooker {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findBooking(Long bookerId, Sort sort) {
        return bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.APPROVED, sort);
    }

    @Override
    public State getState() {
        return State.APPROVED;
    }
}
