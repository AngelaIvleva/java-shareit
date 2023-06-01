package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker(User user, Sort sort);

    List<Booking> findByBookerAndStartIsBeforeAndEndIsAfter(User user, LocalDateTime start, LocalDateTime end,
                                                            Sort sort);

    List<Booking> findByBookerAndStatus(User user, Status status, Sort sort);

    List<Booking> findByBookerAndEndIsBefore(User user, LocalDateTime now, Sort sort);

    List<Booking> findByBookerAndStartIsAfter(User user, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwner(User user, Sort sort);

    List<Booking> findByItemOwnerAndStartBeforeAndEndAfter(
            User user, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerAndEndBefore(User user, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerAndStartAfter(User user, LocalDateTime start, Sort sort);

    List<Booking> findByItemOwnerAndStatusEquals(User user, Status status, Sort sort);
}
