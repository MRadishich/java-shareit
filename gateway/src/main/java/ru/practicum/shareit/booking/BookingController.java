package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.enums.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.booking.util.Constant.*;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> bookItem(@RequestHeader(USER_ID_HEADER) long userId,
                                           @RequestBody @Valid BookingInputDto bookingDto) {
        log.info("Book an item. User id = {}, booking = {}", userId, bookingDto);
        return bookingClient.bookItem(bookingDto, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable long bookingId) {
        log.info("Get booking. User id = {}, bookingId = {}", userId, bookingId);
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByBooker(@RequestHeader(USER_ID_HEADER) long userId,
                                                      @RequestParam(defaultValue = "all") String state,
                                                      @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                                                      @RequestParam(value = "size", defaultValue = DEFAULT_NUMBER_ELEMENT_PER_PAGE) @Positive int size,
                                                      @RequestParam(value = "sort", defaultValue = "start") String sort,
                                                      @RequestParam(value = "dir", defaultValue = DESC) String dir) {
        log.info("Get booking by booker. User id = {}, state = {}, from = {}, size = {}, sort = {}, dir = {}",
                userId, state, from, size, sort, dir);
        return bookingClient.getBookingsByBookerIdAndState(userId, State.getEnum(state), from, size, sort, dir);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader(USER_ID_HEADER) long userId,
                                                     @RequestParam(defaultValue = "all") String state,
                                                     @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                                                     @RequestParam(value = "size", defaultValue = DEFAULT_NUMBER_ELEMENT_PER_PAGE) @Positive int size,
                                                     @RequestParam(value = "sort", defaultValue = "start") String sort,
                                                     @RequestParam(value = "dir", defaultValue = DESC) String dir) {
        log.info("Get booking by owner. User id = {}, state = {}, from = {}, size = {}, sort = {}, dir = {}",
                userId, state, from, size, sort, dir);
        return bookingClient.getBookingsByOwnerIdAndState(userId, State.getEnum(state), from, size, sort, dir);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approvedBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                                  @PathVariable long bookingId,
                                                  @RequestParam boolean approved) {
        log.info("Update booking status. User id = {}, isApproved = {}", bookingId, approved);
        return bookingClient.changeStatus(userId, bookingId, approved);
    }
}
