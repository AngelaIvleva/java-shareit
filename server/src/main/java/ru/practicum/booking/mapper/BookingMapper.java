package ru.practicum.booking.mapper;

import lombok.Data;
import ru.practicum.booking.dto.BookingDateDto;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingOutputDto;
import ru.practicum.booking.model.Booking;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;

@Data
public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto, User user, Item item) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(user)
                .status(bookingDto.getStatus())
                .build();
    }

    public static BookingOutputDto toBookingOutputDto(Booking booking) {
        return BookingOutputDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static BookingDateDto toBookingDateDto(Booking booking) {
        return BookingDateDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}