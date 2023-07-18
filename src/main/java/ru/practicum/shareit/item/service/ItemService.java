package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> getItemsByOwnerId(Long userId, Pageable pageable);

    List<ItemDto> getItemsByKeyword(String text, Pageable pageable);

    ItemDto updateItemById(Long itemId, ItemDto itemDto, Long userId);

    void deleteItemById(Long itemId, Long userId);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long authorId);
}
