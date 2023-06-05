package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.ToCreate;
import ru.practicum.shareit.user.ToUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;
    @NotBlank(groups = {ToCreate.class}, message = "Введите имя")
    private String name;
    @NotNull(groups = {ToCreate.class})
    @Email(groups = {ToCreate.class, ToUpdate.class}, message = "Некорректный адрес электронной почты")
    private String email;

}
