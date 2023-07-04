package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {
    public Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                item.getComments() == null ? null :
                        item.getComments().stream()
                                .map(CommentMapper::toCommentInnerDto)
                                .collect(Collectors.toList())
        );
    }

    public ItemDto toItemDtoForOwner(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getBookings() == null ? null :
                        BookingMapper.toBookingInnerDto(item.getBookings().stream()
                                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now())
                                        && booking.getStatus() == BookingStatus.APPROVED)
                                .max(Comparator.comparing(Booking::getStart)).orElse(null)),
                item.getBookings() == null ? null :
                        BookingMapper.toBookingInnerDto(item.getBookings().stream()
                                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now())
                                        && booking.getStatus() == BookingStatus.APPROVED)
                                .min(Comparator.comparing(Booking::getStart)).orElse(null)),
                item.getComments() == null ? null :
                        item.getComments().stream()
                                .map(CommentMapper::toCommentInnerDto)
                                .collect(Collectors.toList())
        );
    }
}
