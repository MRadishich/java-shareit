package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> getItemsByOwnerId(Long userId);

    List<ItemDto> getItemsByKeyword(String text);

    ItemDto updateItemById(Long itemId, ItemDto itemDto, Long userId);

    void deleteItemById(Long itemId);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long authorId);
}
