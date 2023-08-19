package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.util.Constant.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestHeader(USER_ID_HEADER) long userId,
                              @RequestBody ItemDto itemDto) {
        return itemService.createItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(USER_ID_HEADER) long userId,
                                    @PathVariable long itemId,
                                    @RequestBody CommentDto commentDto) {
        return itemService.createComment(commentDto, itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader(USER_ID_HEADER) long userId,
                                          @RequestParam(value = "from", defaultValue = "0") int from,
                                          @RequestParam(value = "size", defaultValue = DEFAULT_NUMBER_ELEMENT_PER_PAGE) int size,
                                          @RequestParam(value = "sort", defaultValue = "id") String sort,
                                          @RequestParam(value = "dir", defaultValue = ASC) String dir) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.fromString(dir), sort));
        return itemService.getItemsByOwnerId(userId, pageable);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(USER_ID_HEADER) long userId,
                               @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByText(@RequestParam(required = false) String text,
                                        @RequestParam(value = "from", defaultValue = "0") int from,
                                        @RequestParam(value = "size", defaultValue = DEFAULT_NUMBER_ELEMENT_PER_PAGE) int size,
                                        @RequestParam(value = "sort", defaultValue = "id") String sort,
                                        @RequestParam(value = "dir", defaultValue = ASC) String dir) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.fromString(dir), sort));
        return itemService.getItemsByText(text, pageable);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItemById(@PathVariable long itemId,
                                  @RequestHeader(USER_ID_HEADER) long userId,
                                  @RequestBody ItemDto itemDto) {
        return itemService.updateItemById(itemId, itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable long itemId,
                               @RequestHeader(USER_ID_HEADER) long userId) {
        itemService.deleteItemById(itemId, userId);
    }
}
