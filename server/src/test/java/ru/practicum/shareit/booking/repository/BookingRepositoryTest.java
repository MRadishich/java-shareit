package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByBookerIdAndEndIsBefore_whenInvoked_thenReturnOnlyPastBookingByBookerId() {
        //given
        LocalDateTime currentDateTime = LocalDateTime.now();

        User owner1 = createUser(1L);
        User owner2 = createUser(2L);
        User booker1 = createUser(3L);
        User booker2 = createUser(4L);

        Item item1 = createItem(1L, 1L);
        Item item2 = createItem(2L, 2L);
        Booking pastBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(10), currentDateTime.minusDays(10));
        Booking pastBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(10), currentDateTime.minusDays(9));
        Booking currentBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking currentBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking futureBooking1 = createBooking(booker1, item1, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Booking futureBooking2 = createBooking(booker2, item2, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "start"));

        entityManager.persist(owner1);
        entityManager.persist(owner2);
        entityManager.persist(booker1);
        entityManager.persist(booker2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(pastBooking1);
        entityManager.persist(pastBooking2);
        entityManager.persist(currentBooking1);
        entityManager.persist(currentBooking2);
        entityManager.persist(futureBooking1);
        entityManager.persist(futureBooking2);

        //when
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndIsBefore(booker1.getId(), currentDateTime, pageable);

        //then
        assertEquals(1, bookings.size());
        assertTrue(bookings.contains(pastBooking1));
    }

    @Test
    void findByBookerIdAndStartIsAfter_whenInvoked_thenReturnOnlyFutureBookingByBookerId() {
        //given
        LocalDateTime currentDateTime = LocalDateTime.now();

        User owner1 = createUser(1L);
        User owner2 = createUser(2L);
        User booker1 = createUser(3L);
        User booker2 = createUser(4L);

        Item item1 = createItem(1L, 1L);
        Item item2 = createItem(2L, 2L);
        Booking pastBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(10), currentDateTime.minusDays(10));
        Booking pastBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(10), currentDateTime.minusDays(9));
        Booking currentBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking currentBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking futureBooking1 = createBooking(booker1, item1, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Booking futureBooking2 = createBooking(booker2, item2, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "start"));

        entityManager.persist(owner1);
        entityManager.persist(owner2);
        entityManager.persist(booker1);
        entityManager.persist(booker2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(pastBooking1);
        entityManager.persist(pastBooking2);
        entityManager.persist(currentBooking1);
        entityManager.persist(currentBooking2);
        entityManager.persist(futureBooking1);
        entityManager.persist(futureBooking2);

        //when
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartIsAfter(booker1.getId(), currentDateTime, pageable);

        //then
        assertEquals(1, bookings.size());
        assertTrue(bookings.contains(futureBooking1));
    }

    @Test
    void findCurrentBookingByBookerId_whenInvoked_thenReturnOnlyCurrentBookingByBookerId() {
        //given
        LocalDateTime currentDateTime = LocalDateTime.now();

        User owner1 = createUser(1L);
        User owner2 = createUser(2L);
        User booker1 = createUser(3L);
        User booker2 = createUser(4L);

        Item item1 = createItem(1L, 1L);
        Item item2 = createItem(2L, 2L);
        Booking pastBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(10), currentDateTime.minusDays(10));
        Booking pastBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(10), currentDateTime.minusDays(9));
        Booking currentBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking currentBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking futureBooking1 = createBooking(booker1, item1, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Booking futureBooking2 = createBooking(booker2, item2, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "start"));

        entityManager.persist(owner1);
        entityManager.persist(owner2);
        entityManager.persist(booker1);
        entityManager.persist(booker2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(pastBooking1);
        entityManager.persist(pastBooking2);
        entityManager.persist(currentBooking1);
        entityManager.persist(currentBooking2);
        entityManager.persist(futureBooking1);
        entityManager.persist(futureBooking2);

        //when
        List<Booking> bookings = bookingRepository.findCurrentBookingByBookerId(booker1.getId(), pageable);

        //then
        assertEquals(1, bookings.size());
        assertTrue(bookings.contains(currentBooking1));
    }

    @Test
    void findByBookerId_whenInvoked_thenReturnAllBookingsByBookerId() {
        //given
        LocalDateTime currentDateTime = LocalDateTime.now();

        User owner1 = createUser(1L);
        User owner2 = createUser(2L);
        User booker1 = createUser(3L);
        User booker2 = createUser(4L);

        Item item1 = createItem(1L, 1L);
        Item item2 = createItem(2L, 2L);
        Booking pastBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(10), currentDateTime.minusDays(10));
        Booking pastBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(10), currentDateTime.minusDays(9));
        Booking currentBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking currentBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking futureBooking1 = createBooking(booker1, item1, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Booking futureBooking2 = createBooking(booker2, item2, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "start"));

        entityManager.persist(owner1);
        entityManager.persist(owner2);
        entityManager.persist(booker1);
        entityManager.persist(booker2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(pastBooking1);
        entityManager.persist(pastBooking2);
        entityManager.persist(currentBooking1);
        entityManager.persist(currentBooking2);
        entityManager.persist(futureBooking1);
        entityManager.persist(futureBooking2);

        //when
        List<Booking> bookings = bookingRepository.findByBookerId(booker1.getId(), pageable);

        //then
        assertEquals(3, bookings.size());
        assertTrue(bookings.contains(pastBooking1));
        assertTrue(bookings.contains(currentBooking1));
        assertTrue(bookings.contains(futureBooking1));
    }

    @Test
    void findByBookerIdAndStatus_whenInvokedWithStatusApproved_thenReturnOnlyApprovedBookingsByBookerId() {
        //given
        LocalDateTime currentDateTime = LocalDateTime.now();

        User owner1 = createUser(1L);
        User owner2 = createUser(2L);
        User booker1 = createUser(3L);
        User booker2 = createUser(4L);

        Item item1 = createItem(1L, 1L);
        Item item2 = createItem(2L, 2L);
        Booking pastBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(10), currentDateTime.minusDays(10));
        Booking pastBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(10), currentDateTime.minusDays(9));
        Booking currentBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking currentBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking futureBooking1 = createBooking(booker1, item1, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Booking futureBooking2 = createBooking(booker2, item2, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "start"));

        futureBooking1.setStatus(BookingStatus.WAITING);

        entityManager.persist(owner1);
        entityManager.persist(owner2);
        entityManager.persist(booker1);
        entityManager.persist(booker2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(pastBooking1);
        entityManager.persist(pastBooking2);
        entityManager.persist(currentBooking1);
        entityManager.persist(currentBooking2);
        entityManager.persist(futureBooking1);
        entityManager.persist(futureBooking2);

        //when
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatus(booker1.getId(), BookingStatus.APPROVED, pageable);

        //then
        assertEquals(2, bookings.size());
        assertTrue(bookings.contains(pastBooking1));
        assertTrue(bookings.contains(currentBooking1));
    }

    @Test
    void findByBookerIdAndStatus_whenInvokedWithStatusWaiting_thenReturnOnlyWaitingBookingsByBookerId() {
        //given
        LocalDateTime currentDateTime = LocalDateTime.now();

        User owner1 = createUser(1L);
        User owner2 = createUser(2L);
        User booker1 = createUser(3L);
        User booker2 = createUser(4L);

        Item item1 = createItem(1L, 1L);
        Item item2 = createItem(2L, 2L);
        Booking pastBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(10), currentDateTime.minusDays(10));
        Booking pastBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(10), currentDateTime.minusDays(9));
        Booking currentBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking currentBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking futureBooking1 = createBooking(booker1, item1, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Booking futureBooking2 = createBooking(booker2, item2, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "start"));

        futureBooking1.setStatus(BookingStatus.WAITING);

        entityManager.persist(owner1);
        entityManager.persist(owner2);
        entityManager.persist(booker1);
        entityManager.persist(booker2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(pastBooking1);
        entityManager.persist(pastBooking2);
        entityManager.persist(currentBooking1);
        entityManager.persist(currentBooking2);
        entityManager.persist(futureBooking1);
        entityManager.persist(futureBooking2);

        //when
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatus(booker1.getId(), BookingStatus.WAITING, pageable);

        //then
        assertEquals(1, bookings.size());
        assertTrue(bookings.contains(futureBooking1));
    }

    @Test
    void findByBookerIdAndStatus_whenInvokedWithStatusRejected_thenReturnOnlyRejectedBookingsByBookerId() {
        //given
        LocalDateTime currentDateTime = LocalDateTime.now();

        User owner1 = createUser(1L);
        User owner2 = createUser(2L);
        User booker1 = createUser(3L);
        User booker2 = createUser(4L);

        Item item1 = createItem(1L, 1L);
        Item item2 = createItem(2L, 2L);
        Booking pastBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(10), currentDateTime.minusDays(10));
        Booking pastBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(10), currentDateTime.minusDays(9));
        Booking currentBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking currentBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking futureBooking1 = createBooking(booker1, item1, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Booking futureBooking2 = createBooking(booker2, item2, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "start"));

        futureBooking1.setStatus(BookingStatus.REJECTED);

        entityManager.persist(owner1);
        entityManager.persist(owner2);
        entityManager.persist(booker1);
        entityManager.persist(booker2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(pastBooking1);
        entityManager.persist(pastBooking2);
        entityManager.persist(currentBooking1);
        entityManager.persist(currentBooking2);
        entityManager.persist(futureBooking1);
        entityManager.persist(futureBooking2);

        //when
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatus(booker1.getId(), BookingStatus.REJECTED, pageable);

        //then
        assertEquals(1, bookings.size());
        assertTrue(bookings.contains(futureBooking1));
    }

    @Test
    void findByItemOwnerIdAndEndIsBefore_whenInvoked_thenReturnOnlyPastBookingsByItemOwnerId() {
        //given
        LocalDateTime currentDateTime = LocalDateTime.now();

        User owner1 = createUser(1L);
        User owner2 = createUser(2L);
        User booker1 = createUser(3L);
        User booker2 = createUser(4L);

        Item item1 = createItem(1L, 1L);
        Item item2 = createItem(2L, 2L);
        Booking pastBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(10), currentDateTime.minusDays(10));
        Booking pastBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(10), currentDateTime.minusDays(9));
        Booking currentBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking currentBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking futureBooking1 = createBooking(booker1, item1, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Booking futureBooking2 = createBooking(booker2, item2, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "start"));

        entityManager.persist(owner1);
        entityManager.persist(owner2);
        entityManager.persist(booker1);
        entityManager.persist(booker2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(pastBooking1);
        entityManager.persist(pastBooking2);
        entityManager.persist(currentBooking1);
        entityManager.persist(currentBooking2);
        entityManager.persist(futureBooking1);
        entityManager.persist(futureBooking2);

        //when
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndEndIsBefore(item1.getOwnerId(), currentDateTime, pageable);

        //then
        assertEquals(1, bookings.size());
        assertTrue(bookings.contains(pastBooking1));
    }

    @Test
    void findByItemOwnerIdAndStartIsAfter_whenInvoked_thenReturnOnlyFutureBookingsByItemOwnerId() {
        //given
        LocalDateTime currentDateTime = LocalDateTime.now();

        User owner1 = createUser(1L);
        User owner2 = createUser(2L);
        User booker1 = createUser(3L);
        User booker2 = createUser(4L);

        Item item1 = createItem(1L, 1L);
        Item item2 = createItem(2L, 2L);
        Booking pastBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(10), currentDateTime.minusDays(10));
        Booking pastBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(10), currentDateTime.minusDays(9));
        Booking currentBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking currentBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking futureBooking1 = createBooking(booker1, item1, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Booking futureBooking2 = createBooking(booker2, item2, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "start"));

        entityManager.persist(owner1);
        entityManager.persist(owner2);
        entityManager.persist(booker1);
        entityManager.persist(booker2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(pastBooking1);
        entityManager.persist(pastBooking2);
        entityManager.persist(currentBooking1);
        entityManager.persist(currentBooking2);
        entityManager.persist(futureBooking1);
        entityManager.persist(futureBooking2);

        //when
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStartIsAfter(item1.getOwnerId(), currentDateTime, pageable);

        //then
        assertEquals(1, bookings.size());
        assertTrue(bookings.contains(futureBooking1));
    }

    @Test
    void findCurrentBookingByOwnerId_whenInvoked_whenReturnOnlyCurrentBookingsByItemOwnerId() {
        //given
        LocalDateTime currentDateTime = LocalDateTime.now();

        User owner1 = createUser(1L);
        User owner2 = createUser(2L);
        User booker1 = createUser(3L);
        User booker2 = createUser(4L);

        Item item1 = createItem(1L, 1L);
        Item item2 = createItem(2L, 2L);
        Booking pastBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(10), currentDateTime.minusDays(10));
        Booking pastBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(10), currentDateTime.minusDays(9));
        Booking currentBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking currentBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking futureBooking1 = createBooking(booker1, item1, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Booking futureBooking2 = createBooking(booker2, item2, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "start"));

        entityManager.persist(owner1);
        entityManager.persist(owner2);
        entityManager.persist(booker1);
        entityManager.persist(booker2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(pastBooking1);
        entityManager.persist(pastBooking2);
        entityManager.persist(currentBooking1);
        entityManager.persist(currentBooking2);
        entityManager.persist(futureBooking1);
        entityManager.persist(futureBooking2);

        //when
        List<Booking> bookings = bookingRepository.findCurrentBookingByItemOwnerId(item1.getOwnerId(), pageable);

        //then
        assertEquals(1, bookings.size());
        assertTrue(bookings.contains(currentBooking1));
    }

    @Test
    void findByBookerIdAndItemId_whenInvoked_thenReturnBookingsWithOnlyGivenBookerIdAndItemId() {
        //given
        LocalDateTime currentDateTime = LocalDateTime.now();

        User owner1 = createUser(1L);
        User owner2 = createUser(2L);
        User booker1 = createUser(3L);
        User booker2 = createUser(4L);

        Item item1 = createItem(1L, 1L);
        Item item2 = createItem(2L, 2L);
        Booking pastBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(10), currentDateTime.minusDays(10));
        Booking pastBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(10), currentDateTime.minusDays(9));
        Booking currentBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking currentBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking futureBooking1 = createBooking(booker1, item1, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Booking futureBooking2 = createBooking(booker2, item2, currentDateTime.plusDays(3), currentDateTime.plusDays(5));

        entityManager.persist(owner1);
        entityManager.persist(owner2);
        entityManager.persist(booker1);
        entityManager.persist(booker2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(pastBooking1);
        entityManager.persist(pastBooking2);
        entityManager.persist(currentBooking1);
        entityManager.persist(currentBooking2);
        entityManager.persist(futureBooking1);
        entityManager.persist(futureBooking2);

        //when
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemId(booker1.getId(), item1.getId());

        //then
        assertEquals(3, bookings.size());
        assertTrue(bookings.contains(pastBooking1));
        assertTrue(bookings.contains(currentBooking1));
        assertTrue(bookings.contains(futureBooking1));
    }

    @Test
    void findByItemOwnerId_whenInvoker_thenReturnAllBookingsWithOnlyGivenItemOwnerId() {
        //given
        LocalDateTime currentDateTime = LocalDateTime.now();

        User owner1 = createUser(1L);
        User owner2 = createUser(2L);
        User booker1 = createUser(3L);
        User booker2 = createUser(4L);

        Item item1 = createItem(1L, 1L);
        Item item2 = createItem(2L, 2L);
        Booking pastBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(10), currentDateTime.minusDays(10));
        Booking pastBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(10), currentDateTime.minusDays(9));
        Booking currentBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking currentBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking futureBooking1 = createBooking(booker1, item1, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Booking futureBooking2 = createBooking(booker2, item2, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "start"));

        entityManager.persist(owner1);
        entityManager.persist(owner2);
        entityManager.persist(booker1);
        entityManager.persist(booker2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(pastBooking1);
        entityManager.persist(pastBooking2);
        entityManager.persist(currentBooking1);
        entityManager.persist(currentBooking2);
        entityManager.persist(futureBooking1);
        entityManager.persist(futureBooking2);

        //when
        List<Booking> bookings = bookingRepository.findByItemOwnerId(item1.getOwnerId(), pageable);

        //then
        assertEquals(3, bookings.size());
        assertTrue(bookings.contains(pastBooking1));
        assertTrue(bookings.contains(currentBooking1));
        assertTrue(bookings.contains(futureBooking1));
    }

    @Test
    void findByItemOwnerIdAndStatus_whenInvokedWithStatusApproved_thenReturnOnlyApprovedBookingsWithGivenItemOwnerId() {
        //given
        LocalDateTime currentDateTime = LocalDateTime.now();

        User owner1 = createUser(1L);
        User owner2 = createUser(2L);
        User booker1 = createUser(3L);
        User booker2 = createUser(4L);

        Item item1 = createItem(1L, 1L);
        Item item2 = createItem(2L, 2L);
        Booking pastBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(10), currentDateTime.minusDays(10));
        Booking pastBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(10), currentDateTime.minusDays(9));
        Booking currentBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking currentBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking futureBooking1 = createBooking(booker1, item1, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Booking futureBooking2 = createBooking(booker2, item2, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "start"));

        futureBooking1.setStatus(BookingStatus.WAITING);

        entityManager.persist(owner1);
        entityManager.persist(owner2);
        entityManager.persist(booker1);
        entityManager.persist(booker2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(pastBooking1);
        entityManager.persist(pastBooking2);
        entityManager.persist(currentBooking1);
        entityManager.persist(currentBooking2);
        entityManager.persist(futureBooking1);
        entityManager.persist(futureBooking2);

        //when
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStatus(item1.getOwnerId(), BookingStatus.APPROVED, pageable);

        //then
        assertEquals(2, bookings.size());
        assertTrue(bookings.contains(pastBooking1));
        assertTrue(bookings.contains(currentBooking1));
    }

    @Test
    void findByItemOwnerIdAndStatus_whenInvokedWithStatusWaiting_thenReturnOnlyWaitingBookingsWithGivenItemOwnerId() {
        //given
        LocalDateTime currentDateTime = LocalDateTime.now();

        User owner1 = createUser(1L);
        User owner2 = createUser(2L);
        User booker1 = createUser(3L);
        User booker2 = createUser(4L);

        Item item1 = createItem(1L, 1L);
        Item item2 = createItem(2L, 2L);
        Booking pastBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(10), currentDateTime.minusDays(10));
        Booking pastBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(10), currentDateTime.minusDays(9));
        Booking currentBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking currentBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking futureBooking1 = createBooking(booker1, item1, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Booking futureBooking2 = createBooking(booker2, item2, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "start"));

        futureBooking1.setStatus(BookingStatus.WAITING);

        entityManager.persist(owner1);
        entityManager.persist(owner2);
        entityManager.persist(booker1);
        entityManager.persist(booker2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(pastBooking1);
        entityManager.persist(pastBooking2);
        entityManager.persist(currentBooking1);
        entityManager.persist(currentBooking2);
        entityManager.persist(futureBooking1);
        entityManager.persist(futureBooking2);

        //when
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStatus(item1.getOwnerId(), BookingStatus.WAITING, pageable);

        //then
        assertEquals(1, bookings.size());
        assertTrue(bookings.contains(futureBooking1));
    }

    @Test
    void findByItemOwnerIdAndStatus_whenInvokedWithStatusRejected_thenReturnOnlyRejectedBookingsWithGivenItemOwnerId() {
        //given
        LocalDateTime currentDateTime = LocalDateTime.now();

        User owner1 = createUser(1L);
        User owner2 = createUser(2L);
        User booker1 = createUser(3L);
        User booker2 = createUser(4L);

        Item item1 = createItem(1L, 1L);
        Item item2 = createItem(2L, 2L);
        Booking pastBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(10), currentDateTime.minusDays(10));
        Booking pastBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(10), currentDateTime.minusDays(9));
        Booking currentBooking1 = createBooking(booker1, item1, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking currentBooking2 = createBooking(booker2, item2, currentDateTime.minusDays(1), currentDateTime.plusDays(2));
        Booking futureBooking1 = createBooking(booker1, item1, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Booking futureBooking2 = createBooking(booker2, item2, currentDateTime.plusDays(3), currentDateTime.plusDays(5));
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "start"));

        futureBooking1.setStatus(BookingStatus.REJECTED);

        entityManager.persist(owner1);
        entityManager.persist(owner2);
        entityManager.persist(booker1);
        entityManager.persist(booker2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(pastBooking1);
        entityManager.persist(pastBooking2);
        entityManager.persist(currentBooking1);
        entityManager.persist(currentBooking2);
        entityManager.persist(futureBooking1);
        entityManager.persist(futureBooking2);

        //when
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStatus(item1.getOwnerId(), BookingStatus.REJECTED, pageable);

        //then
        assertEquals(1, bookings.size());
        assertTrue(bookings.contains(futureBooking1));
    }

    private User createUser(Long userId) {
        return User.builder()
                .name("User " + userId)
                .email("User" + userId + "@email.ru")
                .build();
    }

    private Item createItem(Long itemId, Long ownerId) {
        return Item.builder()
                .name("Item " + itemId)
                .description("Item " + itemId)
                .ownerId(ownerId)
                .available(true)
                .build();
    }

    private Booking createBooking(User booker, Item item, LocalDateTime start, LocalDateTime end) {
        return Booking.builder()
                .item(item)
                .booker(booker)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .build();
    }
}