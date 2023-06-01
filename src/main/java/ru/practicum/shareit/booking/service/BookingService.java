package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;

import java.util.List;

public interface BookingService {
    BookingOutputDto addBooking(long bookerId, BookingDto bookingDto);

    BookingOutputDto changeStatus(long ownerId, long bookingId, boolean approved);

    BookingOutputDto getBookingById(long userId, long bookingId);

    List<BookingOutputDto> getAllByBooker(long bookerId, String state);

    List<BookingOutputDto> getAllByOwner(long ownerId, String state);
}
