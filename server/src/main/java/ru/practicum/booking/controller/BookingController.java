package ru.practicum.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingOutputDto;
import ru.practicum.booking.model.State;
import ru.practicum.booking.service.BookingService;

import java.util.List;

import static ru.practicum.item.controller.ItemController.HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingOutputDto addBooking(@RequestHeader(HEADER) Long bookerId,
                                       @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto changeStatus(@RequestHeader(HEADER) Long ownerId,
                                         @PathVariable Long bookingId,
                                         @RequestParam boolean approved) {
        return bookingService.changeStatus(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getBookingById(@RequestHeader(HEADER) Long userId,
                                           @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutputDto> getAllByBooker(@RequestHeader(HEADER) Long bookerId,
                                                 @RequestParam(defaultValue = "ALL", required = false) State state,
                                                 @RequestParam(value = "from",
                                                         defaultValue = "0", required = false) int from,
                                                 @RequestParam(value = "size", defaultValue = "10",
                                                         required = false) int size) {
        return bookingService.getAllByBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingOutputDto> getAllByOwner(@RequestHeader(HEADER) Long ownerId,
                                                @RequestParam(defaultValue = "ALL", required = false) State state,
                                                @RequestParam(value = "from",
                                                        defaultValue = "0", required = false) int from,
                                                @RequestParam(value = "size", defaultValue = "10",
                                                        required = false) int size) {
        return bookingService.getAllByOwner(ownerId, state, from, size);
    }
}
