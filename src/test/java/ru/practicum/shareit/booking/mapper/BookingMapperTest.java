package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingInnerDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {

    @Test
    void toBooking_whenInvoked_thenReturnBooking() {
        //given
        BookingInputDto bookingInputDto = new BookingInputDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        //when
        Booking booking = BookingMapper.toBooking(bookingInputDto);

        //then
        assertEquals(bookingInputDto.getStart(), booking.getStart());
        assertEquals(bookingInputDto.getEnd(), booking.getEnd());
    }

    @Test
    void toBookingOutputDto_whenInvoked_thenReturnBookingOutputDto() {
        //given
        Booking booking = new Booking(
                1L,
                new Item(),
                new User(),
                BookingStatus.WAITING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        //when
        BookingOutputDto bookingOutputDto = BookingMapper.toBookingOutputDto(booking).get();

        //then
        assertEquals(booking.getId(), bookingOutputDto.getId());
        assertEquals(booking.getStatus(), bookingOutputDto.getStatus());
        assertEquals(booking.getStart(), bookingOutputDto.getStart());
        assertEquals(booking.getEnd(), bookingOutputDto.getEnd());
    }

    @Test
    void toBookingInnerDto_whenInvoked_thenReturnBookingInnerDto() {
        //given
        Booking booking = new Booking(
                1L,
                new Item(),
                new User(),
                BookingStatus.WAITING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        //when
        BookingInnerDto bookingInnerDto = BookingMapper.toBookingInnerDto(booking).get();

        //then
        assertEquals(booking.getId(), bookingInnerDto.getId());
        assertEquals(booking.getStart(), bookingInnerDto.getStart());
        assertEquals(booking.getEnd(), bookingInnerDto.getEnd());
    }
}