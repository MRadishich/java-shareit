package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime date, Pageable pageable);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start <= current_timestamp " +
            "AND b.end >= current_timestamp ")
    List<Booking> findCurrentBookingByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime date, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.ownerId = :ownerId " +
            "AND b.start <= current_timestamp " +
            "AND b.end >= current_timestamp ")
    List<Booking> findCurrentBookingByItemOwnerId(Long ownerId, Pageable pageable);

    List<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

    List<Booking> findByItemOwnerId(Long ownerId, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);
}
