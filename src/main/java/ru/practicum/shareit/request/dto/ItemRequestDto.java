package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.user.ToCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestDto {
    private Long id;
    @NotBlank(groups = ToCreate.class)
    @Size(groups = ToCreate.class, min = 10, max = 200)
    private String description;
    private Long requestorId;
    private LocalDateTime created;
}
