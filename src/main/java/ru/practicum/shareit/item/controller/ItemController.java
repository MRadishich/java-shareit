package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.Constant;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestHeader(Constant.USER_ID_HEADER) long userId,
                              @Validated(Create.class) @RequestBody ItemDto itemDto) {
        return itemService.createItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(Constant.USER_ID_HEADER) long userId,
                                    @PathVariable long itemId,
                                    @Validated(Create.class) @RequestBody CommentDto commentDto) {
        return itemService.createComment(commentDto, itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader(Constant.USER_ID_HEADER) long userId) {
        return itemService.getItemsByOwnerId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(Constant.USER_ID_HEADER) long userId,
                               @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByKeyword(@RequestParam(required = false) String text) {
        return itemService.getItemsByKeyword(text);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItemById(@PathVariable long itemId,
                                  @RequestHeader(Constant.USER_ID_HEADER) long userId,
                                  @Validated(Update.class) @RequestBody ItemDto itemDto) {
        return itemService.updateItemById(itemId, itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable long itemId) {
        itemService.deleteItemById(itemId);
    }
}
