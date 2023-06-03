package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;

import java.util.List;

public interface BookingService {
    BookingOutputDto addBooking(Long bookerId, BookingDto bookingDto);

    BookingOutputDto changeStatus(Long ownerId, Long bookingId, boolean approved);

    BookingOutputDto getBookingById(Long userId, Long bookingId);

    List<BookingOutputDto> getAllByBooker(Long bookerId, String state);

    List<BookingOutputDto> getAllByOwner(Long ownerId, String state);
}
