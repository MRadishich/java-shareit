package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingInnerDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Objects;

@UtilityClass
public class BookingMapper {
    public Booking toBooking(BookingInputDto bookingDto) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }

    public BookingOutputDto toOutputBookingDto(Booking booking) {
        if (Objects.isNull(booking)) {
            return null;
        }

        return new BookingOutputDto(
                booking.getId(),
                ItemMapper.toItemDtoForOwner(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }

    public BookingInnerDto toBookingInnerDto(Booking booking) {
        if (Objects.isNull(booking)) {
            return null;
        }

        return new BookingInnerDto(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }
}
