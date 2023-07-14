package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.Constant;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingOutputDto bookItem(@RequestHeader(Constant.USER_ID_HEADER) long userId,
                                     @RequestBody @Valid BookingInputDto bookingDto) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getBooking(@RequestHeader(Constant.USER_ID_HEADER) long userId,
                                       @PathVariable long bookingId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingOutputDto> getBookingsByBooker(@RequestHeader(Constant.USER_ID_HEADER) long userId,
                                                      @RequestParam(defaultValue = "all") String state,
                                                      @RequestParam(defaultValue = "desc") String sort) {
        return bookingService.getBookingsByBookerIdAndState(
                userId,
                State.getEnum(state),
                Sort.Direction.valueOf(sort.toUpperCase())
        );
    }

    @GetMapping("/owner")
    public List<BookingOutputDto> getBookingsByOwner(@RequestHeader(Constant.USER_ID_HEADER) long userId,
                                                     @RequestParam(defaultValue = "all") String state,
                                                     @RequestParam(defaultValue = "desc") String sort) {
        return bookingService.getBookingsByOwnerIdAndState(
                userId,
                State.getEnum(state),
                Sort.Direction.valueOf(sort.toUpperCase())
        );
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto approvedBooking(@RequestHeader(Constant.USER_ID_HEADER) long userId,
                                            @PathVariable long bookingId,
                                            @RequestParam boolean approved) {
        return bookingService.changeStatus(userId, bookingId, approved);
    }
}
