package ru.practicum.shareit.booking.finder.byBooker;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.State;

import java.util.List;

public interface BookingFinderByBooker {
    List<Booking> findBooking(Long bookerId, Sort sort);

    State getState();
}
