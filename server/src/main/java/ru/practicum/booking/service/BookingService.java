package ru.practicum.booking.service;

import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingOutputDto;
import ru.practicum.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingOutputDto addBooking(Long bookerId, BookingDto bookingDto);

    BookingOutputDto changeStatus(Long ownerId, Long bookingId, boolean approved);

    BookingOutputDto getBookingById(Long userId, Long bookingId);

    List<BookingOutputDto> getAllByBooker(Long bookerId, State state, int from, int size);

    List<BookingOutputDto> getAllByOwner(Long ownerId, State state, int from, int size);
}
