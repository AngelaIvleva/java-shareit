package ru.practicum.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.booking.model.Status;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDateDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long bookerId;
    private Status status;
}
