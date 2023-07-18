package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.util.Constant.*;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestOutputDto createRequest(@RequestHeader(USER_ID_HEADER) long userId,
                                              @RequestBody @Valid ItemRequestInputDto itemRequestInputDto) {
        return itemRequestService.createRequest(itemRequestInputDto, userId);
    }

    @GetMapping
    public List<ItemRequestOutputDto> getRequests(@RequestHeader(USER_ID_HEADER) long userId,
                                                  @RequestParam(value = "sort", defaultValue = "created") String sortParam,
                                                  @RequestParam(value = "dir", defaultValue = DESC) String sortDirection) {
        return itemRequestService.getRequestsByRequesterId(userId, sortParam, Sort.Direction.fromString(sortDirection));
    }

    @GetMapping("/all")
    public List<ItemRequestOutputDto> getOtherRequests(@RequestHeader(USER_ID_HEADER) long userId,
                                                       @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                                       @RequestParam(value = "size", defaultValue = DEFAULT_NUMBER_ELEMENT_PER_PAGE) @Min(0) int size,
                                                       @RequestParam(value = "sort", defaultValue = "created") String sortParam,
                                                       @RequestParam(value = "dir", defaultValue = DESC) String sortDirection) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.fromString(sortDirection), sortParam));
        return itemRequestService.getOtherRequests(userId, pageable);
    }

    @GetMapping("/{requestId}")
    public ItemRequestOutputDto getRequest(@RequestHeader(USER_ID_HEADER) long userId,
                                           @PathVariable long requestId) {
        return itemRequestService.getRequestById(requestId, userId);
    }
}
