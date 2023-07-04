package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
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
                                                      @RequestParam(defaultValue = "all") State state,
                                                      @RequestParam(defaultValue = "desc") String sort) {
        return bookingService.getBookingsByBookerIdAndState(userId, state, Sort.Direction.valueOf(sort.toUpperCase()));
    }

    @GetMapping("/owner")
    public List<BookingOutputDto> getBookingsByOwner(@RequestHeader(Constant.USER_ID_HEADER) long userId,
                                                     @RequestParam(defaultValue = "all") State state,
                                                     @RequestParam(defaultValue = "desc") String sort) {
        return bookingService.getBookingsByOwnerIdAndState(userId, state, Sort.Direction.valueOf(sort.toUpperCase()));
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto approvedBooking(@RequestHeader(Constant.USER_ID_HEADER) long userId,
                                            @PathVariable long bookingId,
                                            @RequestParam boolean approved) {
        return bookingService.changeStatus(userId, bookingId, approved);
    }
}
