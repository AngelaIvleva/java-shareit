package ru.practicum.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.booking.model.Status;
import ru.practicum.user.ToCreate;
import ru.practicum.user.ToUpdate;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {
    @NotNull(groups = {ToUpdate.class}, message = "Введите id")
    private Long id;
    @NotNull(groups = {ToCreate.class}, message = "Введите дату старта бронирования")
    @FutureOrPresent(groups = {ToCreate.class})
    private LocalDateTime start;
    @NotNull(groups = {ToCreate.class}, message = "Введите дату окончания бронирования")
    @Future(groups = {ToCreate.class})
    private LocalDateTime end;
    @NotNull(groups = {ToCreate.class}, message = "Введите id вещи")
    private Long itemId;
    private Long bookerId;
    private Status status;

}
