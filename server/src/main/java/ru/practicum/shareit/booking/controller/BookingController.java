package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.util.Constant.*;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingOutputDto createBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                          @RequestBody BookingInputDto bookingDto) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                       @PathVariable long bookingId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingOutputDto> getBookingsByBooker(@RequestHeader(USER_ID_HEADER) long userId,
                                                      @RequestParam(defaultValue = "all") String state,
                                                      @RequestParam(value = "from", defaultValue = "0") int from,
                                                      @RequestParam(value = "size", defaultValue = DEFAULT_NUMBER_ELEMENT_PER_PAGE) int size,
                                                      @RequestParam(value = "sort", defaultValue = "start") String sort,
                                                      @RequestParam(value = "dir", defaultValue = DESC) String dir) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.fromString(dir), sort));
        return bookingService.getBookingsByBookerIdAndState(userId, State.getEnum(state), pageable);
    }

    @GetMapping("/owner")
    public List<BookingOutputDto> getBookingsByOwner(@RequestHeader(USER_ID_HEADER) long userId,
                                                     @RequestParam(defaultValue = "all") String state,
                                                     @RequestParam(value = "from", defaultValue = "0") int from,
                                                     @RequestParam(value = "size", defaultValue = DEFAULT_NUMBER_ELEMENT_PER_PAGE) int size,
                                                     @RequestParam(value = "sort", defaultValue = "start") String sort,
                                                     @RequestParam(value = "dir", defaultValue = DESC) String dir) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.fromString(dir), sort));
        return bookingService.getBookingsByOwnerIdAndState(userId, State.getEnum(state), pageable);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto approvedBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                            @PathVariable long bookingId,
                                            @RequestParam boolean approved) {
        return bookingService.changeStatus(userId, bookingId, approved);
    }
}
