package ru.practicum.shareit.booking.finder.booker;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingFinderByBooker {
    List<Booking> findBooking(Long bookerId, Sort sort);

    State getState();
}
