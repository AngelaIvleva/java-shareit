package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");
    private final LocalDateTime now = LocalDateTime.now();

    @Override
    @Transactional
    public BookingOutputDto addBooking(Long bookerId, BookingDto bookingDto) {
        User user = checkUserExistence(bookerId);
        Item item = checkItemExistence(bookingDto.getItemId());
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new BadRequestException("Некорректная дата бронирования");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException(String.format("Объект id %d недоступен для бронирования", item.getId()));
        }
        if (Objects.equals(item.getOwner().getId(), bookerId)) {
            throw new NotFoundException("Владелец вещи не может ее забронировать");
        }
        bookingDto.setStatus(Status.WAITING);
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto, user, item));
        return BookingMapper.toBookingOutputDto(booking);
    }

    @Override
    @Transactional
    public BookingOutputDto changeStatus(Long ownerId, Long bookingId, boolean approved) {
        checkUserExistence(ownerId);
        Booking booking = checkBookingExistence(bookingId);

        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BadRequestException("Бронирование уже было подтверждено");
        }
        Item item = booking.getItem();

        if (!Objects.equals(item.getOwner().getId(), ownerId)) {
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
    public BookingOutputDto getBookingById(Long userId, Long bookingId) {
        checkUserExistence(userId);
        Booking booking = checkBookingExistence(bookingId);
        Item item = booking.getItem();

        if (!Objects.equals(item.getOwner().getId(), userId) && !Objects.equals(booking.getBooker().getId(), userId)) {
            log.info("Пользователь должен быть владельцем вещи или автором бронирования");
            throw new NotFoundException(
                    String.format("Пользователь id %d должен быть владельцем вещи id %d %s " +
                            "или автором бронирования id %d", userId, item.getId(), item.getName(), booking.getId()));
        }
        return BookingMapper.toBookingOutputDto(booking);
    }

    @Override
    @Transactional
    public List<BookingOutputDto> getAllByBooker(Long bookerId, State state, int from, int size) {
        User user = checkUserExistence(bookerId);
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings.addAll(bookingRepository.findByBooker(user, pageRequest));
                break;
            case CURRENT:
                bookings.addAll(bookingRepository.findByBookerIdCurrent(bookerId, pageRequest));
                break;
            case PAST:
                bookings.addAll(bookingRepository.findByBookerIdPast(bookerId, pageRequest));
                break;
            case FUTURE:
                bookings.addAll(bookingRepository.findByBookerAndStartIsAfter(user, now, pageRequest));
                break;
            case WAITING:
                bookings.addAll(bookingRepository.findByBookerAndStatus(user, Status.WAITING, pageRequest));
                break;
            case REJECTED:
                bookings.addAll(bookingRepository.findByBookerAndStatus(user, Status.REJECTED, pageRequest));
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
    public List<BookingOutputDto> getAllByOwner(Long ownerId, State state, int from, int size) {
        User user = checkUserExistence(ownerId);
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings.addAll(bookingRepository.findByItemOwner(user, pageRequest));
                break;
            case CURRENT:
                bookings.addAll(bookingRepository.findByItemOwnerIdCurrent(ownerId, pageRequest));
                break;
            case PAST:
                bookings.addAll(bookingRepository.findByItemOwnerIdPast(ownerId, pageRequest));
                break;
            case FUTURE:
                bookings.addAll(bookingRepository.findByItemOwnerAndStartAfter(user, now, pageRequest));
                break;
            case WAITING:
                bookings.addAll(bookingRepository.findByItemOwnerAndStatusEquals(user, Status.WAITING, pageRequest));
                break;
            case REJECTED:
                bookings.addAll(bookingRepository.findByItemOwnerAndStatusEquals(user, Status.REJECTED, pageRequest));
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream()
                .map(BookingMapper::toBookingOutputDto)
                .collect(Collectors.toList());
    }

    private User checkUserExistence(Long userId) {
        log.info(String.format("Поиск пользователя с id %d", userId));
        return userRepository.findById(userId).orElseThrow(() -> {
            log.info(String.format("Пользователя с id %d не найден", userId));
            throw new NotFoundException(String.format("Пользователь id %d  не найден", userId));
        });
    }

    private Item checkItemExistence(Long itemId) {
        log.info(String.format("Поиск объекта с id %d", itemId));
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.info(String.format("Объект id %d  не найден", itemId));
            throw new NotFoundException(String.format("Объект id %d  не найден", itemId));
        });
    }

    private Booking checkBookingExistence(Long bookingId) {
        log.info(String.format("Поиск брони с id %d", bookingId));
        return bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.info(String.format("Бронь id %d  не найдена", bookingId));
            throw new NotFoundException(String.format("Бронь id %d  не найдена", bookingId));
        });
    }
}
