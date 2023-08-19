package ru.practicum.shareit.booking.finder.owner;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingFinderByOwner {
    List<Booking> findBooking(Long ownerId, Pageable pageable);

    State getState();
}
