package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.ToCreate;
import ru.practicum.shareit.user.ToUpdate;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {
    @NotNull(groups = {ToUpdate.class}, message = "Введите id")
    private long id;
    @NotNull(groups = {ToCreate.class}, message = "Введите дату старта бронирования")
    @FutureOrPresent(groups = {ToCreate.class})
    private LocalDateTime start;
    @NotNull(groups = {ToCreate.class}, message = "Введите дату окончания бронирования")
    @Future(groups = {ToCreate.class})
    private LocalDateTime end;
    @NotNull(groups = {ToCreate.class}, message = "Введите id вещи")
    private long itemId;
    private long bookerId;
    private Status status;

}
