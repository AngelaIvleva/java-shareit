package ru.practicum.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.Status;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker(User user, Pageable p);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start <= current_timestamp " +
            "and b.end >= current_timestamp " +
            "order by b.end desc")
    List<Booking> findByBookerIdCurrent(Long userId, Pageable p);


    List<Booking> findByBookerAndStatus(User user, Status status, Pageable p);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start <= current_timestamp " +
            "and b.end <= current_timestamp " +
            "order by b.end desc")
    List<Booking> findByBookerIdPast(Long userId, Pageable p);

    List<Booking> findByBookerAndStartIsAfter(User user, LocalDateTime now, Pageable p);

    List<Booking> findByItemOwner(User user, Pageable p);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.start <= current_timestamp " +
            "and b.end >= current_timestamp " +
            "order by b.end desc")
    List<Booking> findByItemOwnerIdCurrent(Long userId, Pageable p);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.start <= current_timestamp " +
            "and b.end <= current_timestamp " +
            "order by b.end desc")
    List<Booking> findByItemOwnerIdPast(Long userId, Pageable p);

    List<Booking> findByItemOwnerAndStartAfter(User user, LocalDateTime start, Pageable p);

    List<Booking> findByItemOwnerAndStatusEquals(User user, Status status, Pageable p);

    Optional<Booking> findFirstByItemIdAndEndIsBeforeAndStatusIs(
            Long itemId, LocalDateTime end, Status status, Sort sort);

    Optional<Booking> findFirstByItemIdAndEndIsAfterAndStatusIs(
            Long itemId, LocalDateTime end, Status status, Sort sort);

    Optional<Booking> findFirstByItemAndBookerAndEndIsBeforeOrderByEnd(Item item, User user, LocalDateTime now);
}
