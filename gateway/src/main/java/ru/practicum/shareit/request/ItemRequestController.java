package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.booking.util.Constant.*;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createRequest(@RequestHeader(USER_ID_HEADER) long userId,
                                                @RequestBody @Valid ItemRequestInputDto itemRequestInputDto) {
        log.info("Create item request. User id = {}, request = {}", userId, itemRequestInputDto);
        return itemRequestClient.createRequest(itemRequestInputDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(USER_ID_HEADER) long userId,
                                              @RequestParam(value = "sort", defaultValue = "created") String sort,
                                              @RequestParam(value = "dir", defaultValue = DESC) String dir) {
        log.info("Get requests. User id = {}", userId);
        return itemRequestClient.getRequestsByRequesterId(userId, sort, dir);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherRequests(@RequestHeader(USER_ID_HEADER) long userId,
                                                   @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                                   @RequestParam(value = "size", defaultValue = DEFAULT_NUMBER_ELEMENT_PER_PAGE) @Min(0) int size,
                                                   @RequestParam(value = "sort", defaultValue = "created") String sort,
                                                   @RequestParam(value = "dir", defaultValue = DESC) String dir) {
        log.info("Get other requests. User id = {}", userId);
        return itemRequestClient.getOtherRequests(userId, from, size, sort, dir);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable long requestId) {
        log.info("Delete request. User id = {}, request id = {}", userId, requestId);
        return itemRequestClient.getRequestById(requestId, userId);
    }
}
