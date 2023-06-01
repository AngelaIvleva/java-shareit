package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");
    private final LocalDateTime now = LocalDateTime.now();

    @Override
    @Transactional
    public BookingOutputDto addBooking(long bookerId, BookingDto bookingDto) {
        User user = checkUserExistence(bookerId);
        Item item = checkItemExistence(bookingDto.getItemId());
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new BadRequestException("Некорректная дата бронирования");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException(String.format("Объект id %d недоступен для бронирования",
                    item.getId()));
        }
        if (item.getOwner().getId() == bookerId) {
            throw new NotFoundException("Владелец вещи не может ее забронировать");
        }
        bookingDto.setStatus(Status.WAITING);
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto, user, item));
        return BookingMapper.toBookingOutputDto(booking);
    }

    @Override
    @Transactional
    public BookingOutputDto changeStatus(long ownerId, long bookingId, boolean approved) {
        checkUserExistence(ownerId);
        Booking booking = checkBookingExistence(bookingId);

        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BadRequestException("Бронирование уже было подтверждено");
        }
        Item item = booking.getItem();

        if (item.getOwner().getId() != ownerId) {
            log.info(String.format("Пользователь id %d не является владельцем вещи", ownerId));
            throw new NotFoundException(String.format("Пользователь id %d не является владельцем вещи", ownerId));
        }
        if (approved && booking.getStatus().equals(Status.WAITING)) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingOutputDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingOutputDto getBookingById(long userId, long bookingId) {
        checkUserExistence(userId);
        Booking booking = checkBookingExistence(bookingId);
        Item item = booking.getItem();

        if (item.getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            log.info("Пользователь должен быть владельцем вещи или автором бронирования");
            throw new NotFoundException(
                    String.format("Пользователь с id %d должен быть владельцем вещи с id %d " +
                            "или автором бронирования с id %d", userId, item.getId(), booking.getId()));
        }
        return BookingMapper.toBookingOutputDto(booking);
    }

    @Override
    @Transactional
    public List<BookingOutputDto> getAllByBooker(long bookerId, String state) {
        User user = checkUserExistence(bookerId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepository.findByBooker(user, sort));
                break;
            case "CURRENT":
                bookings.addAll(bookingRepository.findByBookerAndStartIsBeforeAndEndIsAfter(user, now, now, sort));
                break;
            case "PAST":
                bookings.addAll(bookingRepository.findByBookerAndEndIsBefore(user, now, sort));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepository.findByBookerAndStartIsAfter(user, now, sort));
                break;
            case "WAITING":
                bookings.addAll(bookingRepository.findByBookerAndStatus(user, Status.WAITING, sort));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepository.findByBookerAndStatus(user, Status.REJECTED, sort));
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream()
                .map(BookingMapper::toBookingOutputDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<BookingOutputDto> getAllByOwner(long ownerId, String state) {
        User user = checkUserExistence(ownerId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepository.findByItemOwner(user, sort));
                break;
            case "CURRENT":
                bookings.addAll(bookingRepository.findByItemOwnerAndStartBeforeAndEndAfter(user, now, now, sort));
                break;
            case "PAST":
                bookings.addAll(bookingRepository.findByItemOwnerAndEndBefore(user, now, sort));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepository.findByItemOwnerAndStartAfter(user, now, sort));
                break;
            case "WAITING":
                bookings.addAll(bookingRepository.findByItemOwnerAndStatusEquals(user, Status.WAITING, sort));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepository.findByItemOwnerAndStatusEquals(user, Status.REJECTED, sort));
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream()
                .map(BookingMapper::toBookingOutputDto)
                .collect(Collectors.toList());
    }

    private User checkUserExistence(long userId) {
        log.info(String.format("Поиск пользователя с id %d", userId));
        return userRepository.findById(userId).orElseThrow(() -> {
            log.info(String.format("Пользователя с id %d не найден", userId));
            throw new NotFoundException(String.format("Пользователь id %d  не найден", userId));
        });
    }

    private Item checkItemExistence(long itemId) {
        log.info(String.format("Поиск объекта с id %d", itemId));
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.info(String.format("Объект id %d  не найден", itemId));
            throw new NotFoundException(String.format("Объект id %d  не найден", itemId));
        });
    }

    private Booking checkBookingExistence(long bookingId) {
        log.info(String.format("Поиск брони с id %d", bookingId));
        return bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.info(String.format("Бронь id %d  не найдена", bookingId));
            throw new NotFoundException(String.format("Бронь id %d  не найдена", bookingId));
        });
    }
}
