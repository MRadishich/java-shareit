package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
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
    private final Map<State, BookingFinderByBooker> bookingFinderByBooker;
    private final Map<State, BookingFinderByOwner> bookingFinderByOwner;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository,
                              List<BookingFinderByBooker> bookingFinderByBooker,
                              List<BookingFinderByOwner> bookingFinderByOwner) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingFinderByBooker = bookingFinderByBooker.stream()
                .collect(Collectors.toMap(BookingFinderByBooker::getState, Function.identity()));
        this.bookingFinderByOwner = bookingFinderByOwner.stream()
                .collect(Collectors.toMap(BookingFinderByOwner::getState, Function.identity()));
    }

    @Override
    @Transactional
    public BookingOutputDto createBooking(BookingInputDto bookingDto, Long userId) {
        Long itemId = bookingDto.getItemId();

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFountException("User with id = " + userId + " not found."));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFountException("Item with id = " + itemId + " not found."));

        if (!item.getAvailable()) {
            throw new BadRequestException("Item with id = " + itemId + " not available.");
        }

        if (item.getOwnerId().equals(userId)) {
            throw new NotFountException("Owner can't book his own items.");
        }

        Booking booking = BookingMapper.toBooking(bookingDto);
        updateFields(booker, item, booking);
        booking = bookingRepository.save(booking);

        return BookingMapper.toBookingOutputDto(booking).orElse(null);
    }

    private void updateFields(User booker, Item item, Booking booking) {
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingOutputDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFountException("Booking with id = " + bookingId + " not found."));

        if (!userRepository.existsById(userId)) {
            throw new NotFountException("User with id = " + userId + " not found.");
        }

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwnerId().equals(userId)) {
            throw new NotFountException("User with id = " + userId + " "
                    + "does not have booking with id = " + bookingId);
        }

        return BookingMapper.toBookingOutputDto(booking).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingOutputDto> getBookingsByBookerIdAndState(Long bookerId, State state, Pageable pageable) {
        if (!userRepository.existsById(bookerId)) {
            throw new NotFountException("User with id = " + bookerId + " not found.");
        }

        return bookingFinderByBooker.get(state).findBooking(bookerId, pageable)
                .stream()
                .map(BookingMapper::toBookingOutputDto)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingOutputDto> getBookingsByOwnerIdAndState(Long ownerId, State state, Pageable pageable) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFountException("User with id = " + ownerId + " not found.");
        }

        return bookingFinderByOwner.get(state).findBooking(ownerId, pageable)
                .stream()
                .map(BookingMapper::toBookingOutputDto)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingOutputDto changeStatus(Long userId, Long bookingId, Boolean approved) {
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

        return BookingMapper.toBookingOutputDto(booking).orElse(null);
    }
}
