package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingInnerDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemMapperTest {

    @Test
    void toItem_whenInvoked_ReturnItem() {
        //given
        ItemDto itemDto = new ItemDto(
                1L,
                "new item",
                "new item...",
                true,
                3L,
                null,
                null,
                List.of()
        );

        Item expectedItem = new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getRequestId(),
                null,
                null,
                null
        );

        //when
        Item returnedItem = ItemMapper.toItem(itemDto);

        //then
        assertEquals(expectedItem, returnedItem);
    }

    @Test
    void toItemDto_whenInvoked_thenReturnItemDto() {
        //given
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);

        User user = new User(1L, "new user", "user@email.ru");

        Item item = new Item(
                1L,
                "new item",
                "new item...",
                true,
                3L,
                null,
                null,
                null
        );

        List<Booking> bookings = List.of(
                new Booking(
                        1L,
                        item,
                        user,
                        BookingStatus.APPROVED,
                        currentTime.minusDays(2),
                        currentTime.minusDays(1)
                ),
                new Booking(
                        2L,
                        item,
                        user,
                        BookingStatus.APPROVED,
                        currentTime.plusDays(2),
                        currentTime.plusDays(3)
                )
        );

        item.setBookings(bookings);

        ItemDto expectedItemDto = new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId(),
                null,
                null,
                null
        );

        //when
        ItemDto returnredItemDto = ItemMapper.toItemDto(item);

        //then
        assertEquals(expectedItemDto, returnredItemDto);
    }

    @Test
    void toItemDtoForOwner_whenInvoked_thenReturnItemDto() {
        //given
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);

        User user = new User(1L, "new user", "user@email.ru");

        Item item = new Item(
                1L,
                "new item",
                "new item...",
                true,
                3L,
                null,
                null,
                null
        );

        List<Booking> bookings = List.of(
                new Booking(
                        1L,
                        item,
                        user,
                        BookingStatus.APPROVED,
                        currentTime.minusDays(2),
                        currentTime.minusDays(1)
                ),
                new Booking(
                        2L,
                        item,
                        user,
                        BookingStatus.APPROVED,
                        currentTime.plusDays(2),
                        currentTime.plusDays(3)
                )
        );

        item.setBookings(bookings);

        ItemDto expectedItemDto = new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId(),
                new BookingInnerDto(1L, 1L, currentTime.minusDays(2), currentTime.minusDays(1)),
                new BookingInnerDto(2L, 1L, currentTime.plusDays(2), currentTime.plusDays(3)),
                null
        );

        //when
        ItemDto returnredItemDto = ItemMapper.toItemDtoForOwner(item);

        //then
        assertEquals(expectedItemDto, returnredItemDto);
    }
}