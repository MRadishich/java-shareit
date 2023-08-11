package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.booking.util.Constant.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) long userId,
                                             @Validated(Create.class) @RequestBody ItemDto itemDto) {
        log.info("Create Item. User id = {}, item = {}", userId, itemDto);
        return itemClient.createItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_ID_HEADER) long userId,
                                                @PathVariable long itemId,
                                                @Validated(Create.class) @RequestBody CommentDto commentDto) {
        log.info("Create Comment. User id = {}, comment = {}", userId, commentDto);
        return itemClient.createComment(commentDto, itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader(USER_ID_HEADER) long userId,
                                                   @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                                   @RequestParam(value = "size", defaultValue = DEFAULT_NUMBER_ELEMENT_PER_PAGE) @Min(0) int size,
                                                   @RequestParam(value = "sort", defaultValue = "id") String sort,
                                                   @RequestParam(value = "dir", defaultValue = ASC) String dir) {
        log.info("Get items by user id. User id = {}", userId);
        return itemClient.getItemsByOwnerId(userId, from, size, sort, dir);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(USER_ID_HEADER) long userId,
                                              @PathVariable long itemId) {
        log.info("Get item by ud. User id = {}, item id = {}", userId, itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByText(@RequestParam(required = false) String text,
                                                 @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                                 @RequestParam(value = "size", defaultValue = DEFAULT_NUMBER_ELEMENT_PER_PAGE) @Min(0) int size,
                                                 @RequestParam(value = "sort", defaultValue = "id") String sort,
                                                 @RequestParam(value = "dir", defaultValue = ASC) String dir) {
        log.info("Get items by text. Text = {}", text);

        return text.isBlank() ? ResponseEntity.ok(List.of()) :
                itemClient.getItemsByText(text, from, size, sort, dir);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItemById(@PathVariable long itemId,
                                                 @RequestHeader(USER_ID_HEADER) long userId,
                                                 @Validated(Update.class) @RequestBody ItemDto itemDto) {
        log.info("Update item. User id = {}, item id = {}, new item = {}", userId, itemId, itemDto);
        return itemClient.updateItemById(itemId, itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable long itemId,
                               @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Delete item. User id = {}, item id = {}", userId, itemId);
        itemClient.deleteItemById(itemId, userId);
    }
}
