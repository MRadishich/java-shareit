package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestMapperTest {

    @Test
    void toDto_whenInvoked_thenReturnItemRequestOutputDto() {
        //given
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);
        List<Item> items = List.of(
                new Item(
                        1L,
                        "item 1",
                        "item 1...",
                        true,
                        3L,
                        1L,
                        null,
                        null
                ),
                new Item(
                        2L,
                        "item 2",
                        "item 2...",
                        true,
                        3L,
                        1L,
                        null,
                        null
                )
        );

        List<ItemDto> itemDtos = List.of(
                new ItemDto(
                        1L,
                        "item 1",
                        "item 1...",
                        true,
                        1L,
                        null,
                        null,
                        null
                ),
                new ItemDto(
                        2L,
                        "item 2",
                        "item 2...",
                        true,
                        1L,
                        null,
                        null,
                        null
                )
        );

        ItemRequest itemRequest = new ItemRequest(
                1L,
                3L,
                "i need item",
                currentTime,
                items
        );

        ItemRequestOutputDto expectedItemRequestOutputDto = new ItemRequestOutputDto(
                1L,
                "i need item",
                currentTime,
                itemDtos
        );

        //when
        ItemRequestOutputDto itemRequestOutputDto = ItemRequestMapper.toDto(itemRequest);

        //then
        assertEquals(expectedItemRequestOutputDto, itemRequestOutputDto);
    }

    @Test
    void toItemRequest_whenInvoked_thenReturnItemRequestOutput() {
        //given
        Long requesterId = 10L;

        ItemRequestInputDto itemRequestInputDto = new ItemRequestInputDto(
                "I need item"
        );

        ItemRequest expectedItemRequest = new ItemRequest(
                null,
                10L,
                "I need item",
                LocalDateTime.now().withNano(0),
                null
        );

        //when
        ItemRequest returnedItemRequest = ItemRequestMapper.toItemRequest(itemRequestInputDto, requesterId);

        //then
        assertEquals(expectedItemRequest.getRequesterId(), returnedItemRequest.getRequesterId());
        assertEquals(expectedItemRequest.getDescription(), returnedItemRequest.getDescription());
        assertEquals(expectedItemRequest.getCreated(), returnedItemRequest.getCreated().withNano(0));
    }
}