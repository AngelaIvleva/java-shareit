package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.Status;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    BookingRepository bookingRepository;

    User user1 = User.builder()
            .email("email@ex.com")
            .name("Harry Potter")
            .build();

    User user2 = User.builder()
            .email("email@exx.com")
            .name("Hermione Granger")
            .build();

    Item item1 = Item.builder()
            .name("Wand")
            .description("A wand is the object through which a witch or wizard channels his or her magic")
            .available(Boolean.TRUE)
            .owner(user1)
            .build();

    Item item2 = Item.builder()
            .name("item2")
            .description("description2")
            .available(Boolean.TRUE)
            .owner(user2)
            .build();

    Booking booking1 = Booking.builder()
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(1))
            .item(item1)
            .booker(user2)
            .status(Status.WAITING)
            .build();

    Booking booking2 = Booking.builder()
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(2))
            .item(item2)
            .booker(user1)
            .status(Status.WAITING)
            .build();

    @Test
    void shouldFindByBooker() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);

        List<Booking> bookingList = bookingRepository.findByBooker(user1, Pageable.ofSize(1));
        assertEquals(1, bookingList.size());
        assertEquals(booking2.getId(), bookingList.get(0).getId());
    }

    @Test
    void shouldFindByBookerIdCurrent() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);

        List<Booking> bookingList = bookingRepository
                .findByBookerIdCurrent(user1.getId(), Pageable.ofSize(1));
        assertEquals(1, bookingList.size());
        assertEquals(booking2.getId(), bookingList.get(0).getId());
    }

    @Test
    void shouldFindByBookerAndStatus() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);

        List<Booking> bookingList = bookingRepository
                .findByBookerAndStatus(user1, Status.WAITING, Pageable.ofSize(1));
        assertEquals(1, bookingList.size());
        assertEquals(booking2.getId(), bookingList.get(0).getId());
    }

    @Test
    void shouldFindByBookerIdPast() {
        booking2.setStart(LocalDateTime.now().minusDays(1));
        booking2.setEnd(LocalDateTime.now().minusHours(1));
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);

        List<Booking> bookingList = bookingRepository
                .findByBookerIdPast(user1.getId(), Pageable.ofSize(1));
        assertEquals(1, bookingList.size());
        assertEquals(booking2.getId(), bookingList.get(0).getId());
    }

    @Test
    void shouldFindByBookerAndStartIsAfter() {
        booking2.setStart(LocalDateTime.now().plusHours(1));
        booking2.setEnd(LocalDateTime.now().plusHours(5));
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);

        List<Booking> bookingList = bookingRepository
                .findByBookerAndStartIsAfter(user1, LocalDateTime.now(), Pageable.ofSize(1));
        assertEquals(1, bookingList.size());
        assertEquals(booking2.getId(), bookingList.get(0).getId());
    }

    @Test
    void shouldFindByItemOwner() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);

        List<Booking> bookingList = bookingRepository
                .findByItemOwner(user1, Pageable.ofSize(1));
        assertEquals(1, bookingList.size());
        assertEquals(booking1.getId(), bookingList.get(0).getId());
    }

    @Test
    void shouldFindByItemOwnerIdCurrent() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);

        List<Booking> bookingList = bookingRepository
                .findByItemOwnerIdCurrent(user1.getId(), Pageable.ofSize(1));
        assertEquals(1, bookingList.size());
        assertEquals(booking1.getId(), bookingList.get(0).getId());
    }

    @Test
    void shouldFindByItemOwnerIdPast() {
        booking1.setStart(LocalDateTime.now().minusDays(1));
        booking1.setEnd(LocalDateTime.now().minusHours(5));
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);

        List<Booking> bookingList = bookingRepository
                .findByItemOwnerIdPast(user1.getId(), Pageable.ofSize(1));
        assertEquals(1, bookingList.size());
        assertEquals(booking1.getId(), bookingList.get(0).getId());
    }

    @Test
    void shouldFindByItemOwnerAndStartAfter() {
        booking1.setStart(LocalDateTime.now().plusHours(1));
        booking1.setEnd(LocalDateTime.now().plusHours(5));
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);

        List<Booking> bookingList = bookingRepository
                .findByItemOwnerAndStartAfter(user1, LocalDateTime.now(), Pageable.ofSize(1));
        assertEquals(1, bookingList.size());
        assertEquals(booking1.getId(), bookingList.get(0).getId());
    }

    @Test
    void shouldFindByItemOwnerAndStatusEquals() {
        booking1.setStatus(Status.APPROVED);
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);

        List<Booking> bookingList = bookingRepository
                .findByItemOwnerAndStatusEquals(user1, Status.APPROVED, Pageable.ofSize(1));
        assertEquals(1, bookingList.size());
        assertEquals(booking1.getId(), bookingList.get(0).getId());
    }

    @Test
    void shouldFindFirstByItemIdAndEndIsBeforeAndStatusIs() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(booking1);

        Optional<Booking> bookings = bookingRepository
                .findFirstByItemIdAndEndIsBeforeAndStatusIs(
                        item1.getId(), LocalDateTime.now().plusDays(1), Status.WAITING, Sort.by(Sort.Direction.DESC, "end"));
        assertTrue(bookings.isPresent());
        assertEquals(booking1.getId(), bookings.get().getId());
    }

    @Test
    void shouldFindFirstByItemIdAndEndIsAfterAndStatusIs() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(booking1);

        Optional<Booking> bookings = bookingRepository
                .findFirstByItemIdAndEndIsAfterAndStatusIs(
                        item1.getId(), LocalDateTime.now(), Status.WAITING, Sort.by(Sort.Direction.DESC, "end"));
        assertTrue(bookings.isPresent());
        assertEquals(booking1.getId(), bookings.get().getId());
    }

    @Test
    void shouldFindFirstByItemAndBookerAndEndIsBeforeOrderByEnd() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(booking1);

        Optional<Booking> bookings = bookingRepository
                .findFirstByItemAndBookerAndEndIsBeforeOrderByEnd(
                        item1, user2, LocalDateTime.now().plusDays(1));
        assertTrue(bookings.isPresent());
        assertEquals(booking1.getId(), bookings.get().getId());
    }
}