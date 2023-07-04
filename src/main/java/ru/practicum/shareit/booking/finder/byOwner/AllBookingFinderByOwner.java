package ru.practicum.shareit.booking.finder.byOwner;

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
public class AllBookingFinderByOwner implements BookingFinderByOwner {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findBooking(Long ownerId, Sort sort) {
        return bookingRepository.findByItemOwnerId(ownerId, sort);
    }

    @Override
    public State getState() {
        return State.ALL;
    }
}