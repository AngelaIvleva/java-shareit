package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingOutputDto;
import ru.practicum.booking.mapper.BookingMapper;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.State;
import ru.practicum.booking.model.Status;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.booking.service.BookingServiceImpl;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    BookingServiceImpl bookingService;

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
            .build();

    BookingDto bookingDto = BookingMapper.toBookingDto(booking);

    @Test
    void shouldCreateBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingOutputDto bookingDto = bookingService.addBooking(user2.getId(), BookingMapper.toBookingDto(booking));

        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        verify(bookingRepository, Mockito.times(1))
                .save(any());
    }

    @Test
    void shouldNotCreateBookingAndThrowExceptionWhenItemNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(1L, bookingDto));
    }

    @Test
    void shouldNotCreateBookingAndThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(1L, bookingDto));
    }

    @Test
    void shouldNotCreateBookingAndThrowExceptionWhenOwnerTryingToBookHisItem() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));

        assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(1L, bookingDto));
    }

    @Test
    void shouldNotCreateBookingAndThrowExceptionWhenItemNotAvailable() {
        Item itemTest = item;
        itemTest.setId(2L);
        itemTest.setAvailable(Boolean.FALSE);
        bookingDto.setItemId(2L);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));

        assertThrows(BadRequestException.class,
                () -> bookingService.addBooking(2L, bookingDto));
    }

    @Test
    void shouldNotCreateBookingAndThrowExceptionWhenEndBeforeStart() {
        Item itemTest = item;
        itemTest.setId(2L);
        bookingDto.setItemId(2L);
        bookingDto.setEnd(LocalDateTime.now().minusHours(1));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));

        assertThrows(BadRequestException.class,
                () -> bookingService.addBooking(2L, bookingDto));
    }

    @Test
    void shouldChangeStatusWhenOwnerChangeToApprove() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        booking.setStatus(Status.WAITING);
        BookingOutputDto response = bookingService.changeStatus(user1.getId(), booking.getId(), true);

        assertEquals(Status.APPROVED, response.getStatus());
        verify(bookingRepository, Mockito.times(2))
                .save(any());
    }

    @Test
    void shouldChangeStatusWhenOwnerChangeToReject() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        booking.setStatus(Status.WAITING);
        BookingOutputDto response = bookingService.changeStatus(user1.getId(), booking.getId(), false);

        assertEquals(Status.REJECTED, response.getStatus());
        verify(bookingRepository, Mockito.times(2)).save(any());
    }

    @Test
    void shouldNotChangeStatusAndThrowExceptionWhenBookingNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.changeStatus(1L, 1L, true));
    }

    @Test
    void shouldNotChangeStatusAndThrowExceptionWhenNotItemOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.changeStatus(1L, 1L, true));
    }

    @Test
    void shouldNotChangeStatusAndThrowExceptionWhenBookingAlreadyApproved() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        booking.setStatus(Status.APPROVED);

        assertThrows(BadRequestException.class,
                () -> bookingService.changeStatus(1L, 1L, true));
    }

    @Test
    void shouldGetBookingByIdWhenOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingOutputDto response = bookingService.getBookingById(user1.getId(), booking.getId());
        assertEquals(booking.getItem().getName(), response.getItem().getName());
    }

    @Test
    void shouldNotGetBookingByIdAndThrowExceptionWhenNotOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(1L, booking.getId()));
    }

    @Test
    void shouldNotGetBookingByIdAndThrowExceptionWhenBookingNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(user1.getId(), booking.getId()));
    }

    @Test
    void shouldGetAllByBookerWhenBookerAllState() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBooker(any(User.class), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingOutputDto> response = bookingService.getAllByBooker(user1.getId(), State.ALL, 0, 10);
        assertEquals(booking.getItem().getName(), response.get(0).getItem().getName());
        verify(bookingRepository, Mockito.times(1))
                .findByBooker(any(User.class), any());
    }

    @Test
    void shouldGetAllByBookerWhenBookerCurrentState() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerIdCurrent(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingOutputDto> response = bookingService.getAllByBooker(user1.getId(), State.CURRENT, 0, 10);
        assertEquals(booking.getItem().getName(), response.get(0).getItem().getName());
        verify(bookingRepository, Mockito.times(1))
                .findByBookerIdCurrent(anyLong(), any());
    }

    @Test
    void shouldGetAllByBookerWhenBookerPastState() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerIdPast(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingOutputDto> response = bookingService.getAllByBooker(user1.getId(), State.PAST, 0, 10);
        assertEquals(booking.getItem().getName(), response.get(0).getItem().getName());
        verify(bookingRepository, Mockito.times(1))
                .findByBookerIdPast(anyLong(), any());
    }

    @Test
    void shouldGetAllByBookerWhenBookerFutureState() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerAndStartIsAfter(any(User.class), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingOutputDto> response = bookingService.getAllByBooker(user1.getId(), State.FUTURE, 0, 10);
        assertEquals(booking.getItem().getName(), response.get(0).getItem().getName());
        verify(bookingRepository, Mockito.times(1))
                .findByBookerAndStartIsAfter(any(User.class), any(), any());
    }

    @Test
    void shouldGetAllByBookerWhenBookerWaitingStatus() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerAndStatus(any(User.class), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingOutputDto> response = bookingService.getAllByBooker(user1.getId(), State.WAITING, 0, 10);
        assertEquals(booking.getItem().getName(), response.get(0).getItem().getName());
        verify(bookingRepository, Mockito.times(1))
                .findByBookerAndStatus(any(User.class), any(), any());
    }

    @Test
    void shouldGetAllByBookerWhenBookerRejectedStatus() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerAndStatus(any(User.class), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingOutputDto> response = bookingService.getAllByBooker(user1.getId(), State.REJECTED, 0, 10);
        assertEquals(booking.getItem().getName(), response.get(0).getItem().getName());

        verify(bookingRepository, Mockito.times(1))
                .findByBookerAndStatus(any(User.class), any(), any());
    }

    @Test
    void shouldNotGetAllByBookerAndThrowExceptionWhenBookerNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getAllByBooker(user1.getId(), State.ALL, 0, 10));
    }

    @Test
    void shouldGetAllByOwnerWhenBookerAllState() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwner(any(User.class), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingOutputDto> response = bookingService.getAllByOwner(user1.getId(), State.ALL, 0, 10);
        assertEquals(booking.getItem().getName(), response.get(0).getItem().getName());
        verify(bookingRepository, Mockito.times(1))
                .findByItemOwner(any(User.class), any());
    }

    @Test
    void shouldGetAllByOwnerWhenBookerCurrentState() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerIdCurrent(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingOutputDto> response = bookingService.getAllByOwner(user1.getId(), State.CURRENT, 0, 10);
        assertEquals(booking.getItem().getName(), response.get(0).getItem().getName());
        verify(bookingRepository, Mockito.times(1))
                .findByItemOwnerIdCurrent(anyLong(), any());
    }

    @Test
    void shouldGetAllByOwnerWhenBookerPastState() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerIdPast(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingOutputDto> response = bookingService.getAllByOwner(user1.getId(), State.PAST, 0, 10);
        assertEquals(booking.getItem().getName(), response.get(0).getItem().getName());
        verify(bookingRepository, Mockito.times(1))
                .findByItemOwnerIdPast(anyLong(), any());
    }

    @Test
    void shouldGetAllByOwnerWhenBookerFutureState() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerAndStartAfter(any(User.class), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingOutputDto> response = bookingService.getAllByOwner(user1.getId(), State.FUTURE, 0, 10);
        assertEquals(booking.getItem().getName(), response.get(0).getItem().getName());
        verify(bookingRepository, Mockito.times(1))
                .findByItemOwnerAndStartAfter(any(User.class), any(), any());
    }

    @Test
    void shouldGetAllByOwnerWhenBookerWaitingStatus() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerAndStatusEquals(any(User.class), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingOutputDto> response = bookingService.getAllByOwner(user1.getId(), State.WAITING, 0, 10);
        assertEquals(booking.getItem().getName(), response.get(0).getItem().getName());
        verify(bookingRepository, Mockito.times(1))
                .findByItemOwnerAndStatusEquals(any(User.class), any(), any());
    }

    @Test
    void shouldGetAllByOwnerWhenBookerRejectedStatus() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerAndStatusEquals(any(User.class), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingOutputDto> response = bookingService.getAllByOwner(user1.getId(), State.REJECTED, 0, 10);
        assertEquals(booking.getItem().getName(), response.get(0).getItem().getName());
        verify(bookingRepository, Mockito.times(1))
                .findByItemOwnerAndStatusEquals(any(User.class), any(), any());
    }

    @Test
    void shouldNotGetAllByOwnerAndThrowExceptionWhenBookerNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getAllByOwner(user2.getId(), State.ALL, 0, 10));
    }
}
