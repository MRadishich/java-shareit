package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.util.Constant.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestHeader(USER_ID_HEADER) long userId,
                              @Validated(Create.class) @RequestBody ItemDto itemDto) {
        return itemService.createItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(USER_ID_HEADER) long userId,
                                    @PathVariable long itemId,
                                    @Validated(Create.class) @RequestBody CommentDto commentDto) {
        return itemService.createComment(commentDto, itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader(USER_ID_HEADER) long userId,
                                          @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                          @RequestParam(value = "size", defaultValue = DEFAULT_NUMBER_ELEMENT_PER_PAGE) @Min(0) int size,
                                          @RequestParam(value = "sort", defaultValue = "id") String sortParam,
                                          @RequestParam(value = "dir", defaultValue = ASC) String sortDirection) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.fromString(sortDirection), sortParam));
        return itemService.getItemsByOwnerId(userId, pageable);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(USER_ID_HEADER) long userId,
                               @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByKeyword(@RequestParam(required = false) String text,
                                           @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                           @RequestParam(value = "size", defaultValue = DEFAULT_NUMBER_ELEMENT_PER_PAGE) @Min(0) int size,
                                           @RequestParam(value = "sort", defaultValue = "id") String sortParam,
                                           @RequestParam(value = "dir", defaultValue = ASC) String sortDirection) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.fromString(sortDirection), sortParam));
        return itemService.getItemsByKeyword(text, pageable);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItemById(@PathVariable long itemId,
                                  @RequestHeader(USER_ID_HEADER) long userId,
                                  @Validated(Update.class) @RequestBody ItemDto itemDto) {
        return itemService.updateItemById(itemId, itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable long itemId,
                               @RequestHeader(USER_ID_HEADER) long userId) {
        itemService.deleteItemById(itemId, userId);
    }
}
