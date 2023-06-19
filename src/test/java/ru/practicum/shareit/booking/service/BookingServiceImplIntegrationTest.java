package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext
class BookingServiceImplIntegrationTest {

    @Autowired
    BookingServiceImpl bookingService;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    User user1 = User.builder()
            .id(1L)
            .email("email@ex.com")
            .name("Harry Potter")
            .build();

    User user2 = User.builder()
            .id(2L)
            .email("email@exx.com")
            .name("Hermione Granger")
            .build();

    Item item = Item.builder()
            .id(1L)
            .name("Wand")
            .description("A wand is the object through which a witch or wizard channels his or her magic")
            .available(Boolean.TRUE)
            .owner(user1)
            .build();

    Booking booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(1))
            .item(item)
            .booker(user2)
            .status(Status.WAITING)
            .build();

    @Test
    void shouldGetAllByBooker() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item);
        bookingRepository.save(booking);

        List<BookingOutputDto> bookingDtos = bookingService.getAllByBooker(user2.getId(), State.WAITING, 0, 10);

        assertAll(() -> assertEquals(booking.getBooker(), bookingDtos.get(0).getBooker()),
                () -> assertEquals(1, bookingDtos.size()));

        bookingService.changeStatus(user1.getId(), booking.getId(), true);

        List<BookingOutputDto> bookingDto = bookingService.getAllByBooker(user2.getId(), State.CURRENT, 0, 10);
        assertAll(() -> assertEquals(1, bookingDtos.size()));
    }
}