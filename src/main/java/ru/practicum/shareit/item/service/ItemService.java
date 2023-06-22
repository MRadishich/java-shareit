package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getAllItemsByOwnerId(Long userId);

    ItemDto updateItemById(Long itemId, ItemDto itemDto, Long userId);

    void deleteItemById(Long itemId);

    List<ItemDto> getItemsByKeyword(String text);
}
