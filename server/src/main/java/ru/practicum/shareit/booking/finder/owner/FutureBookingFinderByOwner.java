package ru.practicum.shareit.booking.finder.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FutureBookingFinderByOwner implements BookingFinderByOwner {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findBooking(Long ownerId, Pageable pageable) {
        return bookingRepository.findByItemOwnerIdAndStartIsAfter(ownerId, LocalDateTime.now(), pageable);
    }

    @Override
    public State getState() {
        return State.FUTURE;
    }
}
