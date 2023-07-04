package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime date, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime date, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start <= current_timestamp " +
            "AND b.end >= current_timestamp ")
    List<Booking> findCurrentBookingByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime date, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime date, Sort sort);

    List<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.ownerId = :ownerId " +
            "AND b.start <= current_timestamp " +
            "AND b.end >= current_timestamp ")
    List<Booking> findCurrentBookingByOwnerId(Long ownerId, Sort sort);

    List<Booking> findByItemOwnerId(Long ownerId, Sort startDate);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);
}
