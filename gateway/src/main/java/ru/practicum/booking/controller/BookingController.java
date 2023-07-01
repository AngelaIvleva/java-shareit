package ru.practicum.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.client.BookingClient;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.user.ToCreate;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.item.controller.ItemController.HEADER;

@Validated
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(HEADER) Long bookerId,
                                             @Validated(ToCreate.class) @RequestBody BookingDto bookingDto) {
        return bookingClient.addBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeStatus(@RequestHeader(HEADER) Long ownerId,
                                               @PathVariable Long bookingId,
                                               @RequestParam boolean approved) {
        return bookingClient.changeStatus(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(HEADER) Long userId,
                                                 @PathVariable Long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBooker(@RequestHeader(HEADER) Long bookerId,
                                                 @RequestParam(defaultValue = "ALL", required = false) String state,
                                                 @PositiveOrZero @RequestParam(value = "from",
                                                         defaultValue = "0", required = false) int from,
                                                 @Positive @RequestParam(value = "size", defaultValue = "10",
                                                         required = false) int size) {
        return bookingClient.getAllByBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(HEADER) Long ownerId,
                                                @RequestParam(defaultValue = "ALL", required = false) String state,
                                                @PositiveOrZero @RequestParam(value = "from",
                                                        defaultValue = "0", required = false) int from,
                                                @Positive @RequestParam(value = "size", defaultValue = "10",
                                                        required = false) int size) {
        return bookingClient.getAllByOwner(ownerId, state, from, size);
    }
}
