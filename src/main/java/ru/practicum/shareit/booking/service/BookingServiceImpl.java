package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.finder.booker.BookingFinderByBooker;
import ru.practicum.shareit.booking.finder.owner.BookingFinderByOwner;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFountException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final Map<State, BookingFinderByBooker> bookingFinderByUser;
    private final Map<State, BookingFinderByOwner> bookingFinderByOwner;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository,
                              List<BookingFinderByBooker> bookingFinderByBooker,
                              List<BookingFinderByOwner> bookingFinderByOwner) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingFinderByUser = bookingFinderByBooker.stream()
                .collect(Collectors.toMap(BookingFinderByBooker::getState, Function.identity()));
        this.bookingFinderByOwner = bookingFinderByOwner.stream()
                .collect(Collectors.toMap(BookingFinderByOwner::getState, Function.identity()));
    }

    @Override
    @Transactional
    public BookingOutputDto createBooking(BookingInputDto bookingDto, Long userId) {
        Long itemId = bookingDto.getItemId();

        checkDates(bookingDto.getStart(), bookingDto.getEnd());

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFountException("User with id = " + userId + " not found."));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFountException("Item with id = " + itemId + " not found."));

        if (!item.getAvailable()) {
            throw new BadRequestException("Item with id = " + itemId + " not available");
        }

        if (item.getOwnerId().equals(userId)) {
            throw new NotFountException("Owner can't book his own items.");
        }

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);

        return BookingMapper.toOutputBookingDto(booking).orElse(null);
    }

    private void checkDates(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start) || end.equals(start)) {
            throw new IllegalArgumentException("end: must be date after start date");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookingOutputDto getBooking(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFountException("Booking with id = " + bookingId + " not found."));

        if (!userRepository.existsById(userId)) {
            throw new NotFountException("User with id = " + userId + " not found.");
        }

        if (booking.getBooker().getId() != userId && booking.getItem().getOwnerId() != userId) {
            throw new NotFountException("User with id = " + userId + " "
                    + "does not have booking with id = " + bookingId);
        }

        return BookingMapper.toOutputBookingDto(booking).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingOutputDto> getBookingsByBookerIdAndState(long bookerId, State state, Sort.Direction sort) {
        if (!userRepository.existsById(bookerId)) {
            throw new NotFountException("User with id = " + bookerId + " not found.");
        }

        return bookingFinderByUser.get(state).findBooking(bookerId, Sort.by(sort, "start"))
                .stream()
                .map(BookingMapper::toOutputBookingDto)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingOutputDto> getBookingsByOwnerIdAndState(long ownerId, State state, Sort.Direction sort) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFountException("User with id = " + ownerId + " not found.");
        }

        return bookingFinderByOwner.get(state).findBooking(ownerId, Sort.by(sort, "start"))
                .stream()
                .map(BookingMapper::toOutputBookingDto)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingOutputDto changeStatus(long userId, long bookingId, boolean approved) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFountException("User with id = " + userId + " not found."));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFountException("Booking with id = " + bookingId + " not found."));

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BadRequestException("Booking status can only be changed from waiting status.");
        }

        if (!Objects.equals(booking.getItem().getOwnerId(), user.getId())) {
            throw new NotFountException("Booking status can only be changed by item's owner.");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);

        return BookingMapper.toOutputBookingDto(booking).orElse(null);
    }
}
