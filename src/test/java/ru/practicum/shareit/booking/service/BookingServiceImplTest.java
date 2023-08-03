package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.finder.booker.*;
import ru.practicum.shareit.booking.finder.owner.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFountException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private List<BookingFinderByBooker> bookingFinderByBooker;
    @Mock
    private List<BookingFinderByOwner> bookingFinderByOwner;
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    private Map<State, BookingFinderByBooker> fillBookingFinderMap() {
        try {
            Field field = bookingService.getClass().getDeclaredField("bookingFinderByBooker");
            field.setAccessible(true);
            Map<State, BookingFinderByBooker> findersByBooker = (HashMap<State, BookingFinderByBooker>) field.get(bookingService);
            findersByBooker.put(State.ALL, new AllBookingFinderByBooker(bookingRepository));
            findersByBooker.put(State.WAITING, new WaitingBookingFinderByBooker(bookingRepository));
            findersByBooker.put(State.APPROVED, new ApprovedBookingFinderByBooker(bookingRepository));
            findersByBooker.put(State.REJECTED, new RejectedBookingFinderByBooker(bookingRepository));
            findersByBooker.put(State.PAST, new PastBookingFinderByBooker(bookingRepository));
            findersByBooker.put(State.CURRENT, new CurrentBookingFinderByBooker(bookingRepository));
            findersByBooker.put(State.FUTURE, new FutureBookingFinderByBooker(bookingRepository));
            return findersByBooker;
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private Map<State, BookingFinderByOwner> fillBookingOwnerMap() {
        try {
            Field field = bookingService.getClass().getDeclaredField("bookingFinderByOwner");
            field.setAccessible(true);
            Map<State, BookingFinderByOwner> findersByOwner = (HashMap<State, BookingFinderByOwner>) field.get(bookingService);
            findersByOwner.put(State.ALL, new AllBookingFinderByOwner(bookingRepository));
            findersByOwner.put(State.WAITING, new WaitingBookingFinderByOwner(bookingRepository));
            findersByOwner.put(State.APPROVED, new ApprovedBookingFinderByOwner(bookingRepository));
            findersByOwner.put(State.REJECTED, new RejectedBookingFinderByOwner(bookingRepository));
            findersByOwner.put(State.PAST, new PastBookingFinderByOwner(bookingRepository));
            findersByOwner.put(State.CURRENT, new CurrentBookingFinderByOwner(bookingRepository));
            findersByOwner.put(State.FUTURE, new FutureBookingFinderByOwner(bookingRepository));
            return findersByOwner;
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Test
    void createBooking_whenStartDateAfterEndDate_thenThrowException() {
        //given
        BookingInputDto bookingInputDto = new BookingInputDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().minusHours(1)
        );

        Long userId = 1L;

        //when
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(bookingInputDto, userId));

        //then
        assertEquals("end: must be date after start date", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenStartDateEqualsEndDate_thenThrowException() {
        //given
        LocalDateTime datetime = LocalDateTime.now();
        BookingInputDto bookingInputDto = new BookingInputDto(
                1L,
                datetime,
                datetime
        );

        Long userId = 1L;

        //when
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(bookingInputDto, userId));

        //then
        assertEquals("end: must be date after start date", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenUserIsNotExists_thenThrowException() {
        //given
        BookingInputDto bookingInputDto = new BookingInputDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2)
        );

        Long userId = 1L;

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                NotFountException.class,
                () -> bookingService.createBooking(bookingInputDto, userId));

        //then
        assertEquals("User with id = " + userId + " not found.", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenItemIsNotExists_thenThrowException() {
        //given
        BookingInputDto bookingInputDto = new BookingInputDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2)
        );

        User user = User.builder()
                .id(1L)
                .name("New user")
                .email("newUser@email.ru")
                .build();

        Long userId = user.getId();

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingInputDto.getItemId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                NotFountException.class,
                () -> bookingService.createBooking(bookingInputDto, userId));

        //then
        assertEquals("Item with id = " + userId + " not found.", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenItemIsNotAvailable_thenThrowException() {
        //given
        Item item = Item.builder()
                .id(1L)
                .description("new item")
                .ownerId(1L)
                .available(false)
                .build();

        BookingInputDto bookingInputDto = new BookingInputDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2)
        );

        User user = User.builder()
                .id(1L)
                .name("New user")
                .email("newUser@email.ru")
                .build();

        Long userId = user.getId();

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingInputDto.getItemId())).thenReturn(Optional.of(item));

        Exception exception = assertThrows(
                BadRequestException.class,
                () -> bookingService.createBooking(bookingInputDto, userId));

        //then
        assertEquals("Item with id = " + userId + " not available.", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenOwnerIdEqualsBookerId_thenThrowException() {
        //given
        Item item = Item.builder()
                .id(1L)
                .description("new item")
                .ownerId(1L)
                .available(true)
                .build();

        BookingInputDto bookingInputDto = new BookingInputDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2)
        );

        User user = User.builder()
                .id(1L)
                .name("New user")
                .email("newUser@email.ru")
                .build();

        Long userId = user.getId();

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingInputDto.getItemId())).thenReturn(Optional.of(item));

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> bookingService.createBooking(bookingInputDto, userId));

        //then
        assertEquals("Owner can't book his own items.", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenBookingIsValid_thenSaved() {
        //given
        Item item = Item.builder()
                .id(1L)
                .description("new item")
                .ownerId(2L)
                .available(true)
                .build();

        BookingInputDto bookingInputDto = new BookingInputDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2)
        );

        User user = User.builder()
                .id(1L)
                .name("New user")
                .email("newUser@email.ru")
                .build();

        Long userId = user.getId();

        Booking booking = Booking.builder()
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .end(bookingInputDto.getEnd())
                .start(bookingInputDto.getStart()).build();

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingInputDto.getItemId())).thenReturn(Optional.of(item));

        bookingService.createBooking(bookingInputDto, userId);

        //then
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void createBooking_whenSaveBooking_thenSetBookerAndSetItem() {
        //given
        Item item = Item.builder()
                .id(1L)
                .description("new item")
                .ownerId(2L)
                .available(true)
                .build();

        BookingInputDto bookingInputDto = new BookingInputDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2)
        );

        User user = User.builder()
                .id(1L)
                .name("New user")
                .email("newUser@email.ru")
                .build();

        Long userId = user.getId();

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingInputDto.getItemId())).thenReturn(Optional.of(item));

        bookingService.createBooking(bookingInputDto, userId);

        //then
        verify(bookingRepository, times(1)).save(bookingArgumentCaptor.capture());
        Booking actualBooking = bookingArgumentCaptor.getValue();
        assertEquals(item, actualBooking.getItem());
        assertEquals(user, actualBooking.getBooker());
    }

    @Test
    void createBooking_whenSaveBooking_thenStatusEqualsWaiting() {
        //given
        Item item = Item.builder()
                .id(1L)
                .description("new item")
                .ownerId(2L)
                .available(true)
                .build();

        BookingInputDto bookingInputDto = new BookingInputDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2)
        );

        User user = User.builder()
                .id(1L)
                .name("New user")
                .email("newUser@email.ru")
                .build();

        Long userId = user.getId();

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingInputDto.getItemId())).thenReturn(Optional.of(item));

        bookingService.createBooking(bookingInputDto, userId);

        //then
        verify(bookingRepository, times(1)).save(bookingArgumentCaptor.capture());
        Booking actualBooking = bookingArgumentCaptor.getValue();
        assertEquals(BookingStatus.WAITING, actualBooking.getStatus());
    }

    @Test
    public void getBooking_whenBookingIsNotExists_thenThrowException() {
        //given
        Long bookingId = 1L;
        Long userId = 1L;

        //when
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                NotFountException.class,
                () -> bookingService.getBooking(bookingId, userId));

        //then
        assertEquals("Booking with id = " + bookingId + " not found.", exception.getMessage());
    }

    @Test
    public void getBooking_whenUserIsNotExists_thenThrowException() {
        //given
        Long bookingId = 1L;
        Long userId = 1L;

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        //when
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.existsById(userId)).thenReturn(false);

        Exception exception = assertThrows(
                NotFountException.class,
                () -> bookingService.getBooking(bookingId, userId));

        //then
        assertEquals("User with id = " + userId + " not found.", exception.getMessage());

    }

    @Test
    public void getBooking_whenUserIdNotEqualsBookerIdAndUserIdNotEqualsOwnerId_thenThrowException() {
        //given
        Long bookingId = 1L;
        Long userId = 1L;

        User booker = User.builder()
                .id(3L)
                .name("New user")
                .email("newUser@email.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .description("new item")
                .ownerId(2L)
                .available(true)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        //when
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.existsById(userId)).thenReturn(true);

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> bookingService.getBooking(bookingId, userId));

        //then
        assertEquals("User with id = " + userId + " " +
                "does not have booking with id = " + bookingId, exception.getMessage());
    }

    @Test
    public void getBooking_whenUserIdEqualsBookerId_thenReturnBookingOutputDto() {
        //given
        Long bookingId = 1L;
        Long userId = 1L;

        User booker = User.builder()
                .id(1L)
                .name("New user")
                .email("newUser@email.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .description("new item")
                .ownerId(2L)
                .available(true)
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .item(item)
                .author(booker)
                .created(LocalDateTime.now())
                .build();

        item.setComments(List.of(comment));

        Booking booking = Booking.builder()
                .id(1L)
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        BookingOutputDto expectedBookingOutputDto = new BookingOutputDto(
                booking.getId(),
                ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );

        //when
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.existsById(userId)).thenReturn(true);

        BookingOutputDto actualBookingOutputDto = bookingService.getBooking(bookingId, userId);

        //then
        assertEquals(expectedBookingOutputDto, actualBookingOutputDto);
    }

    @Test
    public void getBooking_whenUserIdEqualsOwnerId_thenReturnBookingOutputDto() {
        //given
        Long bookingId = 1L;
        Long userId = 2L;

        User booker = User.builder()
                .id(1L)
                .name("New user")
                .email("newUser@email.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .description("new item")
                .ownerId(2L)
                .available(true)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        BookingOutputDto expectedBookingOutputDto = new BookingOutputDto(
                booking.getId(),
                ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );

        //when
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.existsById(userId)).thenReturn(true);

        BookingOutputDto actualBookingOutputDto = bookingService.getBooking(bookingId, userId);

        //then
        assertEquals(expectedBookingOutputDto, actualBookingOutputDto);
    }

    @Test
    public void getBookingsByBookerIdAndState_whenBookerIsNotExists_thenThrowException() {
        //given
        Long bookerId = 1L;
        State state = State.WAITING;
        Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "start"));

        //when
        when(userRepository.existsById(bookerId)).thenReturn(false);

        Exception exception = assertThrows(NotFountException.class,
                () -> bookingService.getBookingsByBookerIdAndState(bookerId, state, pageable));

        //then
        assertEquals("User with id = " + bookerId + " not found.", exception.getMessage());
    }

    @Test
    public void getBookingsByBookerIdAndState_whenStateWaiting_thenInvokeBookingRepository() {
        //given
        fillBookingFinderMap();
        Long bookerId = 1L;
        State state = State.WAITING;
        Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "start"));

        //when
        when(userRepository.existsById(bookerId)).thenReturn(true);
        bookingService.getBookingsByBookerIdAndState(bookerId, state, pageable);

        //then
        verify(bookingRepository, times(1))
                .findByBookerIdAndStatus(bookerId, BookingStatus.WAITING, pageable);
    }

    @Test
    public void getBookingsByBookerIdAndState_whenStateRejected_thenInvokeBookingRepository() {
        //given
        fillBookingFinderMap();
        Long bookerId = 1L;
        State state = State.REJECTED;
        Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "start"));

        //when
        when(userRepository.existsById(bookerId)).thenReturn(true);
        bookingService.getBookingsByBookerIdAndState(bookerId, state, pageable);

        //then
        verify(bookingRepository, times(1))
                .findByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, pageable);
    }

    @Test
    public void getBookingsByBookerIdAndState_whenStateApproved_thenInvokeBookingRepository() {
        //given
        fillBookingFinderMap();
        Long bookerId = 1L;
        State state = State.APPROVED;
        Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "start"));

        //when
        when(userRepository.existsById(bookerId)).thenReturn(true);
        bookingService.getBookingsByBookerIdAndState(bookerId, state, pageable);

        //then
        verify(bookingRepository, times(1))
                .findByBookerIdAndStatus(bookerId, BookingStatus.APPROVED, pageable);
    }

    @Test
    public void getBookingsByBookerIdAndState_whenStateAll_thenInvokeBookingRepository() {
        //given
        fillBookingFinderMap();
        Long bookerId = 1L;
        State state = State.ALL;
        Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "start"));

        //when
        when(userRepository.existsById(bookerId)).thenReturn(true);
        bookingService.getBookingsByBookerIdAndState(bookerId, state, pageable);

        //then
        verify(bookingRepository, times(1))
                .findByBookerId(bookerId, pageable);
    }

    @Test
    public void getBookingsByBookerIdAndState_whenStateCurren_thenInvokeBookingRepository() {
        //given
        fillBookingFinderMap();
        Long bookerId = 1L;
        State state = State.CURRENT;
        Pageable pageable = PageRequest.of(1, 1, Sort.by(Sort.Direction.DESC, "start"));

        //when
        when(userRepository.existsById(bookerId)).thenReturn(true);
        bookingService.getBookingsByBookerIdAndState(bookerId, state, pageable);

        //then
        verify(bookingRepository, times(1))
                .findCurrentBookingByBookerId(bookerId, pageable);
    }

    @Test
    public void getBookingsByBookerIdAndState_whenStatePast_thenInvokeBookingRepository() {
        //given
        fillBookingFinderMap();
        Long bookerId = 1L;
        State state = State.PAST;
        Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "start"));

        //when
        when(userRepository.existsById(bookerId)).thenReturn(true);
        bookingService.getBookingsByBookerIdAndState(bookerId, state, pageable);

        //then
        verify(bookingRepository, times(1))
                .findByBookerIdAndEndIsBefore(eq(bookerId), any(), eq(pageable));
    }

    @Test
    public void getBookingsByBookerIdAndState_whenStateFuture_thenInvokeBookingRepository() {
        //given
        fillBookingFinderMap();
        Long bookerId = 1L;
        State state = State.FUTURE;
        Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "start"));

        //when
        when(userRepository.existsById(bookerId)).thenReturn(true);
        bookingService.getBookingsByBookerIdAndState(bookerId, state, pageable);

        //then
        verify(bookingRepository, times(1))
                .findByBookerIdAndStartIsAfter(eq(bookerId), any(), eq(pageable));
    }

    @Test
    public void getBookingsByOwnerIdAndState_whenBookerIsNotExists_thenThrowException() {
        //given
        Long ownerId = 1L;
        State state = State.WAITING;
        Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "start"));

        //when
        when(userRepository.existsById(ownerId)).thenReturn(false);

        Exception exception = assertThrows(NotFountException.class,
                () -> bookingService.getBookingsByOwnerIdAndState(ownerId, state, pageable));

        //then
        assertEquals("User with id = " + ownerId + " not found.", exception.getMessage());
    }

    @Test
    public void getBookingsByOwnerIdAndState_whenStateWaiting_thenInvokeBookingRepository() {
        //given
        fillBookingOwnerMap();
        Long ownerId = 1L;
        State state = State.WAITING;
        Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "start"));

        //when
        when(userRepository.existsById(ownerId)).thenReturn(true);
        bookingService.getBookingsByOwnerIdAndState(ownerId, state, pageable);

        //then
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING, pageable);
    }

    @Test
    public void getBookingsByOwnerIdAndState_whenStateRejected_thenInvokeBookingRepository() {
        //given
        fillBookingOwnerMap();
        Long ownerId = 1L;
        State state = State.REJECTED;
        Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "start"));

        //when
        when(userRepository.existsById(ownerId)).thenReturn(true);
        bookingService.getBookingsByOwnerIdAndState(ownerId, state, pageable);

        //then
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, pageable);
    }

    @Test
    public void getBookingsByOwnerIdAndState_whenStateApproved_thenInvokeBookingRepository() {
        //given
        fillBookingOwnerMap();
        Long ownerId = 1L;
        State state = State.APPROVED;
        Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "start"));

        //when
        when(userRepository.existsById(ownerId)).thenReturn(true);
        bookingService.getBookingsByOwnerIdAndState(ownerId, state, pageable);

        //then
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStatus(ownerId, BookingStatus.APPROVED, pageable);
    }

    @Test
    public void getBookingsByOwnerIdAndState_whenStateAll_thenInvokeBookingRepository() {
        //given
        fillBookingOwnerMap();
        Long ownerId = 1L;
        State state = State.ALL;
        Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "start"));

        //when
        when(userRepository.existsById(ownerId)).thenReturn(true);
        bookingService.getBookingsByOwnerIdAndState(ownerId, state, pageable);

        //then
        verify(bookingRepository, times(1))
                .findByItemOwnerId(ownerId, pageable);
    }

    @Test
    public void getBookingsByOwnerIdAndState_whenStateCurren_thenInvokeBookingRepository() {
        //given
        fillBookingOwnerMap();
        Long ownerId = 1L;
        State state = State.CURRENT;
        Pageable pageable = PageRequest.of(1, 1, Sort.by(Sort.Direction.DESC, "start"));

        //when
        when(userRepository.existsById(ownerId)).thenReturn(true);
        bookingService.getBookingsByOwnerIdAndState(ownerId, state, pageable);

        //then
        verify(bookingRepository, times(1))
                .findCurrentBookingByItemOwnerId(ownerId, pageable);
    }

    @Test
    public void getBookingsByOwnerIdAndState_whenStatePast_thenInvokeBookingRepository() {
        //given
        fillBookingOwnerMap();
        Long ownerId = 1L;
        State state = State.PAST;
        Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "start"));

        //when
        when(userRepository.existsById(ownerId)).thenReturn(true);
        bookingService.getBookingsByOwnerIdAndState(ownerId, state, pageable);

        //then
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndEndIsBefore(eq(ownerId), any(), eq(pageable));
    }

    @Test
    public void getBookingsByOwnerIdAndState_whenStateFuture_thenInvokeBookingRepository() {
        //given
        fillBookingOwnerMap();
        Long ownerId = 1L;
        State state = State.FUTURE;
        Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "start"));

        //when
        when(userRepository.existsById(ownerId)).thenReturn(true);
        bookingService.getBookingsByOwnerIdAndState(ownerId, state, pageable);

        //then
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStartIsAfter(eq(ownerId), any(), eq(pageable));
    }

    @Test
    void changeStatus_whenUserIsNotExists_thenThrowException() {
        //given
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = false;

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFountException.class,
                () -> bookingService.changeStatus(userId, bookingId, approved));

        //then
        assertEquals("User with id = " + userId + " not found.", exception.getMessage());
    }

    @Test
    void changeStatus_whenBookingIsNotExists_thenThrowException() {
        //given
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = false;

        User user = User.builder()
                .id(1L)
                .name("New user")
                .email("newUser@email.ru")
                .build();

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFountException.class,
                () -> bookingService.changeStatus(userId, bookingId, approved));

        //then
        assertEquals("Booking with id = " + bookingId + " not found.", exception.getMessage());
    }

    @Test
    public void changeStatus_whenBookingStatusApproved_thenThrowException() {
        //given
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = false;

        User user = User.builder()
                .id(1L)
                .name("New user")
                .email("newUser@email.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .description("new item")
                .ownerId(2L)
                .available(true)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build();

        //given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Exception exception = assertThrows(BadRequestException.class,
                () -> bookingService.changeStatus(userId, bookingId, approved));

        //then
        assertEquals("Booking status can only be changed from waiting status.", exception.getMessage());
    }

    @Test
    public void changeStatus_whenBookingStatusRejected_thenThrowException() {
        //given
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = false;

        User user = User.builder()
                .id(1L)
                .name("New user")
                .email("newUser@email.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .description("new item")
                .ownerId(2L)
                .available(true)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.REJECTED)
                .build();

        //given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Exception exception = assertThrows(BadRequestException.class,
                () -> bookingService.changeStatus(userId, bookingId, approved));

        //then
        assertEquals("Booking status can only be changed from waiting status.", exception.getMessage());
    }

    @Test
    public void changeStatus_whenUserIsNotItemOwner_thenThrowException() {
        //given
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = false;

        User user = User.builder()
                .id(1L)
                .name("New user")
                .email("newUser@email.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .description("new item")
                .ownerId(2L)
                .available(true)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        //given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Exception exception = assertThrows(NotFountException.class,
                () -> bookingService.changeStatus(userId, bookingId, approved));

        //then
        assertEquals("Booking status can only be changed by item's owner.", exception.getMessage());
    }

    @Test
    public void changeStatus_whenApprovedFalse_thenSetStatusToRejected() {
        //given
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = false;

        User user = User.builder()
                .id(1L)
                .name("New user")
                .email("newUser@email.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .description("new item")
                .ownerId(1L)
                .available(true)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        //given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        bookingService.changeStatus(userId, bookingId, approved);

        //then
        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking actualBooking = bookingArgumentCaptor.getValue();
        assertEquals(BookingStatus.REJECTED, actualBooking.getStatus());
    }

    @Test
    public void changeStatus_whenApprovedTrue_thenSetStatusToApproved() {
        //given
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;

        User user = User.builder()
                .id(1L)
                .name("New user")
                .email("newUser@email.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .description("new item")
                .ownerId(1L)
                .available(true)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        //given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        bookingService.changeStatus(userId, bookingId, approved);

        //then
        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking actualBooking = bookingArgumentCaptor.getValue();
        assertEquals(BookingStatus.APPROVED, actualBooking.getStatus());
    }

    @Test
    public void changeStatus_whenRequestIsValid_thenUpdateStatusAndReturnBookingOutputDto() {
        //given
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;

        User user = User.builder()
                .id(1L)
                .name("New user")
                .email("newUser@email.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .description("new item")
                .ownerId(1L)
                .available(true)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        BookingOutputDto expectedBookingOutputDto = new BookingOutputDto(
                booking.getId(),
                ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStart(),
                booking.getEnd(),
                BookingStatus.APPROVED
        );

        //given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        BookingOutputDto actualBookingOutputDto = bookingService.changeStatus(userId, bookingId, approved);

        //then
        assertEquals(expectedBookingOutputDto, actualBookingOutputDto);
    }
}