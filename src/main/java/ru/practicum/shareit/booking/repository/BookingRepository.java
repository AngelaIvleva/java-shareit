package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker(User user, Sort sort);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start <= current_timestamp " +
            "and b.end >= current_timestamp " +
            "order by b.end desc")
    List<Booking> findByBookerIdCurrent(Long userId);


    List<Booking> findByBookerAndStatus(User user, Status status, Sort sort);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start <= current_timestamp " +
            "and b.end <= current_timestamp " +
            "order by b.end desc")
    List<Booking> findByBookerIdPast(Long userId);

    List<Booking> findByBookerAndStartIsAfter(User user, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwner(User user, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.start <= current_timestamp " +
            "and b.end >= current_timestamp " +
            "order by b.end desc")
    List<Booking> findByItemOwnerIdCurrent(Long userId);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.start <= current_timestamp " +
            "and b.end <= current_timestamp " +
            "order by b.end desc")
    List<Booking> findByItemOwnerIdPast(Long userId);

    List<Booking> findByItemOwnerAndStartAfter(User user, LocalDateTime start, Sort sort);

    List<Booking> findByItemOwnerAndStatusEquals(User user, Status status, Sort sort);

    Optional<Booking> findFirstByItemIdAndEndIsBeforeAndStatusIs(
            Long itemId, LocalDateTime end, Status status, Sort sort);

    Optional<Booking> findFirstByItemIdAndEndIsAfterAndStatusIs(
            Long itemId, LocalDateTime end, Status status, Sort sort);

    Optional<Booking> findFirstByItemAndBookerAndEndIsBeforeOrderByEnd(Item item, User user, LocalDateTime now);
}
