package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.ToCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    private long id;
    @NotBlank(groups = ToCreate.class, message = "Введите название")
    private String name;
    @NotBlank(groups = ToCreate.class, message = "Добавьте пару слов об этой вещи")
    private String description;
    @NotNull(groups = ToCreate.class, message = "Доступна ли вещь для аренды?")
    private Boolean available;
}
