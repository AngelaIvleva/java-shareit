package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.ToCreate;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.HEADER;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingOutputDto addBooking(@RequestHeader(HEADER) Long bookerId,
                                       @Validated(ToCreate.class) @RequestBody BookingDto bookingDto) {
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
                                                 @PositiveOrZero @RequestParam(value = "from",
                                                         defaultValue = "0", required = false) int from,
                                                 @Positive @RequestParam(value = "size", defaultValue = "10",
                                                         required = false) int size) {
        return bookingService.getAllByBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingOutputDto> getAllByOwner(@RequestHeader(HEADER) Long ownerId,
                                                @RequestParam(defaultValue = "ALL", required = false) State state,
                                                @PositiveOrZero @RequestParam(value = "from",
                                                        defaultValue = "0", required = false) int from,
                                                @Positive @RequestParam(value = "size", defaultValue = "10",
                                                        required = false) int size) {
        return bookingService.getAllByOwner(ownerId, state, from, size);
    }
}
