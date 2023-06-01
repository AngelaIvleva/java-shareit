package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.ToCreate;

import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.HEADER;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingOutputDto addBooking(@RequestHeader(HEADER) long bookerId,
                                       @Validated(ToCreate.class) @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto changeStatus(@RequestHeader(HEADER) long ownerId,
                                           @PathVariable long bookingId,
                                           @RequestParam boolean approved) {
        return bookingService.changeStatus(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getBookingById(@RequestHeader(HEADER) long userId,
                                      @PathVariable long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutputDto> getAllByBooker(@RequestHeader(HEADER) long bookerId,
                                              @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getAllByBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingOutputDto> getAllByOwner(@RequestHeader(HEADER) long ownerId,
                                               @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getAllByOwner(ownerId, state);
    }
}
